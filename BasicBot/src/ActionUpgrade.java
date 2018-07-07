import java.util.ArrayList;

import bwapi.TechType;
import bwapi.Unit;
import bwapi.UnitType;
import bwapi.UpgradeType;

public class ActionUpgrade implements ActionInterface {

	@Override
	public void action() {
		if (MyVariable.mapSelfUnit.containsKey(UnitType.Terran_Engineering_Bay) && MyVariable.mapSelfUnit.containsKey(UnitType.Terran_Marine)) {
			ArrayList<Unit> marin = MyVariable.mapSelfUnit.get(UnitType.Terran_Marine);
			if (marin.size() > 10) {
				// 지상유닛 방어력 업그레이드
				if (MyVariable.mapSelfUnit.get(UnitType.Terran_Engineering_Bay).get(0).canUpgrade(UpgradeType.Terran_Infantry_Armor)) {
					BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.Terran_Infantry_Armor, false);
				}

				// Marine 사거리 업그레이드
				if (MyVariable.mapSelfUnit.get(UnitType.Terran_Academy).get(0).canUpgrade(UpgradeType.U_238_Shells)) {
					if (BuildManager.Instance().buildQueue.getItemCount(UpgradeType.U_238_Shells) == 0) {
						BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.U_238_Shells, false);
					}
				}
			}

			if (marin.size() > 20) {
				if (MyVariable.mapSelfUnit.get(UnitType.Terran_Engineering_Bay).get(0).canUpgrade(UpgradeType.Terran_Infantry_Weapons)) {
					MyVariable.mapSelfUnit.get(UnitType.Terran_Engineering_Bay).get(0).upgrade(UpgradeType.Terran_Infantry_Weapons);
				}
			}
		}
	}
}
