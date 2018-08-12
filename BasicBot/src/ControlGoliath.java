import java.util.ArrayList;
import java.util.HashMap;

import bwapi.Position;
import bwapi.Race;
import bwapi.TilePosition;
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
					setSpecialAction(unit,0);
				}
			}
		}

		if (groupAbstract == GroupManager.instance().groupAttack) {
			if (ActionControlDropShip.needDropShipMember == true) {
				for (Unit dropship : MyVariable.getSelfUnit(UnitType.Terran_Dropship)) {
					if (dropship.isCompleted() && MyVariable.mapMyRegion.contains(dropship.getTilePosition())) {
						if (dropship.getSpaceRemaining() > 0 && ControlDropShip.mapPatrol.get(dropship.getID()).size()==0  && ControlDropShip.mapBackPath.get(dropship.getID()).size()==0  ) {
							CommandUtil.rightClick(unit, dropship);
						}
					}
				}
			}
		}

		if (groupAbstract == GroupManager.instance().groupPatrol) {
			patrolGroupAction(unit, groupAbstract);
		}
		
		// 적이 탱크가 없으면 일단 전진
		if (groupAbstract == GroupManager.instance().groupAttack && MyVariable.findTank == false && MyVariable.findBunker == false) {
			if (MyVariable.isFullScaleAttackStarted == false) {
				CommandUtil.attackMove(unit, InformationManager.Instance().getFirstExpansionLocation(MyBotModule.Broodwar.enemy()).getPoint());
			}
		}		

		Position target = groupAbstract.getTargetPosition(unit);
		CommandUtil.attackMove(unit, target);

	}

	static HashMap<Integer, ArrayList<TilePosition>> mapPatrol = new HashMap<Integer, ArrayList<TilePosition>>();

	void patrolGroupAction(Unit wraith, GroupAbstract groupAbstract) {
		if (!mapPatrol.containsKey(wraith.getID()) || mapPatrol.get(wraith.getID()).size() == 0) {
			mapPatrol.put(wraith.getID(), GroupWraith.makeNewList());
		}
		ArrayList<TilePosition> targetList = mapPatrol.get(wraith.getID());
		if (targetList.size() > 0) {
			TilePosition target = targetList.get(0);
			if (MyUtil.distanceTilePosition(target, wraith.getTilePosition()) <= 2) {
				targetList.remove(0);
			} else {
				CommandUtil.attackMove(wraith, target.toPosition());
			}
		}
	}
}
