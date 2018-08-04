import bwapi.Unit;
import bwapi.UnitType;

public class ControlMarin extends ControlAbstract {
	void actionMain(Unit unit, GroupAbstract groupAbstract) {
		if (unit.isAttacking() && unit.isStimmed() == false && 1.0 * unit.getHitPoints() / unit.getInitialHitPoints() > 0.5) {
			CommandUtil.useStim_Packs(unit);
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

		CommandUtil.attackMove(unit, groupAbstract.getTargetPosition(unit));
	}
}
