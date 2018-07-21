import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;

public class ActionMicroControl implements ActionInterface {
	CommandUtil commandUtil = new CommandUtil();

	@Override
	public void action() {
		TilePosition myStartLocation = MyBotModule.Broodwar.self().getStartLocation().getPoint();
		int selfCnt = MyVariable.attackUnit.size() + MyVariable.defenceUnit.size();
		int enemyCnt = MyVariable.enemyUnitAroundMyStartPoint.size();

		// 내 본진 주위에 다크템플러가 있고, 보이는 경우에 가장 먼저 공격한다.
		if (MyVariable.enemyUnitAroundMyStartPoint.size() > 0 && MyVariable.getEnemyUnit(UnitType.Protoss_Dark_Templar).size() > 0) {
			for (Unit enemyUnit : MyVariable.getEnemyUnit(UnitType.Protoss_Dark_Templar)) {
				if (enemyUnit.isCloaked() == false) {
					for (Unit selfUnit : MyVariable.attackUnit) {
						commandUtil.attackUnit(selfUnit, enemyUnit);
					}
					for (Unit selfUnit : MyVariable.defenceUnit) {
						commandUtil.attackUnit(selfUnit, enemyUnit);
					}
					return;
				}
			}
		}

		// 적의 숫자가 많으면 SCV를 동원한다.
		if (selfCnt < enemyCnt * 2 && selfCnt < 10) {
			int cnt = 0;
			for (Unit unit : MyVariable.getSelfUnit(UnitType.Terran_SCV)) {
				double distance1 = 50;
				
				if (MyVariable.mostCloseBunker != null) {
					distance1 = MyUtil.distanceTilePosition(myStartLocation.getPoint(), MyVariable.mostCloseBunker.getTilePosition()) + 3;
				}
				double distance2 = MyUtil.distanceTilePosition(myStartLocation.getPoint(), unit.getPoint().toTilePosition());

				if (distance1 <= distance2) {
					commandUtil.attackMove(unit, myStartLocation.getPoint().toPosition());
				} else {
					cnt++;
					if (cnt > 10) {
						break;
					}
					Unit enemyUnit = null;
					Double minDistance = Double.MAX_VALUE;
					for (Unit unit2 : MyVariable.enemyUnitAroundMyStartPoint) {
						if (unit2.isFlying() == false && unit2.getType() != UnitType.Terran_SCV && unit2.getType() != UnitType.Protoss_Probe && unit2.getType() != UnitType.Zerg_Drone) {
							double distance = MyUtil.distancePosition(unit.getPosition(), unit2.getPosition());
							if (minDistance > distance) {
								minDistance = distance;
								enemyUnit = unit2;
							}
						}
					}

					if (enemyUnit != null) {
						commandUtil.attackUnit(unit, enemyUnit);
					}
				}
			}
		}

	}

}
