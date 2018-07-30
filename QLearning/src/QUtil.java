import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import bwapi.Color;
import bwapi.Player;
import bwapi.Position;
import bwapi.Race;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;
import bwta.BaseLocation;
import bwta.Chokepoint;
import bwta.Polygon;
import bwta.Region;

public class QUtil {
	
	/**
	 * Action을 수행한다.
	 * @param myUnitList
	 * @param actionsFromCurrentState
	 * @return
	 */
	public static QAction actActionRandom(QUnitList myUnitList, List<QAction> actionsFromCurrentState) {
		
		boolean isAttack = false;
		QAction action;
		
		// 사이즈가 1인경우 maxQ로 실행
		if (actionsFromCurrentState.size() < 2) {
			action = actMaxQ(myUnitList, actionsFromCurrentState.get(0));
		} else {
			// 랜덤으로 공격 또는 후퇴
			Random rd = new Random();
			int rdIdx = rd.nextInt(2);
			// 0이면 공격
			if (rdIdx == 0 && actionsFromCurrentState.size() >= QConstants.RANDOM_ACTION_CNT) {
				isAttack = true;
			}
			
			action = actRandom(myUnitList, actionsFromCurrentState, isAttack);
		}
		return action;
	}
	
	/**
	 * 랜덤하게 Action을 수행한다.
	 * @param myUnitList
	 * @param actionsFromCurrentState
	 * @param isAttack
	 * @return
	 */
	private static QAction actRandom(QUnitList myUnitList, List<QAction> actionsFromCurrentState, boolean isAttack) {
		QAction qAction = null;
		// 공격인 경우
		if (isAttack) {
			if (actionsFromCurrentState.size() > QConstants.RANDOM_ACTION_CNT) {
				// 클로킹 공격
				for (QAction action : actionsFromCurrentState) {
					if (QConstants.ActionType.Cloak.equals(action.getActionType())) {
		    			// 공격
		    			Unit targetUnit = getUnit(action.getTargetId());
		    			myUnitList.cloak();
		    			myUnitList.rightClick(targetUnit);
		    			qAction = action;
		    			// 사용자 화면에 표시하기 위한 플래그처리
		    			QFlag.setTargetUnit(targetUnit);
		    			break;
					}
				}
			} else {
				for (QAction action : actionsFromCurrentState) {
					if (QConstants.ActionType.Attack.equals(action.getActionType())) {
		    			// 공격
		    			Unit targetUnit = getUnit(action.getTargetId());
		    			myUnitList.decloak();
		    			myUnitList.rightClick(targetUnit);
		    			qAction = action;
		    			// 사용자 화면에 표시하기 위한 플래그처리
		    			QFlag.setTargetUnit(targetUnit);
		    			break;
					}
				}
			}

		} else {
			// Move인 경우
			// 랜덤하게 이동할 경로 선택
			Random rd = new Random();
			int rdIdx = rd.nextInt(actionsFromCurrentState.size()-1);
			QAction action = actionsFromCurrentState.get(rdIdx);
            Position ps = new Position(action.getMoveX(), action.getMoveY());
            //myUnitList.decloak();
			myUnitList.rightClick(ps);
			
			// 사용자 화면에 표시하기 위한 플래그처리
			QFlag.setMovingTargetGrdIdx(action.getMoveGrdIdx());
			qAction = action;
		}
		
		return qAction;
	}

	/**
	 * 파라미터로 입력받은 유닛ID의 BWAPI유닛 객체를 리턴한다.
	 * @param unitId
	 * @return
	 */
	public static Unit getUnit(int unitId) {
		Player enemy = MyBotModule.Broodwar.enemy();
		Unit rtnEnemyUnit = null;
		for (Unit enemyUnit : enemy.getUnits()) {
			if (enemyUnit.getID() == unitId) {
				rtnEnemyUnit = enemyUnit;
				break;
			}
		}
		return rtnEnemyUnit;
	}

