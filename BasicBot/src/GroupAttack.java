import java.util.HashMap;
import java.util.Random;

import bwapi.Position;
import bwapi.Race;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;
import bwta.BaseLocation;
import bwta.Chokepoint;

public class GroupAttack extends GroupAbstract {
	int minPointX = Integer.MAX_VALUE;
	int maxPointX = Integer.MIN_VALUE;
	int minPointY = Integer.MAX_VALUE;
	int maxPointY = Integer.MIN_VALUE;

	static GroupAttack actionControlAttackUnit = new GroupAttack();

	GroupAttack instance() {
		return actionControlAttackUnit;
	}

	public Position getTargetPosition(Unit unit) {
		Position result = null;

		if (targetPosition == null) {
			if (mapTargetPosition.containsKey(unit.getID())) {
				Position tmp = mapTargetPosition.get(unit.getID());
				if (MyUtil.distanceTilePosition(tmp.toTilePosition(), unit.getTilePosition()) < 5) {
					int x = MyUtil.random.nextInt(maxPointX);
					int y = MyUtil.random.nextInt(maxPointY);
					mapTargetPosition.put(unit.getID(), new TilePosition(x, y).toPosition());
				}
				result = mapTargetPosition.get(unit.getID());
			} else {

				int x = MyUtil.random.nextInt(maxPointX);
				int y = MyUtil.random.nextInt(maxPointY);
				mapTargetPosition.put(unit.getID(), new TilePosition(x, y).toPosition());

				result = mapTargetPosition.get(unit.getID());
			}
		} else {
			result = targetPosition;
		}

		return result;
	}

	public GroupAttack() {
		for (int i = 0; i < MyBotModule.Broodwar.getStartLocations().size(); i++) {
			minPointX = Math.min(minPointX, MyBotModule.Broodwar.getStartLocations().get(i).getPoint().getX());
			maxPointX = Math.max(maxPointX, MyBotModule.Broodwar.getStartLocations().get(i).getPoint().getX());
			minPointY = Math.min(minPointY, MyBotModule.Broodwar.getStartLocations().get(i).getPoint().getY());
			maxPointY = Math.max(maxPointY, MyBotModule.Broodwar.getStartLocations().get(i).getPoint().getY());
		}
	}

	public boolean decisionWaitAroundBunker() {
		boolean decision = false;
		// 적이 나보다 많으면
		if (MyVariable.enemyUnitAroundMyStartPoint.size() * 1.5 > MyVariable.attackUnit.size()) {
			decision = true;
		}
		// 벙커 밖에서 벌처가 알짱거리면
		if (MyVariable.mostCloseEnemyUnit != null && MyVariable.mostCloseEnemyUnit.getType() == UnitType.Terran_Vulture && MyVariable.distanceOfMostCloseEnemyUnit > MyVariable.distanceOfMostCloseBunker) {
			decision = true;
		}
		return decision;
	}

