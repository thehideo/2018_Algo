import bwapi.Position;
import bwapi.Race;
import bwapi.Unit;
import bwapi.UnitType;

public class ControlGoliath extends ControlAbstract {

	public static final int SIEGE_MODE_MAX_RANGE = UnitType.Terran_Siege_Tank_Siege_Mode.groundWeapon().maxRange(); // 384

	void actionMain(Unit unit, GroupAbstract groupAbstract) {

		// 주위에 캐리어가 있고, 각 골리앗 마다 가장 가까운 녀석을 공격한다.
		if (MyVariable.getEnemyUnit(UnitType.Protoss_Carrier).size() > 0) {
			Unit mostCloseCarrier = MyUtil.getMostCloseEnemyUnit(UnitType.Protoss_Carrier, unit);
			if (mostCloseCarrier != null) {
				if (mostCloseCarrier.getDistance(unit) < 1200) {
					CommandUtil.attackUnit(unit, mostCloseCarrier);
					setSpecialAction(unit);
				}
			}
		}
		
	

		if (MyVariable.isFullScaleAttackStarted == false && GroupManager.instance().groupAttack == groupAbstract && InformationManager.Instance().enemyRace == Race.Terran) {
			if (MyUtil.getSaveTilePosition(13) != null && unit.getDistance(MyUtil.getSaveTilePosition(13).toPosition()) <= SIEGE_MODE_MAX_RANGE + 1) {
				CommandUtil.move(unit, MyVariable.myStartLocation.toPosition());
			}		
			
			if (MyVariable.getEnemyUnit(UnitType.Terran_Wraith).size() > 0) {
				Unit mostCloseCarrier = MyUtil.getMostCloseEnemyUnit(UnitType.Terran_Wraith, unit);
				if (mostCloseCarrier != null) {
					if (mostCloseCarrier.getDistance(unit) < 32*20) {
						CommandUtil.attackMove(unit, mostCloseCarrier.getPosition());
						//setSpecialAction(unit);
					}
				}
			}
		}

		Position target = groupAbstract.getTargetPosition(unit);
		CommandUtil.attackMove(unit, target);

	}
}
