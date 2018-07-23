import bwapi.TechType;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;

public class ActionControlMarin implements ActionInterface {
	//CommandUtil commandUtil = new CommandUtil();

	@Override
	public void action() {
		for (Unit unit : MyVariable.getSelfUnit(UnitType.Terran_Marine)) {
			if (unit.isAttacking() && unit.isStimmed() == false && 1.0 * unit.getHitPoints() / unit.getInitialHitPoints() > 0.5) {
				unit.useTech(TechType.Stim_Packs);
			}
		}
		for (Unit unit : MyVariable.getSelfUnit(UnitType.Terran_Firebat)) {
			if (unit.isAttacking() && unit.isStimmed() == false && 1.0 * unit.getHitPoints() / unit.getInitialHitPoints() > 0.5) {
				unit.useTech(TechType.Stim_Packs);
			}
		}
	}
}
