import bwapi.TechType;
import bwapi.UnitType;

public class ActionCreateBuilding implements ActionInterface {

	@Override
	public void action() {
		if (MyVariable.isInitialBuildOrderFinished == false) {
			return;
		}
		// 베슬이 필요하면 바로 건설
		if (MyVariable.needTerran_Science_Vessel) {
			if (checkNeedToBuild(UnitType.Terran_Factory, 1))
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Factory, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
			if (checkNeedToBuild(UnitType.Terran_Starport, 1))
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Starport, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
			if (checkNeedToBuild(UnitType.Terran_Control_Tower, 1))
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Control_Tower, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
			if (checkNeedToBuild(UnitType.Terran_Physics_Lab, 1))
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Physics_Lab, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
			if (checkNeedToBuild(UnitType.Terran_Science_Facility, 1))
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Science_Facility, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
			if (checkNeedToBuild(UnitType.Terran_Science_Vessel, 1))
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Science_Vessel, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
		}

		if (MyVariable.findLucker || MyVariable.findMutal) {
			if (checkNeedToBuild(UnitType.Terran_Missile_Turret, 2))
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Missile_Turret, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
		}

		if (MyVariable.isFullScaleAttackStarted) {
			if (checkNeedToBuild(UnitType.Terran_Factory, 2))
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Factory, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
			if (checkNeedToBuild(UnitType.Terran_Machine_Shop, 2))
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Machine_Shop, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
			if (checkNeedToBuild(UnitType.Terran_Armory, 1))
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Armory, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
			if (checkNeedResearchTechType(TechType.Tank_Siege_Mode))
				BuildManager.Instance().buildQueue.queueAsLowestPriority(TechType.Tank_Siege_Mode, false);
			if (checkNeedToBuild(UnitType.Terran_Command_Center, 2)) {
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Command_Center, BuildOrderItem.SeedPositionStrategy.FirstExpansionLocation, false);
			}
		}
		// Terran_Barracks 건설
		if (checkNeedToBuild(UnitType.Terran_Barracks, 3)) {
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Barracks, false);
		}
		// Terran_Refinery 건설
		if (checkNeedToBuild(UnitType.Terran_Refinery, MyUtil.getCommandCenterCount())) {
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Refinery, false);
		}
		// Terran_Academy 건설
		if (checkNeedToBuild(UnitType.Terran_Academy, 1)) {
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Academy, false);
		}
		// Terran_Comsat_Station 건설
		if (checkNeedToBuild(UnitType.Terran_Comsat_Station, MyUtil.getCommandCenterCount())) {
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Comsat_Station, false);
		}
		// Stim_Packs 업그레이드
		if (checkNeedResearchTechType(TechType.Stim_Packs)) {
			BuildManager.Instance().buildQueue.queueAsLowestPriority(TechType.Stim_Packs, false);
		}
		// Terran_Engineering_Bay 건설
		if (checkNeedToBuild(UnitType.Terran_Engineering_Bay, 1)) {
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Engineering_Bay, false);
		}
	}

	boolean checkNeedToBuild(UnitType unitType, int cnt) {
		boolean result = false;
		if (MyBotModule.Broodwar.self().allUnitCount(unitType) < cnt && BuildManager.Instance().getBuildQueue().getItemCount(unitType) == 0 && ConstructionManager.Instance().getConstructionQueueItemCount(unitType, null) == 0) {
			result = true;
		}
		return result;
	}

	boolean checkNeedResearchTechType(TechType techType) {
		boolean result = false;
		if (MyBotModule.Broodwar.self().isResearchAvailable(techType) && !MyBotModule.Broodwar.self().isResearching(techType) && !MyBotModule.Broodwar.self().hasResearched(techType) && BuildManager.Instance().getBuildQueue().getItemCount(techType) == 0) {
			result = true;
		}
		return result;
	}
}
