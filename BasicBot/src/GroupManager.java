import java.util.HashMap;

import bwapi.Unit;
import bwapi.UnitType;

public class GroupManager {
	// 그룹 종류
	GroupAttack groupAttack = new GroupAttack();
	GroupDefence groupDefence = new GroupDefence();
	GroupScanUnit groupScanUnit = new GroupScanUnit();
	GroupWorker groupWorker = new GroupWorker();
	GroupWraith groupWraith = new GroupWraith();

	// 싱글톤
	public static GroupManager instance() {
		return groupManager;
	}

	static GroupManager groupManager = new GroupManager();

	// 유닛 컨트롤 기본
	static ControlDefault controlDefault = new ControlDefault();

	HashMap<UnitType, ControlAbstract> mapUnitTypeControl = new HashMap<UnitType, ControlAbstract>();

	// 유닛마다 기본 컨트롤 할당
	public GroupManager() {
		mapUnitTypeControl.put(UnitType.Terran_Marine, new ControlMarin());
		mapUnitTypeControl.put(UnitType.Terran_Firebat, new ControlFireBat());
		mapUnitTypeControl.put(UnitType.Terran_Ghost, new ControlGhost());
		mapUnitTypeControl.put(UnitType.Terran_Goliath, new ControlGoliath());
		mapUnitTypeControl.put(UnitType.Terran_SCV, new ControlSCV());
		mapUnitTypeControl.put(UnitType.Terran_Siege_Tank_Siege_Mode, new ControlTank());
		mapUnitTypeControl.put(UnitType.Terran_Siege_Tank_Tank_Mode, new ControlTank());
		mapUnitTypeControl.put(UnitType.Terran_Vulture, new ControlVulture());
		mapUnitTypeControl.put(UnitType.Terran_Wraith, new ControlWraith());
		mapUnitTypeControl.put(UnitType.Terran_Science_Vessel, new ControlScienceVessel());
	}

	public ControlAbstract getControlAbstract(UnitType unitType) {
		if (mapUnitTypeControl.containsKey(unitType)) {
			return mapUnitTypeControl.get(unitType);
		} else {
			return controlDefault;
		}
	}

	// 그룹별로 타켓 업데이트
	void updateGroupTarget() {
		groupAttack.action();
		groupDefence.action();
		groupScanUnit.action();
		groupWorker.action();
		groupWraith.action();
	}

	// 유닛이 어떤 그룹에 속해있는지 관리
	HashMap<Integer, GroupAbstract> mapUnitGroup = new HashMap<Integer, GroupAbstract>();

	GroupAbstract getUnitsGroup(Unit unit) {
		GroupAbstract result = mapUnitGroup.get(unit.getID());
		return result;
	}

	void addToAttackGroup(Unit unit) {
		mapUnitGroup.put(unit.getID(), groupAttack);
		groupAttack.addUnit(unit);
	}

	void addToDefenceGroup(Unit unit) {
		mapUnitGroup.put(unit.getID(), groupDefence);
		groupDefence.addUnit(unit);
	}
	
	void addToWraithGroup(Unit unit) {
		mapUnitGroup.put(unit.getID(), groupWraith);
		groupWraith.addUnit(unit);
	}

	void addToWorkerGroup(Unit unit) {
		mapUnitGroup.put(unit.getID(), groupWorker);
		groupWorker.addUnit(unit);
	}

	void addScanGroup(Unit unit) {
		mapUnitGroup.put(unit.getID(), groupScanUnit);
		groupScanUnit.addUnit(unit);
	}

	void remove(Unit unit) {
		mapUnitGroup.remove(unit.getID());
		groupAttack.removeUnit(unit);
		groupDefence.removeUnit(unit);
		groupScanUnit.removeUnit(unit);
		groupWraith.removeUnit(unit);
	}
}
