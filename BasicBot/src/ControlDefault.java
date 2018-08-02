import bwapi.Unit;

public class ControlDefault extends ControlAbstract {
	void actionMain(Unit unit, GroupAbstract groupAbstract) {
		if (unit.canAttack() == false) {
			CommandUtil.move(unit, groupAbstract.getTargetPosition(unit));
		} else {
			CommandUtil.attackMove(unit, groupAbstract.getTargetPosition(unit));
		}
	}
}
