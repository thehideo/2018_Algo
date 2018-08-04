import java.util.HashMap;
import java.util.HashSet;

import bwapi.Position;
import bwapi.Unit;
import bwapi.UnitType;

// 액션별 타켓이나 해야할 일 정의
public abstract class GroupAbstract {
	// 유닛별로 공격해야할 적유닛
	HashMap<Integer, Unit> mapTargetUnit = new HashMap<Integer, Unit>();

	// 할당 되어야할 유닛의 총 개수
	HashMap<UnitType, Integer> mapUnitTotal = new HashMap<UnitType, Integer>();
	HashMap<UnitType, HashSet<Integer>> mapUnit = new HashMap<UnitType, HashSet<Integer>>();

	// 전체 Target
	protected Position targetPosition = null;

	// Unit별 Target
	protected HashMap<Integer, Position> mapTargetPosition = new HashMap<Integer, Position>();

	public void addUnit(UnitType unitType, Integer unitID) {
		if (mapUnit.get(unitType) == null) {
			mapUnit.put(unitType, new HashSet<Integer>());
		}
		mapUnit.get(unitType).add(unitID);
	}

	public void removeUnit(UnitType unitType,Integer unitID) {
		if (mapUnit.get(unitType) == null) {
			mapUnit.put(unitType, new HashSet<Integer>());
		}
		mapUnit.get(unitType).remove(unitID);
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
