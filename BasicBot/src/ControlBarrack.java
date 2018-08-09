import bwapi.Position;
import bwapi.Unit;

public class ControlBarrack extends ControlAbstract {
	void actionMain(Unit unit, GroupAbstract groupAbstract) {
		if (unit.canLift() == true) {
			unit.lift();
		} else {
			Position target = null;
			if (unit.isUnderAttack() == true) {
				target = MyVariable.myStartLocation.toPosition();
			} else {
				target = groupAbstract.getTargetPosition(unit);
			}
			target = new Position(target.getX() - 1, target.getY() - 1);
		
			CommandUtil.move(unit, target);
		}
	}
}