	@Override
	public void action() {

		boolean needToWaitVessel = false;

		// scanner가 모두 소진되었고 베슬도 없으면 본진에 대기해야함 (싸이언스 베슬 생산이 완료될 때 까지)
		if ((MyVariable.findDarkTempler || MyVariable.findLucker) && MyUtil.canUseScan() == false && !MyUtil.haveCompletedScienceVessle()) {
			needToWaitVessel = true;
			MyVariable.isFullScaleAttackStarted = false;
		}

		// 본진에 적이 있으면 공격
		if (MyVariable.enemyUnitAroundMyStartPoint.size() > 0) {
			if (decisionWaitAroundBunker()) {
				Position bunkerPosition = MyUtil.GetMyBunkerPosition();
				if (bunkerPosition == null) {
					for (Unit enemyUnit : MyVariable.enemyUnitAroundMyStartPoint) {
						if (enemyUnit.isDetected() == true) {
							targetPosition = enemyUnit.getPosition();
							break;
						}
					}
				} else {
					targetPosition = bunkerPosition;
				}
			}
			// 적이 나보다 적으면 공격한다.
			else {

				if (MyVariable.mostCloseEnemyUnit != null && MyVariable.mostCloseEnemyUnit.isDetected() == true) {
					targetPosition = MyVariable.mostCloseEnemyUnit.getPoint();
				} else {
					for (Unit enemyUnit : MyVariable.enemyUnitAroundMyStartPoint) {
						if (enemyUnit.isDetected() == true) {
							targetPosition = enemyUnit.getPosition();
							break;
						}
					}
				}

			}
		}
		// 공격 모드가 아닐 때에는 전투유닛들을 아군 진영 길목에 집결시켜서 방어
		else if (MyVariable.isFullScaleAttackStarted == false || needToWaitVessel == true) {
			Chokepoint saveChokePoint = MyUtil.getSaveChokePoint();
			if (MyVariable.attackUnit.size() <= 4) {

				targetPosition = MyVariable.myStartLocation.toPosition();

			} else {

				targetPosition = saveChokePoint.getCenter();

			}
			// 프로토스 공격 조건
			if (InformationManager.Instance().enemyRace == Race.Protoss) {
				// 캐리어를 발견했을 때
				//if (MyVariable.findCarrier == true) {
				//	if ((MyVariable.attackUnit.size() > 40 && MyVariable.getSelfUnit(UnitType.Terran_Ghost).size() > 8)) {
				//		MyVariable.isFullScaleAttackStarted = true;
				//	}
				//} else 
				
				{
					if (MyVariable.getSelfUnit(UnitType.Terran_Command_Center).size() <= 1) {
						if (MyVariable.attackUnit.size() > 30 ) {
							MyVariable.isFullScaleAttackStarted = true;
						}
					}
					// 확장 기지가 있다면
					else {
						if (MyVariable.attackUnit.size() > 30) {
							MyVariable.isFullScaleAttackStarted = true;
						}
					}
				}
			}
			// 테란 공격 조건
			else if (InformationManager.Instance().enemyRace == Race.Terran) {
				if (MyVariable.getSelfUnit(UnitType.Terran_Command_Center).size() <= 1) {
					if (MyVariable.attackUnit.size() > 40 && MyUtil.GetMyTankCnt() >= 6) {
						MyVariable.isFullScaleAttackStarted = true;
					}
				}
				// 확장 기지가 있다면
				else {
					if (MyVariable.attackUnit.size() > 50 && MyUtil.GetMyTankCnt() >= 6) {
						MyVariable.isFullScaleAttackStarted = true;
					}
				}
			}
			// 저그 공격 조건
			else {
				if (MyVariable.getSelfUnit(UnitType.Terran_Command_Center).size() <= 1) {
					if (MyVariable.attackUnit.size() > 30) {
						MyVariable.isFullScaleAttackStarted = true;
					}
				}
				// 확장 기지가 있다면
				else {
					if (MyVariable.attackUnit.size() > 40 && MyUtil.GetMyTankCnt() >= 3) {
						MyVariable.isFullScaleAttackStarted = true;
					}
				}
			}
			if (needToWaitVessel == true) {
				MyVariable.isFullScaleAttackStarted = false;
			}
		}
		// 공격 모드가 되면, 모든 전투유닛들을 적군 Main BaseLocation 로 공격 가도록 합니다
		else {
			// 유닛이 많이 죽으면 방어 모드로 전환
			if (InformationManager.Instance().enemyRace == Race.Protoss) {
				// 상대가 캐리어가 있는데 골리앗이 없으면 방어 모드
				if (MyVariable.findCarrier == true) {
					if (MyVariable.attackUnit.size() < 10) {
						MyVariable.isFullScaleAttackStarted = false;
					}
				} else {
					if (MyVariable.attackUnit.size() < 10 ) {
						MyVariable.isFullScaleAttackStarted = false;
					}
				}
			} else if (InformationManager.Instance().enemyRace == Race.Terran) {
				if (MyVariable.attackUnit.size() < 10) {
					MyVariable.isFullScaleAttackStarted = false;
				}
			} else {
				if (MyVariable.attackUnit.size() < 10) {
					MyVariable.isFullScaleAttackStarted = false;
				}
			}

			// 메딕 비율만 높아도 방어 모드로 변경 (50%이상)
			if (1.0 * MyVariable.getSelfAttackUnit(UnitType.Terran_Medic).size() / MyVariable.attackUnit.size() > 0.5) {
				MyVariable.isFullScaleAttackStarted = false;
			}

			// 더 이상 발견한 건물이 없다면 아무 곳으로 이동
			if (MyVariable.enemyBuildingUnit.size() == 0) {
				targetPosition = null;

			} else {
				mapTargetUnit.clear();
				for (TilePosition tilePosition : MyVariable.enemyBuildingUnit) {
					targetPosition = tilePosition.toPosition();
					break;
				}
			}

		}
	}
}
