import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import bwapi.TechType;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import bwapi.UpgradeType;

public class MyStrategyManager {

	CommandUtil commandUtil = new CommandUtil();

	static MyStrategyManager myStrategyManager = new MyStrategyManager();

	public static MyStrategyManager getInstance() {
		return myStrategyManager;
	}

	int minPointX = Integer.MAX_VALUE;
	int maxPointX = Integer.MIN_VALUE;
	int minPointY = Integer.MAX_VALUE;
	int maxPointY = Integer.MIN_VALUE;

	public MyStrategyManager() {
		for (int i = 0; i < MyBotModule.Broodwar.getStartLocations().size(); i++) {
			minPointX = Math.min(minPointX, MyBotModule.Broodwar.getStartLocations().get(i).getPoint().getX());
			maxPointX = Math.max(maxPointX, MyBotModule.Broodwar.getStartLocations().get(i).getPoint().getX());
			minPointY = Math.min(minPointY, MyBotModule.Broodwar.getStartLocations().get(i).getPoint().getY());
			maxPointY = Math.max(maxPointY, MyBotModule.Broodwar.getStartLocations().get(i).getPoint().getY());
		}
	}

	// 업데이트 해야할 것을 관리
	void actionCheckUnitUpdate() {
		if (MyVariable.mapSelfUnit.containsKey(UnitType.Terran_Engineering_Bay) && MyVariable.mapSelfUnit.containsKey(UnitType.Terran_Marine)) {
			ArrayList<Unit> marin = MyVariable.mapSelfUnit.get(UnitType.Terran_Marine);
			if (marin.size() > 20) {
				if (MyVariable.mapSelfUnit.get(UnitType.Terran_Engineering_Bay).get(0).canUpgrade(UpgradeType.Terran_Infantry_Weapons)) {
					MyVariable.mapSelfUnit.get(UnitType.Terran_Engineering_Bay).get(0).upgrade(UpgradeType.Terran_Infantry_Weapons);
				}
			}
		}
	}

	void actionSetUnitCountRatio() {
		MyVariable.attackUnitRatio.clear();
		MyVariable.defenceUnitCountTotal.clear();

		if (StrategyManager.Instance().isFullScaleAttackStarted()) {
			// 방어 유닛 구성
			MyVariable.defenceUnitCountTotal.put(UnitType.Terran_Marine, 4);
			MyVariable.defenceUnitCountTotal.put(UnitType.Terran_Medic, 1);
			MyVariable.defenceUnitCountTotal.put(UnitType.Terran_Siege_Tank_Tank_Mode, 1);
			if (MyVariable.findMutal) {
				MyVariable.defenceUnitCountTotal.put(UnitType.Terran_Goliath, 3);
			}
		}

		// 공격 유닛 비율
		MyVariable.attackUnitRatio.put(UnitType.Terran_Marine, 4);
		MyVariable.attackUnitRatio.put(UnitType.Terran_Medic, 1);
		MyVariable.attackUnitRatio.put(UnitType.Terran_Siege_Tank_Tank_Mode, 1);
	}

	// 빌딩 지어야할 것을 관리
	void actionCheckBuilding() {
		if (MyVariable.needTerran_Science_Vessel) {
			if (checkNeedToBuild(UnitType.Terran_Factory, 1))
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Factory, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
			if (checkNeedToBuild(UnitType.Terran_Starport, 1))
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Starport, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
			if (checkNeedToBuild(UnitType.Terran_Control_Tower, 1))
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Control_Tower, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
			if (checkNeedToBuild(UnitType.Terran_Physics_Lab, 1))
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Physics_Lab, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
			if (checkNeedToBuild(UnitType.Terran_Science_Facility, 1))
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Science_Facility, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
			if (checkNeedToBuild(UnitType.Terran_Science_Vessel, 1))
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Science_Vessel, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
		}
		if (MyVariable.findMutal) {
			if (checkNeedToBuild(UnitType.Terran_Factory, 2))
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Factory, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
		}
	}

	// 건설된 것이 하나도 없는지 확인
	boolean checkNeedToBuild(UnitType unitType, int cnt) {
		boolean result = false;
		if (MyBotModule.Broodwar.self().allUnitCount(unitType) < cnt && BuildManager.Instance().getBuildQueue().getItemCount(unitType) == 0 && ConstructionManager.Instance().getConstructionQueueItemCount(unitType, null) == 0) {
			result = true;
		}
		return result;
	}

