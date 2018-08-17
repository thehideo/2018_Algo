import java.util.Iterator;

import bwapi.Race;
import bwapi.TechType;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;

public class ControlTank extends ControlAbstract {

	public static final int SIEGE_MODE_MIN_RANGE = UnitType.Terran_Siege_Tank_Siege_Mode.groundWeapon().minRange(); // 64
	public static final int SIEGE_MODE_MAX_RANGE = UnitType.Terran_Siege_Tank_Siege_Mode.groundWeapon().maxRange(); // 384

	boolean needSiege(Unit unit, GroupAbstract groupAbstract) {
		boolean result = false;
		// 방어 모드이면 시즈 모드로 대기

		if (GroupManager.instance().groupAttack == groupAbstract && InformationManager.Instance().enemyRace == Race.Terran && MyBotModule.Broodwar.self().hasResearched(TechType.Tank_Siege_Mode)) {
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
			Iterator<Integer> tankIDs = MyVariable.mapTankPosition.keySet().iterator();
			while (tankIDs.hasNext()) {
				Integer tankID = tankIDs.next();
				TilePosition tp = MyVariable.mapTankPosition.get(tankID);
				if (MyUtil.distancePosition(unit.getPosition(), tp.toPosition()) <= SIEGE_MODE_MAX_RANGE + 64) {
					result = true;
					break;
				}
			}
			for (Unit enemyUnit : MyVariable.enemyGroundUnit) {
				int distance = unit.getDistance(enemyUnit);
				// 적 유닛이 시즈 탱크이면 좀더 멀리 대기 한다.
				if (enemyUnit.getType() == UnitType.Terran_Siege_Tank_Siege_Mode && distance <= SIEGE_MODE_MAX_RANGE + 64) {
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
		boolean need = needSiege(unit, groupAbstract);
		if (need == false && unit.isSieged()) {
			CommandUtil.unsiege(unit);
		}
		if (need == true && !unit.isSieged()) {
			CommandUtil.siege(unit);
			setSpecialAction(unit, 50);
		}
		CommandUtil.attackMove(unit, groupAbstract.getTargetPosition(unit));
	}
}
