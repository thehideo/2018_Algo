import java.util.ArrayList;

import bwapi.Race;
import bwapi.TechType;
import bwapi.Unit;
import bwapi.UnitType;
import bwapi.UpgradeType;

public class ActionUpgrade implements ActionInterface {

	@Override
	public void action() {
		if (InformationManager.Instance().enemyRace == Race.Protoss || InformationManager.Instance().enemyRace == Race.Terran) {
			// 시즈 모드
			if (MyVariable.getSelfUnit(UnitType.Terran_Machine_Shop).size() > 0) {
				if (checkNeedResearchTechType(TechType.Tank_Siege_Mode)) {
					BuildManager.Instance().buildQueue.queueAsLowestPriority(TechType.Tank_Siege_Mode, false);
				}
			}
			// 골리앗 사거리
			if (MyVariable.getSelfAttackUnit(UnitType.Terran_Goliath).size() > 4)
				for (Unit unit : MyVariable.getSelfUnit(UnitType.Terran_Machine_Shop)) {
					if (unit.canUpgrade(UpgradeType.Charon_Boosters)) {
						if (BuildManager.Instance().buildQueue.getItemCount(UpgradeType.Charon_Boosters) == 0) {
							{
								BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.Charon_Boosters, false);
								break;
							}
						}
					}
				}
			// 메카닉 업그레이드
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
			// Marine 사거리 업그레이드
			for (Unit unit : MyVariable.getSelfUnit(UnitType.Terran_Academy)) {
				if (unit.canUpgrade(UpgradeType.U_238_Shells)) {
					if (BuildManager.Instance().buildQueue.getItemCount(UpgradeType.U_238_Shells) == 0) {
						BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.U_238_Shells, false);
						break;
					}
				}
			}
			// Stim_Packs 업그레이드
			if (MyVariable.getSelfUnit(UnitType.Terran_Academy).size() > 0 && checkNeedResearchTechType(TechType.Stim_Packs)) {
				BuildManager.Instance().buildQueue.queueAsLowestPriority(TechType.Stim_Packs, false);
			}
		} else {
			// 바이오닐 공격력, 방어력 업그레이드
			if (MyVariable.getSelfUnit(UnitType.Terran_Marine).size() > 10) {
				for (Unit unit : MyVariable.getSelfUnit(UnitType.Terran_Engineering_Bay)) {
					if (unit.canUpgrade(UpgradeType.Terran_Infantry_Armor) && BuildManager.Instance().buildQueue.getItemCount(UpgradeType.Terran_Infantry_Armor) == 0) {
						BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.Terran_Infantry_Armor, false);
						break;
					}
					if (unit.canUpgrade(UpgradeType.Terran_Infantry_Weapons) && BuildManager.Instance().buildQueue.getItemCount(UpgradeType.Terran_Infantry_Weapons) == 0 && MyVariable.getSelfUnit(UnitType.Terran_Marine).size() > 20) {
						if (MyVariable.getSelfUnit(UnitType.Terran_Engineering_Bay).get(0).canUpgrade(UpgradeType.Terran_Infantry_Weapons)) {
							MyVariable.getSelfUnit(UnitType.Terran_Engineering_Bay).get(0).upgrade(UpgradeType.Terran_Infantry_Weapons);
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
			// Stim_Packs 업그레이드
			if (MyVariable.getSelfUnit(UnitType.Terran_Academy).size() > 0 && checkNeedResearchTechType(TechType.Stim_Packs)) {
				BuildManager.Instance().buildQueue.queueAsLowestPriority(TechType.Stim_Packs, false);
			}
			if (MyVariable.getSelfUnit(UnitType.Terran_Machine_Shop).size() > 0 && checkNeedResearchTechType(TechType.Tank_Siege_Mode)) {
				BuildManager.Instance().buildQueue.queueAsLowestPriority(TechType.Tank_Siege_Mode, false);
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
