import java.util.List;

import bwapi.Position;
import bwapi.Race;
import bwapi.TechType;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;

public class ActionCreateBuilding implements ActionInterface {

	@Override
	public void action() {
		if (MyVariable.isInitialBuildOrderFinished == false) {
			return;
		}

		// 베슬이 필요하면 바로 건설
		if (MyVariable.needTerran_Science_Vessel) {
			if (checkNeedToBuild(UnitType.Terran_Factory, 1) && MyVariable.getSelfUnit(UnitType.Terran_Barracks).size() >= 1 && MyVariable.getSelfUnit(UnitType.Terran_Refinery).size() >= 1)
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Factory, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
			if (checkNeedToBuild(UnitType.Terran_Starport, 1) && MyVariable.getSelfUnit(UnitType.Terran_Factory).size() >= 1)
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Starport, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
			if (checkNeedToBuild(UnitType.Terran_Control_Tower, 1) && MyVariable.getSelfUnit(UnitType.Terran_Starport).size() >= 1)
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Control_Tower, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);

			if (checkNeedToBuild(UnitType.Terran_Science_Facility, 1) && MyVariable.getSelfUnit(UnitType.Terran_Control_Tower).size() >= 1)
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Science_Facility, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);

			if (checkNeedToBuild(UnitType.Terran_Physics_Lab, 1) && MyVariable.getSelfUnit(UnitType.Terran_Science_Facility).size() >= 1)
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Physics_Lab, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);

			if (checkNeedToBuild(UnitType.Terran_Science_Vessel, 1) && MyVariable.getSelfUnit(UnitType.Terran_Physics_Lab).size() >= 1)
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Science_Vessel, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
		}

		if (InformationManager.Instance().enemyRace == Race.Protoss || InformationManager.Instance().enemyRace == Race.Terran) {

			if (MyVariable.isFullScaleAttackStarted) {
				if (checkNeedToBuild(UnitType.Terran_Factory, 3))
					BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Factory, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
				if (checkNeedToBuild(UnitType.Terran_Machine_Shop, 3))
					BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Machine_Shop, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
				if (checkNeedToBuild(UnitType.Terran_Command_Center, 2)) {
					BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Command_Center, BuildOrderItem.SeedPositionStrategy.FirstExpansionLocation, true);
					if (checkNeedToBuild(UnitType.Terran_Refinery, 2)) {
						BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Refinery, BuildOrderItem.SeedPositionStrategy.FirstExpansionLocation, true);
					}
				}
				// MyVariable.needTerran_Science_Vessel = true;
			}

			// 벙커 건설
			if (InformationManager.Instance().enemyRace == Race.Protoss) {
				if (checkNeedToBuild(UnitType.Terran_Bunker, 1) && MyVariable.getSelfUnit(UnitType.Terran_Marine).size() >= 2)
					BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Bunker, MyUtil.GetMyBunkerBuildPosition().toTilePosition(), false);

				if (checkNeedToBuild(UnitType.Terran_Bunker, 2) && MyVariable.getSelfUnit(UnitType.Terran_Marine).size() >= 6)
					BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Bunker, MyUtil.GetMyBunkerBuildPosition().toTilePosition(), false);

				if (checkNeedToBuild(UnitType.Terran_Bunker, 3) && MyVariable.getSelfUnit(UnitType.Terran_Marine).size() >= 10)
					BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Bunker, MyVariable.myStartLocation, false);
			} else if (InformationManager.Instance().enemyRace == Race.Terran) {
				if (checkNeedToBuild(UnitType.Terran_Bunker, 1) && MyVariable.getSelfUnit(UnitType.Terran_Marine).size() >= 2)
					BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Bunker, MyUtil.GetMyBunkerBuildPosition().toTilePosition(), false);
				if (checkNeedToBuild(UnitType.Terran_Bunker, 2) && MyVariable.getSelfUnit(UnitType.Terran_Marine).size() >= 6)
					BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Bunker, MyUtil.GetMyBunkerBuildPosition().toTilePosition(), false);
			}

			if (checkNeedToBuild(UnitType.Terran_Refinery, 1) && MyVariable.getSelfUnit(UnitType.Terran_Bunker).size() >= 2)
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Refinery, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);

			if (checkNeedToBuild(UnitType.Terran_Engineering_Bay, 1) && MyVariable.getSelfUnit(UnitType.Terran_Bunker).size() >= 1 && MyVariable.getSelfUnit(UnitType.Terran_Marine).size() >= 2) {
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Engineering_Bay, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
			}

			if (InformationManager.Instance().enemyRace == Race.Protoss) {
				if (checkNeedToBuild(UnitType.Terran_Missile_Turret, 1) && MyVariable.getSelfUnit(UnitType.Terran_Engineering_Bay).size() >= 1) {
					Position BunkerDonthaveTurretPosition = MyUtil.GetMyBunkerDonthaveTurretPosition();
					if (BunkerDonthaveTurretPosition == null) {
						BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Missile_Turret, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
					} else {
						BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Missile_Turret, BunkerDonthaveTurretPosition.toTilePosition(), false);
					}
				}
				if (checkNeedToBuild(UnitType.Terran_Missile_Turret, 2) && MyVariable.getSelfUnit(UnitType.Terran_Engineering_Bay).size() >= 1 && MyVariable.getSelfUnit(UnitType.Terran_Bunker).size() >= 2) {
					Position BunkerDonthaveTurretPosition = MyUtil.GetMyBunkerDonthaveTurretPosition();
					if (BunkerDonthaveTurretPosition == null) {
						BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Missile_Turret, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
					} else {
						BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Missile_Turret, BunkerDonthaveTurretPosition.toTilePosition(), false);
					}
				}

				if (checkNeedToBuild(UnitType.Terran_Missile_Turret, 3) && MyVariable.getSelfUnit(UnitType.Terran_Engineering_Bay).size() >= 1 && MyVariable.getSelfUnit(UnitType.Terran_Bunker).size() >= 3) {
					Position BunkerDonthaveTurretPosition = MyUtil.GetMyBunkerDonthaveTurretPosition();
					if (BunkerDonthaveTurretPosition == null) {
						BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Missile_Turret, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
					} else {
						BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Missile_Turret, BunkerDonthaveTurretPosition.toTilePosition(), false);
					}
				}
				if (checkNeedToBuild(UnitType.Terran_Academy, 1) && MyVariable.getSelfUnit(UnitType.Terran_Bunker).size() >= 2 && MyVariable.getSelfUnit(UnitType.Terran_Missile_Turret).size() >= 2) {
					BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Academy, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
				}
			} else {
				if (checkNeedToBuild(UnitType.Terran_Academy, 1) && MyVariable.getSelfUnit(UnitType.Terran_Bunker).size() >= 2) {
					BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Academy, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
				}
			}

			if (checkNeedToBuild(UnitType.Terran_Comsat_Station, 1) && MyVariable.getSelfUnit(UnitType.Terran_Academy).size() >= 1) {
				// Comsat Station 건설
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Comsat_Station, false);
			}

			// Terran_Factory 건설
			if (checkNeedToBuild(UnitType.Terran_Factory, 2) && MyVariable.getSelfUnit(UnitType.Terran_Bunker).size() >= 2 && MyVariable.getSelfUnit(UnitType.Terran_Comsat_Station).size() >= 1)
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Factory, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
			if (checkNeedToBuild(UnitType.Terran_Factory, 3) && MyVariable.getSelfUnit(UnitType.Terran_Refinery).size() >= 1 && MyVariable.getSelfUnit(UnitType.Terran_Comsat_Station).size() >= 1)
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Factory, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);

			// Terran_Machine_Shop 건설
			if (checkNeedToBuild(UnitType.Terran_Machine_Shop, MyVariable.getSelfUnit(UnitType.Terran_Factory).size())) {
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Machine_Shop, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
			}

			// Terran_Armory 건설
			if (checkNeedToBuild(UnitType.Terran_Armory, 1) && MyVariable.getSelfUnit(UnitType.Terran_Factory).size() >= 2 && MyVariable.getSelfUnit(UnitType.Terran_Machine_Shop).size() >= 2 && MyVariable.getSelfUnit(UnitType.Terran_Siege_Tank_Siege_Mode).size() + MyVariable.getSelfUnit(UnitType.Terran_Siege_Tank_Tank_Mode).size() >= 4) {
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Armory, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
			}
		}
		// 저그이면
		else {
			if (MyVariable.isFullScaleAttackStarted) {
				if (checkNeedToBuild(UnitType.Terran_Command_Center, 2)) {
					BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Command_Center, BuildOrderItem.SeedPositionStrategy.FirstExpansionLocation, true);

				}
				if (checkNeedToBuild(UnitType.Terran_Refinery, 2)) {
					BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Refinery, BuildOrderItem.SeedPositionStrategy.FirstExpansionLocation, true);
				}
				// MyVariable.needTerran_Science_Vessel = true;
			}

			// 무탈이 보이면 터렛 건설
			if (MyVariable.findMutal) {
				if (checkNeedToBuild(UnitType.Terran_Missile_Turret, 5) && MyVariable.getSelfUnit(UnitType.Terran_Engineering_Bay).size() >= 1)
					BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Terran_Missile_Turret, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
			}
			// 러커가 보이면 터렛 건설
			if (MyVariable.findLucker) {
				if (checkNeedToBuild(UnitType.Terran_Missile_Turret, 1) && MyVariable.getSelfUnit(UnitType.Terran_Engineering_Bay).size() >= 1) {
					Position BunkerDonthaveTurretPosition = MyUtil.GetMyBunkerDonthaveTurretPosition();
					if (BunkerDonthaveTurretPosition == null) {
						BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Missile_Turret, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
					} else {
						BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Missile_Turret, BunkerDonthaveTurretPosition.toTilePosition(), false);
					}
				}
				MyVariable.needTerran_Science_Vessel = true;
			}

			if (checkNeedToBuild(UnitType.Terran_Bunker, 1) && MyVariable.getSelfUnit(UnitType.Terran_Marine).size() >= 2)
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Bunker, MyUtil.GetMyBunkerBuildPosition().toTilePosition(), false);

			if (checkNeedToBuild(UnitType.Terran_Bunker, 2) && MyVariable.getSelfUnit(UnitType.Terran_Marine).size() >= 6)
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Bunker, MyUtil.GetMyBunkerBuildPosition().toTilePosition(), false);

			if (checkNeedToBuild(UnitType.Terran_Refinery, 1) && MyVariable.getSelfUnit(UnitType.Terran_Bunker).size() >= 2)
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Refinery, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);

			if (checkNeedToBuild(UnitType.Terran_Barracks, 5) && MyBotModule.Broodwar.self().minerals() > 300) {
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Barracks, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
			}

			if (checkNeedToBuild(UnitType.Terran_Engineering_Bay, 1) && MyVariable.getSelfUnit(UnitType.Terran_Bunker).size() >= 1 && MyVariable.getSelfUnit(UnitType.Terran_Marine).size() >= 2) {
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Engineering_Bay, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
			}

			if (checkNeedToBuild(UnitType.Terran_Academy, 1) && MyVariable.getSelfUnit(UnitType.Terran_Bunker).size() >= 2) {
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Academy, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
			}

			if (checkNeedToBuild(UnitType.Terran_Comsat_Station, 1) && MyVariable.getSelfUnit(UnitType.Terran_Academy).size() >= 1) {
				// Comsat Station 건설
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Comsat_Station, false);
			}

			// Terran_Factory 건설
			if (checkNeedToBuild(UnitType.Terran_Factory, 2) && MyVariable.getSelfUnit(UnitType.Terran_Bunker).size() >= 2 && MyVariable.getSelfUnit(UnitType.Terran_Comsat_Station).size() >= 1 && MyVariable.getSelfUnit(UnitType.Terran_Barracks).size() >= 4)
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Factory, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);

			// Terran_Machine_Shop 건설
			if (checkNeedToBuild(UnitType.Terran_Machine_Shop, MyVariable.getSelfUnit(UnitType.Terran_Factory).size())) {
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Machine_Shop, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
			}

			// Terran_Armory 건설
			if (checkNeedToBuild(UnitType.Terran_Armory, 1) && MyVariable.getSelfUnit(UnitType.Terran_Factory).size() >= 2 && MyVariable.getSelfUnit(UnitType.Terran_Machine_Shop).size() >= 2 && MyVariable.getSelfUnit(UnitType.Terran_Siege_Tank_Siege_Mode).size() + MyVariable.getSelfUnit(UnitType.Terran_Siege_Tank_Tank_Mode).size() >= 4) {
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Armory, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
			}
		}
	}

	boolean checkNeedToBuild(UnitType unitType, int cnt) {
		boolean result = false;
		if (cnt > 0 && MyBotModule.Broodwar.self().allUnitCount(unitType) < cnt && BuildManager.Instance().getBuildQueue().getItemCount(unitType) == 0 && ConstructionManager.Instance().getConstructionQueueItemCount(unitType, null) == 0) {
			result = true;
		}
		return result;
	}

}
