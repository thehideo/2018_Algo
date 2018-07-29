import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import bwapi.Game;
import bwapi.Player;
import bwapi.Unit;
import bwapi.UnitType;

public class QStateZ extends QState implements Serializable {
	
	private static final long serialVersionUID = -8339056876656299811L;

	/**
	 * Q유직의 시야 그리드 격자
	 */
	private List<Grid> grdList;
	
	/**
	 * 공격범위 안에 적중 랜덤하게 타격할 수 있는 유닛
	 */
	private List<Integer> randomTagetIdList;
	
	/**
	 * 아군 Q유닛의 HP 총합
	 */
	private int myHitPoints;
	
	/**
	 * 아군 Q유닛의 cooldown
	 */
	private int cooldown;
	
	/**
	 * 공격해야할 적군의 유닛ID
	 */
	private int targetUnitId;
	
	/**
	 * 공격해야할 적군의 유닛타입<히드라, 오버로드, 드론>
	 */
	private String targetUnitType;
	
	/**
	 * 공격해야할 오버로드
	 */
	private int targetOverlordUnitId;
	/**
	 * 공격해야할 드론
	 */
	private int targetDroneUnitId;
	/**
	 * 공격해야할 히드라
	 */
	private int targetHydraliskUnitId;
	
	/**
	 * 공격해야할 적군의 유닛 수
	 */
	private int targetUnitsCnt;
	
	/**
	 * Q유닛을 공격할 수 있는 범위에 있는 적군의 수
	 */
	private int eneAttackCnt;
	
	/**
	 * Q유닛 시야에 있는 적군의 수
	 */
	private int eneCount;
	
	/**
	 *Q유닛 공격범위에 있는 드론 수
	 */
	private int eneTargetDronCount;
	
	/**
	 *Q유닛 공격범위에 있는 드론 수
	 */
	private int eneTargetOverLoadCount;
	
	/**
	 * Q유닛 시야에 있는 오버로드 수
	 */
	private int eneOverLoadCount;
	
	/**
	 * Q유닛 시야에 있는 적군리스트
	 */
	private List<Integer> eneList;
	
	/**
	 * Q유닛을 공격할수 있는 범위에 있는 적군리스트
	 */
	private List<Integer> eneAttackList;
	
	public List<QStateZ.Grid> getGrdList() {
		return grdList;
	}

	public void setGrdList(List<Grid> grdList) {
		this.grdList = grdList;
	}

	public void setTargetOverlordUnitId(int targetOverlordUnitId) {
		this.targetOverlordUnitId = targetOverlordUnitId;
	}

	public void setTargetDroneUnitId(int targetDroneUnitId) {
		this.targetDroneUnitId = targetDroneUnitId;
	}

	public void setTargetHydraliskUnitId(int targetHydraliskUnitId) {
		this.targetHydraliskUnitId = targetHydraliskUnitId;
	}

	public String getTargetUnitType() {
		return targetUnitType;
	}

	public void setTargetUnitType(String targetUnitType) {
		this.targetUnitType = targetUnitType;
	}

	public int getMyHitPoints() {
		return myHitPoints;
	}

	public void setMyHitPoints(int myHitPoints) {
		this.myHitPoints = myHitPoints;
	}

	public int getCooldown() {
		return cooldown;
	}

	public void setCooldown(int cooldown) {
		this.cooldown = cooldown;
	}

	public int getTargetUnitsCnt() {
		return targetUnitsCnt;
	}

	public void setTargetUnitsCnt(int targetUnitsCnt) {
		this.targetUnitsCnt = targetUnitsCnt;
	}
	
	public int getEneAttackCnt() {
		return eneAttackCnt;
	}

	public void setEneAttackCnt(int eneAttackCnt) {
		this.eneAttackCnt = eneAttackCnt;
	}

	public int getEneCount() {
		return eneCount;
	}

	public void setEneCount(int eneCount) {
		this.eneCount = eneCount;
	}