	/**
	 * Action을 수행한다.
	 * @param myUnitList
	 * @param qAction
	 * @return
	 */
	private static QAction actMaxQ(QUnitList myUnitList, QAction qAction) {
		if (QConstants.ActionType.Cloak.equals(qAction.getActionType())) {
			// Cloak 공격
			Unit targetUnit = getUnit(qAction.getTargetId());
			if (targetUnit != null) {
				myUnitList.cloak();
				myUnitList.rightClick(targetUnit);

				// 사용자 화면에 표시하기 위한 플래그처리
				QFlag.setTargetUnit(targetUnit);
			}
		}
		else if (QConstants.ActionType.Attack.equals(qAction.getActionType())) {
			// 공격
			Unit targetUnit = getUnit(qAction.getTargetId());
			if (targetUnit != null) {
				myUnitList.rightClick(targetUnit);
				
				// 사용자 화면에 표시하기 위한 플래그처리
				QFlag.setTargetUnit(targetUnit);
			}

		} else {
			// 이동
            Position ps = new Position(qAction.getMoveX(), qAction.getMoveY());
			myUnitList.rightClick(ps);
			
			// 사용자 화면에 표시하기 위한 플래그처리
			QFlag.setMovingTargetGrdIdx(qAction.getMoveGrdIdx());
		}
		return qAction;
	}

	/**
	 * Action이 종료되었는지 여부를 리턴한다.
	 * 공격의 경우에는 Cooldown으로
	 * 이동의 경우에는 실제이동이 완료된것으로 판단한다.
	 * @param crtStat
	 * @param nextState
	 * @param qAction
	 * @return
	 */
	public static boolean isActDone(QState crtStat, QState nextState, QAction action) {
		
		boolean isActionEnd = false;
		
		if (action.getActionType().equals(QConstants.ActionType.Attack) || action.getActionType().equals(QConstants.ActionType.Cloak)) {
			
			QState thisState = crtStat;
			QState otherState = nextState;
			
			// 나를 타격할 수 있는 새로운 유닛이 1이상이면 다시 판단한다.
			/*
			 *   && !QFlag.isTraningMode()
			 */
			if (nextState.getEneAttackCnt() > 0) {
				int newAttackUnitCnt = 0;
				boolean isNewAttackUnit = true;
				List<Integer> eneList = crtStat.getEneList();
				List<Integer> eneAttackList = nextState.getEneAttackList();
				
				for (int j = 0; j < eneAttackList.size(); j++) {
					isNewAttackUnit = true;
					for (int i = 0; i < eneList.size(); i++) {
						if (eneList.get(i) - eneAttackList.get(j) == 0) {
							isNewAttackUnit = false;
							break;
						}
					}
					
					if (isNewAttackUnit) {
						newAttackUnitCnt++;
					}
				}

				if (newAttackUnitCnt > 0) {
					System.out.println("======== action.getActionType().equals(QConstants.ActionType.Attack)");
					return true;
				}
			}
			
			/*
			 * 공격대기가 20을 초과할 경우 리턴
			 */
			int thisCooldown = thisState.getCooldown();
			int otherCooldown = otherState.getCooldown();
			//System.out.println("thisCooldown - otherCooldown : " + (thisCooldown - otherCooldown)+"");
			if (Math.abs(thisCooldown - otherCooldown) > 20) {
				return true;
			}
			
			/*
			 * 공격할 유닛 변경되었다면 id를 반영한다.
			 */
			if (action.getTargetId() != nextState.getTargetUnitId()) {
				
    			// 공격
    			Unit nextTargetUnit = getUnit(nextState.getTargetUnitId());
    			if (nextTargetUnit != null) {
        			action.setTargetId(nextState.getTargetUnitId());
        			QFlag.getMyQUnits().rightClick(nextTargetUnit);
        			QFlag.setTargetUnit(nextTargetUnit);
				}
			}
			
			/*
			 * 공격할 유닛이 사정거리를 벗어난 경우 리턴
			 */
			if (getUnit(crtStat.getTargetUnitId()) == null) {
				System.out.println("getUnit(crtStat.getTargetUnitId()) == null");
				return true;
			}
			int attackRange = QConstants.QUNIT_GROUNDWEAPON_ATTCK_RANGE + 25;
			
			int unitDistance = QFlag.getMyUnit().getDistance(getUnit(action.getTargetId()));
			if (attackRange < unitDistance) {
				return true;
			}
		}

		// 이동인경우
		else if (action.getActionType().equals(QConstants.ActionType.Move)) {

			// 이동중 나를 타격할 수 있는 유닛이 1이상이면 다시 판단한다.
			//   && !QFlag.isTraningMode()
			if (nextState.getEneAttackCnt() > 0) {
				int newAttackUnitCnt = 0;
				boolean isNewAttackUnit = true;
				List<Integer> eneList = crtStat.getEneList();
				List<Integer> eneAttackList = nextState.getEneAttackList();
				
				for (int j = 0; j < eneAttackList.size(); j++) {
					isNewAttackUnit = true;
					for (int i = 0; i < eneList.size(); i++) {
						if (eneList.get(i) - eneAttackList.get(j) == 0) {
							isNewAttackUnit = false;
							break;
						}
					}
					
					if (isNewAttackUnit) {
						newAttackUnitCnt++;
					}
				}

				if (newAttackUnitCnt > 0) {
					System.out.println("======== action.getActionType().equals(QConstants.ActionType.Move)");
					return true;
				}
			}
			
			// 이동인 경우 타겟지점과 현재 유닛의 지점의 거리차이에 도달한 경우로 판단한다.
			Position targetPosition = new Position(action.getMoveX(), action.getMoveY());
			Position myUnitPosition = QFlag.getMyUnit().getPosition();
			double distance = myUnitPosition.getDistance(targetPosition);
			if (distance < QConstants.MOVE_ACTION_DISTANCE) {
				return true;
			} else {
				QFlag.getMyQUnits().rightClick(targetPosition);
			}
		}
		return isActionEnd;
	}
	
