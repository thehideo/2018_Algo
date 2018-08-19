import java.util.HashSet;
import java.util.Iterator;

import bwapi.Unit;
import bwapi.UnitType;

public class ActionMicroControl extends ActionControlAbstract {

	public ActionMicroControl() {

	}

	@Override
	public void action() {
		CommandUtil.clearCommandHash();
		HashSet<UnitType> unitTypes = new HashSet<UnitType>();
		unitTypes.addAll(MyVariable.mapSelfUnit.keySet());
		for (UnitType unitType : unitTypes) {
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