	public int getTargetUnitId() {
		return targetUnitId;
	}

	public void setTargetUnitId(int unitId) {
		this.targetUnitId = unitId;
	}

	public List<Integer> getEneList() {
		return eneList;
	}

	public void setEneList(List<Integer> eneList) {
		this.eneList = eneList;
	}
	
	public List<Integer> getEneAttackList() {
		return eneAttackList;
	}

	public void setEneAttackList(List<Integer> eneAttackList) {
		this.eneAttackList = eneAttackList;
	}
	

	public int getEneTargetDronCount() {
		return eneTargetDronCount;
	}

	public void setEneTargetDronCount(int eneTargetDronCount) {
		this.eneTargetDronCount = eneTargetDronCount;
	}

	public int getEneTargetOverLoadCount() {
		return eneTargetOverLoadCount;
	}

	public void setEneTargetOverLoadCount(int eneTargetOverLoadCount) {
		this.eneTargetOverLoadCount = eneTargetOverLoadCount;
	}

	public int getEneOverLoadCount() {
		return eneOverLoadCount;
	}

	public void setEneOverLoadCount(int eneOverLoadCount) {
		this.eneOverLoadCount = eneOverLoadCount;
	}

	class Grid implements Serializable {
		
		
		private static final long serialVersionUID = -6644891819898487279L;

		/**
		 * 그리드격자의 인덱스
		 */
		private int idx;
		
		/**
		 * 그리드 격자안의 적유닛 수
		 */
		private int eneGrdCount;
		
		/**
		 * 이동해야할 X좌표
		 */
		private int movePositionX;
		
		/**
		 * 이동해야할 Y좌표
		 */
		private int movePositionY;
		
		public int getIdx() {
			return idx;
		}
		public void setIdx(int idx) {
			this.idx = idx;
		}
		public int getEneCount() {
			return eneGrdCount;
		}
		public void setEneCount(int eneCount) {
			this.eneGrdCount = eneCount;
		}
		public int getTargetUnit() {
			return targetUnitId;
		}
		public int getMovePositionX() {
			return movePositionX;
		}
		public void setMovePositionX(int movePositionX) {
			this.movePositionX = movePositionX;
		}
		public int getMovePositionY() {
			return movePositionY;
		}
		public void setMovePositionY(int movePositionY) {
			this.movePositionY = movePositionY;
		}
	}
	
