import bwapi.Unit;
import bwapi.UnitType;

public class ActionMicroControl implements ActionInterface {
	@Override
	public void action() {
		int selfCnt = MyVariable.attackUnit.size() + MyVariable.defenceUnit.size();
		int enemyCnt = MyVariable.enemyUnitAroundMyStartPoint.size();

		// 싸이오닉 스톰 맞으면 뒤로 뺀다.
		for (Unit selfUnit : MyVariable.underStormUnit) {
			CommandUtil.move(selfUnit, MyVariable.myStartLocation.toPosition());
		}

		// 주위에 다크템플러가 있고, 보이는 경우에 가장 먼저 공격한다.
		for (Unit enemyUnit : MyVariable.getEnemyUnit(UnitType.Protoss_Dark_Templar)) {
			if (enemyUnit.isCloaked() == false) {
				for (Unit selfUnit : MyVariable.attackUnit) {
					CommandUtil.attackUnit(selfUnit, enemyUnit);
				}
				for (Unit selfUnit : MyVariable.defenceUnit) {
					CommandUtil.attackUnit(selfUnit, enemyUnit);
				}
				return;
			}
		}

		// 주위에 캐리어가 있고, 보이는 경우에 가장 먼저 공격한다.
		if (MyVariable.mostCloseCarrier != null) {
			for (Unit selfUnit : MyVariable.getSelfUnit(UnitType.Terran_Goliath)) {
				if (MyUtil.distanceTilePosition(selfUnit.getTilePosition(), MyVariable.mostCloseCarrier.getTilePosition()) * 32 < UnitType.Terran_Goliath.airWeapon().maxRange()) {
					CommandUtil.attackUnit(selfUnit, MyVariable.mostCloseCarrier);
				}
			}
		}

		// 러커 또는 다크템플러가 확인되었는데 스캔할 방법이 없으면 터렛으로 도망
		if ((MyVariable.findLucker == true || MyVariable.findDarkTempler) && MyUtil.canUseScan() == false && MyVariable.getSelfUnit(UnitType.Terran_Science_Vessel).size() == 0) {
			if (MyVariable.mostFarTurret != null) {
				for (Unit unit : MyVariable.attackUnit) {
					CommandUtil.attackMove(unit, MyVariable.mostFarTurret.getPosition());
				}
			}
			return;
		}

		// 탱크보다 멀리있는 유닛은 뒤로 보냄
		if (MyVariable.mostFarAttackUnit != null && MyVariable.mostFarTank != null && MyVariable.mostFarTank.isCompleted() == true && MyVariable.distanceOfMostFarTank < MyVariable.distanceOfMostFarAttackUnit) {
			CommandUtil.attackMove(MyVariable.mostFarAttackUnit, MyVariable.myStartLocation.toPosition());
		}

		// 적의 숫자가 많으면 SCV를 동원한다. (초반에만 동작)
		if (selfCnt < enemyCnt * 2 && selfCnt < 10 && MyBotModule.Broodwar.getFrameCount() < 12000) {
			int cnt = 0;
			for (Unit unit : MyVariable.getSelfUnit(UnitType.Terran_SCV)) {
				double distance1 = 50;

				if (MyVariable.mostCloseBunker != null) {
					distance1 = MyUtil.distanceTilePosition(MyVariable.myStartLocation.getPoint(), MyVariable.mostCloseBunker.getTilePosition()) + 3;
				}
				double distance2 = MyUtil.distanceTilePosition(MyVariable.myStartLocation.getPoint(), unit.getPoint().toTilePosition());

				if (distance1 <= distance2) {
					CommandUtil.attackMove(unit, MyVariable.myStartLocation.getPoint().toPosition());
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
						CommandUtil.attackUnit(unit, enemyUnit);
					}
				}
			}
		}

	}

}
