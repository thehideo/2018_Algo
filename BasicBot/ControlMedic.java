import bwapi.Race;
import bwapi.Unit;
import bwapi.UnitType;

public class ControlMedic extends ControlAbstract {
	void actionMain(Unit unit, GroupAbstract groupAbstract) {
		if (InformationManager.Instance().enemyRace == Race.Protoss) {
			for (Unit Protoss_High_Templar : MyVariable.getEnemyUnit(UnitType.Protoss_High_Templar)) {
				if (Protoss_High_Templar.getEnergy() >= 74) {
					if (!MyVariable.mapMyRegion.contains(Protoss_High_Templar.getTilePosition())) {
						if (unit.getDistance(Protoss_High_Templar) < 12 * 32) {
							CommandUtil.move(unit, MyVariable.myStartLocation.toPosition());
						}
					}
				}
			}
			for (Unit Protoss_Reaver : MyVariable.getEnemyUnit(UnitType.Protoss_Reaver)) {
				if (!MyVariable.mapMyRegion.contains(Protoss_Reaver.getTilePosition())) {
					if (unit.getDistance(Protoss_Reaver) < 12 * 32) {
						CommandUtil.move(unit, MyVariable.myStartLocation.toPosition());
					}
				}
			}
		}
		CommandUtil.attackMove(unit, groupAbstract.getTargetPosition(unit));
	}
}