	// 공격 유닛 컨트롤
	void actionUnitControl() {
		if (MyVariable.mapSelfUnit.containsKey(UnitType.Terran_Marine)) {
			for (Unit unit : MyVariable.mapSelfUnit.get(UnitType.Terran_Marine)) {
				if (unit.isAttacking() && unit.isStimmed() == false) {
					unit.useTech(TechType.Stim_Packs);
				}
			}
		}
		if (MyVariable.mapSelfUnit.containsKey(UnitType.Terran_Firebat)) {
			for (Unit unit : MyVariable.mapSelfUnit.get(UnitType.Terran_Firebat)) {
				if (unit.isAttacking() && unit.isStimmed() == false) {
					unit.useTech(TechType.Stim_Packs);
				}
			}
		}

		TilePosition myStartLocation = MyBotModule.Broodwar.self().getStartLocation().getPoint();

		for (Unit unit : MyVariable.defenceUnit) {
			if (unit.isIdle() == true) {
				commandUtil.attackMove(unit, myStartLocation.toPosition());
			}
		}

	}

	// 스캔 유닛 컨트롤
	public void actionControlScanUnit() {
		for (Unit unit : MyVariable.scanUnit) {
			if (unit.isIdle() == true && MyVariable.attackedUnit.size() > 0) {
				unit.move(MyVariable.attackedUnit.get(0).getPosition());
			}
		}
	}

	// 할일이 없는 유닛의 경우에 다음 포지션으로 이동
	public void actionCheckOtherPoint() {
		for (Unit unit : MyVariable.attackUnit) {
			// 더 이상 발견한 건물이 없다면 아무 곳으로 이동
			if (MyVariable.enemyBuildingUnit.size() == 0 && unit.isIdle() == true) {
				int xValue = minPointX + MyUtil.random.nextInt(maxPointX - minPointX);
				int yValue = minPointY + MyUtil.random.nextInt(maxPointY - minPointY);
				TilePosition position = new TilePosition(xValue, yValue);
				commandUtil.attackMove(unit, position.toPosition());
			}
			// 발견한 건물이 있다면 그쪽으로 이동
			else {
				for (TilePosition tilePosition : MyVariable.enemyBuildingUnit) {
					commandUtil.attackMove(unit, tilePosition.toPosition());
					break;
				}
			}
		}
	}

	// 벙커가 비어있으면 채워넣음
	public void actionCheckBunker() {
		for (Unit unit : MyBotModule.Broodwar.self().getUnits()) {
			if (unit.getType() == UnitType.Terran_Bunker && unit.isCompleted()) {
				if (unit.getLoadedUnits().size() < 4) {
					for (Unit unit2 : MyBotModule.Broodwar.self().getUnits()) {
						if (unit2.getType() == UnitType.Terran_Marine && unit2.isCompleted() && unit2.isLoaded() == false) {
							unit2.rightClick(unit);
						}
					}
					return;
				}
			}
		}
	}

