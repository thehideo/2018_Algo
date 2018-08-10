import bwapi.Race;
import bwapi.TechType;
import bwapi.Unit;
import bwapi.UnitType;

public class ControlTank extends ControlAbstract {

	public static final int SIEGE_MODE_MIN_RANGE = UnitType.Terran_Siege_Tank_Siege_Mode.groundWeapon().minRange(); // 64
	public static final int SIEGE_MODE_MAX_RANGE = UnitType.Terran_Siege_Tank_Siege_Mode.groundWeapon().maxRange(); // 384

	boolean needSiege(Unit unit) {
		boolean result = false;
		// 방어 모드이면 시즈 모드로 대기

		if (InformationManager.Instance().enemyRace == Race.Terran && MyBotModule.Broodwar.self().hasResearched(TechType.Tank_Siege_Mode)) {
			if (MyVariable.isFullScaleAttackStarted == false) {
				if (unit.getDistance(MyUtil.getSaveTilePosition(13).toPosition()) <= SIEGE_MODE_MAX_RANGE) {
					result = true;
				}
			}
		} else {
			if (unit.getDistance(MyUtil.getSaveTilePosition(0).toPosition()) <= 2 * 32 && MyVariable.isFullScaleAttackStarted == false) {
				result = true;
			}
		}

		if (result == false) {
			for (Unit enemyUnit : MyVariable.enemyGroundUnit) {
				int distance = unit.getDistance(enemyUnit);
				// 적 유닛이 시즈 탱크이면 좀더 멀리 대기 한다.
				if (enemyUnit.getType() == UnitType.Terran_Siege_Tank_Siege_Mode && distance <= SIEGE_MODE_MAX_RANGE + 32) {
					result = true;
					break;
				} else if (unit.canAttack(enemyUnit) && distance >= SIEGE_MODE_MIN_RANGE && distance <= SIEGE_MODE_MAX_RANGE) {
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
