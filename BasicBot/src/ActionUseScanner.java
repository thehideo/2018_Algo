import java.util.ArrayList;
import java.util.HashSet;

import bwapi.TechType;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;

public class ActionUseScanner implements ActionInterface {

	HashSet<TilePosition> scanTilePosition = new HashSet<TilePosition>();

	@Override
	public void action() {

		for (Unit unit : MyVariable.getEnemyUnit(UnitType.Zerg_Lurker)) {
			MyVariable.needTerran_Science_Vessel = true;
			if (unit.isAttacking() && unit.isBurrowed() == true) {
				useScanner_Sweep(unit);
			}
		}

		for (Unit unit : MyVariable.getEnemyUnit(UnitType.Protoss_Dark_Templar)) {
			MyVariable.needTerran_Science_Vessel = true;
			if (unit.isAttacking() && unit.isCloaked() == true) {
				useScanner_Sweep(unit);
			}
		}

		for (Unit unit : MyVariable.getEnemyUnit(UnitType.Terran_Wraith)) {
			MyVariable.needTerran_Science_Vessel = true;
			if (unit.isAttacking() && unit.isCloaked() == true) {
				useScanner_Sweep(unit);
			}
		}

		if (MyVariable.findDarkTempler == false) {
			for (Unit unit : MyVariable.getSelfUnit(UnitType.Terran_Comsat_Station)) {
				if (unit.getEnergy() > 150) {
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
							break;
						}
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