	// 유닛 생산
	void actionCreateUnit() {
		if (MyBotModule.Broodwar.self().minerals() >= 200) {

			HashMap<UnitType, Double> tmp = new HashMap<UnitType, Double>();

			for (UnitType unitType : MyVariable.attackUnitRatio.keySet()) {
				// 0은 계산 불가
				if (MyVariable.attackUnitRatio.get(unitType) == 0 && MyBotModule.Broodwar.canMake(unitType) == false) {
					continue;
				}
				int count = 0;
				if (MyVariable.mapSelfAttackUnit.containsKey(unitType)) {
					count = MyVariable.mapSelfAttackUnit.get(unitType).size();
				}
				tmp.put(unitType, (double) (1.0 * count / MyVariable.attackUnitRatio.get(unitType)));
			}

			ArrayList<Double> tmp2 = new ArrayList<Double>();
			tmp2.addAll(tmp.values());
			Collections.sort(tmp2);

			for (int i = 0; i < tmp2.size(); i++) {
				for (UnitType unitType : MyVariable.attackUnitRatio.keySet()) {
					if (tmp.get(unitType) == tmp2.get(i) && MyBotModule.Broodwar.canMake(unitType)) {
						if (BuildManager.Instance().buildQueue.getItemCount(unitType) == 0) {
							BuildManager.Instance().buildQueue.queueAsLowestPriority(unitType, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
						}
						return;
					}
				}
			}
		}

		/*
		 * if (MyVariable.mapSelfUnit.containsKey(UnitType.Terran_Marine) &&
		 * MyVariable.mapSelfUnit.containsKey(UnitType.Terran_Medic)) { if
		 * (MyVariable.mapSelfUnit.get(UnitType.Terran_Marine).size() >
		 * MyVariable.mapSelfUnit.get(UnitType.Terran_Medic).size() * 4) { if
		 * (BuildManager.Instance().buildQueue.getItemCount(UnitType.Terran_Medic) == 0)
		 * { BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
		 * Terran_Medic, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true); }
		 * } }
		 */
	}

	// 스캐너 사용
	int beforeTime = 0;

	void useScanner_Sweep(Unit unit) {
		if (MyVariable.mapSelfUnit.containsKey(UnitType.Terran_Comsat_Station)) {
			ArrayList<Unit> units = MyVariable.mapSelfUnit.get(UnitType.Terran_Comsat_Station);
			for (int i = 0; i < units.size(); i++) {
				if (units.get(i).canUseTech(TechType.Scanner_Sweep, unit) && MyBotModule.Broodwar.getFrameCount() - beforeTime > 24 * 3) {
					units.get(i).useTech(TechType.Scanner_Sweep, unit);
					beforeTime = MyBotModule.Broodwar.getFrameCount();
					break;
				}
			}
		}
	}

	// 스캐너 사용이 가능한지 확인
	void canUseScanner_Sweep(Unit unit) {
		if (MyVariable.mapSelfUnit.containsKey(UnitType.Terran_Comsat_Station)) {
			ArrayList<Unit> units = MyVariable.mapSelfUnit.get(UnitType.Terran_Comsat_Station);
			for (int i = 0; i < units.size(); i++) {
				if (units.get(i).canUseTech(TechType.Scanner_Sweep, unit)) {
					if (MyBotModule.Broodwar.getFrameCount() - beforeTime > 24 * 3) {
						units.get(i).useTech(TechType.Scanner_Sweep, unit);
						beforeTime = MyBotModule.Broodwar.getFrameCount();
					}
					break;
				} else {
					MyVariable.needTerran_Science_Vessel = true;
				}
			}
		}
	}

	// 내 본진에 들어온 유닛을 공격
	void actionAttackEnemyUnitAroundMyStartPoint() {
		if (MyVariable.enemyUnitAroundMyStartPoint.size() > 0) {
			if (MyVariable.defenceUnit.size() > 0) {
				for (Unit unit : MyVariable.defenceUnit) {
					commandUtil.attackMove(unit, MyVariable.enemyUnitAroundMyStartPoint.get(0).getPoint());
				}
			} else {
				for (Unit unit : MyVariable.attackUnit) {
					commandUtil.attackMove(unit, MyVariable.enemyUnitAroundMyStartPoint.get(0).getPoint());
				}
			}
		}
		if (MyVariable.enemyUnitAroundMyStartPoint.size() > 10) {
			for (Unit unit : MyVariable.attackUnit) {
				commandUtil.attackMove(unit, MyVariable.enemyUnitAroundMyStartPoint.get(0).getPoint());
			}
		}
	}

	// 적 유닛 목록 업데이트
	void actionUpdateEnemyUnitMap() {
		MyVariable.mapEnemyUnit.clear();
		MyVariable.enemyUnitAroundMyStartPoint.clear();
		TilePosition myStartLocation = MyBotModule.Broodwar.self().getStartLocation().getPoint();

		for (Unit unit : MyBotModule.Broodwar.enemy().getUnits()) {
			if (unit.getType().isBuilding() && unit.exists() && unit.getType() != UnitType.Unknown && unit.getPosition().isValid()) {
				if (!unit.getType().isRefinery()) {
					MyVariable.enemyBuildingUnit.add(unit.getTilePosition());
				}
			}
			double distance = MyUtil.distance(unit.getTilePosition(), myStartLocation);
			if (distance < 20) {
				MyVariable.enemyUnitAroundMyStartPoint.add(unit);
			}
			if (!MyVariable.mapEnemyUnit.containsKey(unit.getType())) {
				MyVariable.mapEnemyUnit.put(unit.getType(), new ArrayList<Unit>());
			}
			MyVariable.mapEnemyUnit.get(unit.getType()).add(unit);
		}

		if (MyVariable.mapEnemyUnit.containsKey(UnitType.Zerg_Lurker)) {
			for (Unit unit : MyVariable.mapEnemyUnit.get(UnitType.Zerg_Lurker)) {
				if (unit.isAttacking()) {
					useScanner_Sweep(unit);
				}
			}
		}
		if (MyVariable.mapEnemyUnit.containsKey(UnitType.Protoss_Dark_Templar)) {
			for (Unit unit : MyVariable.mapEnemyUnit.get(UnitType.Protoss_Dark_Templar)) {
				if (unit.isAttacking()) {
					useScanner_Sweep(unit);
				}
			}
		}
		if (MyVariable.mapEnemyUnit.containsKey(UnitType.Terran_Wraith)) {
			for (Unit unit : MyVariable.mapEnemyUnit.get(UnitType.Terran_Wraith)) {
				if (unit.isAttacking()) {
					useScanner_Sweep(unit);
				}
			}
		}
		/*
		 * System.out.println("MyVariable.enemyBuildingUnit.size()=" +
		 * MyVariable.enemyBuildingUnit.size()); for (TilePosition unit :
		 * MyVariable.enemyBuildingUnit) { System.out.println("unit=" +
		 * unit.toString()); }
		 */
	}

	// 방어 유닛 구성
	boolean setUnitAsDefence(Unit unit) {
		boolean result = false;
		if (MyVariable.defenceUnitCountTotal.containsKey(unit.getType())) {
			if (!MyVariable.defenceUnitCount.containsKey(unit.getType())) {
				MyVariable.defenceUnitCount.put(unit.getType(), 0);
			}
			int total = MyVariable.defenceUnitCountTotal.get(unit.getType());
			int now = MyVariable.defenceUnitCount.get(unit.getType());
			if (total > now) {
				MyVariable.defenceUnit.add(unit);
				MyVariable.defenceUnitCount.put(unit.getType(), now + 1);
				result = true;
			}
		}
		return result;
	}

	void actionUpdateSelfUnitMap() {
		// 전체 유닛 목록 초기화
		MyVariable.mapSelfUnit.clear();
		MyVariable.mapSelfAttackUnit.clear();

		// 역할별 유닛 목록
		MyVariable.attackUnit.clear();
		MyVariable.attackedUnit.clear();
		MyVariable.defenceUnit.clear();
		MyVariable.scanUnit.clear();
		MyVariable.bunkerUnit.clear();

		// 방어할 유닛 개수 초기화
		MyVariable.defenceUnitCount.clear();

		for (Unit unit : MyBotModule.Broodwar.self().getUnits()) {
			if (unit.isLoaded() == true) {
				continue;
			}
			if (!MyVariable.mapSelfUnit.containsKey(unit.getType())) {
				MyVariable.mapSelfUnit.put(unit.getType(), new ArrayList<Unit>());
				MyVariable.mapSelfAttackUnit.put(unit.getType(), new ArrayList<Unit>());
			}
			MyVariable.mapSelfUnit.get(unit.getType()).add(unit);

			// defenceUnit에 할당
			if (!setUnitAsDefence(unit)) {
				// scanUnit에 할당
				if (unit.getType() == UnitType.Terran_Science_Vessel) {
					MyVariable.scanUnit.add(unit);
				} else if (unit.canAttack() && unit.getType() != UnitType.Terran_SCV) {
					MyVariable.attackUnit.add(unit);
					MyVariable.enemyBuildingUnit.remove(unit.getTilePosition());

					MyVariable.mapSelfAttackUnit.get(unit.getType()).add(unit);

				}
			}

			// 공격당하고 있는 유닛
			if (unit.isUnderAttack()) {
				MyVariable.attackedUnit.add(unit);
			}
		}

		if (MyVariable.scanUnit.size() > 0) {
			MyVariable.haveTerran_Science_Vessel = true;
		} else {
			MyVariable.haveTerran_Science_Vessel = false;
		}

	}
}