	public QStateZ(Game game) {
		
		Player self = game.self();
		Player enemy = game.enemy();
		Unit myUnit = QFlag.getMyUnit();
		List<Integer> eneList = new ArrayList<Integer>();
		List<Integer> eneAttackList = new ArrayList<Integer>();
		
		/*
		 * 아군 체력, cooldown의 합
		 */
		for (Unit selfUnit : self.getUnits()) {
			if (selfUnit.getType() == UnitType.Terran_Wraith) {
				
				if (selfUnit.getID() - QFlag.getMyUnit().getID() == 0) {
					myHitPoints += selfUnit.getHitPoints();
				}
				
				cooldown += selfUnit.getGroundWeaponCooldown();
			}
		}
		
		int unitDistance = Integer.MAX_VALUE;
		int tmpUnitDistance = Integer.MAX_VALUE;
		int tmpHydraUnitDistance = Integer.MAX_VALUE;
		int tmpDroneUnitDistance = Integer.MAX_VALUE;
		int tmpOverloadUnitDistance = Integer.MAX_VALUE;
		
		eneAttackCnt = 0; // 나를 공격할 수 있는 유닛의 수
		eneCount = 0; // 시야에 있는 공격유닛수
		targetUnitsCnt = 0; // 내가 공격할 수 있는 공격유닛 수
		eneTargetDronCount = 0; // 내가 공격할 수 있는 드론 수
		eneTargetOverLoadCount = 0; // 내가 공격할 수 있는 오버로드 수
		eneOverLoadCount = 0; // 시야에 있는 오버로드 수
		
		randomTagetIdList = new ArrayList<Integer>();
		
		for (Unit enemyUnit : enemy.getUnits()) {
			
			if (enemyUnit.getType() == UnitType.Zerg_Drone 
					|| enemyUnit.getType() == UnitType.Zerg_Overlord
					|| enemyUnit.getType() == UnitType.Zerg_Hydralisk
					) {
				// 시야에 보이지 않는 유닛은 스킵
				unitDistance = myUnit.getDistance(enemyUnit);
				if (unitDistance > QConstants.QUNIT_SINGHT_RANGE) {
					continue;
				}
				
				// 타겟을 지정한다.
				if (tmpUnitDistance > unitDistance) {
					tmpUnitDistance = unitDistance;
					this.setTargetUnitId(enemyUnit.getID());
					this.setTargetUnitType(enemyUnit.getType()+"");
				}
				
				if (enemyUnit.getType() == UnitType.Zerg_Hydralisk) {
					if (tmpHydraUnitDistance > unitDistance) {
						tmpHydraUnitDistance = unitDistance;
						targetHydraliskUnitId = enemyUnit.getID();
					}
				}
				else if (enemyUnit.getType() == UnitType.Zerg_Drone) {
					if (tmpDroneUnitDistance > unitDistance) {
						tmpDroneUnitDistance = unitDistance;
						targetDroneUnitId = enemyUnit.getID();
					}
				}
				else if (enemyUnit.getType() == UnitType.Zerg_Overlord) {
					if (tmpOverloadUnitDistance > unitDistance) {
						tmpOverloadUnitDistance = unitDistance;
						targetOverlordUnitId = enemyUnit.getID();
					}
				}

				// 내가 공격가능한 거리에 있는 경우
				int attackRange = QConstants.QUNIT_GROUNDWEAPON_ATTCK_RANGE;
				if (attackRange >= unitDistance) {
					if (enemyUnit.getType() == UnitType.Zerg_Hydralisk) {
						targetUnitsCnt++;
					}
					else if (enemyUnit.getType() == UnitType.Zerg_Drone) {
						eneTargetDronCount++;
					}
					else if (enemyUnit.getType() == UnitType.Zerg_Overlord) {
						eneTargetOverLoadCount++;
					}
					randomTagetIdList.add(enemyUnit.getID());
				}
				
				if (enemyUnit.getType() == UnitType.Zerg_Hydralisk) {
					eneCount++;
					eneList.add(enemyUnit.getID());
					
					// 상대방이 공격가능한 거리에 있는 경우
					if (UnitType.Zerg_Hydralisk.airWeapon().maxRange() + 40 > unitDistance) {
						eneAttackCnt++;
						eneAttackList.add(enemyUnit.getID());
					}
				} else if (enemyUnit.getType() == UnitType.Zerg_Overlord) {
					eneOverLoadCount++;
				}
			}
		}
		
		this.setEneList(eneList);
		this.setEneAttackList(eneAttackList);
		
		grdList = new ArrayList<QStateZ.Grid>();
		int sightRange = QConstants.QUNIT_SINGHT_RANGE; // 224
		int divideCnt = QConstants.SINGHT_DIVIDE_CNT;
		int cellSize = sightRange / divideCnt;
		int cellSizeHalf = cellSize / 2;
		int cellCenterInitX = myUnit.getPosition().getX() - cellSize*divideCnt;
		int cellCenterInitY = myUnit.getPosition().getY() - cellSize*divideCnt;
		
		int cellCenterXL = cellCenterInitX;
		int cellCenterYD = cellCenterInitY;
		int idx = 0;
		for (int i = 0; i < divideCnt*2; i++) {
			int cellCenterYU = cellCenterYD + cellSize;
			for (int j = 0; j < divideCnt*2; j++) {
				int cellCenterXR = cellCenterXL + cellSize;

				int x = (cellCenterXL + cellCenterXR) / 2;
				int y = (cellCenterYD + cellCenterYU) / 2;
				idx++;
				
				QStateZ.Grid grd = new QStateZ.Grid();
				grd.setIdx(idx);
				
				// 그리드 격자 안에 적군을 update한다.
				int eneGrdCount = 0;
				
				int fx = x - cellSizeHalf;
				int tx = x + cellSizeHalf;
				int fy = y - cellSizeHalf;
				int ty = y + cellSizeHalf;
				
				for (Unit eneUnit : enemy.getUnits()) {
					if (eneUnit.getType() == UnitType.Zerg_Hydralisk 
							|| eneUnit.getType() == UnitType.Zerg_Overlord 
							|| eneUnit.getType() == UnitType.Zerg_Drone 
							) {
						
						int eneX = eneUnit.getX();
						int eneY = eneUnit.getY();
						if (fx <= eneX && eneX <= tx) {
							if (fy <= eneY && eneY <= ty) {
								if (eneUnit.getType() == UnitType.Zerg_Hydralisk) {
									eneGrdCount++;
								}
							}
						}
					}
				}
				grd.setEneCount(eneGrdCount);
				grd.setMovePositionX(x);
				grd.setMovePositionY(y);
				grdList.add(grd);
				cellCenterXL = cellCenterXL + cellSize;
			}
			cellCenterYD = cellCenterYD + cellSize;
			cellCenterXL = cellCenterInitX;
		}
	}

