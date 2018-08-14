import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import bwapi.Race;
import bwapi.TechType;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;

public class ControlWraith extends ControlAbstract {

	HashMap<Integer, Boolean> mapMode = new HashMap<Integer, Boolean>();

	void actionMain(Unit wraith, GroupAbstract groupAbstract) {

		if (InformationManager.Instance().enemyRace == Race.Terran) {
			// 터렛,골리앗,벙커,발키리를 만나면 뒤로 도망간다.
			terranAction(wraith, groupAbstract);
			// 주위에 베슬이 보이면 바로 공격
			Unit Terran_Science_Vessel = MyUtil.getMostCloseEnemyUnit(UnitType.Terran_Science_Vessel, wraith);
			if (Terran_Science_Vessel != null && wraith.getDistance(Terran_Science_Vessel) < 400) {
				CommandUtil.attackUnit(wraith, Terran_Science_Vessel);
				return;
			}
		}

		// 클로킹 관련 액션
		cloakingAction(wraith, groupAbstract);

		if (groupAbstract == GroupManager.instance().groupWraithPatrol) {
			wraithGroupAction(wraith, groupAbstract);
		} else if (groupAbstract == GroupManager.instance().groupAttack) {
			// 공격 당하고 있는 유닛 쪽으로 이동
			if (MyVariable.attackedUnit.size() > 0 && !CommandUtil.commandHash.contains(wraith)) {
				Unit mostCloseEnemy = MyUtil.getMostCloseUnit(wraith, MyVariable.attackedUnit);
				if (mostCloseEnemy != null) {
					CommandUtil.attackUnit(wraith, mostCloseEnemy);
					return;
				}
			}
			// 적 유닛에게 공격
			Unit mostCloseEnemyAttackUnit = MyUtil.getMostCloseUnit(wraith, MyVariable.enemyAttactUnit);
			if (mostCloseEnemyAttackUnit != null && wraith.getDistance(mostCloseEnemyAttackUnit) < 500 && mostCloseEnemyAttackUnit.getType() != UnitType.Terran_Goliath) {
				CommandUtil.attackUnit(wraith, mostCloseEnemyAttackUnit);
			}
		}
		CommandUtil.attackMove(wraith, groupAbstract.getTargetPosition(wraith));
	}

	static HashMap<Integer, ArrayList<TilePosition>> mapWaithPatrol = new HashMap<Integer, ArrayList<TilePosition>>();

	void wraithGroupAction(Unit wraith, GroupAbstract groupAbstract) {
		if (!mapWaithPatrol.containsKey(wraith.getID()) || mapWaithPatrol.get(wraith.getID()).size() == 0) {
			mapWaithPatrol.put(wraith.getID(), GroupWraith.makeNewList());
		}
		ArrayList<TilePosition> targetList = mapWaithPatrol.get(wraith.getID());
		if (targetList.size() > 0) {
			TilePosition target = targetList.get(0);
			if (MyUtil.distanceTilePosition(target, wraith.getTilePosition()) <= 2) {
				targetList.remove(0);
			} else {
				CommandUtil.attackMove(wraith, target.toPosition());
			}
		}
	}
	/*
	 * void attackGroupAction(Unit unit, GroupAbstract groupAbstract) { int
	 * sizeEnemy = MyVariable.getEnemyUnit(UnitType.Terran_Valkyrie).size() +
	 * MyVariable.getEnemyUnit(UnitType.Terran_Goliath).size() +
	 * MyVariable.getEnemyUnit(UnitType.Terran_Missile_Turret).size() +
	 * MyVariable.getEnemyUnit(UnitType.Terran_Marine).size() +
	 * MyVariable.getEnemyUnit(UnitType.Terran_Bunker).size() * 3 +
	 * MyVariable.getEnemyUnit(UnitType.Terran_Battlecruiser).size() * 2; int
	 * sizeSelf = groupAbstract.mapUnit.get(UnitType.Terran_Wraith).size(); if
	 * (sizeEnemy * 5 < sizeSelf) { Unit Terran_Missile_Turret =
	 * MyUtil.getMostCloseEnemyUnit(UnitType.Terran_Missile_Turret, unit); if
	 * (Terran_Missile_Turret != null) { CommandUtil.attackUnit(unit,
	 * Terran_Missile_Turret); return; } Unit Terran_Goliath =
	 * MyUtil.getMostCloseEnemyUnit(UnitType.Terran_Goliath, unit); if
	 * (Terran_Goliath != null && unit.getDistance(Terran_Goliath) < 400) {
	 * CommandUtil.attackUnit(unit, Terran_Goliath); return; } Unit Terran_Marine =
	 * MyUtil.getMostCloseEnemyUnit(UnitType.Terran_Marine, unit); if (Terran_Marine
	 * != null && unit.getDistance(Terran_Marine) < 400) {
	 * CommandUtil.attackUnit(unit, Terran_Marine); return; } Unit Terran_Wraith =
	 * MyUtil.getMostCloseEnemyUnit(UnitType.Terran_Wraith, unit); if (Terran_Wraith
	 * != null && unit.getDistance(Terran_Wraith) < 400) {
	 * CommandUtil.attackUnit(unit, Terran_Wraith); return; } Unit Terran_Bunker =
	 * MyUtil.getMostCloseEnemyUnit(UnitType.Terran_Bunker, unit); if (Terran_Bunker
	 * != null && unit.getDistance(Terran_Bunker) < 400) {
	 * CommandUtil.attackUnit(unit, Terran_Bunker); return; } Unit
	 * Terran_Battlecruiser =
	 * MyUtil.getMostCloseEnemyUnit(UnitType.Terran_Battlecruiser, unit); if
	 * (Terran_Battlecruiser != null && unit.getDistance(Terran_Battlecruiser) <
	 * 400) { CommandUtil.attackUnit(unit, Terran_Battlecruiser); return; } Unit
	 * Terran_Valkyrie = MyUtil.getMostCloseEnemyUnit(UnitType.Terran_Valkyrie,
	 * unit); if (Terran_Valkyrie != null && unit.getDistance(Terran_Valkyrie) <
	 * 400) { CommandUtil.attackUnit(unit, Terran_Valkyrie); return; } } }
	 */

