import bwapi.Unit;

public class ControlBarrack extends ControlAbstract {
	void actionMain(Unit unit, GroupAbstract groupAbstract) {
		if (unit.canLift() == true) {
			unit.lift();
		} else {
			if (unit.isUnderAttack() == true) {
				unit.move(MyVariable.myStartLocation.toPosition());
			} else {
				unit.move(groupAbstract.getTargetPosition(unit));
			}
		}
	}
}