	/**
	 * 현재상태에서 진행가능한 모든 Action을 리스트로 반환한다.
	 * @return
	 */
	public List<QAction> possibleActionsFromState() {
		List<QAction> actionList = new ArrayList<QAction>();
		// Move 액션
		for (Grid grid : this.getGrdList()) {
			QAction moveAction = new QAction();
			moveAction.setActionType(QConstants.ActionType.Move);
			moveAction.setMoveGrdIdx(grid.getIdx());
			// 갈수 없는 곳은 스킵처리 - 모서리등
			if (QUtil.isUnreachablePosition(grid.getMovePositionX(), grid.getMovePositionY())) {
				continue;
			}
			moveAction.setMoveX(grid.getMovePositionX());
			moveAction.setMoveY(grid.getMovePositionY());
			moveAction.setTargetUnitType("moveAction");
			actionList.add(moveAction);
		}
		// 공격 액션
		if (this.getEneCount() > 0 || this.getEneTargetOverLoadCount() > 0 || this.getEneTargetDronCount() > 0) {
			QAction attackAction = new QAction();
			attackAction.setActionType(QConstants.ActionType.Attack);
			// 공격대상을 랜덤하게 선택한다.
			if (this.randomTagetIdList.size() > 0) {
				Random rd = new Random();
				int rdIdx = rd.nextInt(this.randomTagetIdList.size());
				attackAction.setTargetId(randomTagetIdList.get(rdIdx));
				attackAction.setTargetUnitType(QUtil.getUnit(randomTagetIdList.get(rdIdx)).getType()+"");
			} else {
				attackAction.setTargetId(this.getTargetUnitId());
				attackAction.setTargetUnitType(this.getTargetUnitType());
			}
			actionList.add(attackAction);
		}
		// cloaking 액션
		if ((this.getEneCount() > 0 || this.getEneTargetOverLoadCount() > 0 || this.getEneTargetDronCount() > 0) && QFlag.getMyUnit().getEnergy() >= QConstants.CLOAKING_STD_ENG) {
			QFlag.getMyQUnits().decloak();
			QAction attackAction = new QAction();
			attackAction.setActionType(QConstants.ActionType.Cloak);
			if (this.randomTagetIdList.size() > 0) {
				Random rd = new Random();
				int rdIdx = rd.nextInt(this.randomTagetIdList.size());
				attackAction.setTargetId(randomTagetIdList.get(rdIdx));
				attackAction.setTargetUnitType(QUtil.getUnit(randomTagetIdList.get(rdIdx)).getType()+"");
			} else {
				attackAction.setTargetId(this.getTargetUnitId());
				attackAction.setTargetUnitType(this.getTargetUnitType());
			}
			actionList.add(attackAction);
		}

		return actionList;
	}