	/**
	 * Reward를 계산하여 리턴한다.
	 * @param crtStat
	 * @param nextState
	 * @param qAction
	 * @return
	 */
	public static double getReward(QState crtStat, QState nextState, QAction qAction) {
		double reward = 0.;
		
		// 아군의 체력차이
		double myHpDiff = crtStat.getMyHitPoints() - nextState.getMyHitPoints();
		
		// 체력이 증가된것은 reward에서 제외
		if (nextState.getMyHitPoints() > crtStat.getMyHitPoints()) {
			myHpDiff = 0.;
		}
		
		// 공격인 경우 적 유닛에 따라 보상으로 주고 공격가능유닛수로 페널티를 준다.
		int killPize = 0;
		int penalty = 0;
		int overLoadCnt = nextState.getEneOverLoadCount();
		
		if (QConstants.ActionType.Attack.equals(qAction.getActionType())) {
			if (qAction.getTargetUnitType().equals(UnitType.Zerg_Overlord+"")) {
				killPize = 4;
			} else if (qAction.getTargetUnitType().equals(UnitType.Zerg_Drone+"")) {
				killPize = 3;
			} else if (qAction.getTargetUnitType().equals(UnitType.Zerg_Hydralisk+"")) {
				killPize = 6;
			}
			penalty = (int) Math.pow(4, nextState.getEneAttackCnt());
			
		} else if (QConstants.ActionType.Cloak.equals(qAction.getActionType())) {
			
			if (qAction.getTargetUnitType().equals(UnitType.Zerg_Overlord+"")) {
				killPize = 3;
			} else if (qAction.getTargetUnitType().equals(UnitType.Zerg_Drone+"")) {
				// 오버로드가 없는데 Cloak으로 공격하면 penalty
				if (overLoadCnt < 1) {
					penalty = 100;
				}
			} else if (qAction.getTargetUnitType().equals(UnitType.Zerg_Hydralisk+"")) {
				// 오버로드가 없을때는 히드라를 우선으로 공격
				if (overLoadCnt < 1) {
					killPize = 100;
				}
			}
			if (overLoadCnt > 0) {
				penalty = (int) Math.pow(4, nextState.getEneAttackCnt());
			}
		}
		
		// 리워드 = 공격보상 - 아군체력소모 - 적군공격가능유닛수페널티
		reward = killPize - myHpDiff - penalty;

		return reward;
	}
	
	/**
	 * 사용자 화면에 Q유닛의 정보를 표시한다.
	 * @param unit
	 */
	public static void drawUnitViewMap(Unit unit) {
		
		int sightRange = QConstants.QUNIT_SINGHT_RANGE; // 224
		int attackRange = QConstants.QUNIT_GROUNDWEAPON_ATTCK_RANGE;
		int divideCnt = QConstants.SINGHT_DIVIDE_CNT;
		int cellSize = sightRange / divideCnt + 10;
		
		int cellCenterInitX = unit.getPosition().getX() - cellSize*divideCnt;
		int cellCenterInitY = unit.getPosition().getY() - cellSize*divideCnt;
		
		MyBotModule.Broodwar.drawCircleMap(unit.getPosition(), sightRange, Color.Cyan);
		MyBotModule.Broodwar.drawCircleMap(unit.getPosition(), attackRange, Color.Red);
		MyBotModule.Broodwar.drawCircleMap(unit.getPosition(), attackRange, Color.Red);
		
		if (QFlag.getTargetUnit() != null && QFlag.getNextAction() !=null &&  QConstants.ActionType.Attack.equals(QFlag.getNextAction().getActionType())) {
			MyBotModule.Broodwar.drawCircleMap(QFlag.getTargetUnit().getPosition(), 10, Color.Red);
		}
		
		int idx = 0;
		int cellCenterXL = cellCenterInitX;
		int cellCenterYD = cellCenterInitY;
		for (int i = 0; i < divideCnt*2; i++) {
			int cellCenterYU = cellCenterYD + cellSize;
			for (int j = 0; j < divideCnt*2; j++) {
				int cellCenterXR = cellCenterXL + cellSize;
				MyBotModule.Broodwar.drawBoxMap(cellCenterXL, cellCenterYD, cellCenterXR, cellCenterYU, Color.Orange);
				int x = (cellCenterXL + cellCenterXR) / 2;
				int y = (cellCenterYD + cellCenterYU) / 2;
				idx++;
				
				MyBotModule.Broodwar.drawTextMap(x, y, ""+idx);

				if (QFlag.getNextAction() !=null && idx == QFlag.getMovingTargetGrdIdx() && QConstants.ActionType.Move.equals(QFlag.getNextAction().getActionType())) {
					MyBotModule.Broodwar.drawBox(bwapi.CoordinateType.Enum.Map, cellCenterXL, cellCenterYD, cellCenterXR, cellCenterYU, Color.Yellow, true);
				}
				cellCenterXL = cellCenterXL + cellSize;
				
			}
			cellCenterYD = cellCenterYD + cellSize;
			cellCenterXL = cellCenterInitX;
		}
	}
	
