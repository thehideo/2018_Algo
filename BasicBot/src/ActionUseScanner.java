import java.util.ArrayList;

import bwapi.TechType;
import bwapi.Unit;
import bwapi.UnitType;

public class ActionUseScanner implements ActionInterface {

	@Override
	public void action() {
		if (MyVariable.mapEnemyUnit.containsKey(UnitType.Zerg_Lurker)) {
			for (Unit unit : MyVariable.mapEnemyUnit.get(UnitType.Zerg_Lurker)) {
				if (unit.isAttacking()) {
					useScanner_Sweep(unit);
				}
			}
		}
		if (MyVariable.mapEnemyUnit.containsKey(UnitType.Protoss_Dark_Templar)) {
			for (Unit unit : MyVariable.mapEnemyUnit.get(UnitType.Protoss_Dark_Templar)) {
				if (unit.isAttacking()) {
					useScanner_Sweep(unit);
				}
			}
		}
		if (MyVariable.mapEnemyUnit.containsKey(UnitType.Terran_Wraith)) {
			for (Unit unit : MyVariable.mapEnemyUnit.get(UnitType.Terran_Wraith)) {
				if (unit.isAttacking()) {
					useScanner_Sweep(unit);
				}
			}
		}
	}

	// 스캐너 사용
	int beforeTime = 0;
	void useScanner_Sweep(Unit unit) {
		if (MyVariable.mapSelfUnit.containsKey(UnitType.Terran_Comsat_Station)) {
			ArrayList<Unit> units = MyVariable.mapSelfUnit.get(UnitType.Terran_Comsat_Station);
			for (int i = 0; i < units.size(); i++) {
				if (units.get(i).canUseTech(TechType.Scanner_Sweep, unit) && MyBotModule.Broodwar.getFrameCount() - beforeTime > 24 * 3) {
					units.get(i).useTech(TechType.Scanner_Sweep, unit);
					beforeTime = MyBotModule.Broodwar.getFrameCount();
					break;
				}
			}
		}
	}

	// 스캐너 사용이 가능한지 확인
	void canUseScanner_Sweep(Unit unit) {
		if (MyVariable.mapSelfUnit.containsKey(UnitType.Terran_Comsat_Station)) {
			ArrayList<Unit> units = MyVariable.mapSelfUnit.get(UnitType.Terran_Comsat_Station);
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
}