	/**
	 * Train을 해야하는 상태인지 여부를 리턴한다.
	 * @return
	 */
	public boolean isTrainingState() {
		
		// 나를 공격할 수 있는 적군이 있는 경우
		if (this.getEneAttackCnt() > 0) {
			return true;
		}
		
		// 공격할 수 있는 적이 있는 경우
		if (this.getTargetUnitsCnt() > 0) {
			return true;	
		}
		if (this.getEneTargetDronCount() > 0) {
			return true;
		}
		if (this.getEneTargetOverLoadCount() > 0) {
			return true;	
		}
		
		return false;
	}
	
	@Override
	public boolean equals(Object o) {
		
		if (!(o instanceof QStateZ)) {
			return false;
		}
		
		QStateZ thisState = this;
		QStateZ otherState = (QStateZ) o;
		
		/*
		 * 오버로드의 수
		 */
		int thisEneOverLoadCount = thisState.getEneOverLoadCount();
		int otherEneOverLoadCount = otherState.getEneOverLoadCount();
		if (thisEneOverLoadCount != otherEneOverLoadCount) {
			return false;
		}
		
		/*
		 * 히드라의 수
		 */
		int thisEneCount = thisState.getEneCount();
		int otherEneCount = otherState.getEneCount();
		if (thisEneCount == 0) {
			if (otherEneCount > 0) {
				return false;
			}
		}
		if (otherEneCount == 0) {
			if (thisEneCount > 0) {
				return false;
			}
		}
		
		/*
		 * 적이 공격할 수 있는 유닛의 갯수
		 */
		int thisEneAttackCnt = thisState.getEneAttackCnt();
		int otherEneAttackCnt = otherState.getEneAttackCnt();
		if (thisEneAttackCnt == 0) {
			if (otherEneAttackCnt > 0) {
				return false;
			}
		}
		if (otherEneAttackCnt == 0) {
			if (thisEneAttackCnt > 0) {
				return false;
			}
		}
		
		/*
		 * 공격할 수 있는 드론수
		 */
		int thisEneTagetDroneCnt = thisState.getEneTargetDronCount();
		int otherEneTagetDroneCnt = otherState.getEneTargetDronCount();
		if (thisEneTagetDroneCnt == 0) {
			if (otherEneTagetDroneCnt > 0) {
				return false;
			}
		}
		if (otherEneTagetDroneCnt == 0) {
			if (thisEneTagetDroneCnt > 0) {
				return false;
			}
		}
		
		/*
		 * 공격할 수 있는 히드라수
		 */
		int thisEneTagetCnt = thisState.getTargetUnitsCnt();
		int otherEneTagetCnt = otherState.getTargetUnitsCnt();
		if (thisEneTagetCnt == 0) {
			if (otherEneTagetCnt > 0) {
				return false;
			}
		}
		if (otherEneTagetCnt == 0) {
			if (thisEneTagetCnt > 0) {
				return false;
			}
		}
		
		/*
		 * 공격할 수 있는 오버로드 수
		 */
		int thisEneTagetOverLoadCnt = thisState.getEneTargetOverLoadCount();
		int otherEneTagetOverLoadCnt = otherState.getEneTargetOverLoadCount();
		if (thisEneTagetOverLoadCnt == 0) {
			if (otherEneTagetOverLoadCnt > 0) {
				return false;
			}
		}
		if (otherEneTagetOverLoadCnt == 0) {
			if (thisEneTagetOverLoadCnt > 0) {
				return false;
			}
		}
		
		/*
		 * 그리드 격자내의 유닛정보
		 */
		for (int i = 0; i < thisState.getGrdList().size(); i++) {
			Grid thisGid = thisState.getGrdList().get(i);
			Grid otherGid = otherState.getGrdList().get(i);
			

			int thisGrdEneCount = thisGid.getEneCount();
			int otherGrdEneCount = otherGid.getEneCount();
			/*
			 * 적군의 수
			 */
			if (thisGrdEneCount == 0) {
				if (otherGrdEneCount > 0) {
					return false;
				}
			}
			if (otherGrdEneCount == 0) {
				if (thisGrdEneCount > 0) {
					return false;
				}
			}
		}
        return true;
	}
	
