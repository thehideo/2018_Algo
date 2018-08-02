import java.util.HashMap;
import java.util.HashSet;

import bwapi.Position;
import bwapi.Unit;
import bwapi.UnitType;

// 액션별 타켓이나 해야할 일 정의
public abstract class GroupAbstract {
	// 유닛별로 공격해야할 적유닛
	HashMap<Integer, Unit> mapTargetUnit = new HashMap<Integer, Unit>();

	HashMap<UnitType, HashSet<Integer>> mapUnit = new HashMap<UnitType, HashSet<Integer>>();

	// 전체 Target
	protected Position targetPosition = null;

	// Unit별 Target
	protected HashMap<Integer, Position> mapTargetPosition = new HashMap<Integer, Position>();

	public void addUnit(Unit unit) {
		if (mapUnit.get(unit.getType()) == null) {
			mapUnit.put(unit.getType(), new HashSet<Integer>());
		}
		mapUnit.get(unit.getType()).add(unit.getID());
	}

	public void removeUnit(Unit unit) {
		if (mapUnit.get(unit.getType()) == null) {
			mapUnit.put(unit.getType(), new HashSet<Integer>());
		}
		mapUnit.get(unit.getType()).remove(unit.getID());
	}

	public Unit getTargetUnit(Unit unit) {
		return mapTargetUnit.get(unit.getID());
	}

	public Position getTargetPosition(Unit unit) {
		if (mapTargetPosition.containsKey(unit.getID())) {
			return mapTargetPosition.get(unit.getID());
		}
		return targetPosition;
	}

	abstract void action();
}
