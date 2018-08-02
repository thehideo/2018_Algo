import bwapi.Unit;

public class ControlFireBat extends ControlAbstract {
	void actionMain(Unit unit, GroupAbstract groupAbstract) {
		if (unit.isAttacking() && unit.isStimmed() == false && 1.0 * unit.getHitPoints() / unit.getInitialHitPoints() > 0.5) {
			CommandUtil.useStim_Packs(unit);
			return;
		}
		CommandUtil.attackMove(unit, groupAbstract.getTargetPosition(unit));
	}
}
