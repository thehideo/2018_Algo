import bwapi.Unit;
import bwapi.UnitType;

public class ActionControlMarin implements ActionInterface {
	@Override
	public void action() {
		for (Unit unit : MyVariable.getSelfUnit(UnitType.Terran_Marine)) {
			if (unit.isAttacking() && unit.isStimmed() == false && 1.0 * unit.getHitPoints() / unit.getInitialHitPoints() > 0.5) {
				CommandUtil.useStim_Packs(unit);
			}
		}
		for (Unit unit : MyVariable.getSelfUnit(UnitType.Terran_Firebat)) {
			if (unit.isAttacking() && unit.isStimmed() == false && 1.0 * unit.getHitPoints() / unit.getInitialHitPoints() > 0.5) {
				CommandUtil.useStim_Packs(unit);
			}
		}
	}
}
