import bwapi.Race;
import bwapi.Unit;
import bwapi.UnitType;

public class ControlFireBat extends ControlAbstract {
	void actionMain(Unit unit, GroupAbstract groupAbstract) {
		if (unit.isAttacking() && unit.isStimmed() == false && 1.0 * unit.getHitPoints() / unit.getInitialHitPoints() > 0.5) {
			CommandUtil.useStim_Packs(unit);
			setSpecialAction(unit, 0);
			return;
		}

		if (InformationManager.Instance().enemyRace == Race.Zerg) {
			for (Unit Zerg_Lurker : MyVariable.getEnemyUnit(UnitType.Zerg_Lurker)) {
				if (MyUtil.distancePosition(unit.getPosition(), MyVariable.myStartLocation.toPosition()) < MyUtil.distancePosition(Zerg_Lurker.getPosition(), MyVariable.myStartLocation.toPosition())) {
					if (Zerg_Lurker.isBurrowed() == true) {
						if (unit.getDistance(Zerg_Lurker) < 6 * 32) { // 러커 사거리 6
							CommandUtil.move(unit, MyVariable.myStartLocation.toPosition());
							return;
						}
					}
				}
			}
		}

		CommandUtil.attackMove(unit, groupAbstract.getTargetPosition(unit));
	}
}