	/**
	 * Q Table java객체 직렬화 -  파일쓰기
	 * @param race 
	 */
	public static void saveFileToQTable(String race) {
		try {
			String fileNm = "qtable" +  race + ".dat";
			FileOutputStream fos = new FileOutputStream(fileNm);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(QTable.getInstance());
			oos.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Q Table java객체 직렬화 -  파일읽기
	 * @param race 
	 */
	public static void readQTableFile(String race) {
		try {
			String fileNm = "qtable" +  race + ".dat";
			FileInputStream fis = new FileInputStream(fileNm);
			ObjectInputStream ois = new ObjectInputStream(fis);
			QTable.getInstance().setInstance(ois.readObject());
			ois.close();
			fis.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Q table에 reward를 Update한다.
	 * @param qTable
	 * @param crtStat
	 * @param nextState
	 * @param nextAction
	 * @return
	 */
	public static double updateQTable(QTable qTable, QState crtStat, QState nextState, QAction nextAction) {
		// 다음상태의 Max Q값을 취득한다.
		double nextMaxQ = qTable.maxQvForState(nextState);
		
		// Reward를 계산한다.
		double reward = getReward(crtStat, nextState, nextAction);
		
		// 현재 상태에서의 이전 Q값을 취득한다.
		double preQ = QFlag.getQ();
		
		// Q값 계산
		double qv = reward + QConstants.ALPHA * (preQ + QConstants.GAMMA * nextMaxQ - reward);
		
		// Q 테이블에 Q값을 반영한다.
		qTable.setQ(crtStat, nextAction, qv);
		
		QFlag.setNextAction(null);
		if (nextAction.getActionType().equals(QConstants.ActionType.Attack) || nextAction.getActionType().equals(QConstants.ActionType.Cloak)) {
			System.out.println("*** updateQTable : " + nextAction.getActionType() + ", maxQ : " + nextMaxQ + ", reward : " + reward + ", value : " + qv + ", getMoveGrdIdx : " + nextAction.getMoveGrdIdx());
		}
		return qv;
	}

	/**
	 * position이 targetPosition에 근접했는지 여부를 리턴한다.
	 * @param position
	 * @param targetPosition
	 * @return
	 */
	public static boolean isNearByPostion(Position position, Position targetPosition) {
		if (Math.abs(position.getX() - targetPosition.getX()) < 30 && Math.abs(position.getY() - targetPosition.getY()) < 30) {
			return true;
		}
		return false;
	}

	/**
	 * 전투가 가능한 상태인지를 리턴한다.
	 * @param targetPositionList
	 * @return
	 */
	public static boolean isCombatMode(List<Position> targetPositionList) {
		
		// 타겟 확인
		if (targetPositionList.isEmpty()) {
			return false;
		}
		
		// 초기화 완료여부 확인
		if (!QFlag.isInitComplete()) {
			return false;
		}
		
		// 유닛준비여부 확인
		if (QFlag.getMyQUnits().size() < QConstants.Q_LEARNING_START_UNIT_CNT) {
			return false;
		}
		
		return true;
	}

	/**
	 * 파라미터로 넘겨받은 좌표가 접근 가능한 좌표인지 리턴한다.
	 * @param movePositionX
	 * @param movePositionY
	 * @return
	 */
	public static boolean isUnreachablePosition(int movePositionX, int movePositionY) {
		
		if (movePositionX < 50 || movePositionY < 50) {
			return true;
		}
		
		int mapWidth = MyBotModule.Broodwar.mapHeight() * 32 - 50;
		int mapHeight = MyBotModule.Broodwar.mapHeight() * 32 - 50;
		
		if (movePositionX > mapWidth || movePositionY > mapHeight) {
			return true;
		}
		
		return false;
	}

	/**
	 * 파라미터로 입력받은 상태가 학습된 상태인지 여부를 리턴한다.
	 * @param crtStateMap
	 * @return
	 */
	public static boolean isTraningState(Map<QAction, Double> crtStateMap) {
		
		if (crtStateMap == null) {
			System.out.println("isTraningState crtStateMap == null");
			return false;
		}
		
		int actionSize = 0;
		Iterator<QAction> keys = crtStateMap.keySet().iterator();
        while( keys.hasNext() ){
        	QAction qAction = keys.next();
        	if (qAction.getActionType().equals(QConstants.ActionType.Attack) || qAction.getActionType().equals(QConstants.ActionType.Move)) {
        		actionSize++;
			}
        }
        System.out.println("isTraningState actionSize : " + actionSize);
        
        // 전체액션의 갯수 - 18
        if (actionSize > 10) {
        	return true;
		} else {
			return false;
		}
	}

	/**
	 * 공격 및 도망치는 포인트를 생성한다.
	 * 적군의 베이스 및 확장기지 주변에 포인트를 생성한다.
	 * @param targetPositionList 
	 * @return
	 */
	public static void makeTargetPositionList(List<Position> targetPositionList) {
		
		BaseLocation eneBaseLocation = InformationManager.Instance().getMainBaseLocation(InformationManager.Instance().enemyPlayer);
		BaseLocation eneFirstExpansionLocation = InformationManager.Instance().getFirstExpansionLocation(InformationManager.Instance().enemyPlayer);
		
		if (eneBaseLocation == null || eneFirstExpansionLocation == null) {
			return;
		}
		
		Position eneBasePosition = InformationManager.Instance().getMainBaseLocation(InformationManager.Instance().enemyPlayer).getPosition();
		Position eneFirstExpansionPosition = InformationManager.Instance().getFirstExpansionLocation(InformationManager.Instance().enemyPlayer).getPosition();
		
		if (targetPositionList.isEmpty()) {
			
			List<Position> basePositionList = getLocationPositionList(eneBaseLocation);
			List<Position> FirstExpansionPositionList = getLocationPositionList(eneFirstExpansionLocation);
			
			for (int i = 0; i < 100; i++) {
				targetPositionList.add(FirstExpansionPositionList.get(0));
				for (int j = 1; j < FirstExpansionPositionList.size(); j++) {
					//targetPositionList.add(eneFirstExpansionPosition);
					targetPositionList.add(FirstExpansionPositionList.get(j));
				}
				targetPositionList.add(basePositionList.get(0));
				for (int j = 1; j < basePositionList.size(); j++) {
					//targetPositionList.add(eneBasePosition);
					targetPositionList.add(basePositionList.get(j));
				}
			}
		}
	}

	/**
	 * 해당되는 로케이션에 포인츠를 반환한다.
	 * @param baseLocation
	 * @return
	 */
	private static List<Position> getLocationPositionList(BaseLocation baseLocation) {
		
		List<Position> retList = new ArrayList<Position>();
		Polygon p = baseLocation.getRegion().getPolygon();
		for (int j = 0; j<p.getPoints().size(); j++)
		{
			if (j % 10 == 0) {
				retList.add(p.getPoints().get((j + 1) % p.getPoints().size()));
			}
		}
		return retList;
	}

	/**
	 * 현재 상태를 리턴한다.
	 * 모든 종족에 대해 Q봇을 만들고자 하면 아래의 상태를 모두 정의하면 된다.
	 * (저그에 대해서만 정의되어 있음)
	 * @return
	 */
	public static QState getState() {
		
		QState state = null;
		if (MyBotModule.Broodwar.enemy().getRace() == Race.Terran) {
			state = new QStateT(MyBotModule.Broodwar);
		} else if (MyBotModule.Broodwar.enemy().getRace() == Race.Protoss) {
			state = new QStateP(MyBotModule.Broodwar);
		} else if (MyBotModule.Broodwar.enemy().getRace() == Race.Zerg) {
			state = new QStateZ(MyBotModule.Broodwar);
		}
		return state;
	}
}
