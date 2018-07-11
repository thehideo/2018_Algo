import bwapi.Unit;
import bwapi.UnitType;

public class ActionMicroControl implements ActionInterface {

	@Override
	public void action() {
		int selfCnt = MyVariable.attackUnit.size() + MyVariable.defenceUnit.size();
		int enemyCnt = MyVariable.enemyUnitAroundMyStartPoint.size();

		// 적과 나의 숫자가 작을 경우에만 동작
		if (enemyCnt >= 1 && selfCnt * enemyCnt < 800) {
			// if (enemyCnt >= 1) {
			double minDistance = Double.MAX_VALUE;
			Unit minUnit = null;
			Unit enemyUnit = null;
			for (Unit unit : MyVariable.attackUnit) {
				for (Unit unit2 : MyVariable.enemyUnitAroundMyStartPoint) {
					double distance = MyUtil.distancePosition(unit.getPosition(), unit2.getPosition());
					if (minDistance > distance) {
						minDistance = distance;
						minUnit = unit;
						enemyUnit = unit2;
					}
				}
			}

			for (Unit unit : MyVariable.defenceUnit) {
				for (Unit unit2 : MyVariable.enemyUnitAroundMyStartPoint) {
					double distance = MyUtil.distancePosition(unit.getPosition(), unit2.getPosition());
					if (minDistance > distance) {
						minDistance = distance;
						minUnit = unit;
						enemyUnit = unit2;
					}
				}
			}

			if (selfCnt < enemyCnt && enemyUnit != null) {
				// SCV가 더 가까우면 제외한다.
				for (Unit unit : MyVariable.getSelfUnit(UnitType.Terran_SCV)) {
					double distance = MyUtil.distancePosition(unit.getPosition(), enemyUnit.getPosition());
					if (minDistance + 10 > distance) {
						minDistance = distance;
						minUnit = null;
					}
				}
			}

			// 가장 가까운 유닛은 도망간다.
			if (minUnit != null) {
				double distance = MyUtil.distancePosition(minUnit.getPosition(), InformationManager.Instance().selfPlayer.getStartLocation().toPosition());
				minUnit.move(InformationManager.Instance().selfPlayer.getStartLocation().toPosition());
			}

			// 적의 숫자가 많으면 SCV를 동원한다.
			if (selfCnt < enemyCnt) {
				for (Unit unit : MyVariable.getSelfUnit(UnitType.Terran_SCV)) {
					enemyUnit = null;
					minDistance = Double.MAX_VALUE;
					for (Unit unit2 : MyVariable.enemyUnitAroundMyStartPoint) {
						double distance = MyUtil.distancePosition(unit.getPosition(), unit2.getPosition());
						if (minDistance > distance) {
							minDistance = distance;
							enemyUnit = unit2;
						}
					}

					if (enemyUnit != null) {
						unit.attack(enemyUnit);
					}
				}

			}

		} else {
			selfCnt = MyVariable.attackUnit.size();
			enemyCnt = MyVariable.enemyAttactUnit.size();

			// 적과 나의 숫자가 작을 경우에만 동작
			if (enemyCnt >= 1 && selfCnt * enemyCnt < 800) {
				// if (enemyCnt >= 1) {
				double minDistance = Double.MAX_VALUE;
				Unit minUnit = null;
				for (Unit unit : MyVariable.attackUnit) {
					for (Unit unit2 : MyVariable.enemyAttactingUnit) {
						double distance = MyUtil.distancePosition(unit.getPosition(), unit2.getPosition());
						if (minDistance > distance) {
							minDistance = distance;
							minUnit = unit;
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
			}
		}
	}

}