	@Override
	public int getMinAttackUnitGrdIdx(Object grdListObj) {
		int enemyCnt = Integer.MAX_VALUE;
		int retIdx = 0;
		
		List<Grid> grdList = (List<Grid>) grdListObj; 
		
		for (Grid grid : grdList) {
			// 갈수 없는 곳은 스킵처리
			if (QUtil.isUnreachablePosition(grid.getMovePositionX(), grid.getMovePositionY())) {
				continue;
			}
			int tmpEnemyCnt = grid.getEneCount();
			if (enemyCnt > tmpEnemyCnt) {
				enemyCnt = tmpEnemyCnt;
				retIdx = grid.getIdx();
			}
		}
		return retIdx;
	}

	/**
	 * 그리드 인덱스에 해당되는 좌표를 리턴한다.
	 */
	@Override
	public QAction getMoveActionWidGrdIdx(int grdIdx) {
		QAction moveAction = new QAction();
		moveAction.setActionType(QConstants.ActionType.Move);
		moveAction.setMoveGrdIdx(grdIdx);
		moveAction.setMoveX(this.getGrdList().get(grdIdx-1).getMovePositionX());
		moveAction.setMoveY(this.getGrdList().get(grdIdx-1).getMovePositionY());
		moveAction.setTargetUnitType("moveAction");
		return moveAction;
	}

	/**
	 * 파라미터로 넘겨받은 그리드 Index에 해당하는 좌표가 이동가능 좌표인지를 리턴
	 */
	@Override
	public boolean isUnreachableArea(int idx) {
		int x = this.getGrdList().get(idx-1).getMovePositionX();
		int y = this.getGrdList().get(idx-1).getMovePositionY();
		// 도달하지 못하는 부분 패스
		if (QUtil.isUnreachablePosition(x, y)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 과거 Action(Max Q값으로 가져온 Action)에 대해 현재상태의 Action정보를 반영한다.
	 */
	@Override
	public QAction setAttackMaxQAction(QAction ret) {
		if (QConstants.ActionType.Attack.equals(ret.getActionType()) || QConstants.ActionType.Cloak.equals(ret.getActionType())) {
			
			if (ret.getTargetUnitType().equals(UnitType.Zerg_Hydralisk+"")) {
				ret.setTargetId(this.getTargetHydraliskUnitId());
			} else if (ret.getTargetUnitType().equals(UnitType.Zerg_Drone+"")) {
				ret.setTargetId(this.getTargetDroneUnitId());
			} else if (ret.getTargetUnitType().equals(UnitType.Zerg_Overlord+"")) {
				ret.setTargetId(this.getTargetOverlordUnitId());
			}


		} else if (QConstants.ActionType.Move.equals(ret.getActionType())) {
			ret.setMoveX(this.getGrdList().get(ret.getMoveGrdIdx()-1).getMovePositionX());
			ret.setMoveY(this.getGrdList().get(ret.getMoveGrdIdx()-1).getMovePositionY());
		}
		return ret;
	}

	public int getTargetOverlordUnitId() {
		return targetOverlordUnitId;
	}

	public int getTargetDroneUnitId() {
		return targetDroneUnitId;
	}
	
	public int getTargetHydraliskUnitId() {
		return targetHydraliskUnitId;
	}
}