	void cloakingAction(Unit wraith, GroupAbstract groupAbstract) {
		// 적이 근처에 없으면 클로킹 해제
		if (MyVariable.enemyAttactUnit.size() == 0) {
			if (wraith.isCloaked() == true) {
				wraith.decloak();
				CommandUtil.commandHash.add(wraith);
				return;
			}
		} else {
			// 전쟁 상황이면 클로킹
			if (wraith.isUnderAttack() == true) {
				if (wraith.canUseTech(TechType.Cloaking_Field)) {
					CommandUtil.useTech(wraith, TechType.Cloaking_Field);
					return;
				}
			}
		}
	}

	void terranAction(Unit wraith, GroupAbstract groupAbstract) {
		Iterator<Integer> turretIDs = MyVariable.mapTurretPosition.keySet().iterator();
		while (turretIDs.hasNext()) {
			Integer turretID = turretIDs.next();
			if (MyUtil.distanceTilePosition(wraith.getTilePosition(), MyVariable.mapTurretPosition.get(turretID)) < 10) { // 터렛 최대 사거리 7
				CommandUtil.move(wraith, MyVariable.myStartLocation.toPosition());
				return;
			}
		}
		Iterator<Integer> GoliatIDs = MyVariable.mapGoliatPosition.keySet().iterator();
		while (GoliatIDs.hasNext()) {
			Integer GoliatID = GoliatIDs.next();
			if (MyUtil.distanceTilePosition(wraith.getTilePosition(), MyVariable.mapGoliatPosition.get(GoliatID)) < 11) { // 골리앗 최대 사거리 8
				CommandUtil.move(wraith, MyVariable.myStartLocation.toPosition());
				return;
			}
		}
		if (wraith.isDetected() == false && wraith.getEnergy() < 5) {
			Unit Terran_Bunker = MyUtil.getMostCloseEnemyUnit(UnitType.Terran_Bunker, wraith);
			if (Terran_Bunker != null && MyUtil.distanceTilePosition(Terran_Bunker.getTilePosition(), wraith.getTilePosition()) < 8) { // 마린 사거리 5
				CommandUtil.move(wraith, MyVariable.myStartLocation.toPosition());
				return;
			}

			Unit Terran_Valkyrie = MyUtil.getMostCloseEnemyUnit(UnitType.Terran_Valkyrie, wraith);
			if (Terran_Valkyrie != null && MyUtil.distanceTilePosition(Terran_Valkyrie.getTilePosition(), wraith.getTilePosition()) < 10) { // 발키리 사거리 7
				CommandUtil.move(wraith, MyVariable.myStartLocation.toPosition());
				return;
			}
		}
	}

	void zergAction(Unit wraith, GroupAbstract groupAbstract) {
		for (Unit unit : MyVariable.getEnemyUnit(UnitType.Zerg_Drone)) {
			if (wraith.isCloaked() == true) {
				CommandUtil.attackUnit(wraith, unit);
				return;
			}
		}
	}

	void protossAction(Unit wraith, GroupAbstract groupAbstract) {
		// 옵저버가 보이면 바로 공격
		for (Unit unit : MyVariable.getEnemyUnit(UnitType.Protoss_Observer)) {
			if (unit.isDetected() == true) {
				if (wraith.isCloaked() == true) {
					CommandUtil.attackUnit(wraith, unit);
					return;
				}
			} else {
				MyVariable.needTerran_Science_Vessel = true;
				boolean aroundWraith = false;
				if (wraith.getDistance(unit) < 400) {
					aroundWraith = true;
				}
				if (aroundWraith == true) {
					ActionUseScanner.useScanner_Sweep(unit);
				}
			}
		}

		for (Unit photon_cannon : MyVariable.getEnemyUnit(UnitType.Protoss_Photon_Cannon)) {
			if (photon_cannon.getDistance(wraith) < photon_cannon.getType().airWeapon().maxRange() + 10) {
				CommandUtil.move(wraith, MyVariable.myStartLocation.toPosition());
				return;
			}
		}

		for (Unit Dragoon : MyVariable.getEnemyUnit(UnitType.Protoss_Dragoon)) {
			if (wraith.isDetected() == true && Dragoon.getDistance(wraith) < Dragoon.getType().airWeapon().maxRange() + 10) {
				CommandUtil.move(wraith, MyVariable.myStartLocation.toPosition());
				return;
			}
		}

		for (Unit Corsair : MyVariable.getEnemyUnit(UnitType.Protoss_Corsair)) {
			if (wraith.isDetected() == true && Corsair.getDistance(wraith) < Corsair.getType().airWeapon().maxRange() + 10) {
				CommandUtil.move(wraith, MyVariable.myStartLocation.toPosition());
				return;
			}
		}

		for (Unit unit : MyVariable.getEnemyUnit(UnitType.Protoss_Carrier)) {
			CommandUtil.attackUnit(wraith, unit);
			return;
		}

		for (Unit unit : MyVariable.getEnemyUnit(UnitType.Protoss_Probe)) {
			CommandUtil.attackUnit(wraith, unit);
			return;
		}
	}
}
