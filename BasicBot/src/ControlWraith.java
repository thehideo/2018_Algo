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
			}
		} else {
			// 전쟁 상황이면 클로킹
			if (wraith.isUnderAttack() == true) {
				if (wraith.canUseTech(TechType.Cloaking_Field)) {
					CommandUtil.useTech(wraith, TechType.Cloaking_Field);
				}
			}
		}
		if (InformationManager.Instance().enemyRace == Race.Terran) {
			terranAction(wraith, groupAbstract);
		}

		CommandUtil.attackMove(wraith, groupAbstract.getTargetPosition(wraith));
	}

	void terranAction(Unit wraith, GroupAbstract groupAbstract) {

		for (Unit unit : MyVariable.getEnemyUnit(UnitType.Terran_Missile_Turret)) {
			CommandUtil.move(wraith, MyVariable.myStartLocation.toPosition());
			return;
		}
		for (Unit unit : MyVariable.getEnemyUnit(UnitType.Terran_Goliath)) {
			CommandUtil.move(wraith, MyVariable.myStartLocation.toPosition());
			return;
		}
		for (Unit unit : MyVariable.getEnemyUnit(UnitType.Terran_SCV)) {
			CommandUtil.attackUnit(wraith, unit);
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
