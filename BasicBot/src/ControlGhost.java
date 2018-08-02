import java.util.HashMap;

import bwapi.TechType;
import bwapi.Unit;
import bwapi.UnitType;

public class ControlGhost extends ControlAbstract {

	static HashMap<Unit, Integer> mapLockDown = new HashMap<Unit, Integer>();

	void actionMain(Unit ghost, GroupAbstract groupAbstract) {

		// 캐논 근처에는 가지 않는다
		for (Unit photon_cannon : MyVariable.getEnemyUnit(UnitType.Protoss_Photon_Cannon)) {
			if (photon_cannon.getDistance(ghost) < photon_cannon.getType().airWeapon().maxRange() + 10) {
				CommandUtil.move(ghost, MyVariable.myStartLocation.toPosition());
			}
		}

		// 클로킹 되어있는데 적이 없으면 decloak
		if (ghost.isCloaked() == true && MyVariable.enemyAttactUnit.size() == 0) {
			if (!CommandUtil.commandHash.contains(ghost)) {
				CommandUtil.commandHash.add(ghost);
				ghost.decloak();
			}
		}

		// 공격 받고 있으면 클로킹 사용
		if (ghost.isUnderAttack() == true && ghost.isCloaked() == false) {
			if (ghost.canUseTech(TechType.Personnel_Cloaking)) {
				if (!CommandUtil.commandHash.contains(ghost)) {
					CommandUtil.commandHash.add(ghost);
					ghost.useTech(TechType.Personnel_Cloaking);
				}
			}
		}

		// 공격 받고 있으면 본진으로 도망, 위에 클로킹 사용이 우선
		if (ghost.isUnderAttack() == true && ghost.isCloaked() == false) {
			if (!CommandUtil.commandHash.contains(ghost)) {
				CommandUtil.commandHash.add(ghost);
				CommandUtil.move(ghost, MyVariable.myStartLocation.toPosition());
			}
		}

		// 락다운 사용할 캐리어가 있으면 락다운 사용
		Unit carrier = getMostCloseCarrierLockDown(ghost);
		if (carrier != null && ghost.canUseTech(TechType.Lockdown, carrier)) {
			if (!CommandUtil.commandHash.contains(ghost)) {
				CommandUtil.commandHash.add(ghost);
				ghost.useTech(TechType.Lockdown, carrier);
				mapLockDown.put(carrier, MyBotModule.Broodwar.getFrameCount());
				setSpecialAction(ghost);
			}
		}

		if (ghost.isIdle() && MyVariable.mostFarTank != null) {
			CommandUtil.attackMove(ghost, MyVariable.mostFarTank.getPosition());
		}

		CommandUtil.attackMove(ghost, groupAbstract.getTargetPosition(ghost));
	}

	public Unit getMostCloseCarrierLockDown(Unit myUnit) {
		Unit mostCloseEnemyUnit = null;
		double minDistance = Double.MAX_VALUE;
		for (Unit enemyUnit : MyVariable.getEnemyUnit(UnitType.Protoss_Carrier)) {
			if (!mapLockDown.containsKey(enemyUnit)) {
				mapLockDown.put(enemyUnit, 0);
			}
			int lockDownBeforTime = mapLockDown.get(enemyUnit);
			if (MyBotModule.Broodwar.getFrameCount() - lockDownBeforTime > 48) {
				double distance = MyUtil.distanceTilePosition(myUnit.getTilePosition(), enemyUnit.getTilePosition());
				if (minDistance > distance && enemyUnit.isLockedDown() == false) {
					minDistance = distance;
					mostCloseEnemyUnit = enemyUnit;
				}
			}
		}
		return mostCloseEnemyUnit;
	}

}
