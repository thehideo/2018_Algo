import java.util.List;
import java.util.Random;

import bwapi.GameType;
import bwapi.Position;
import bwapi.Race;
import bwapi.Unit;

/**
 * Q learning 메인 클래스
 * @author SDS
 *
 */
public class Qlearning {
	
	private static Qlearning instance = new Qlearning();
	private QTable qTable;
    public Qlearning() {
    	
	}
    
	/**
	 * 학습메서드
	 */
	public void train() {
		
		// 대표Q유닛
		Unit myUnit = QFlag.getMyUnit();
		
		if (myUnit == null) {
			return;
		}
		
		// 학습 유닛들
		QUnitList myUnitList = QFlag.getMyQUnits();
		
		/*
		 * 현재상태를 객체화한다.
		 */
		QState crtState = QUtil.getState();
		
		// 유저 화면에 현재상태를 표시한다.
		// 소스제출시 주석처리 필요
		QUtil.drawUnitViewMap(myUnit);
		
		/*
		 * 학습상태에 있는경우리턴
		 * Frame은 계속 돌기 때문에 학습시작 또는 학습완료단계 체크시 다음Frame은 Skip처리한다.
		 */
		if (QFlag.getTrainingStateFlag().equals(QConstants.StateFlagType.ING)) {
			return;
		}
		
		// Q Table 객체
		qTable = QTable.getInstance();
		
		// 타겟지점에 도달하면 다음 타겟으로 이동한다.
		if (QUtil.isNearByPostion(myUnit.getPosition(), QFlag.getTargetPosition())) {
			int idx = QFlag.getTargetPositionIdx() + 1;
			QFlag.setTargetPositionIdx(idx);
		}
		
		
		/*
		 * 유닛이 서로 공격 할 수 있는 거리에 있지 않고 이전 Action이 있는 경우
		 * 공격을 갓다가 뒤로 빠진 경우이다.
		 * 이전 액션이 있으니 이전 액션에 대한 reward를 반영한다
		 */
		if (!crtState.isTrainingState() && QFlag.getNextAction() != null) {
			// 이전 학습시작이력이 있는 경우 q update
			if (QFlag.getTrainingStateFlag().equals(QConstants.StateFlagType.START)) {
				QState crtStat = QFlag.getCrtState();
				QState nextState = QUtil.getState();
				QAction nextAction = QFlag.getNextAction();
				QUtil.updateQTable(qTable, crtStat, nextState, nextAction);
				QFlag.setTrainingStateFlag(QConstants.StateFlagType.END);
			}
			
			/*
			 * 다시 동일한 타겟으로 공격가면 동일한 Max Q값으로 동일한 행위를 반복하므로
			 * 적진영을 나선으로 돌면서 공격한다.
			 */
			if (((QStateZ) QFlag.getCrtState()).getEneAttackCnt() > 2 && MyBotModule.Broodwar.getGameType() != GameType.Use_Map_Settings) {
				int idx = QFlag.getTargetPositionIdx() + 1;
				QFlag.setTargetPositionIdx(idx);
			}
		}
		else if (!crtState.isTrainingState() && QFlag.getNextAction() == null) {
			
			// 타겟으로 전진한다.
			if (MyBotModule.Broodwar.getGameType() == GameType.Use_Map_Settings) {
				myUnitList.attack(QFlag.getTargetPosition());
			} else {
				myUnitList.attackMove(QFlag.getTargetPosition());
			}
		} else {
			/**
			 * 학습상태가 학습전이거나 Action이 완료된 상태인경우 다음Action을 취하기 위해 학습을 시작한다.
			 */
			if (QFlag.getTrainingStateFlag().equals(QConstants.StateFlagType.STANDBY) 
					|| QFlag.getTrainingStateFlag().equals(QConstants.StateFlagType.END)) {

				// 다음 Frame이 간섭하지 못하도록 진행중 처리
				QFlag.setTrainingStateFlag(QConstants.StateFlagType.ING);
				
				// 현재상태의 스냅샷을 QFlag에 따로 저장 - reward 계산시 필요
				QFlag.setCrtState(crtState);
				List<QAction> actionsFromCurrentState;
				
				/*
				 * 학습모드인 경우에는 Random Action, 아닌 경우에는 maxQ Action으로 Action리스트를 구한다.
				 * TraningMode는 QConstants.MLTraning에서 제어할 수 있다.
				 */
				if (QConstants.MLTraning) {
					actionsFromCurrentState = crtState.possibleActionsFromState(); // 랜덤
				} else {
					actionsFromCurrentState = qTable.maxQActionsFromState(crtState); // Max Q
				}
				
				// Action을 수행한다.
				QAction nextAction = QUtil.actActionRandom(myUnitList, actionsFromCurrentState);
				
				// 현재선택한 Action정보를 Flag로 저장한다.
				QFlag.setQ(qTable.getQ(crtState, nextAction));
				QFlag.setNextAction(nextAction);
				QFlag.setTrainingStateFlag(QConstants.StateFlagType.START);
				
			} else if (QFlag.getTrainingStateFlag().equals(QConstants.StateFlagType.START)) {
				// Action 시작된 후... 완료되었는지를 체크한다.
				
				// 다음 Frame이 간섭하지 못하도록 진행중 처리
				QFlag.setTrainingStateFlag(QConstants.StateFlagType.ING);
				QState crtStat = QFlag.getCrtState();
				QState nextState = QUtil.getState();
				QAction nextAction = QFlag.getNextAction();
				
				// Action이 완료되었는지 체크한다.
				if (QUtil.isActDone(crtStat, nextState, nextAction)) {
					// 완료되었다면 Q 테이블에 reward를 반영한다.
					QUtil.updateQTable(qTable, crtStat, nextState, nextAction);
					QFlag.setTrainingStateFlag(QConstants.StateFlagType.END);
				} else {
					QFlag.setTrainingStateFlag(QConstants.StateFlagType.START);
				}
			}
		}
	}
	
	public static Qlearning getInstance() {
		return instance;
	}
}
