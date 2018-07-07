import java.util.ArrayList;

import bwapi.Unit;
import bwapi.UnitType;

public class ActionUpdateSelfUnitMap implements ActionInterface {

	@Override
	public void action() {
		MyVariable.mapSelfUnit.clear();
		MyVariable.mapSelfAttackUnit.clear();

		// 역할별 유닛 목록
		MyVariable.attackUnit.clear();
		MyVariable.attackedUnit.clear();
		MyVariable.defenceUnit.clear();
		MyVariable.scanUnit.clear();
		MyVariable.bunkerUnit.clear();

		// 방어할 유닛 개수 초기화
		MyVariable.defenceUnitCount.clear();

		for (Unit unit : MyBotModule.Broodwar.self().getUnits()) {
			if (unit.isLoaded() == true) {
				continue;
			}
			if (!MyVariable.mapSelfUnit.containsKey(unit.getType())) {
				MyVariable.mapSelfUnit.put(unit.getType(), new ArrayList<Unit>());
				MyVariable.mapSelfAttackUnit.put(unit.getType(), new ArrayList<Unit>());
			}
			MyVariable.mapSelfUnit.get(unit.getType()).add(unit);

			// defenceUnit에 할당
			if (!setUnitAsDefence(unit)) {
				// scanUnit에 할당
				if (unit.getType() == UnitType.Terran_Science_Vessel) {
					MyVariable.scanUnit.add(unit);
				} else if (unit.canAttack() && unit.getType() != UnitType.Terran_SCV) {
					MyVariable.attackUnit.add(unit);
					MyVariable.enemyBuildingUnit.remove(unit.getTilePosition());

					MyVariable.mapSelfAttackUnit.get(unit.getType()).add(unit);

				}
			}

			// 공격당하고 있는 유닛
			if (unit.isUnderAttack()) {
				MyVariable.attackedUnit.add(unit);
			}
		}

		if (MyVariable.scanUnit.size() > 0) {
			MyVariable.haveTerran_Science_Vessel = true;
		} else {
			MyVariable.haveTerran_Science_Vessel = false;
		}

	}

	// 방어 유닛 구성
	boolean setUnitAsDefence(Unit unit) {
		boolean result = false;
		if (MyVariable.defenceUnitCountTotal.containsKey(unit.getType())) {
			if (!MyVariable.defenceUnitCount.containsKey(unit.getType())) {
				MyVariable.defenceUnitCount.put(unit.getType(), 0);
			}
			int total = MyVariable.defenceUnitCountTotal.get(unit.getType());
			int now = MyVariable.defenceUnitCount.get(unit.getType());
			if (total > now) {
				MyVariable.defenceUnit.add(unit);
				MyVariable.defenceUnitCount.put(unit.getType(), now + 1);
				result = true;
			}
		}
		return result;
	}

}
