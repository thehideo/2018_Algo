import java.util.ArrayList;

import bwapi.Unit;
import bwapi.UnitType;
import bwapi.UpgradeType;

public class ActionCheckUnitUpdate implements ActionInterface {

	@Override
	public void action() {
		if (MyVariable.mapSelfUnit.containsKey(UnitType.Terran_Engineering_Bay) && MyVariable.mapSelfUnit.containsKey(UnitType.Terran_Marine)) {
			ArrayList<Unit> marin = MyVariable.mapSelfUnit.get(UnitType.Terran_Marine);
			if (marin.size() > 20) {
				if (MyVariable.mapSelfUnit.get(UnitType.Terran_Engineering_Bay).get(0).canUpgrade(UpgradeType.Terran_Infantry_Weapons)) {
					MyVariable.mapSelfUnit.get(UnitType.Terran_Engineering_Bay).get(0).upgrade(UpgradeType.Terran_Infantry_Weapons);
				}
			}
		}
	}
}
