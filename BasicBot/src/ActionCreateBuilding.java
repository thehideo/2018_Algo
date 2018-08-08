import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bwapi.Position;
import bwapi.Race;
import bwapi.TechType;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import bwapi.UpgradeType;
import bwta.BWTA;
import bwta.BaseLocation;

public class ActionCreateBuilding extends ActionControlAbstract {

	@Override
	public void action() {
		if (MyVariable.isInitialBuildOrderFinished == true) {
			commonBuild();
			if (InformationManager.Instance().enemyRace == Race.Protoss) {
				protossBuild();
			} else if (InformationManager.Instance().enemyRace == Race.Terran) {
				terranBuild();
			} else {
				zergBuild();
			}
		}
	}

	void commonBuild() {
		// 베슬이 필요하면 바로 건설
		if (MyVariable.needTerran_Science_Vessel && InformationManager.Instance().enemyRace != Race.Protoss) {
			if (checkNeedToBuild(UnitType.Terran_Factory, 1) && MyVariable.getSelfUnit(UnitType.Terran_Barracks).size() >= 1 && MyVariable.getSelfUnit(UnitType.Terran_Refinery).size() >= 1)
				BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Terran_Factory, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
			if (checkNeedToBuild(UnitType.Terran_Starport, 1) && MyVariable.getSelfUnit(UnitType.Terran_Factory).size() >= 1)
				BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Terran_Starport, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
			if (checkNeedToBuild(UnitType.Terran_Control_Tower, 1) && MyVariable.getSelfUnit(UnitType.Terran_Starport).size() >= 1)
				BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Terran_Control_Tower, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
			if (checkNeedToBuild(UnitType.Terran_Science_Facility, 1) && MyVariable.getSelfUnit(UnitType.Terran_Starport).size() >= 1)
				BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Terran_Science_Facility, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
			if (checkNeedToBuild(UnitType.Terran_Science_Vessel, 1) && MyVariable.getSelfUnit(UnitType.Terran_Science_Facility).size() >= 1)
				BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Terran_Science_Vessel, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		}

		// 내가 유리하면 배틀크루즈 생산
		if (MyVariable.getSelfUnit(UnitType.Terran_Command_Center).size() >= 4) {
			if (checkNeedToBuild(UnitType.Terran_Factory, 1) && MyVariable.getSelfUnit(UnitType.Terran_Barracks).size() >= 1 && MyVariable.getSelfUnit(UnitType.Terran_Refinery).size() >= 1)
				BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Terran_Factory, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
			if (checkNeedToBuild(UnitType.Terran_Starport, 1) && MyVariable.getSelfUnit(UnitType.Terran_Factory).size() >= 1)
				BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Terran_Starport, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
			if (checkNeedToBuild(UnitType.Terran_Control_Tower, 1) && MyVariable.getSelfUnit(UnitType.Terran_Starport).size() >= 1)
				BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Terran_Control_Tower, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
			if (checkNeedToBuild(UnitType.Terran_Science_Facility, 1) && MyVariable.getSelfUnit(UnitType.Terran_Starport).size() >= 1)
				BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Terran_Science_Facility, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
			if (checkNeedToBuild(UnitType.Terran_Physics_Lab, 1) && MyVariable.getSelfUnit(UnitType.Terran_Science_Facility).size() >= 1)
				BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Terran_Physics_Lab, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		}

		// 7000 프레임 마다 확장을 하나씩 추가한다.
		int needCommandCount = MyBotModule.Broodwar.getFrameCount() / 7000;
		if (MyVariable.getSelfUnit(UnitType.Terran_Command_Center).size() < needCommandCount) {
			if (checkNeedToBuild(UnitType.Terran_Command_Center, needCommandCount)) {
				ArrayList<TilePosition> listTilePosition = new ArrayList<TilePosition>();
				List<BaseLocation> listBaseLocation = BWTA.getBaseLocations();
				BaseLocation bl1 = InformationManager.Instance().getMainBaseLocation(MyBotModule.Broodwar.self());
				BaseLocation bl3 = InformationManager.Instance().getMainBaseLocation(MyBotModule.Broodwar.enemy());
				BaseLocation bl4 = InformationManager.Instance().getFirstExpansionLocation(MyBotModule.Broodwar.enemy());
				if (bl3 != null && bl4 != null) {
					for (BaseLocation bl : listBaseLocation) {
						if (!bl.getTilePosition().equals(bl1.getTilePosition()) && !bl.getTilePosition().equals(bl3.getTilePosition()) && !bl.getTilePosition().equals(bl4.getTilePosition()) && !MyVariable.mapSelfMainBuilding.contains(bl.getTilePosition()) && !MyVariable.mapEnemyMainBuilding.contains(bl.getTilePosition()) && !MyVariable.enemyBuildingUnit.contains(bl.getTilePosition())) {
							listTilePosition.add(bl.getTilePosition());
						}
					}
					Collections.sort(listTilePosition, new ComparatorBaseLocationClose());
				}
				if (listTilePosition.size() > 0) {
					// 마인이 박혀있을 수 있어서 터렛을 먼저 건설한다.
					if (MyVariable.getSelfUnit(UnitType.Terran_Engineering_Bay).size() >= 1) {
						ArrayList<Unit> turrets = MyVariable.getSelfUnit(UnitType.Terran_Missile_Turret);
						boolean hasTurrets = false;
						for (Unit unit : turrets) {
							if (MyUtil.distanceTilePosition(unit.getTilePosition(), listTilePosition.get(0)) <= 7) {
								hasTurrets = true;
								break;
							}
						}
						if (hasTurrets == false && checkNeedToBuild(UnitType.Terran_Missile_Turret, turrets.size() + 1)) {
							BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Missile_Turret, listTilePosition.get(0), true);
						}
					}
					BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Command_Center, listTilePosition.get(0), true);
				}
			}
		}

		// 초반에 바로 가스를 지으면 안되기 때무에 12마리 보다 크다는 조건을 넣었음
		if (MyVariable.attackUnit.size() > 8) {
			// Command Center 주위에 가스를 건설할수 있으면 추가한다.
			for (Unit unit : MyVariable.getSelfUnit(UnitType.Terran_Command_Center)) {
				TilePosition tilePosition = ConstructionPlaceFinder.Instance().getRefineryPositionNear(unit.getTilePosition());
				if (tilePosition != null && !MyVariable.mapRefineryPosition.contains(tilePosition) && BuildManager.Instance().getBuildQueue().getItemCount(UnitType.Terran_Refinery) == 0 && ConstructionManager.Instance().getConstructionQueueItemCount(UnitType.Terran_Refinery, null) == 0) {
					BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Refinery, tilePosition, true);
				}
			}
		}

		// 적이 다 파괴된것 같은데 계속 게임지 지속될 때
		if (MyVariable.enemyBuildingUnit.size() == 0 && MyVariable.isFullScaleAttackStarted == true) {
			if (checkNeedToBuild(UnitType.Terran_Starport, 2))
				BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Terran_Starport, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		}
	}

	void buildForLockLown() {
		if (checkNeedToBuild(UnitType.Terran_Factory, 1) && MyVariable.getSelfUnit(UnitType.Terran_Barracks).size() >= 1 && MyVariable.getSelfUnit(UnitType.Terran_Refinery).size() >= 1)
			BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Terran_Factory, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		if (checkNeedToBuild(UnitType.Terran_Starport, 1) && MyVariable.getSelfUnit(UnitType.Terran_Factory).size() >= 1)
			BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Terran_Starport, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		if (checkNeedToBuild(UnitType.Terran_Control_Tower, 1) && MyVariable.getSelfUnit(UnitType.Terran_Starport).size() >= 1)
			BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Terran_Control_Tower, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		if (checkNeedToBuild(UnitType.Terran_Science_Facility, 1) && MyVariable.getSelfUnit(UnitType.Terran_Starport).size() >= 1)
			BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Terran_Science_Facility, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		if (checkNeedToBuild(UnitType.Terran_Covert_Ops, 1) && MyVariable.getSelfUnit(UnitType.Terran_Science_Facility).size() >= 1)
			BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Terran_Covert_Ops, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		for (Unit unit : MyVariable.getSelfUnit(UnitType.Terran_Covert_Ops)) {
			if (unit.canResearch(TechType.Lockdown)) {
				if (BuildManager.Instance().buildQueue.getItemCount(TechType.Lockdown) == 0) {
					{
						BuildManager.Instance().buildQueue.queueAsHighestPriority(TechType.Lockdown, true);
					}
				}
				break;
			}
			if (unit.canResearch(TechType.Personnel_Cloaking)) {
				if (BuildManager.Instance().buildQueue.getItemCount(TechType.Personnel_Cloaking) == 0) {
					{
						BuildManager.Instance().buildQueue.queueAsHighestPriority(TechType.Personnel_Cloaking, true);
					}
				}
			}
			if (unit.canUpgrade(UpgradeType.Ocular_Implants)) {
				if (BuildManager.Instance().buildQueue.getItemCount(UpgradeType.Ocular_Implants) == 0) {
					{
						BuildManager.Instance().buildQueue.queueAsHighestPriority(UpgradeType.Ocular_Implants, true);
					}
				}
			}
		}
	}

	void terranBuild() {
		if (MyVariable.isFullScaleAttackStarted || MyVariable.mapEnemyMainBuilding.size() >= 2) {
			if (checkNeedToBuild(UnitType.Terran_Factory, 3))
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Factory, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
			if (checkNeedToBuild(UnitType.Terran_Machine_Shop, 3))
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Machine_Shop, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
			if (checkNeedToBuild(UnitType.Terran_Command_Center, 2)) {
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Command_Center, BuildOrderItem.SeedPositionStrategy.FirstExpansionLocation, false);
			}
			if (checkNeedToBuild(UnitType.Terran_Comsat_Station, 2) && MyVariable.getSelfUnit(UnitType.Terran_Command_Center).size() >= 2 && MyVariable.getSelfUnit(UnitType.Terran_Academy).size() >= 1) {
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Comsat_Station, BuildOrderItem.SeedPositionStrategy.FirstExpansionLocation, false);
			}
			if (checkNeedToBuild(UnitType.Terran_Refinery, 2) && MyVariable.getSelfUnit(UnitType.Terran_Command_Center).size() >= 2) {
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Refinery, BuildOrderItem.SeedPositionStrategy.FirstExpansionLocation, false);
			}
		}

		if (checkNeedToBuild(UnitType.Terran_Refinery, 1))
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Refinery, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);

		// Terran_Factory 건설
		if (checkNeedToBuild(UnitType.Terran_Factory, 2) && MyVariable.getSelfUnit(UnitType.Terran_Refinery).size() >= 1)
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Factory, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);

		// Terran_Machine_Shop 건설
		if (checkNeedToBuild(UnitType.Terran_Machine_Shop, 1)) {
			BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Terran_Machine_Shop, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
		}

		// Terran_Machine_Shop 건설
		if (checkNeedToBuild(UnitType.Terran_Machine_Shop, MyVariable.getSelfUnit(UnitType.Terran_Factory).size()) && MyVariable.getSelfUnit(UnitType.Terran_Vulture).size() >= 4) {
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Machine_Shop, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
		}

		if (checkNeedToBuild(UnitType.Terran_Academy, 1) && MyUtil.GetMyTankCnt() >= 2) {
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Academy, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
		}

		if (checkNeedToBuild(UnitType.Terran_Comsat_Station, 1) && MyVariable.getSelfUnit(UnitType.Terran_Academy).size() >= 1) {
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Comsat_Station, false);
		}

		if (checkNeedToBuild(UnitType.Terran_Engineering_Bay, 1) && MyUtil.GetMyTankCnt() >= 4) {
			BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Terran_Engineering_Bay, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		}

		if (checkNeedToBuild(UnitType.Terran_Missile_Turret, 1) && MyVariable.getSelfUnit(UnitType.Terran_Engineering_Bay).size() >= 1) {
			Position BunkerDonthaveTurretPosition = MyUtil.GetMyBunkerDonthaveTurretPosition();
			if (BunkerDonthaveTurretPosition == null) {
				BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Terran_Missile_Turret, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
			} else {
				BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Terran_Missile_Turret, BunkerDonthaveTurretPosition.toTilePosition(), true);
			}
		}

		if (checkNeedToBuild(UnitType.Terran_Factory, 3) && MyVariable.getSelfUnit(UnitType.Terran_Refinery).size() >= 1 && MyVariable.getSelfUnit(UnitType.Terran_Comsat_Station).size() >= 1)
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Factory, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);

		if (checkNeedToBuild(UnitType.Terran_Starport, 1) && MyVariable.getSelfUnit(UnitType.Terran_Factory).size() >= 1 && MyUtil.GetMyTankCnt() >= 8)
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Starport, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);

		// Terran_Machine_Shop 건설
		if (checkNeedToBuild(UnitType.Terran_Control_Tower, MyVariable.getSelfUnit(UnitType.Terran_Starport).size())) {
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Control_Tower, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
		}

		// if (checkNeedToBuild(UnitType.Terran_Starport, 1) &&
		// MyVariable.getSelfUnit(UnitType.Terran_Factory).size() >= 3)
		// BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Terran_Starport,
		// BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		// if (checkNeedToBuild(UnitType.Terran_Control_Tower, 1) &&
		// MyVariable.getSelfUnit(UnitType.Terran_Starport).size() >= 1)
		// BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Terran_Control_Tower,
		// BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

		// Terran_Armory 건설
		if (checkNeedToBuild(UnitType.Terran_Armory, 1) && MyVariable.getSelfUnit(UnitType.Terran_Factory).size() >= 2 && MyVariable.getSelfUnit(UnitType.Terran_Machine_Shop).size() >= 2) {
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Armory, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
		}

		if (checkNeedToBuild(UnitType.Terran_Factory, 6) && MyVariable.getSelfUnit(UnitType.Terran_Refinery).size() >= 1 && MyBotModule.Broodwar.self().minerals() > 400) {
			TilePosition tp = null;
			for (Unit commandCenter : MyVariable.getSelfUnit(UnitType.Terran_Command_Center)) {
				if (commandCenter.getTilePosition() != MyVariable.myStartLocation) {
					tp = commandCenter.getTilePosition();
					break;
				}
			}
			if (tp == null) {
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Factory, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
			} else {
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Factory, tp, false);
			}
		}
	}

	void protossBuild() {

		if (MyVariable.isFullScaleAttackStarted || MyVariable.mapEnemyMainBuilding.size() >= 2) {
			if (checkNeedToBuild(UnitType.Terran_Factory, 3))
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Factory, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
			if (checkNeedToBuild(UnitType.Terran_Machine_Shop, 3))
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Machine_Shop, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
			if (checkNeedToBuild(UnitType.Terran_Command_Center, 2)) {
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Command_Center, BuildOrderItem.SeedPositionStrategy.FirstExpansionLocation, false);
			}
			if (checkNeedToBuild(UnitType.Terran_Comsat_Station, 2) && MyVariable.getSelfUnit(UnitType.Terran_Command_Center).size() >= 2) {
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Comsat_Station, BuildOrderItem.SeedPositionStrategy.FirstExpansionLocation, false);
			}
			if (checkNeedToBuild(UnitType.Terran_Refinery, 2) && MyVariable.getSelfUnit(UnitType.Terran_Command_Center).size() >= 2) {
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Refinery, BuildOrderItem.SeedPositionStrategy.FirstExpansionLocation, false);
			}
		}

		// 벙커 건설
		if (checkNeedToBuild(UnitType.Terran_Bunker, 1) && MyVariable.getSelfUnit(UnitType.Terran_Barracks).size() >= 1)
			BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Terran_Bunker, InformationManager.Instance().getSecondChokePoint(InformationManager.Instance().selfPlayer).getPoint().toTilePosition(), false);

		if (checkNeedToBuild(UnitType.Terran_Bunker, 2) && MyVariable.getSelfUnit(UnitType.Terran_Marine).size() >= 4)
			BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Terran_Bunker, InformationManager.Instance().getSecondChokePoint(InformationManager.Instance().selfPlayer).getPoint().toTilePosition(), false);

		if (checkNeedToBuild(UnitType.Terran_Bunker, 3) && MyVariable.getSelfUnit(UnitType.Terran_Marine).size() >= 8)
			BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Terran_Bunker, InformationManager.Instance().getSecondChokePoint(InformationManager.Instance().selfPlayer).getPoint().toTilePosition(), false);

		if (checkNeedToBuild(UnitType.Terran_Engineering_Bay, 1) && MyVariable.getSelfUnit(UnitType.Terran_Bunker).size() >= 2 && MyVariable.getSelfUnit(UnitType.Terran_Marine).size() >= 2) {
			BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Terran_Engineering_Bay, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		}

		if (checkNeedToBuild(UnitType.Terran_Missile_Turret, 1) && MyVariable.getSelfUnit(UnitType.Terran_Engineering_Bay).size() >= 1) {
			Position BunkerDonthaveTurretPosition = MyUtil.GetMyBunkerDonthaveTurretPosition();
			if (BunkerDonthaveTurretPosition == null) {
				BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Terran_Missile_Turret, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
			} else {
				BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Terran_Missile_Turret, BunkerDonthaveTurretPosition.toTilePosition(), true);
			}
		}

		if (checkNeedToBuild(UnitType.Terran_Refinery, 1) && MyVariable.getSelfUnit(UnitType.Terran_Bunker).size() >= 2)
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Refinery, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);

		if (checkNeedToBuild(UnitType.Terran_Academy, 1) && MyVariable.getSelfUnit(UnitType.Terran_Bunker).size() >= 1 && MyVariable.getSelfUnit(UnitType.Terran_Missile_Turret).size() >= 1) {
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Academy, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
		}

		if (checkNeedToBuild(UnitType.Terran_Comsat_Station, 1) && MyVariable.getSelfUnit(UnitType.Terran_Academy).size() >= 1) {
			// Comsat Station 건설
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Comsat_Station, false);
		}

		// Terran_Factory 건설
		if (checkNeedToBuild(UnitType.Terran_Factory, 2) && MyVariable.getSelfUnit(UnitType.Terran_Bunker).size() >= 1 && MyVariable.getSelfUnit(UnitType.Terran_Comsat_Station).size() >= 1)
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Factory, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);

		// Terran_Machine_Shop 건설
		if (checkNeedToBuild(UnitType.Terran_Machine_Shop, MyVariable.getSelfUnit(UnitType.Terran_Factory).size())) {
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Machine_Shop, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
		}

		if (MyVariable.findCarrier == true) {
			if (checkNeedToBuild(UnitType.Terran_Factory, 3) && MyVariable.getSelfUnit(UnitType.Terran_Refinery).size() >= 1 && MyVariable.getSelfUnit(UnitType.Terran_Comsat_Station).size() >= 1)
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Factory, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);

			if (checkNeedToBuild(UnitType.Terran_Factory, 4) && MyVariable.getSelfUnit(UnitType.Terran_Command_Center).size() >= 2 && MyVariable.getSelfUnit(UnitType.Terran_Refinery).size() >= 1 && MyVariable.getSelfUnit(UnitType.Terran_Comsat_Station).size() >= 1 && MyBotModule.Broodwar.self().minerals() > 400) {
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Factory, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
			}

			// Terran_Armory 건설
			if (checkNeedToBuild(UnitType.Terran_Armory, 1) && MyVariable.getSelfUnit(UnitType.Terran_Factory).size() >= 1 && MyVariable.getSelfUnit(UnitType.Terran_Machine_Shop).size() >= 1) {
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Armory, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
			}

			// if (checkNeedToBuild(UnitType.Terran_Factory, 6) &&
			// MyVariable.getSelfUnit(UnitType.Terran_Command_Center).size() >= 2 &&
			// MyVariable.getSelfUnit(UnitType.Terran_Refinery).size() >= 1 &&
			// MyVariable.getSelfUnit(UnitType.Terran_Comsat_Station).size() >= 1 &&
			// MyBotModule.Broodwar.self().minerals() > 400) {
			// BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Factory,
			// BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
			// }

		} else {
			if (checkNeedToBuild(UnitType.Terran_Factory, 3) && MyVariable.getSelfUnit(UnitType.Terran_Refinery).size() >= 1 && MyVariable.getSelfUnit(UnitType.Terran_Comsat_Station).size() >= 1)
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Factory, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);

			// Terran_Armory 건설
			if (checkNeedToBuild(UnitType.Terran_Armory, 1) && MyVariable.getSelfUnit(UnitType.Terran_Factory).size() >= 2 && MyVariable.getSelfUnit(UnitType.Terran_Machine_Shop).size() >= 2 && (MyVariable.getSelfUnit(UnitType.Terran_Siege_Tank_Siege_Mode).size() + MyVariable.getSelfUnit(UnitType.Terran_Siege_Tank_Tank_Mode).size() >= 4)) {
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Armory, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
			}

			// if (checkNeedToBuild(UnitType.Terran_Factory, 6) &&
			// MyVariable.getSelfUnit(UnitType.Terran_Command_Center).size() >= 2 &&
			// MyVariable.getSelfUnit(UnitType.Terran_Refinery).size() >= 1 &&
			// MyVariable.getSelfUnit(UnitType.Terran_Comsat_Station).size() >= 1 &&
			// MyBotModule.Broodwar.self().minerals() > 400) {
			// BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Factory,
			// BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
			// }
		}

		if (checkNeedToBuild(UnitType.Terran_Factory, 6) && MyVariable.getSelfUnit(UnitType.Terran_Refinery).size() >= 1 && MyBotModule.Broodwar.self().minerals() > 400) {
			TilePosition tp = null;
			for (Unit commandCenter : MyVariable.getSelfUnit(UnitType.Terran_Command_Center)) {
				if (commandCenter.getTilePosition() != MyVariable.myStartLocation) {
					tp = commandCenter.getTilePosition();
					break;
				}
			}
			if (tp == null) {
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Factory, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
			} else {
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Factory, tp, false);
			}
		}

	}

	void protossBuildBackup() {
		if (MyVariable.isFullScaleAttackStarted || MyVariable.mapEnemyMainBuilding.size() >= 2) {
			if (checkNeedToBuild(UnitType.Terran_Factory, 3))
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Factory, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
			if (checkNeedToBuild(UnitType.Terran_Machine_Shop, 3))
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Machine_Shop, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
			if (checkNeedToBuild(UnitType.Terran_Command_Center, 2)) {
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Command_Center, BuildOrderItem.SeedPositionStrategy.FirstExpansionLocation, false);
			}
			if (checkNeedToBuild(UnitType.Terran_Comsat_Station, 2) && MyVariable.getSelfUnit(UnitType.Terran_Command_Center).size() >= 2 && MyVariable.getSelfUnit(UnitType.Terran_Academy).size() >= 1) {
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Comsat_Station, BuildOrderItem.SeedPositionStrategy.FirstExpansionLocation, false);
			}
			if (checkNeedToBuild(UnitType.Terran_Refinery, 2) && MyVariable.getSelfUnit(UnitType.Terran_Command_Center).size() >= 2) {
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Refinery, BuildOrderItem.SeedPositionStrategy.FirstExpansionLocation, false);
			}
		}

		if (checkNeedToBuild(UnitType.Terran_Refinery, 1))
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Refinery, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);

		// Terran_Factory 건설
		if (checkNeedToBuild(UnitType.Terran_Factory, 2) && MyVariable.getSelfUnit(UnitType.Terran_Refinery).size() >= 1)
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Factory, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);

		// Terran_Machine_Shop 건설
		if (checkNeedToBuild(UnitType.Terran_Machine_Shop, MyVariable.getSelfUnit(UnitType.Terran_Factory).size())) {
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Machine_Shop, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
		}

		if (checkNeedToBuild(UnitType.Terran_Academy, 1) && (MyUtil.GetMyTankCnt() >= 2 || MyVariable.findDarkTempler == true)) {
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Academy, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
		}

		if (checkNeedToBuild(UnitType.Terran_Comsat_Station, 1) && MyVariable.getSelfUnit(UnitType.Terran_Academy).size() >= 1) {
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Comsat_Station, false);
		}

		if (checkNeedToBuild(UnitType.Terran_Engineering_Bay, 1) && MyVariable.findDarkTempler == true) {
			BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Terran_Engineering_Bay, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		}

		if (checkNeedToBuild(UnitType.Terran_Missile_Turret, 1) && MyVariable.getSelfUnit(UnitType.Terran_Engineering_Bay).size() >= 1) {
			Position BunkerDonthaveTurretPosition = MyUtil.GetMyBunkerDonthaveTurretPosition();
			if (BunkerDonthaveTurretPosition == null) {
				BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Terran_Missile_Turret, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
			} else {
				BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Terran_Missile_Turret, BunkerDonthaveTurretPosition.toTilePosition(), true);
			}
		}

		if (checkNeedToBuild(UnitType.Terran_Factory, 3) && MyVariable.getSelfUnit(UnitType.Terran_Refinery).size() >= 1 && MyVariable.getSelfUnit(UnitType.Terran_Comsat_Station).size() >= 1)
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Factory, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);

		// Terran_Armory 건설
		if (checkNeedToBuild(UnitType.Terran_Armory, 1) && MyVariable.getSelfUnit(UnitType.Terran_Factory).size() >= 2 && MyVariable.getSelfUnit(UnitType.Terran_Machine_Shop).size() >= 2 && (MyUtil.GetMyTankCnt() >= 4 || MyVariable.findCarrier)) {
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Armory, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
		}

		if (checkNeedToBuild(UnitType.Terran_Factory, 6) && MyVariable.getSelfUnit(UnitType.Terran_Command_Center).size() >= 2 && MyVariable.getSelfUnit(UnitType.Terran_Refinery).size() >= 1 && MyBotModule.Broodwar.self().minerals() > 400) {
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Factory, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
		}

	}

	void zergBuild() {
		if (MyVariable.isFullScaleAttackStarted) {
			if (checkNeedToBuild(UnitType.Terran_Command_Center, 2)) {
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Command_Center, BuildOrderItem.SeedPositionStrategy.FirstExpansionLocation, false);

			}
			if (checkNeedToBuild(UnitType.Terran_Refinery, 2) && MyVariable.getSelfUnit(UnitType.Terran_Command_Center).size() >= 2) {
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Refinery, BuildOrderItem.SeedPositionStrategy.FirstExpansionLocation, false);
			}

			if (checkNeedToBuild(UnitType.Terran_Comsat_Station, 2) && MyVariable.getSelfUnit(UnitType.Terran_Command_Center).size() >= 2) {
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Comsat_Station, BuildOrderItem.SeedPositionStrategy.FirstExpansionLocation, false);
			}
		}

		// 무탈이 보이면 터렛 건설
		if (MyVariable.findMutal) {
			if (checkNeedToBuild(UnitType.Terran_Missile_Turret, 5) && MyVariable.getSelfUnit(UnitType.Terran_Engineering_Bay).size() >= 1)
				BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Terran_Missile_Turret, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
		}

		if (checkNeedToBuild(UnitType.Terran_Bunker, 1) && MyVariable.getSelfUnit(UnitType.Terran_Marine).size() >= 2)
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Bunker, MyUtil.GetMyBunkerBuildPosition().toTilePosition(), false);

		if (checkNeedToBuild(UnitType.Terran_Refinery, 1) && MyVariable.getSelfUnit(UnitType.Terran_Bunker).size() >= 1)
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Refinery, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);

		if (checkNeedToBuild(UnitType.Terran_Engineering_Bay, 1) && MyVariable.getSelfUnit(UnitType.Terran_Bunker).size() >= 1 && MyVariable.getSelfUnit(UnitType.Terran_Marine).size() >= 2) {
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Engineering_Bay, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
		}

		if (checkNeedToBuild(UnitType.Terran_Academy, 1) && MyVariable.getSelfUnit(UnitType.Terran_Bunker).size() >= 1) {
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Academy, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
		}

		if (checkNeedToBuild(UnitType.Terran_Comsat_Station, 1) && MyVariable.getSelfUnit(UnitType.Terran_Academy).size() >= 1) {
			// Comsat Station 건설
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Comsat_Station, false);
		}

		if (checkNeedToBuild(UnitType.Terran_Barracks, 2)) {
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Barracks, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
		}

		if (checkNeedToBuild(UnitType.Terran_Barracks, 3) && MyVariable.getSelfUnit(UnitType.Terran_Comsat_Station).size() > 0) {
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Barracks, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
		}

		if (checkNeedToBuild(UnitType.Terran_Missile_Turret, 1) && MyVariable.getSelfUnit(UnitType.Terran_Engineering_Bay).size() >= 1) {
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Missile_Turret, MyUtil.GetMyBunkerPosition().toTilePosition(), false);
		}
		// 배럭 짓다가 싸이언스 베슬을 못뽑는 경우가 있어서 아래와 같은 조건을 만들었다.
		if (checkNeedToBuild(UnitType.Terran_Barracks, 5) && MyVariable.getSelfUnit(UnitType.Terran_Command_Center).size() >= 2 && (MyVariable.getSelfUnit(UnitType.Terran_Science_Vessel).size() > 0 || MyVariable.needTerran_Science_Vessel == false)) {
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Barracks, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
		}

		// Terran_Factory 건설
		if (checkNeedToBuild(UnitType.Terran_Factory, 2) && MyVariable.getSelfUnit(UnitType.Terran_Comsat_Station).size() >= 1 && MyVariable.getSelfUnit(UnitType.Terran_Barracks).size() >= 3)
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Factory, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);

		// Terran_Machine_Shop 건설
		if (checkNeedToBuild(UnitType.Terran_Machine_Shop, MyVariable.getSelfUnit(UnitType.Terran_Factory).size())) {
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Machine_Shop, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
		}

		if (MyVariable.findMutal) {
			// Terran_Armory 건설
			if (checkNeedToBuild(UnitType.Terran_Armory, 1) && MyVariable.getSelfUnit(UnitType.Terran_Factory).size() >= 1 && MyVariable.getSelfUnit(UnitType.Terran_Machine_Shop).size() >= 1 && MyVariable.getSelfUnit(UnitType.Terran_Siege_Tank_Siege_Mode).size() + MyVariable.getSelfUnit(UnitType.Terran_Siege_Tank_Tank_Mode).size() >= 4) {
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

	void terranBuildBackup() {
		if (MyVariable.isFullScaleAttackStarted || MyVariable.mapEnemyMainBuilding.size() >= 2) {
			if (checkNeedToBuild(UnitType.Terran_Factory, 3))
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Factory, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
			if (checkNeedToBuild(UnitType.Terran_Machine_Shop, 3))
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Machine_Shop, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
			if (checkNeedToBuild(UnitType.Terran_Command_Center, 2)) {
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Command_Center, BuildOrderItem.SeedPositionStrategy.FirstExpansionLocation, false);
			}
			if (checkNeedToBuild(UnitType.Terran_Comsat_Station, 2) && MyVariable.getSelfUnit(UnitType.Terran_Command_Center).size() >= 2) {
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Comsat_Station, BuildOrderItem.SeedPositionStrategy.FirstExpansionLocation, false);
			}
			if (checkNeedToBuild(UnitType.Terran_Refinery, 2) && MyVariable.getSelfUnit(UnitType.Terran_Command_Center).size() >= 2) {
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Refinery, BuildOrderItem.SeedPositionStrategy.FirstExpansionLocation, false);
			}
		}

		// 벙커 건설
		if (checkNeedToBuild(UnitType.Terran_Bunker, 1) && MyVariable.getSelfUnit(UnitType.Terran_Marine).size() >= 2)
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Bunker, MyUtil.GetMyBunkerBuildPosition().toTilePosition(), false);

		if (checkNeedToBuild(UnitType.Terran_Bunker, 2) && MyVariable.getSelfUnit(UnitType.Terran_Marine).size() >= 6)
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Bunker, MyUtil.GetMyBunkerBuildPosition().toTilePosition(), false);

		if (checkNeedToBuild(UnitType.Terran_Engineering_Bay, 1) && MyVariable.getSelfUnit(UnitType.Terran_Bunker).size() >= 1 && MyVariable.getSelfUnit(UnitType.Terran_Marine).size() >= 2) {
			BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Terran_Engineering_Bay, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		}

		if (checkNeedToBuild(UnitType.Terran_Missile_Turret, 2) && MyVariable.getSelfUnit(UnitType.Terran_Engineering_Bay).size() >= 1 && MyVariable.findWraith == true) {
			Position BunkerDonthaveTurretPosition = MyUtil.GetMyBunkerDonthaveTurretPosition();
			if (BunkerDonthaveTurretPosition == null) {
				BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Terran_Missile_Turret, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
			} else {
				BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Terran_Missile_Turret, BunkerDonthaveTurretPosition.toTilePosition(), true);
			}
		}

		if (checkNeedToBuild(UnitType.Terran_Refinery, 1) && MyVariable.getSelfUnit(UnitType.Terran_Bunker).size() >= 1)
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Refinery, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);

		if (checkNeedToBuild(UnitType.Terran_Academy, 1) && MyVariable.getSelfUnit(UnitType.Terran_Bunker).size() >= 1) {
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Academy, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
		}

		if (checkNeedToBuild(UnitType.Terran_Comsat_Station, 1) && MyVariable.getSelfUnit(UnitType.Terran_Academy).size() >= 1) {
			// Comsat Station 건설
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Comsat_Station, false);
		}

		// Terran_Factory 건설
		if (checkNeedToBuild(UnitType.Terran_Factory, 2) && MyVariable.getSelfUnit(UnitType.Terran_Bunker).size() >= 1 && MyVariable.getSelfUnit(UnitType.Terran_Comsat_Station).size() >= 1)
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Factory, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);

		// Terran_Machine_Shop 건설
		if (checkNeedToBuild(UnitType.Terran_Machine_Shop, MyVariable.getSelfUnit(UnitType.Terran_Factory).size())) {
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Machine_Shop, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
		}

		if (MyVariable.findCarrier == true) {

			buildForLockLown();

			if (checkNeedToBuild(UnitType.Terran_Factory, 3) && MyVariable.getSelfUnit(UnitType.Terran_Refinery).size() >= 1 && MyVariable.getSelfUnit(UnitType.Terran_Comsat_Station).size() >= 1)
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Factory, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);

			if (checkNeedToBuild(UnitType.Terran_Factory, 4) && MyVariable.getSelfUnit(UnitType.Terran_Command_Center).size() >= 2 && MyVariable.getSelfUnit(UnitType.Terran_Refinery).size() >= 1 && MyVariable.getSelfUnit(UnitType.Terran_Comsat_Station).size() >= 1 && MyBotModule.Broodwar.self().minerals() > 400) {
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Factory, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
			}

			// Terran_Armory 건설
			if (checkNeedToBuild(UnitType.Terran_Armory, 1) && MyVariable.getSelfUnit(UnitType.Terran_Factory).size() >= 1 && MyVariable.getSelfUnit(UnitType.Terran_Machine_Shop).size() >= 1) {
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Armory, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
			}

		} else {
			if (checkNeedToBuild(UnitType.Terran_Factory, 3) && MyVariable.getSelfUnit(UnitType.Terran_Refinery).size() >= 1 && MyVariable.getSelfUnit(UnitType.Terran_Comsat_Station).size() >= 1)
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Factory, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);

			// Terran_Armory 건설
			if (checkNeedToBuild(UnitType.Terran_Armory, 1) && MyVariable.getSelfUnit(UnitType.Terran_Factory).size() >= 2 && MyVariable.getSelfUnit(UnitType.Terran_Machine_Shop).size() >= 2 && (MyVariable.getSelfUnit(UnitType.Terran_Siege_Tank_Siege_Mode).size() + MyVariable.getSelfUnit(UnitType.Terran_Siege_Tank_Tank_Mode).size() >= 4)) {
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Armory, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
			}

			if (checkNeedToBuild(UnitType.Terran_Factory, 6) && MyVariable.getSelfUnit(UnitType.Terran_Command_Center).size() >= 2 && MyVariable.getSelfUnit(UnitType.Terran_Refinery).size() >= 1 && MyVariable.getSelfUnit(UnitType.Terran_Comsat_Station).size() >= 1 && MyBotModule.Broodwar.self().minerals() > 400) {
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Factory, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
			}
		}
	}

}
