import bwapi.TechType;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;

public class ActionControlMarin implements ActionInterface {
	CommandUtil commandUtil = new CommandUtil();

	@Override
	public void action() {
		if (MyVariable.mapSelfUnit.containsKey(UnitType.Terran_Marine)) {
			for (Unit unit : MyVariable.mapSelfUnit.get(UnitType.Terran_Marine)) {
				if (unit.isAttacking() && unit.isStimmed() == false) {
					unit.useTech(TechType.Stim_Packs);
				}
			}
		}
		if (MyVariable.mapSelfUnit.containsKey(UnitType.Terran_Firebat)) {
			for (Unit unit : MyVariable.mapSelfUnit.get(UnitType.Terran_Firebat)) {
				if (unit.isAttacking() && unit.isStimmed() == false) {
					unit.useTech(TechType.Stim_Packs);
				}
			}
		}
	}
}
