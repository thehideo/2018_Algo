import bwapi.Unit;
import bwapi.UnitType;

public class ActionMicroControl implements ActionInterface {

	@Override
	public void action() {
		int selfCnt = MyVariable.attackUnit.size() + MyVariable.defenceUnit.size();
		int enemyCnt = MyVariable.enemyUnitAroundMyStartPoint.size();

		// 적과 나의 숫자가 작을 경우에만 동작
		if (enemyCnt > 1 && selfCnt * enemyCnt < 200) {
			double minDistance = Double.MAX_VALUE;
			Unit minUnit = null;
			Unit enemyUnit = null;
			for (Unit unit : MyVariable.attackUnit) {
				for (Unit unit2 : MyVariable.enemyUnitAroundMyStartPoint) {
					double distance = MyUtil.distancePosition(unit.getPosition(), unit2.getPosition());
					if (unit.getType().groundWeapon() != null && unit.getType().groundWeapon().maxRange() * 0.7 > distance) {
						if (minDistance > distance) {
							minDistance = distance;
							minUnit = unit;
							enemyUnit = unit2;
						}
					}

				}
			}
			for (Unit unit : MyVariable.defenceUnit) {
				for (Unit unit2 : MyVariable.enemyUnitAroundMyStartPoint) {
					double distance = MyUtil.distancePosition(unit.getPosition(), unit2.getPosition());
					if (unit.getType().groundWeapon() != null && unit.getType().groundWeapon().maxRange() > distance) {
						if (minDistance > distance) {
							minDistance = distance;
							minUnit = unit;
							enemyUnit = unit2;
						}
					}
				}
			}

			// 가장 가까운 유닛은 도망간다.
			if (minUnit != null) {
				double distance = MyUtil.distancePosition(minUnit.getPosition(), InformationManager.Instance().selfPlayer.getStartLocation().toPosition());
				if (distance > 20) {
					minUnit.move(InformationManager.Instance().selfPlayer.getStartLocation().toPosition());
				}
			}

			if (MyVariable.mapSelfUnit.get(UnitType.Terran_SCV) != null) {
				// 적의 숫자가 많으면 SCV를 동원한다.
				if (selfCnt < enemyCnt) {
					minDistance = Double.MAX_VALUE;
					if (enemyUnit == null) {
						for (Unit unit : MyVariable.mapSelfUnit.get(UnitType.Terran_SCV)) {
							for (Unit unit2 : MyVariable.enemyUnitAroundMyStartPoint) {
								double distance = MyUtil.distancePosition(unit.getPosition(), unit2.getPosition());
								if (minDistance > distance) {
									minDistance = distance;
									enemyUnit = unit2;
								}
							}
							if (enemyUnit != null && unit.canAttack(enemyUnit)) {
								unit.attack(enemyUnit);
							}
						}
					}
				}
			}

		}
	}

}
