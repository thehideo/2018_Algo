import java.util.Iterator;

import bwapi.Unit;
import bwapi.UnitType;

public class ActionMicroControl extends ActionControlAbstract {

	public ActionMicroControl() {

	}

	@Override
	public void action() {
		CommandUtil.clearCommandHash();
		Iterator<UnitType> unitTypes = MyVariable.mapSelfUnit.keySet().iterator();
		while (unitTypes.hasNext()) {
			UnitType unitType = unitTypes.next();
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
