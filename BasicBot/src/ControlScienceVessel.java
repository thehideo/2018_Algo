import bwapi.TechType;
import bwapi.Unit;
import bwapi.UnitType;

public class ControlScienceVessel extends ControlAbstract {
	void actionMain(Unit unit, GroupAbstract groupAbstract) {
		if (MyVariable.getEnemyUnit(UnitType.Zerg_Mutalisk).size() > 0) {
			Unit mostCloseMutalisk = getMostCloseEnemyUnit(UnitType.Zerg_Mutalisk, unit);
			if (mostCloseMutalisk != null) {
				if (mostCloseMutalisk.getDistance(unit) < 1200 && unit.canUseTech(TechType.Irradiate, mostCloseMutalisk)) {
					CommandUtil.useTech(unit, TechType.Irradiate, mostCloseMutalisk);
					setSpecialAction(unit, 0);
				}
			}
		}else if (MyVariable.getEnemyUnit(UnitType.Zerg_Lurker).size() > 0) {
			Unit mostCloseLurker = getMostCloseEnemyUnit(UnitType.Zerg_Lurker, unit);
			if (mostCloseLurker != null) {
				if (mostCloseLurker.getDistance(unit) < 1200 && unit.canUseTech(TechType.Irradiate, mostCloseLurker)) {
					CommandUtil.useTech(unit, TechType.Irradiate, mostCloseLurker);
					setSpecialAction(unit, 0);
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
