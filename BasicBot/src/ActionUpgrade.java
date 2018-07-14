import java.util.ArrayList;

import bwapi.Race;
import bwapi.TechType;
import bwapi.Unit;
import bwapi.UnitType;
import bwapi.UpgradeType;

public class ActionUpgrade implements ActionInterface {

	@Override
	public void action() {
		if (InformationManager.Instance().enemyRace == Race.Protoss) {
			
			if (MyVariable.getSelfUnit(UnitType.Terran_Machine_Shop).size() > 0) {
				if (checkNeedResearchTechType(TechType.Tank_Siege_Mode)) {
					BuildManager.Instance().buildQueue.queueAsLowestPriority(TechType.Tank_Siege_Mode, false);
				}
			}

			for (Unit unit : MyVariable.getSelfUnit(UnitType.Terran_Armory)) {
				if (unit.canUpgrade(UpgradeType.Terran_Vehicle_Weapons)) {
					if (BuildManager.Instance().buildQueue.getItemCount(UpgradeType.Terran_Vehicle_Weapons) == 0) {
						{
							BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.Terran_Vehicle_Weapons, false);
							break;
						}
					}
				}
				if (unit.canUpgrade(UpgradeType.Terran_Vehicle_Plating)) {
					if (BuildManager.Instance().buildQueue.getItemCount(UpgradeType.Terran_Vehicle_Plating) == 0) {
						{
							BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.Terran_Vehicle_Plating, false);
							break;
						}
					}
				}
			}
		} else {
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
					if (unit.canUpgrade(UpgradeType.Terran_Infantry_Armor)) {
						if (BuildManager.Instance().buildQueue.getItemCount(UpgradeType.Terran_Infantry_Armor) == 0) {
							{
								BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.Terran_Infantry_Armor, false);
								break;
							}
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
			if (MyVariable.getSelfUnit(UnitType.Terran_Machine_Shop).size() > 0 && checkNeedResearchTechType(TechType.Tank_Siege_Mode)) {
				BuildManager.Instance().buildQueue.queueAsLowestPriority(TechType.Tank_Siege_Mode, false);
			}
			// Stim_Packs 업그레이드
			if (MyVariable.getSelfUnit(UnitType.Terran_Academy).size() > 0 && checkNeedResearchTechType(TechType.Stim_Packs)) {
				BuildManager.Instance().buildQueue.queueAsLowestPriority(TechType.Stim_Packs, false);
			}
			if (MyVariable.getSelfUnit(UnitType.Terran_Engineering_Bay).size() > 0 && marin.size() > 20) {
				if (MyVariable.getSelfUnit(UnitType.Terran_Engineering_Bay).get(0).canUpgrade(UpgradeType.Terran_Infantry_Weapons)) {
					MyVariable.getSelfUnit(UnitType.Terran_Engineering_Bay).get(0).upgrade(UpgradeType.Terran_Infantry_Weapons);
				}
			}
		}

	}

	boolean checkNeedResearchTechType(TechType techType) {
		boolean result = false;
		if (MyBotModule.Broodwar.self().isResearchAvailable(techType) && !MyBotModule.Broodwar.self().isResearching(techType) && !MyBotModule.Broodwar.self().hasResearched(techType) && BuildManager.Instance().getBuildQueue().getItemCount(techType) == 0) {
			result = true;
		}
		return result;
	}
}
