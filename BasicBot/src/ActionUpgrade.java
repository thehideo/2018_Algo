
import bwapi.Race;
import bwapi.TechType;
import bwapi.Unit;
import bwapi.UnitType;
import bwapi.UpgradeType;

public class ActionUpgrade extends ActionControlAbstract {

	@Override
	public void action() {
		if (InformationManager.Instance().enemyRace == Race.Protoss) {
			updateProtoss();
		} else if (InformationManager.Instance().enemyRace == Race.Terran) {
			updateTerran();
		} else {
			updateZerg();
		}
	}

	public void updateProtoss() {
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

		if (MyVariable.findFastCarrier == false) {
			// 바이오닉 공격력, 방어력 업그레이드
			if (MyVariable.getSelfUnit(UnitType.Terran_Marine).size() > 10) {
				for (Unit unit : MyVariable.getSelfUnit(UnitType.Terran_Engineering_Bay)) {
					if (unit.canUpgrade(UpgradeType.Terran_Infantry_Weapons) && BuildManager.Instance().buildQueue.getItemCount(UpgradeType.Terran_Infantry_Weapons) == 0) {
						BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.Terran_Infantry_Weapons, false);
						break;
					}
					if (unit.canUpgrade(UpgradeType.Terran_Infantry_Armor) && BuildManager.Instance().buildQueue.getItemCount(UpgradeType.Terran_Infantry_Armor) == 0) {
						BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.Terran_Infantry_Armor, false);
						break;
					}
				}
			}

			// 시즈 모드
			for (Unit unit : MyVariable.getSelfUnit(UnitType.Terran_Machine_Shop)) {
				if (unit.canResearch(TechType.Tank_Siege_Mode)) {
					if (BuildManager.Instance().buildQueue.getItemCount(TechType.Tank_Siege_Mode) == 0) {
						BuildManager.Instance().buildQueue.queueAsHighestPriority(TechType.Tank_Siege_Mode, true);
						break;
					}
				}
			}

			// 골리앗 사거리
			if (MyVariable.getSelfAttackUnit(UnitType.Terran_Goliath).size() > 4 || MyVariable.findCarrier == true) {
				for (Unit unit : MyVariable.getSelfUnit(UnitType.Terran_Machine_Shop)) {
					if (unit.canUpgrade(UpgradeType.Charon_Boosters)) {
						if (BuildManager.Instance().buildQueue.getItemCount(UpgradeType.Charon_Boosters) == 0) {
							{
								if (MyVariable.findCarrier == false) {
									BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.Charon_Boosters, false);
								} else {
									BuildManager.Instance().buildQueue.queueAsHighestPriority(UpgradeType.Charon_Boosters, true);
								}
								break;
							}
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

			// EMP
			for (Unit unit : MyVariable.getSelfUnit(UnitType.Terran_Science_Facility)) {
				if (unit.canResearch(TechType.EMP_Shockwave)) {
					if (BuildManager.Instance().buildQueue.getItemCount(TechType.EMP_Shockwave) == 0) {
						{
							BuildManager.Instance().buildQueue.queueAsHighestPriority(TechType.EMP_Shockwave, true);
						}
					}
					break;
				}
			}
		}
		// 패스트 캐리어이면 공업
		else {
			for (Unit unit : MyVariable.getSelfUnit(UnitType.Terran_Engineering_Bay)) {
				if (unit.canUpgrade(UpgradeType.Terran_Infantry_Weapons) && BuildManager.Instance().buildQueue.getItemCount(UpgradeType.Terran_Infantry_Weapons) == 0 && MyVariable.getSelfUnit(UnitType.Terran_Marine).size() > 20) {
					BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.Terran_Infantry_Weapons, false);
				}
			}
		}
	}

	public void updateTerran() {
		// 벌처
		for (Unit unit : MyVariable.getSelfUnit(UnitType.Terran_Machine_Shop)) {
			if (unit.canResearch(TechType.Spider_Mines)) {
				if (BuildManager.Instance().buildQueue.getItemCount(TechType.Spider_Mines) == 0) {
					BuildManager.Instance().buildQueue.queueAsLowestPriority(TechType.Spider_Mines, false);
					break;
				}
			}
			// if (unit.canUpgrade(UpgradeType.Ion_Thrusters)) {
			// if
			// (BuildManager.Instance().buildQueue.getItemCount(UpgradeType.Ion_Thrusters)
			// == 0) {
			// BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.Ion_Thrusters,
			// false);
			// break;
			// }
			// }
		}

		// 시즈 모드
		for (Unit unit : MyVariable.getSelfUnit(UnitType.Terran_Machine_Shop)) {
			if (unit.canResearch(TechType.Tank_Siege_Mode)) {
				if (BuildManager.Instance().buildQueue.getItemCount(TechType.Tank_Siege_Mode) == 0) {
					BuildManager.Instance().buildQueue.queueAsLowestPriority(TechType.Tank_Siege_Mode, false);
					break;
				}
			}
		}

		// 레이스 클로킹
		for (Unit unit : MyVariable.getSelfUnit(UnitType.Terran_Control_Tower)) {
			if (unit.canResearch(TechType.Cloaking_Field)) {
				if (BuildManager.Instance().buildQueue.getItemCount(TechType.Cloaking_Field) == 0) {
					BuildManager.Instance().buildQueue.queueAsLowestPriority(TechType.Cloaking_Field, false);
					break;
				}
			}
		}

		// 골리앗 사거리
		if (MyVariable.getSelfAttackUnit(UnitType.Terran_Goliath).size() > 4) {
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
		}

		if (MyUtil.GetMyTankCnt() >= 8) {
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
		}

	}

	public void updateZerg() {
		// 바이오닉 공격력, 방어력 업그레이드
		if (MyVariable.getSelfUnit(UnitType.Terran_Marine).size() > 10) {
			for (Unit unit : MyVariable.getSelfUnit(UnitType.Terran_Engineering_Bay)) {
				if (unit.canUpgrade(UpgradeType.Terran_Infantry_Weapons) && BuildManager.Instance().buildQueue.getItemCount(UpgradeType.Terran_Infantry_Weapons) == 0) {
					BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.Terran_Infantry_Weapons, false);
					break;
				}
				if (unit.canUpgrade(UpgradeType.Terran_Infantry_Armor) && BuildManager.Instance().buildQueue.getItemCount(UpgradeType.Terran_Infantry_Armor) == 0) {
					BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.Terran_Infantry_Armor, false);
					break;
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
		// 시즈 모드 개발
		for (Unit unit : MyVariable.getSelfUnit(UnitType.Terran_Machine_Shop)) {
			if (unit.canResearch(TechType.Tank_Siege_Mode)) {
				if (BuildManager.Instance().buildQueue.getItemCount(TechType.Tank_Siege_Mode) == 0) {
					BuildManager.Instance().buildQueue.queueAsLowestPriority(TechType.Tank_Siege_Mode, false);
					break;
				}
			}
		}
		// Irradiate 개발
		for (Unit unit : MyVariable.getSelfUnit(UnitType.Terran_Science_Facility)) {
			if (unit.canResearch(TechType.Irradiate)) {
				if (BuildManager.Instance().buildQueue.getItemCount(TechType.Irradiate) == 0) {
					{
						BuildManager.Instance().buildQueue.queueAsHighestPriority(TechType.Irradiate, true);
					}
				}
				break;
			}
		}
	}
}
