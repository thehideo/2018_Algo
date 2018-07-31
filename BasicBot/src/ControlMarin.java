import bwapi.Unit;
import bwapi.UnitType;

public class ControlMarin extends ControlAbstract {
	void actionMain(Unit unit, Group groupAbstract) {
		if (unit.isAttacking() && unit.isStimmed() == false && 1.0 * unit.getHitPoints() / unit.getInitialHitPoints() > 0.5) {
			CommandUtil.useStim_Packs(unit);
			return;
		}

		if (ActionControlBunker.needMarinCnt > 0) {
			for (Unit bunker : MyVariable.getSelfUnit(UnitType.Terran_Bunker)) {
				if (bunker.getLoadedUnits().size() < 4) {
					CommandUtil.commandHash.add(unit);
					bunker.load(unit);
					break;
				}
			}
		}

		CommandUtil.attackMove(unit, groupAbstract.getTarget(unit));
	}
}
