import java.util.HashMap;

import bwapi.Race;
import bwapi.TechType;
import bwapi.Unit;
import bwapi.UnitType;

public class ControlWraith extends ControlAbstract {

	HashMap<Integer, Boolean> mapMode = new HashMap<Integer, Boolean>();

	void actionMain(Unit wraith, GroupAbstract groupAbstract) {
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

		// Wraith를 공격할 유닛 개수가 작으면 Wraith가 공격한다.
		if (groupAbstract == GroupManager.instance().groupAttack) {
			int sizeEnemy =MyVariable.getEnemyUnit(UnitType.Terran_Valkyrie).size()+ MyVariable.getEnemyUnit(UnitType.Terran_Goliath).size() + MyVariable.getEnemyUnit(UnitType.Terran_Missile_Turret).size() + MyVariable.getEnemyUnit(UnitType.Terran_Marine).size() + MyVariable.getEnemyUnit(UnitType.Terran_Bunker).size() * 3 + MyVariable.getEnemyUnit(UnitType.Terran_Battlecruiser).size() * 2;
			int sizeSelf = groupAbstract.mapUnit.get(UnitType.Terran_Wraith).size();
			if (sizeEnemy * 5 < sizeSelf) {
				Unit Terran_Missile_Turret = MyUtil.getMostCloseEnemyUnit(UnitType.Terran_Missile_Turret, wraith);
				if (Terran_Missile_Turret != null) {
					CommandUtil.attackUnit(wraith, Terran_Missile_Turret);
					return;
				}
				Unit Terran_Goliath = MyUtil.getMostCloseEnemyUnit(UnitType.Terran_Goliath, wraith);
				if (Terran_Goliath != null) {
					CommandUtil.attackUnit(wraith, Terran_Goliath);
					return;
				}
				
				Unit Terran_Marine = MyUtil.getMostCloseEnemyUnit(UnitType.Terran_Marine, wraith);
				if (Terran_Marine != null) {
					CommandUtil.attackUnit(wraith, Terran_Marine);
					return;
				}
				Unit Terran_Wraith = MyUtil.getMostCloseEnemyUnit(UnitType.Terran_Wraith, wraith);
				if (Terran_Wraith != null) {
					CommandUtil.attackUnit(wraith, Terran_Wraith);
					return;
				}
				Unit Terran_Bunker = MyUtil.getMostCloseEnemyUnit(UnitType.Terran_Bunker, wraith);
				if (Terran_Bunker != null) {
					CommandUtil.attackUnit(wraith, Terran_Bunker);
					return;
				}
				Unit Terran_Battlecruiser = MyUtil.getMostCloseEnemyUnit(UnitType.Terran_Battlecruiser, wraith);
				if (Terran_Battlecruiser != null) {
					CommandUtil.attackUnit(wraith, Terran_Battlecruiser);
					return;
				}
				Unit Terran_Valkyrie = MyUtil.getMostCloseEnemyUnit(UnitType.Terran_Valkyrie, wraith);
				if (Terran_Valkyrie != null) {
					CommandUtil.attackUnit(wraith, Terran_Valkyrie);
					return;
				}
			}
		}

		if (InformationManager.Instance().enemyRace == Race.Terran) {
			terranAction(wraith, groupAbstract);
		}

		if (groupAbstract == GroupManager.instance().groupAttack && MyVariable.enemyAttactUnit.size() > 0 && !CommandUtil.commandHash.contains(wraith)) {
			Unit mostCloseEnemy = MyUtil.getMostCloseEnemyUnit(wraith, MyVariable.enemyAttactUnit);
			if (mostCloseEnemy != null) {
				CommandUtil.attackUnit(wraith, mostCloseEnemy);
				return;
			}
		}

		CommandUtil.attackMove(wraith, groupAbstract.getTargetPosition(wraith));
	}

	void terranAction(Unit wraith, GroupAbstract groupAbstract) {
		Unit Terran_Missile_Turret = MyUtil.getMostCloseEnemyUnit(UnitType.Terran_Missile_Turret, wraith);
		if (Terran_Missile_Turret != null && MyUtil.distanceTilePosition(Terran_Missile_Turret.getTilePosition(), wraith.getTilePosition()) < 10) {
			CommandUtil.move(wraith, MyVariable.myStartLocation.toPosition());
			return;
		}

		Unit Terran_Goliath = MyUtil.getMostCloseEnemyUnit(UnitType.Terran_Goliath, wraith);
		if (Terran_Goliath != null && MyUtil.distanceTilePosition(Terran_Goliath.getTilePosition(), wraith.getTilePosition()) < 15) {
			CommandUtil.move(wraith, MyVariable.myStartLocation.toPosition());
			return;
		}

		Unit Terran_Marine = MyUtil.getMostCloseEnemyUnit(UnitType.Terran_Marine, wraith);
		if (Terran_Marine != null && MyUtil.distanceTilePosition(Terran_Marine.getTilePosition(), wraith.getTilePosition()) < 10) {
			CommandUtil.move(wraith, MyVariable.myStartLocation.toPosition());
			return;
		}
		
		Unit Terran_Bunker = MyUtil.getMostCloseEnemyUnit(UnitType.Terran_Bunker, wraith);
		if (Terran_Bunker != null && MyUtil.distanceTilePosition(Terran_Bunker.getTilePosition(), wraith.getTilePosition()) < 10) {
			CommandUtil.move(wraith, MyVariable.myStartLocation.toPosition());
			return;
		}
		
		Unit Terran_Valkyrie = MyUtil.getMostCloseEnemyUnit(UnitType.Terran_Valkyrie, wraith);
		if (Terran_Valkyrie != null && MyUtil.distanceTilePosition(Terran_Valkyrie.getTilePosition(), wraith.getTilePosition()) < 15) {
			CommandUtil.move(wraith, MyVariable.myStartLocation.toPosition());
			return;
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
