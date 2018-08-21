import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;

public class ControlGoliath extends ControlAbstract {

	public static final int SIEGE_MODE_MIN_RANGE = UnitType.Terran_Siege_Tank_Siege_Mode.groundWeapon().minRange(); // 64
	public static final int SIEGE_MODE_MAX_RANGE = UnitType.Terran_Siege_Tank_Siege_Mode.groundWeapon().maxRange(); // 384

	void actionMain(Unit unit, GroupAbstract groupAbstract) {
		// 어텍 그룹일 때는 탱크를 피해서 움직인다.
		// if (groupAbstract == GroupManager.instance().groupAttack) {
		if (MyUtil.GetMyTankCnt() >= 1) {
			Iterator<Integer> tankIDs = MyVariable.mapTankPosition.keySet().iterator();
			while (tankIDs.hasNext()) {
				Integer TankID = tankIDs.next();
				if (MyUtil.distancePosition(unit.getPosition(), MyVariable.mapTankPosition.get(TankID)) <= 384 + 32 * 4) {
					if (MyBotModule.Broodwar.getFrameCount() % 2 == 0) {
						CommandUtil.move(unit, MyVariable.myStartLocation.toPosition());
					} else {
						CommandUtil.move(unit, MyVariable.myFirstchokePoint.toPosition());
					}
					return;
				}
			}
		}
		// }

		// 주위에 캐리어가 있고, 각 골리앗 마다 가장 가까운 녀석을 공격한다.
		if (MyVariable.getEnemyUnit(UnitType.Protoss_Carrier).size() > 0) {
			Unit mostCloseCarrier = MyUtil.getMostCloseEnemyUnit(UnitType.Protoss_Carrier, unit);
			if (mostCloseCarrier != null) {
				if (mostCloseCarrier.getDistance(unit) < 1200) {
					CommandUtil.attackUnit(unit, mostCloseCarrier);
					setSpecialAction(unit, 0);
				}
			}
		}

		// 드랍 멤버가 필요한 경우 드랍쉽으로 이동
		if (groupAbstract == GroupManager.instance().groupAttack) {
			if (ActionControlDropShip.needDropShipMember == true) {
				for (Unit dropship : MyVariable.getSelfUnit(UnitType.Terran_Dropship)) {
					if (dropship.isCompleted() && MyVariable.mapMyRegion.contains(dropship.getTilePosition())) {
						if (dropship.getSpaceRemaining() > 0 && ControlDropShip.mapPatrol.get(dropship.getID()).size() == 0 && ControlDropShip.mapBackPath.get(dropship.getID()).size() == 0) {
							CommandUtil.rightClick(unit, dropship);
						}
					}
				}
			}
		}

		if (groupAbstract == GroupManager.instance().groupPatrol) {
			patrolGroupAction(unit, groupAbstract);
		}

		Position target = groupAbstract.getTargetPosition(unit);
		CommandUtil.attackMove(unit, target);

	}

	static HashMap<Integer, ArrayList<TilePosition>> mapPatrol = new HashMap<Integer, ArrayList<TilePosition>>();

	void patrolGroupAction(Unit unit, GroupAbstract groupAbstract) {
		if (!mapPatrol.containsKey(unit.getID()) || mapPatrol.get(unit.getID()).size() == 0) {
			mapPatrol.put(unit.getID(), GroupWraith.makeNewList());
		}
		ArrayList<TilePosition> targetList = mapPatrol.get(unit.getID());
		if (targetList.size() > 0) {
			TilePosition target = targetList.get(0);
			if (MyUtil.distanceTilePosition(target, unit.getTilePosition()) <= 2) {
				targetList.remove(0);
			} else {
				CommandUtil.attackMove(unit, target.toPosition());
			}
		}
	}
}
