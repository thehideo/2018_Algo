import bwapi.Unit;

public class ControlSCV extends ControlAbstract {
	void actionMain(Unit unit, GroupAbstract groupAbstract) {
		CommandUtil.attackUnit(unit, groupAbstract.getTargetUnit(unit));
	}
}
