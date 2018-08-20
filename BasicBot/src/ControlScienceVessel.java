import bwapi.Race;
import bwapi.TechType;
import bwapi.Unit;
import bwapi.UnitType;

public class ControlScienceVessel extends ControlAbstract {
	// 스캐너 사용
	static int beforeTime = 0;

	void actionMain(Unit unit, GroupAbstract groupAbstract) {
		if (InformationManager.Instance().enemyRace == Race.Zerg) {
			if (MyVariable.getEnemyUnit(UnitType.Zerg_Mutalisk).size() > 0) {
				Unit mostCloseMutalisk = getMostCloseEnemyUnit(UnitType.Zerg_Mutalisk, unit);
				if (mostCloseMutalisk != null) {
					if (mostCloseMutalisk.getDistance(unit) < 1200 && unit.canUseTech(TechType.Irradiate, mostCloseMutalisk)) {
						CommandUtil.useTech(unit, TechType.Irradiate, mostCloseMutalisk);
						setSpecialAction(unit, 0);
					}
				}
			} else if (MyVariable.getEnemyUnit(UnitType.Zerg_Lurker).size() > 0) {
				Unit mostCloseLurker = getMostCloseEnemyUnit(UnitType.Zerg_Lurker, unit);
				if (mostCloseLurker != null) {
					if (mostCloseLurker.getDistance(unit) < 1200 && unit.canUseTech(TechType.Irradiate, mostCloseLurker)) {
						CommandUtil.useTech(unit, TechType.Irradiate, mostCloseLurker);
						setSpecialAction(unit, 0);
					}
				}
			}
		} else if (InformationManager.Instance().enemyRace == Race.Protoss) {
			if (MyBotModule.Broodwar.getFrameCount() - beforeTime > 24 * 2) {
				Unit target = null;
				if (MyVariable.getEnemyUnit(UnitType.Protoss_Carrier).size() > 0) {
					for (Unit Protoss_Carrier : MyVariable.getEnemyUnit(UnitType.Protoss_Carrier)) {
						if (Protoss_Carrier.getShields() > 50 && unit.getDistance(Protoss_Carrier) < 800 && unit.canUseTech(TechType.EMP_Shockwave, Protoss_Carrier)) {
							target = Protoss_Carrier;
							break;
						}
					}
				} else if (MyVariable.getEnemyUnit(UnitType.Protoss_High_Templar).size() > 0) {
					for (Unit Protoss_High_Templar : MyVariable.getEnemyUnit(UnitType.Protoss_High_Templar)) {
						if (Protoss_High_Templar.getEnergy() > 50 && unit.getDistance(Protoss_High_Templar) < 800 && unit.canUseTech(TechType.EMP_Shockwave, Protoss_High_Templar)) {
							target = Protoss_High_Templar;
							break;
						}
					}
				} else if (MyVariable.getEnemyUnit(UnitType.Protoss_Dragoon).size() > 0) {
					for (Unit Protoss_Dragoon : MyVariable.getEnemyUnit(UnitType.Protoss_Dragoon)) {
						if (Protoss_Dragoon.getShields() > 50 && unit.getDistance(Protoss_Dragoon) < 800 && unit.canUseTech(TechType.EMP_Shockwave, Protoss_Dragoon)) {
							target = Protoss_Dragoon;
							break;
						}
					}
				}
				if (target != null) {
					CommandUtil.useTech(unit, TechType.EMP_Shockwave, target);
					setSpecialAction(unit, 50);
					beforeTime = MyBotModule.Broodwar.getFrameCount();
				}
			}
		}
		CommandUtil.move(unit, groupAbstract.getTargetPosition(unit));
	}

	public static Unit getMostCloseEnemyUnit(UnitType unitType, Unit myUnit) {
		Unit mostCloseEnemyUnit = null;
		double minDistance = Double.MAX_VALUE;
		for (Unit enemyUnit : MyVariable.getEnemyUnit(unitType)) {
			double distance = MyUtil.distanceTilePosition(myUnit.getTilePosition(), enemyUnit.getTilePosition());
			if (minDistance > distance && enemyUnit.isIrradiated() == false) {
				minDistance = distance;
				mostCloseEnemyUnit = enemyUnit;
			}
		}
		return mostCloseEnemyUnit;
	}

}
