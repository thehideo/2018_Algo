
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
			for (Unit unit : MyVariable.getSelfUnit(UnitType.Terran_Machine_Shop)) {
				if (unit.canResearch(TechType.Tank_Siege_Mode)) {
					if (BuildManager.Instance().buildQueue.getItemCount(TechType.Tank_Siege_Mode) == 0) {
						BuildManager.Instance().buildQueue.queueAsLowestPriority(TechType.Tank_Siege_Mode, false);
						break;
					}
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

			// 바이오닉 공격력, 방어력 업그레이드
			if (MyVariable.getSelfUnit(UnitType.Terran_Marine).size() > 20) {
				for (Unit unit : MyVariable.getSelfUnit(UnitType.Terran_Engineering_Bay)) {
					if (unit.canUpgrade(UpgradeType.Terran_Infantry_Armor) && BuildManager.Instance().buildQueue.getItemCount(UpgradeType.Terran_Infantry_Armor) == 0) {
						BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.Terran_Infantry_Armor, false);
						break;
					}
					if (unit.canUpgrade(UpgradeType.Terran_Infantry_Weapons) && BuildManager.Instance().buildQueue.getItemCount(UpgradeType.Terran_Infantry_Weapons) == 0) {
						BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.Terran_Infantry_Weapons, false);
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
			// Marine 사거리/스팀팩 업그레이드
			for (Unit unit : MyVariable.getSelfUnit(UnitType.Terran_Academy)) {
				if (unit.canUpgrade(UpgradeType.U_238_Shells)) {
					if (BuildManager.Instance().buildQueue.getItemCount(UpgradeType.U_238_Shells) == 0) {
						BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.U_238_Shells, false);
						break;
					}
				}
				if (unit.canResearch(TechType.Stim_Packs)) {
					if (BuildManager.Instance().buildQueue.getItemCount(TechType.Stim_Packs) == 0) {
						BuildManager.Instance().buildQueue.queueAsLowestPriority(TechType.Stim_Packs, false);
						break;
					}
				}
			}
		} else {
			// 바이오닉 공격력, 방어력 업그레이드
			if (MyVariable.getSelfUnit(UnitType.Terran_Marine).size() > 10) {
				for (Unit unit : MyVariable.getSelfUnit(UnitType.Terran_Engineering_Bay)) {
					if (unit.canUpgrade(UpgradeType.Terran_Infantry_Armor) && BuildManager.Instance().buildQueue.getItemCount(UpgradeType.Terran_Infantry_Armor) == 0) {
						BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.Terran_Infantry_Armor, false);
						break;
					}
					if (unit.canUpgrade(UpgradeType.Terran_Infantry_Weapons) && BuildManager.Instance().buildQueue.getItemCount(UpgradeType.Terran_Infantry_Weapons) == 0 && MyVariable.getSelfUnit(UnitType.Terran_Marine).size() > 20) {
						BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.Terran_Infantry_Weapons, false);
					}
				}
			}
			// Marine 사거리/스팀팩 업그레이드
			for (Unit unit : MyVariable.getSelfUnit(UnitType.Terran_Academy)) {
				if (unit.canUpgrade(UpgradeType.U_238_Shells)) {
					if (BuildManager.Instance().buildQueue.getItemCount(UpgradeType.U_238_Shells) == 0) {
						BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.U_238_Shells, false);
						break;
					}
				}
				if (unit.canResearch(TechType.Stim_Packs)) {
					if (BuildManager.Instance().buildQueue.getItemCount(TechType.Stim_Packs) == 0) {
						BuildManager.Instance().buildQueue.queueAsLowestPriority(TechType.Stim_Packs, false);
						break;
					}
				}
			}
			for (Unit unit : MyVariable.getSelfUnit(UnitType.Terran_Machine_Shop)) {
				if (unit.canResearch(TechType.Tank_Siege_Mode)) {
					if (BuildManager.Instance().buildQueue.getItemCount(TechType.Tank_Siege_Mode) == 0) {
						BuildManager.Instance().buildQueue.queueAsLowestPriority(TechType.Tank_Siege_Mode, false);
						break;
					}
				}
			}
		}
	}
}
