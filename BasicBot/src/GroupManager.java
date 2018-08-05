import java.util.HashMap;

import bwapi.Unit;
import bwapi.UnitType;

public class GroupManager {
	// 그룹 종류
	public GroupAttack groupAttack = new GroupAttack();
	public GroupDefence groupDefence = new GroupDefence();
	public GroupScanUnit groupScanUnit = new GroupScanUnit();
	public GroupWorker groupWorker = new GroupWorker();
	public GroupWraith groupWraith = new GroupWraith();
	public GroupPatrol groupPatrol = new GroupPatrol();
	public GroupLandBuilding groupLandBuilding = new GroupLandBuilding();

	// 싱글톤
	public static GroupManager instance() {
		return groupManager;
	}

	static GroupManager groupManager = new GroupManager();

	// 유닛 컨트롤 기본
	static ControlDefault controlDefault = new ControlDefault();

	HashMap<UnitType, ControlAbstract> mapUnitTypeControl = new HashMap<UnitType, ControlAbstract>();

	// 유닛이 어떤 그룹에 속해있는지 관리
	HashMap<Integer, GroupAbstract> mapUnitGroup = new HashMap<Integer, GroupAbstract>();

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
		mapUnitTypeControl.put(UnitType.Terran_Barracks, new ControlBarrack());
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
		groupPatrol.action();
		groupLandBuilding.action();
	}

	// 모자라는 그룹에 인원을 채워넛는다.
	// 항상 Attack 그룹에서 충원한다.
	void fillGroup() {
		fillGroup(groupDefence);
		fillGroup(groupPatrol);
	}

	void fillGroup(GroupAbstract groupAbstract) {
		for (UnitType unitType : groupAbstract.mapUnitTotal.keySet()) {
			int totalCnt = groupAbstract.mapUnitTotal.get(unitType);
			if (groupAbstract.mapUnit.get(unitType) != null) {
				if (totalCnt > groupAbstract.mapUnit.get(unitType).size()) {
					for (Integer unitID : groupAttack.mapUnit.get(unitType)) {
						if (MyVariable.mapUnitIDUnit.get(unitID).isLoaded() == false) {
							addToGroup(unitType, unitID, groupAbstract);
							break;
						}
						
					}
				}
			}
		}
	}

	GroupAbstract getUnitsGroup(Unit unit) {
		GroupAbstract result = mapUnitGroup.get(unit.getID());
		return result;
	}
	
	void addToGroup(UnitType unitType, Integer unitID, GroupAbstract groupAbstract) {
		remove(unitType, unitID);
		mapUnitGroup.put(unitID, groupAbstract);
		groupAbstract.addUnit(unitType, unitID);
	}

	

	void remove(UnitType unitType, Integer unitID) {
		mapUnitGroup.remove(unitID);
		groupAttack.removeUnit(unitType, unitID);
		groupDefence.removeUnit(unitType, unitID);
		groupScanUnit.removeUnit(unitType, unitID);
		groupWraith.removeUnit(unitType, unitID);
		groupPatrol.removeUnit(unitType, unitID);
		groupLandBuilding.removeUnit(unitType, unitID);
	}
}
