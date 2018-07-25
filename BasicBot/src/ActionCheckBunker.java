import bwapi.Unit;
import bwapi.UnitType;

// 벙커가 있는데 안에 마린이 없으면 채워넣는다.
public class ActionCheckBunker implements ActionInterface {

	@Override
	public void action() {
		for (Unit unit : MyVariable.getSelfUnit(UnitType.Terran_Bunker)) {
			if (unit.isCompleted()) {
				if (MyVariable.isFullScaleAttackStarted == true) {
					if (unit.getLoadedUnits().size() > 0) {
						unit.unloadAll();
					}
				} else {
					if (unit.getLoadedUnits().size() < 4) {
						for (Unit unit2 : MyVariable.getSelfUnit(UnitType.Terran_Marine)) {
							if (unit2.isCompleted() && unit2.isLoaded() == false && unit2.isIdle() == true) {
								unit2.rightClick(unit);
								MyVariable.bunkerUnit.add(unit);
								MyVariable.attackUnit.remove(unit);
							}
						}
						return;
					}
				}
			}
		}
	}
}
