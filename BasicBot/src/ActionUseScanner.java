import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import bwapi.TechType;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;
import bwta.BaseLocation;

public class ActionUseScanner implements ActionInterface {

	HashSet<TilePosition> scanTilePosition = new HashSet<TilePosition>();

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
			MyVariable.needTerran_Science_Vessel = true;
			if (unit.isAttacking() && unit.isCloaked() == true && unit.isDetected() == false) {
				useScanner_Sweep(unit);
			}
		}

		// 스캔이 많이 남으면 적 본진을 스캔함
		int ScanPoint = 200;
		if (MyVariable.findDarkTempler == false && MyVariable.findLucker == false) {
			ScanPoint = 100;
		} else {
			ScanPoint = 200;
		}
		for (Unit unit : MyVariable.getSelfUnit(UnitType.Terran_Comsat_Station)) {
			if (unit.getEnergy() > ScanPoint) {
				boolean use = false;
				for (TilePosition enemyBuildingPosition : MyVariable.enemyBuildingUnit) {
					if (!scanTilePosition.contains(enemyBuildingPosition)) {
						int X = enemyBuildingPosition.getX();
						int Y = enemyBuildingPosition.getY();
						for (int i = -4; i <= 4; i++) {
							for (int j = -4; j <= 4; j++) {
								scanTilePosition.add(new TilePosition(i + X, j + Y));
							}
						}
						useScanner_Sweep(unit, enemyBuildingPosition);
						use = true;
						break;
					}
				}
				if (use == false) {
					if (index < BWTA.getBaseLocations().size()) {
						BaseLocation baseLocation = listBaseLocation.get(index);
						TilePosition tilePosition = baseLocation.getTilePosition();
						if (!scanTilePosition.contains(tilePosition)) {
							useScanner_Sweep(unit, tilePosition);
						}
						index++;
					} else {
						index = 0;
					}
				}
			}
		}
	}

	// 스캐너 사용
	int beforeTime = 0;

	void useScanner_Sweep(Unit unit) {
		ArrayList<Unit> units = MyVariable.getSelfUnit(UnitType.Terran_Comsat_Station);
		for (int i = 0; i < units.size(); i++) {
			if (units.get(i).canUseTech(TechType.Scanner_Sweep, unit) && MyBotModule.Broodwar.getFrameCount() - beforeTime > 24 * 3) {
				units.get(i).useTech(TechType.Scanner_Sweep, unit);
				beforeTime = MyBotModule.Broodwar.getFrameCount();
				break;
			}
		}
	}

	void useScanner_Sweep(Unit scanner, TilePosition tilePosition) {
		if (scanner.canUseTech(TechType.Scanner_Sweep, tilePosition.toPosition())) {
			scanner.useTech(TechType.Scanner_Sweep, tilePosition.toPosition());
		}

	}

	// 스캐너 사용이 가능한지 확인
	void canUseScanner_Sweep(Unit unit) {
		ArrayList<Unit> units = MyVariable.getSelfUnit(UnitType.Terran_Comsat_Station);
		for (int i = 0; i < units.size(); i++) {
			if (units.get(i).canUseTech(TechType.Scanner_Sweep, unit)) {
				if (MyBotModule.Broodwar.getFrameCount() - beforeTime > 24 * 3) {
					units.get(i).useTech(TechType.Scanner_Sweep, unit);
					beforeTime = MyBotModule.Broodwar.getFrameCount();
				}
				break;
			} else {
				MyVariable.needTerran_Science_Vessel = true;
			}
		}
	}
}
