import bwapi.Unit;
import bwapi.UnitType;

// 벙커가 있는데 안에 마린이 없으면 채워넣는다.
public class ActionCheckBunker implements ActionInterface {

	@Override
	public void action() {
		for (Unit unit : MyBotModule.Broodwar.self().getUnits()) {
			if (unit.getType() == UnitType.Terran_Bunker && unit.isCompleted()) {
				if (MyVariable.attackUnit.size() > 20) {
					unit.unloadAll();
				} else if (unit.getLoadedUnits().size() < 4) {
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
