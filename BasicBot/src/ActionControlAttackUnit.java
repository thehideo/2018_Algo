import java.util.List;

import bwapi.Position;
import bwapi.Race;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.Chokepoint;

public class ActionControlAttackUnit implements ActionInterface {
	int minPointX = Integer.MAX_VALUE;
	int maxPointX = Integer.MIN_VALUE;
	int minPointY = Integer.MAX_VALUE;
	int maxPointY = Integer.MIN_VALUE;

	public ActionControlAttackUnit() {
		for (int i = 0; i < MyBotModule.Broodwar.getStartLocations().size(); i++) {
			minPointX = Math.min(minPointX, MyBotModule.Broodwar.getStartLocations().get(i).getPoint().getX());
			maxPointX = Math.max(maxPointX, MyBotModule.Broodwar.getStartLocations().get(i).getPoint().getX());
			minPointY = Math.min(minPointY, MyBotModule.Broodwar.getStartLocations().get(i).getPoint().getY());
			maxPointY = Math.max(maxPointY, MyBotModule.Broodwar.getStartLocations().get(i).getPoint().getY());
		}
	}

	@Override
	public void action() {
		boolean needToWaitVessel = false;

		// scanner가 모두 소진되었고 베슬도 없으면 본진에 대기해야함 (베슬 생산이 완료될 때 까지)
		if ((MyVariable.findDarkTempler || MyVariable.findLucker) && MyUtil.canUseScan() == false && !MyUtil.haveCompletedScienceVessle()) {
			needToWaitVessel = true;
			MyVariable.isFullScaleAttackStarted = false;
		}

		// 본진에 적이 있으면 공격
		if (MyVariable.enemyUnitAroundMyStartPoint.size() > 0) {
			// 적이 나보다 많으면 벙커로 가고
			if (MyVariable.enemyUnitAroundMyStartPoint.size() * 1.5 > MyVariable.attackUnit.size()) {
				for (Unit unit : MyVariable.attackUnit) {
					Position bunkerPosition = MyUtil.GetMyBunkerPosition();
					if (bunkerPosition == null) {
						for (Unit enemyUnit : MyVariable.enemyUnitAroundMyStartPoint) {
							if (enemyUnit.isDetected() == true) {
								CommandUtil.attackMove(unit, enemyUnit.getPosition());
								break;
							}
						}
					} else {
						CommandUtil.attackMove(unit, bunkerPosition);
					}

				}
			}
			// 적이 나보다 적으면 공격한다.
			else {
				for (Unit unit : MyVariable.attackUnit) {
					if (MyVariable.mostCloseEnemyUnit != null && MyVariable.mostCloseEnemyUnit.isDetected() == true) {
						CommandUtil.attackMove(unit, MyVariable.mostCloseEnemyUnit.getPoint());
					} else {

						for (Unit enemyUnit : MyVariable.enemyUnitAroundMyStartPoint) {
							if (enemyUnit.isDetected() == true) {
								CommandUtil.attackMove(unit, enemyUnit.getPosition());
								break;
							}
						}
					}
				}
			}
		}
		// 공격 모드가 아닐 때에는 전투유닛들을 아군 진영 길목에 집결시켜서 방어
		else if (MyVariable.isFullScaleAttackStarted == false || needToWaitVessel == true) {
			Chokepoint saveChokePoint = MyUtil.getSaveChokePoint();
			if (MyVariable.attackUnit.size() <= 4) {
				for (Unit unit : MyVariable.attackUnit) {
					CommandUtil.attackMove(unit, MyVariable.myStartLocation.toPosition());
				}
			} else {
				for (Unit unit : MyVariable.attackUnit) {
					CommandUtil.attackMove(unit, saveChokePoint.getCenter());
				}
			}
			// 프로토스 공격 조건
			if (InformationManager.Instance().enemyRace == Race.Protoss) {
				// 캐리어를 발견했을 때
				if (MyVariable.findCarrier == true) {
					if (MyVariable.attackUnit.size() > 40 && MyVariable.getSelfUnit(UnitType.Terran_Ghost).size() > 8) {
						MyVariable.isFullScaleAttackStarted = true;
					}
				} else {
					if (MyVariable.getSelfUnit(UnitType.Terran_Command_Center).size() <= 1) {
						if (MyVariable.attackUnit.size() > 30 && MyUtil.GetMyTankCnt() >= 2) {
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
					if (MyVariable.attackUnit.size() < 10 || MyUtil.GetMyTankCnt() <= 2) {
						MyVariable.isFullScaleAttackStarted = false;
					}
				}
			} else if (InformationManager.Instance().enemyRace == Race.Terran) {
				if (MyVariable.attackUnit.size() < 10 || MyUtil.GetMyTankCnt() <= 2) {
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

			for (Unit unit : MyVariable.attackUnit) {
				// 더 이상 발견한 건물이 없다면 아무 곳으로 이동
				if (MyVariable.enemyBuildingUnit.size() == 0) {
					if (unit.isIdle()) {
						int xValue = minPointX + MyUtil.random.nextInt(maxPointX - minPointX);
						int yValue = minPointY + MyUtil.random.nextInt(maxPointY - minPointY);
						TilePosition position = new TilePosition(xValue, yValue);
						CommandUtil.attackMove(unit, position.toPosition());
					}
				}
				// 발견한 건물이 있다면 그쪽으로 이동
				else {
					for (TilePosition tilePosition : MyVariable.enemyBuildingUnit) {
						CommandUtil.attackMove(unit, tilePosition.toPosition());
						break;
					}
				}
			}
		}
	}
}
