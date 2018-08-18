import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import bwapi.TilePosition;
import bwapi.Unit;

public class ControlDropShip extends ControlAbstract {

	void actionMain(Unit unit, GroupAbstract groupAbstract) {
		// 가야할 곳
		if (!mapPatrol.containsKey(unit.getID())) {
			mapPatrol.put(unit.getID(), new ArrayList<TilePosition>());

		}

		// 돌아가야하는 위치가 등록된 적이 없으면 초기화
		if (!mapBackPath.containsKey(unit.getID())) {
			mapBackPath.put(unit.getID(), new ArrayList<TilePosition>());
		}

		// 인원을 다 채웠을면 가야할 곳을 등록한다.
		if (mapPatrol.get(unit.getID()).size() == 0 && unit.getSpaceRemaining() == 0) {
			mapPatrol.put(unit.getID(), GroupWraith.makeNewList());
			mapBackPath.put(unit.getID(), new ArrayList<TilePosition>());
		}

		// 인원을 모두 내렸으면, 가야하는 곳을 제거.
		if (unit.getSpaceRemaining() == 8) {
			mapPatrol.put(unit.getID(), new ArrayList<TilePosition>());
		}

		// 근처에 적이 있는지 확인
		List<Unit> units = MyBotModule.Broodwar.getUnitsInRadius(unit.getPoint(), 32 * 10);
		boolean findEnemy = false;
		for (Unit enemy : units) {
			if (enemy.getPlayer() == InformationManager.Instance().enemyPlayer && enemy.exists() == true) {
				findEnemy = true;
			}
		}

		// 적을 발견하면 유닛을 모두 내린다
		if (findEnemy == true && unit.canUnloadAllPosition(unit.getPosition())) {
			CommandUtil.unloadAll(unit);
			setSpecialAction(unit,0);
			return;
		}
		// 다 채웠으면 적 기지로 날아간다.
		else if (mapPatrol.get(unit.getID()).size() > 0) {
			// 확장 할 수 있는 곳으로 이동
			ArrayList<TilePosition> targetList = mapPatrol.get(unit.getID());
			if (targetList.size() > 0) {
				TilePosition target = targetList.get(0);
				if (MyUtil.distanceTilePosition(target, unit.getTilePosition()) <= 2) {
					mapBackPath.get(unit.getID()).add(targetList.get(0));
					targetList.remove(0);
				} else {
					CommandUtil.move(unit, target.toPosition());
					return;
				}
			}
		}
		// 가야할 곳이 없으면 왔던 길을 돌아간다.
		else if (mapPatrol.get(unit.getID()).size() == 0 && mapBackPath.get(unit.getID()).size() > 0) {
			ArrayList<TilePosition> targetList = mapBackPath.get(unit.getID());
			TilePosition target = targetList.get(targetList.size() - 1);
			if (MyUtil.distanceTilePosition(target, unit.getTilePosition()) <= 2) {
				targetList.remove(targetList.size() - 1);
			} else {
				CommandUtil.move(unit, target.toPosition());
				return;
			}
		} else {
			CommandUtil.move(unit, MyVariable.myStartLocation.toPosition());
			return;
		}
	}

	static HashMap<Integer, ArrayList<TilePosition>> mapPatrol = new HashMap<Integer, ArrayList<TilePosition>>();

	static HashMap<Integer, ArrayList<TilePosition>> mapBackPath = new HashMap<Integer, ArrayList<TilePosition>>();

}
