import java.util.HashSet;

import bwapi.Unit;
import bwapi.UnitType;

public class ActionMicroControl extends ActionControlAbstract {

	public ActionMicroControl() {

	}

	@Override
	public void action() {
		CommandUtil.clearCommandHash();
		HashSet<UnitType> map = new HashSet<UnitType>();

		map.addAll(MyVariable.getSelfUnitKey());

		for (UnitType unitType : map) {
			if (unitType.isBuilding() == true)
				continue;
			ControlAbstract controlAbstract = GroupManager.instance().getControlAbstract(unitType);
			if (controlAbstract != null) {
				for (Unit unit : MyVariable.getSelfUnit(unitType)) {
					GroupAbstract groupAbstract = GroupManager.instance().getUnitsGroup(unit);
					if (groupAbstract != null) {
						controlAbstract.action(unit, groupAbstract);
					}
				}
			}
		}
	}
}
