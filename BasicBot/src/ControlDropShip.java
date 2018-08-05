import java.util.HashMap;

import bwapi.Race;
import bwapi.TechType;
import bwapi.Unit;
import bwapi.UnitType;

public class ControlDropShip extends ControlAbstract {

	HashMap<Integer, Boolean> mapMode = new HashMap<Integer, Boolean>();

	void actionMain(Unit unit, GroupAbstract groupAbstract) {

		CommandUtil.attackMove(unit, groupAbstract.getTargetPosition(unit));
	}

}
