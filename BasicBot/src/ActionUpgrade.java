import java.util.ArrayList;

import bwapi.TechType;
import bwapi.Unit;
import bwapi.UnitType;
import bwapi.UpgradeType;

public class ActionUpgrade implements ActionInterface {

	@Override
	public void action() {

		ArrayList<Unit> marin = MyVariable.getSelfUnit(UnitType.Terran_Marine);
		if (marin.size() > 10) {
			// 지상유닛 방어력 업그레이드
			for (Unit unit : MyVariable.getSelfUnit(UnitType.Terran_Engineering_Bay)) {
				if (unit.canUpgrade(UpgradeType.Terran_Infantry_Armor))
					if (BuildManager.Instance().buildQueue.getItemCount(UpgradeType.Terran_Infantry_Armor) == 0) {
						{
							BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.Terran_Infantry_Armor, false);
							break;
						}
					}
			}

			// Marine 사거리 업그레이드
			for (Unit unit : MyVariable.getSelfUnit(UnitType.Terran_Academy)) {
				if (unit.canUpgrade(UpgradeType.U_238_Shells)) {
					if (BuildManager.Instance().buildQueue.getItemCount(UpgradeType.U_238_Shells) == 0) {
						BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.U_238_Shells, false);
						break;
					}
				}
			}
		}

		if (marin.size() > 20) {
			if (MyVariable.getSelfUnit(UnitType.Terran_Engineering_Bay).get(0).canUpgrade(UpgradeType.Terran_Infantry_Weapons)) {
				MyVariable.getSelfUnit(UnitType.Terran_Engineering_Bay).get(0).upgrade(UpgradeType.Terran_Infantry_Weapons);
			}
		}

	}
}
