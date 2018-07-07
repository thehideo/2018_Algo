import bwapi.TechType;
import bwapi.UnitType;

public class ActionCheckBuilding implements ActionInterface {

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

		// 기본으로 Factory 2개는 건설함, 시즈모드 개발
		if (checkNeedToBuild(UnitType.Terran_Factory, 2))
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Factory, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
		if (checkNeedToBuild(UnitType.Terran_Machine_Shop, 2))
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Machine_Shop, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
		if (checkNeedToBuild(UnitType.Terran_Armory, 1))
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Armory, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
		if (checkNeedTechType(TechType.Tank_Siege_Mode))
			BuildManager.Instance().buildQueue.queueAsLowestPriority(TechType.Tank_Siege_Mode, false);

		if (MyVariable.isFullScaleAttackStarted) {
			if (checkNeedToBuild(UnitType.Terran_Command_Center, 2)) {
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Command_Center, BuildOrderItem.SeedPositionStrategy.FirstExpansionLocation, false);
			}
		}
	}

	// 건설된 것이 하나도 없는지 확인
	boolean checkNeedToBuild(UnitType unitType, int cnt) {
		boolean result = false;
		if (MyBotModule.Broodwar.self().allUnitCount(unitType) < cnt && BuildManager.Instance().getBuildQueue().getItemCount(unitType) == 0 && ConstructionManager.Instance().getConstructionQueueItemCount(unitType, null) == 0) {
			result = true;
		}
		return result;
	}

	boolean checkNeedTechType(TechType techType) {
		boolean result = false;
		if (!MyBotModule.Broodwar.self().hasResearched(techType) && MyBotModule.Broodwar.self().isResearchAvailable(techType) && !MyBotModule.Broodwar.self().isResearching(techType) && BuildManager.Instance().getBuildQueue().getItemCount(techType) == 0) {
			result = true;
		}
		return result;
	}
}
