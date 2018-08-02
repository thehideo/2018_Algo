import bwapi.Unit;

public class ControlScienceVessel extends ControlAbstract {
	void actionMain(Unit unit, GroupAbstract groupAbstract) {

		CommandUtil.move(unit, groupAbstract.getTargetPosition(unit));
	}

}
