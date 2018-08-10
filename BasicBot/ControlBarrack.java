import bwapi.Unit;

public class ControlBarrack extends ControlAbstract {
	void actionMain(Unit unit, GroupAbstract groupAbstract) {
		if (unit.canLift() == true) {
			unit.lift();
		} else {
			if (unit.isUnderAttack() == true) {
				CommandUtil.move(unit, myStartLocation.toPosition());	
			} else {
				CommandUtil.move(unit, groupAbstract.getTargetPosition(unit));
			}
		}
	}
}
