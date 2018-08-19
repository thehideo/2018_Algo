import bwapi.Race;
import bwapi.TechType;
import bwapi.Unit;
import bwapi.UnitType;

public class ControlScienceVessel extends ControlAbstract {
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
			if (MyVariable.isFullScaleAttackStarted == true) {
				if (MyVariable.getEnemyUnit(UnitType.Protoss_Carrier).size() > 0) {
					Unit Protoss_Carrier = MyUtil.getMostCloseEnemyUnit(unit, MyVariable.getEnemyUnit(UnitType.Protoss_Carrier), 600);
					if (Protoss_Carrier != null) {
						if (unit.canUseTech(TechType.EMP_Shockwave, Protoss_Carrier)) {
							CommandUtil.useTech(unit, TechType.EMP_Shockwave, Protoss_Carrier);
							setSpecialAction(unit, 0);
						}
					}
				} else if (MyVariable.getEnemyUnit(UnitType.Protoss_High_Templar).size() > 0) {
					Unit Protoss_High_Templar = MyUtil.getMostCloseEnemyUnit(unit, MyVariable.getEnemyUnit(UnitType.Protoss_High_Templar), 600);
					if (Protoss_High_Templar != null) {
						if (unit.canUseTech(TechType.EMP_Shockwave, Protoss_High_Templar)) {
							CommandUtil.useTech(unit, TechType.EMP_Shockwave, Protoss_High_Templar);
							setSpecialAction(unit, 0);
						}
					}
				} else if (MyVariable.getEnemyUnit(UnitType.Protoss_Dragoon).size() > 0) {
					Unit Protoss_Dragoon = MyUtil.getMostCloseEnemyUnit(unit, MyVariable.getEnemyUnit(UnitType.Protoss_Dragoon), 600);
					if (Protoss_Dragoon != null) {
						if (unit.canUseTech(TechType.EMP_Shockwave, Protoss_Dragoon)) {
							CommandUtil.useTech(unit, TechType.EMP_Shockwave, Protoss_Dragoon);
							setSpecialAction(unit, 0);
						}
					}
				} else if (MyVariable.getEnemyUnit(UnitType.Protoss_Zealot).size() > 0) {
					Unit Protoss_Zealot = MyUtil.getMostCloseEnemyUnit(unit, MyVariable.getEnemyUnit(UnitType.Protoss_Zealot), 600);
					if (Protoss_Zealot != null) {
						if (unit.canUseTech(TechType.EMP_Shockwave, Protoss_Zealot)) {
							CommandUtil.useTech(unit, TechType.EMP_Shockwave, Protoss_Zealot);
							setSpecialAction(unit, 0);
						}
					}
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
