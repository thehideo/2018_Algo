import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import bwapi.Position;
import bwapi.Race;
import bwapi.TechType;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;
import bwta.BaseLocation;
import bwta.Chokepoint;

public class ActionUseScanner extends ActionControlAbstract {

	HashSet<TilePosition> scanTilePosition = new HashSet<TilePosition>();

	int useIndex = 0;
	int index = 0;

	List<BaseLocation> listBaseLocation = BWTA.getBaseLocations();

	@Override
	public void action() {
		for (Unit unit : MyVariable.getEnemyUnit(UnitType.Zerg_Lurker)) {
			MyVariable.needTerran_Science_Vessel = true;
			if (unit.isAttacking() && unit.isBurrowed() == true && unit.isDetected() == false) {
				useScanner_Sweep(unit);
			}
		}

		for (Unit unit : MyVariable.getEnemyUnit(UnitType.Protoss_Dark_Templar)) {
			MyVariable.needTerran_Science_Vessel = true;
			if (unit.isAttacking() && unit.isCloaked() == true && unit.isDetected() == false) {
				useScanner_Sweep(unit);
			}
		}

		for (Unit unit : MyVariable.getEnemyUnit(UnitType.Terran_Wraith)) {
			// MyVariable.needTerran_Science_Vessel = true;
			if (unit.isAttacking() && unit.isCloaked() == true && unit.isDetected() == false) {
				useScanner_Sweep(unit);
			}
		}

		// 스캔이 많이 남으면 적 본진을 스캔함
		int ScanPoint = 200;
		if (MyVariable.findDarkTempler == false && MyVariable.findLucker == false && InformationManager.Instance().enemyRace == Race.Protoss) {
			ScanPoint = 100;
		} 
		if (MyVariable.findWraith == false && InformationManager.Instance().enemyRace == Race.Terran) {
			ScanPoint = 100;
		}
		

		boolean use = false;
		use = false;
		for (Unit unit : MyVariable.getSelfUnit(UnitType.Terran_Comsat_Station)) {
			if (use == true) {
				break;
			}
			if (unit.getEnergy() >= ScanPoint) {
				// 적 확장 기지를 스캔함
				if (useIndex % 2 == 0) {
					Chokepoint bl = InformationManager.Instance().getFirstChokePoint(MyBotModule.Broodwar.enemy());
					if (bl != null) {
						if (useScanner_Sweep(bl.getPoint())) {
							use = true;
						}

					}
				}
				// 적 본진과 확장 중간을 스캔
				else if (useIndex % 2 == 1) {
					Chokepoint bl = InformationManager.Instance().getFirstChokePoint(MyBotModule.Broodwar.enemy());
					BaseLocation bl2 = InformationManager.Instance().getMainBaseLocation(MyBotModule.Broodwar.enemy());
					if (bl != null && bl2 != null) {
						if (useScanner_Sweep(new Position((bl.getPoint().getX() + bl2.getPoint().getX()) / 2, (bl.getPoint().getY() + bl2.getPoint().getY()) / 2))) {
							use = true;
						}
					}
				}
			}

			/*
			 * 
			 * // 적 본진을 스캔함 else if (useIndex % 2 == 1) { BaseLocation bl =
			 * InformationManager.Instance().getMainBaseLocation(MyBotModule.Broodwar.enemy(
			 * )); if (bl != null) { if (useScanner_Sweep(bl.getPoint())) { use = true; }
			 * 
			 * } }
			 */

			/*
			 * for (TilePosition enemyBuildingPosition : MyVariable.enemyBuildingUnit) { if
			 * (!scanTilePosition.contains(enemyBuildingPosition)) { int X =
			 * enemyBuildingPosition.getX(); int Y = enemyBuildingPosition.getY(); for (int
			 * i = -4; i <= 4; i++) { for (int j = -4; j <= 4; j++) {
			 * scanTilePosition.add(new TilePosition(i + X, j + Y)); } } if
			 * (useScanner_Sweep(enemyBuildingPosition.toPosition())) { use = true; } break;
			 * } } if (use == false) { if (index < BWTA.getBaseLocations().size()) {
			 * BaseLocation baseLocation = listBaseLocation.get(index); TilePosition
			 * tilePosition = baseLocation.getTilePosition(); if
			 * (!scanTilePosition.contains(tilePosition)) { if
			 * (useScanner_Sweep(tilePosition.toPosition())) { use = true; } } index++; }
			 * else { index = 0; } }
			 */

			if (use == true) {
				useIndex++;
			}
		}
	}

	// 스캐너 사용
	static int beforeTime = 0;

	public static void useScanner_Sweep(Unit unit) {
		ArrayList<Unit> units = MyVariable.getSelfUnit(UnitType.Terran_Comsat_Station);
		for (int i = 0; i < units.size(); i++) {
			if (units.get(i).canUseTech(TechType.Scanner_Sweep, unit) && MyBotModule.Broodwar.getFrameCount() - beforeTime > 24 * 3) {
				units.get(i).useTech(TechType.Scanner_Sweep, unit);
				beforeTime = MyBotModule.Broodwar.getFrameCount();
				break;
			}
		}
	}

	public static boolean useScanner_Sweep(Position position) {
		boolean use = false;
		ArrayList<Unit> units = MyVariable.getSelfUnit(UnitType.Terran_Comsat_Station);
		for (int i = 0; i < units.size(); i++) {
			if (units.get(i).canUseTechPosition(TechType.Scanner_Sweep, position) && MyBotModule.Broodwar.getFrameCount() - beforeTime > 24 * 3) {
				units.get(i).useTech(TechType.Scanner_Sweep, position);
				use = true;
				beforeTime = MyBotModule.Broodwar.getFrameCount();
				break;
			}
		}
		return use;
	}

	static void useScanner_Sweep(Unit scanner, TilePosition tilePosition) {
		if (scanner.canUseTechPosition(TechType.Scanner_Sweep, tilePosition.toPosition())) {
			scanner.useTech(TechType.Scanner_Sweep, tilePosition.toPosition());
		}

	}
}
