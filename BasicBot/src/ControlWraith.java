import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import bwapi.Race;
import bwapi.TechType;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;

public class ControlWraith extends ControlAbstract {

	HashMap<Integer, Boolean> mapMode = new HashMap<Integer, Boolean>();

	void actionMain(Unit wraith, GroupAbstract groupAbstract) {

		// 클로킹 관련 액션
		cloakingAction(wraith, groupAbstract);

		if (InformationManager.Instance().enemyRace == Race.Terran) {

			if (groupAbstract == GroupManager.instance().groupAttack) {
				attackGroupAction(wraith, groupAbstract);

			}
			// 터렛,골리앗,벙커,발키리를 만나면 뒤로 도망간다.
			terranAction(wraith, groupAbstract);

			// 적 Wraith를 1번으로 공격한다.
			Unit Terran_Wraith = MyUtil.getMostCloseEnemyUnit(UnitType.Terran_Wraith, wraith);
			if (Terran_Wraith != null) {
				CommandUtil.attackUnit(wraith, Terran_Wraith);
				return;
			}

			// 주위에 베슬이 보이면 바로 공격
			Unit Terran_Science_Vessel = MyUtil.getMostCloseEnemyUnit(UnitType.Terran_Science_Vessel, wraith);
			if (Terran_Science_Vessel != null && wraith.getDistance(Terran_Science_Vessel) < 400) {
				CommandUtil.attackUnit(wraith, Terran_Science_Vessel);
				return;
			}
		}

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

	void attackGroupAction(Unit unit, GroupAbstract groupAbstract) {

		List<Unit> ourUnits = MyBotModule.Broodwar.getUnitsInRadius(unit.getPosition(), 32 * 10);

		int enemyCnt = 0;

		int selfWraithCnt = 0;

		Double distanceMostClose = Double.MAX_VALUE;
		Unit enemy = null;

		for (Unit u : ourUnits) {
			if (u.getPlayer() == MyBotModule.Broodwar.enemy()) {
				boolean find = false;
				if (unit.isDetected() == false) {
					if (u.getType() == UnitType.Terran_Goliath) {
						find = true;
					} else if (u.getType() == UnitType.Terran_Marine) {
						find = true;
					} else if (u.getType() == UnitType.Terran_Wraith) {
						find = true;
					} else if (u.getType() == UnitType.Terran_Bunker) {
						find = true;
					} else if (u.getType() == UnitType.Terran_Battlecruiser) {
						find = true;
					} else if (u.getType() == UnitType.Terran_Valkyrie) {
						find = true;
					}
				} else {
					if (u.getType() == UnitType.Terran_Goliath) {
						enemyCnt = enemyCnt + 3;
						find = true;
					} else if (u.getType() == UnitType.Terran_Marine) {
						enemyCnt = enemyCnt + 1;
						find = true;
					} else if (u.getType() == UnitType.Terran_Wraith) {
						enemyCnt = enemyCnt + 1;
						find = true;
					} else if (u.getType() == UnitType.Terran_Bunker) {
						enemyCnt = enemyCnt + 10;
						find = true;
					} else if (u.getType() == UnitType.Terran_Battlecruiser) {
						enemyCnt = enemyCnt + 5;
						find = true;
					} else if (u.getType() == UnitType.Terran_Valkyrie) {
						enemyCnt = enemyCnt + 3;
						find = true;
					}
				}
				if (find == true) {
					double distanceEnemy = unit.getDistance(u);
					if (distanceMostClose > distanceEnemy) {
						distanceMostClose = distanceEnemy;
						enemy = u;
					}

				}
			} else if (u.getPlayer() == MyBotModule.Broodwar.self()) {
				if (u.getType() == UnitType.Terran_Wraith) {
					selfWraithCnt++;
				}
			}
		}
		if (enemy != null && selfWraithCnt >= enemyCnt) {
			CommandUtil.attackUnit(unit, enemy);
		}
	}

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
			if (MyUtil.distanceTilePosition(wraith.getTilePosition(), MyVariable.mapTurretPosition.get(turretID)) < 9) { // 터렛 최대 사거리 7
				CommandUtil.move(wraith, MyVariable.myStartLocation.toPosition());
				return;
			}
		}

		if (wraith.isDetected() == false) {
			Iterator<Integer> GoliatIDs = MyVariable.mapGoliatPosition.keySet().iterator();
			while (GoliatIDs.hasNext()) {
				Integer GoliatID = GoliatIDs.next();
				if (MyUtil.distanceTilePosition(wraith.getTilePosition(), MyVariable.mapGoliatPosition.get(GoliatID)) < 10) { // 골리앗 최대 사거리 8
					CommandUtil.move(wraith, MyVariable.myStartLocation.toPosition());
					return;
				}
			}

			Unit Terran_Bunker = MyUtil.getMostCloseEnemyUnit(UnitType.Terran_Bunker, wraith);
			if (Terran_Bunker != null && MyUtil.distanceTilePosition(Terran_Bunker.getTilePosition(), wraith.getTilePosition()) < 7) { // 마린 사거리 5
				CommandUtil.move(wraith, MyVariable.myStartLocation.toPosition());
				return;
			}

			Unit Terran_Valkyrie = MyUtil.getMostCloseEnemyUnit(UnitType.Terran_Valkyrie, wraith);
			if (Terran_Valkyrie != null && MyUtil.distanceTilePosition(Terran_Valkyrie.getTilePosition(), wraith.getTilePosition()) < 9) { // 발키리 사거리 7
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
