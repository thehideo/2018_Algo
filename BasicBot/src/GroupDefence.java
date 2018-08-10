import java.util.HashMap;

import bwapi.Position;
import bwapi.Unit;
import bwapi.UnitType;

public class GroupDefence extends GroupAbstract {
	@Override
	public void action() {
		if (MyVariable.enemyUnitAroundMyStartPoint.size() > 0) {
			this.mapUnitTotal.put(UnitType.Terran_Goliath, MyVariable.getSelfUnit(UnitType.Terran_Goliath).size() / 3);
			this.mapUnitTotal.put(UnitType.Terran_Siege_Tank_Siege_Mode, MyVariable.getSelfUnit(UnitType.Terran_Siege_Tank_Siege_Mode).size() / 3);
			this.mapUnitTotal.put(UnitType.Terran_Siege_Tank_Tank_Mode, MyVariable.getSelfUnit(UnitType.Terran_Siege_Tank_Tank_Mode).size() / 3);
			this.mapUnitTotal.put(UnitType.Terran_Wraith, MyVariable.getSelfUnit(UnitType.Terran_Wraith).size() / 3);
			this.mapUnitTotal.put(UnitType.Terran_Vulture, MyVariable.getSelfUnit(UnitType.Terran_Vulture).size() / 3);
		} else {
			this.mapUnitTotal.clear();
		}
		// 방어 유닛 비율
		if (MyVariable.findMutal) {
			this.mapUnitTotal.put(UnitType.Terran_Goliath, 4);
			this.mapUnitTotal.put(UnitType.Terran_Marine, 8);
			this.mapUnitTotal.put(UnitType.Terran_Medic, 2);
		}
		// 방어 유닛 비율
		if (MyVariable.findWraith) {
			this.mapUnitTotal.put(UnitType.Terran_Goliath, 2);
		}

		// 많이 할당 된것은 다시 attack Group으로 이동한다.
		for (UnitType unitType : this.mapUnit.keySet()) {
			Integer cnt = 0;
			if (this.mapUnitTotal.get(unitType) != null) {
				cnt = this.mapUnitTotal.get(unitType);
			}

			while (this.mapUnit.get(unitType) != null && cnt < this.mapUnit.get(unitType).size()) {
				for (Integer unitID : this.mapUnit.get(unitType)) {
					GroupManager.instance().addToGroup(unitType, unitID, GroupManager.instance().groupAttack);
					break;
				}
			}
		}

		if (MyVariable.enemyUnitAroundMyStartPoint.size() > 0) {
			for (Unit enemyUnit : MyVariable.enemyUnitAroundMyStartPoint) {
				targetPosition = enemyUnit.getPosition();
				targetPositionForTank = enemyUnit.getPosition();
				break;
			}
		}
	}
}
