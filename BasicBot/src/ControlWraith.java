import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import bwapi.Position;
import bwapi.Race;
import bwapi.TechType;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;

public class ControlWraith extends ControlAbstract {

	HashMap<Integer, Boolean> mapMode = new HashMap<Integer, Boolean>();
	Random r = new Random();

	void actionMain(Unit wraith, GroupAbstract groupAbstract) {
		// 클로킹 관련 액션
		cloakingAction(wraith, groupAbstract);

		if (InformationManager.Instance().enemyRace == Race.Terran) {
			// 터렛을 만나면 뒤로 간다.
			Iterator<Integer> turretIDs = MyVariable.mapTurretPosition.keySet().iterator();
			while (turretIDs.hasNext()) {
				Integer turretID = turretIDs.next();
				Position turretPosition = MyVariable.mapTurretPosition.get(turretID);
				if (MyUtil.distancePosition(wraith.getPosition(), turretPosition) < 11 * 32) { // 터렛 최대 사거리 7
					int X2 = wraith.getPosition().getX();
					int Y2 = wraith.getPosition().getY();
					int X1 = turretPosition.getX();
					int Y1 = turretPosition.getY();

					Position postion = new Position(2 * X2 - X1, 2 * Y2 - Y1);
					if (postion.isValid() == false) {
						CommandUtil.move(wraith, MyVariable.myStartLocation.toPosition());
					} else {
						CommandUtil.move(wraith, postion);
					}
					setSpecialAction(wraith, 0);
					return;
				}
			}

			// 골리앗,벙커,발키리를 만나면 뒤로 도망간다.
			runawayAction(wraith, groupAbstract);

			if (groupAbstract == GroupManager.instance().groupAttack) {
				// 적 Wraith를 1번으로 공격한다.
				Unit Terran_Wraith = MyUtil.getMostCloseEnemyUnit(UnitType.Terran_Wraith, wraith);
				if (Terran_Wraith != null) {
					if (checkGoliat(Terran_Wraith) == false) {
						CommandUtil.attackUnit(wraith, Terran_Wraith);
						return;
					}
				}

				// 주위에 베슬이 보이면 바로 공격
				Unit Terran_Science_Vessel = MyUtil.getMostCloseEnemyUnit(UnitType.Terran_Science_Vessel, wraith);
				if (Terran_Science_Vessel != null && wraith.getDistance(Terran_Science_Vessel) < 400) {
					if (checkGoliat(Terran_Science_Vessel) == false) {
						CommandUtil.attackUnit(wraith, Terran_Science_Vessel);
						return;
					}
				}
			}
		}

		if (groupAbstract == GroupManager.instance().groupWraithPatrol) {
			wraithGroupAction(wraith, groupAbstract);
		} else if (groupAbstract == GroupManager.instance().groupAttack) {
			if (MyVariable.findWraith == false && MyVariable.findGoliat == false) {
				CommandUtil.attackMove(wraith, MyVariable.enemyStartLocation.toPosition());
				return;
			}

			// 적 유닛에게 공격
			Unit mostCloseEnemyAttackUnit = MyUtil.getMostCloseUnit(wraith, MyVariable.enemyAttactUnit);
			if (mostCloseEnemyAttackUnit != null && wraith.getDistance(mostCloseEnemyAttackUnit) < 500 && mostCloseEnemyAttackUnit.getType() != UnitType.Terran_Goliath) {
				if (checkGoliat(mostCloseEnemyAttackUnit) == false) {
					CommandUtil.attackUnit(wraith, mostCloseEnemyAttackUnit);
				}
			}

			if (MyUtil.GetMyTankCnt() > 0) {
				if (MyVariable.mostFarTank != null) {
					CommandUtil.attackMove(wraith, MyVariable.mostFarTank.getPosition());
				}
			} else {
				if (MyVariable.mostFarAttackUnit != null) {
					CommandUtil.attackMove(wraith, MyVariable.mostFarAttackUnit.getPosition());
				}
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

	boolean checkGoliat(Unit unit) {
		boolean goliatAround = false;
		Iterator<Integer> goliatIDs = MyVariable.mapGoliatPosition.keySet().iterator();
		while (goliatIDs.hasNext()) {
			Integer goliatID = goliatIDs.next();
			Position tp = MyVariable.mapGoliatPosition.get(goliatID);
			if (MyUtil.distancePosition(unit.getPosition(), tp) <= 10 * 32) {
				goliatAround = true;
			}
		}
		return goliatAround;
	}

	void cloakingAction(Unit wraith, GroupAbstract groupAbstract) {
		if (wraith.isUnderAttack() == true) {
			if (wraith.canUseTech(TechType.Cloaking_Field)) {
				CommandUtil.useTech(wraith, TechType.Cloaking_Field);
				return;
			}
		}
	}

	void runawayAction(Unit wraith, GroupAbstract groupAbstract) {
		if (wraith.isDetected() == true || wraith.isCloaked() == false) {
			Iterator<Integer> GoliatIDs = MyVariable.mapGoliatPosition.keySet().iterator();
			while (GoliatIDs.hasNext()) {
				Integer goliatID = GoliatIDs.next();
				Position goliatPosition = MyVariable.mapGoliatPosition.get(goliatID);
				if (MyUtil.distancePosition(wraith.getPosition(), goliatPosition) < 10 * 32) { // 골리앗 최대 사거리 8
					int X2 = wraith.getPosition().getX();
					int Y2 = wraith.getPosition().getY();
					int X1 = goliatPosition.getX();
					int Y1 = goliatPosition.getY();

					if (wraith.canUseTech(TechType.Cloaking_Field)) {
						CommandUtil.useTech(wraith, TechType.Cloaking_Field);
						return;
					} else {
						CommandUtil.move(wraith, new Position(2 * X2 - X1 + r.nextInt(3), 2 * Y2 - Y1 + r.nextInt(3)));
						setSpecialAction(wraith, 0);
						return;
					}
				}
			}
			Unit Terran_Bunker = MyUtil.getMostCloseEnemyUnit(UnitType.Terran_Bunker, wraith);
			if (Terran_Bunker != null && MyUtil.distanceTilePosition(Terran_Bunker.getTilePosition(), wraith.getTilePosition()) < 7) { // 마린 사거리 5
				int X2 = wraith.getPosition().getX();
				int Y2 = wraith.getPosition().getY();
				int X1 = Terran_Bunker.getPosition().getX();
				int Y1 = Terran_Bunker.getPosition().getY();
				if (wraith.canUseTech(TechType.Cloaking_Field)) {
					CommandUtil.useTech(wraith, TechType.Cloaking_Field);
					return;
				} else {
					CommandUtil.move(wraith, new Position(2 * X2 - X1 + r.nextInt(1), 2 * Y2 - Y1 + r.nextInt(1)));
					setSpecialAction(wraith, 0);
					return;
				}
			}

			Unit Terran_Valkyrie = MyUtil.getMostCloseEnemyUnit(UnitType.Terran_Valkyrie, wraith);
			if (Terran_Valkyrie != null && MyUtil.distanceTilePosition(Terran_Valkyrie.getTilePosition(), wraith.getTilePosition()) < 9) { // 발키리 사거리 7
				int X2 = wraith.getPosition().getX();
				int Y2 = wraith.getPosition().getY();
				int X1 = Terran_Valkyrie.getPosition().getX();
				int Y1 = Terran_Valkyrie.getPosition().getY();
				if (wraith.canUseTech(TechType.Cloaking_Field)) {
					CommandUtil.useTech(wraith, TechType.Cloaking_Field);
					return;
				} else {
					CommandUtil.move(wraith, new Position(2 * X2 - X1 + r.nextInt(1), 2 * Y2 - Y1 + r.nextInt(1)));
					setSpecialAction(wraith, 0);
					return;
				}
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
