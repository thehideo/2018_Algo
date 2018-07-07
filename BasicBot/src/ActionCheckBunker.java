import bwapi.Unit;
import bwapi.UnitType;

public class ActionCheckBunker implements ActionInterface {

	@Override
	public void action() {
		for (Unit unit : MyBotModule.Broodwar.self().getUnits()) {
			if (unit.getType() == UnitType.Terran_Bunker && unit.isCompleted()) {
				if (unit.getLoadedUnits().size() < 4) {
					for (Unit unit2 : MyBotModule.Broodwar.self().getUnits()) {
						if (unit2.getType() == UnitType.Terran_Marine && unit2.isCompleted() && unit2.isLoaded() == false) {
							unit2.rightClick(unit);
						}
					}
					return;
				}
			}
		}
	}
}
