import bwapi.Race;
import bwapi.Unit;
import bwapi.UnitType;

public class ControlMarin extends ControlAbstract {
	void actionMain(Unit unit, GroupAbstract groupAbstract) {
		if (unit.isAttacking() && unit.isStimmed() == false && 1.0 * unit.getHitPoints() / unit.getInitialHitPoints() > 0.5) {
			CommandUtil.useStim_Packs(unit);
			setSpecialAction(unit, 0);
			return;
		}

		if (groupAbstract == GroupManager.instance().groupAttack && ActionControlBunker.needMarinCnt > 0) {
			for (Unit bunker : MyVariable.getSelfUnit(UnitType.Terran_Bunker)) {
				if (bunker.getLoadedUnits().size() < 4) {
					CommandUtil.commandHash.add(unit);
					unit.rightClick(bunker);
					break;
				}
			}
		}

		if (InformationManager.Instance().enemyRace == Race.Terran) {
			if (MyVariable.isFullScaleAttackStarted == false) {
				if (MyVariable.enemyStartLocation != null) {
					CommandUtil.attackMove(unit, MyUtil.getSaveTilePosition(6).toPosition());
				}
			}
		}

		CommandUtil.attackMove(unit, groupAbstract.getTargetPosition(unit));
	}
}
