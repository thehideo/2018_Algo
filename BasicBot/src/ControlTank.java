import bwapi.Race;
import bwapi.Unit;
import bwapi.UnitType;

public class ControlTank extends ControlAbstract {

	public static final int SIEGE_MODE_MIN_RANGE = UnitType.Terran_Siege_Tank_Siege_Mode.groundWeapon().minRange(); // 64
	public static final int SIEGE_MODE_MAX_RANGE = UnitType.Terran_Siege_Tank_Siege_Mode.groundWeapon().maxRange(); // 384

	boolean needSiege(Unit unit) {
		boolean result = false;
		// 방어 모드이면 시즈 모드로 대기

		if (InformationManager.Instance().enemyRace == Race.Terran) {
			if (MyVariable.isFullScaleAttackStarted == false && MyUtil.indexToGo >= 2) {
				if (unit.getDistance(MyUtil.getSaveTilePosition(12).toPosition()) <= SIEGE_MODE_MAX_RANGE) {
					result = true;
				}
			}
		} else {
			if (unit.getDistance(MyUtil.getSaveTilePosition(0).toPosition()) <= 5) {
				result = true;
			}
		}

		if (result == false) {
			for (Unit enemyUnit : MyVariable.enemyGroundUnit) {
				int distance = unit.getDistance(enemyUnit);
				if (unit.canAttack(enemyUnit) && distance >= SIEGE_MODE_MIN_RANGE && distance <= SIEGE_MODE_MAX_RANGE) {
					result = true;
					break;
				}
			}
		}
		return result;
	}

	public void actionMain(Unit unit, GroupAbstract groupAbstract) {
		if (needSiege(unit) == false && unit.isSieged()) {
			CommandUtil.unsiege(unit);

		}
		if (needSiege(unit) == true && !unit.isSieged()) {
			CommandUtil.siege(unit);

		}
		CommandUtil.attackMove(unit, groupAbstract.getTargetPosition(unit));
	}
}
