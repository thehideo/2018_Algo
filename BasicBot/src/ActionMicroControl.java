import java.util.HashMap;

import bwapi.Race;
import bwapi.TechType;
import bwapi.Unit;
import bwapi.UnitType;

public class ActionMicroControl implements ActionInterface {
	@Override
	public void action() {
		checkBunker();
		scvControl();

		// 모아 가기 위해서 맨 앞에 유닛을 뒤로 보낸다.
		if (MyVariable.isFullScaleAttackStarted == true && MyVariable.distanceOfMostFarAttackUnit > 40) {
			CommandUtil.attackMove(MyVariable.mostFarAttackUnit, MyVariable.myStartLocation.toPosition());
		}

		if (MyBotModule.Broodwar.enemy().getRace() == Race.Protoss) {
			underStormControl();
			enemyDarkTempler();
			ghostControl();
			// goliathControl();
		}

		// 러커 또는 다크템플러가 확인되었는데 스캔할 방법이 없으면 터렛으로 도망
		if ((MyVariable.findLucker == true || MyVariable.findDarkTempler) && MyUtil.canUseScan() == false && MyVariable.getSelfUnit(UnitType.Terran_Science_Vessel).size() == 0) {
			if (MyVariable.mostFarTurret != null) {
				for (Unit unit : MyVariable.attackUnit) {
					CommandUtil.attackMove(unit, MyVariable.mostFarTurret.getPosition());
				}
			}
			return;
		}
	}

	public void scvControl() {
		int selfCnt = MyVariable.attackUnit.size() + MyVariable.defenceUnit.size();
		int enemyCnt = MyVariable.enemyUnitAroundMyStartPoint.size();

		// 적의 숫자가 많으면 SCV를 동원한다. (초반에만 동작)
		if (selfCnt < enemyCnt * 1.5 && selfCnt < 10 && MyBotModule.Broodwar.getFrameCount() < 12000 && MyVariable.distanceOfMostCloseEnemyUnit < MyVariable.distanceOfMostCloseBunker + 3) {
			int cnt = 0;
			for (Unit unit : MyVariable.getSelfUnit(UnitType.Terran_SCV)) {
				double distance1 = 50;

				if (MyVariable.mostCloseBunker != null) {
					distance1 = MyUtil.distanceTilePosition(MyVariable.myStartLocation.getPoint(), MyVariable.mostCloseBunker.getTilePosition()) + 3;
				}
				double distance2 = MyUtil.distanceTilePosition(MyVariable.myStartLocation.getPoint(), unit.getPoint().toTilePosition());

				if (distance1 <= distance2) {
					// WorkerManager.Instance().
				} else {
					cnt++;
					if (cnt >= 8 || cnt >= MyVariable.getSelfUnit(UnitType.Terran_SCV).size() - 4) {
						break;
					}
					Unit enemyUnit = null;
					Double minDistance = Double.MAX_VALUE;
					for (Unit unit2 : MyVariable.enemyUnitAroundMyStartPoint) {
						if (unit2.isFlying() == false && unit2.getType() != UnitType.Terran_SCV && unit2.getType() != UnitType.Protoss_Probe && unit2.getType() != UnitType.Zerg_Drone) {
							double distance = MyUtil.distancePosition(unit.getPosition(), unit2.getPosition());
							if (minDistance > distance) {
								minDistance = distance;
								enemyUnit = unit2;
							}
						}
					}

					if (enemyUnit != null) {
						CommandUtil.attackUnit(unit, enemyUnit);
					}
				}
			}
		}
	}

	public void underStormControl() {
		// 싸이오닉 스톰 맞으면 뒤로 뺀다.
		for (Unit selfUnit : MyVariable.underStormUnit) {

			CommandUtil.move(selfUnit, MyVariable.myStartLocation.toPosition());
		}
	}

	public void enemyDarkTempler() {
		// 주위에 다크템플러가 있고, 보이는 경우에 가장 먼저 공격한다.
		for (Unit enemyUnit : MyVariable.getEnemyUnit(UnitType.Protoss_Dark_Templar)) {
			if (enemyUnit.isCloaked() == false) {
				for (Unit selfUnit : MyVariable.attackUnit) {
					CommandUtil.attackUnit(selfUnit, enemyUnit);
				}
				for (Unit selfUnit : MyVariable.defenceUnit) {
					CommandUtil.attackUnit(selfUnit, enemyUnit);
				}
				return;
			}
		}
	}

	public void goliathControl() {
		// 주위에 캐리어가 있고, 각 골리앗 마다 가장 가까운 녀석을 공격한다.
		if (MyVariable.getEnemyUnit(UnitType.Protoss_Carrier).size() > 0) {
			for (Unit selfUnit : MyVariable.getSelfUnit(UnitType.Terran_Goliath)) {
				Unit mostCloseCarrier = getMostCloseEnemyUnit(UnitType.Protoss_Carrier, selfUnit);
				if (mostCloseCarrier != null) {
					if (mostCloseCarrier.getDistance(selfUnit) < 1200) {
						CommandUtil.attackUnit(selfUnit, mostCloseCarrier);
					}
				}
			}
		}
	}

	HashMap<Unit, Integer> mapLockDown = new HashMap<Unit, Integer>();

	public void ghostControl() {

		for (Unit ghost : MyVariable.getSelfUnit(UnitType.Terran_Ghost)) {
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
				}
			}

			if (ghost.isIdle() && MyVariable.mostFarTank != null) {
				CommandUtil.attackMove(ghost, MyVariable.mostFarTank.getPosition());
			}
		}
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

	public void wraithControl() {
		// 적이 근처에 없으면 클로킹 해제
		if (MyVariable.enemyAttactUnit.size() == 0) {
			for (Unit Wraith : MyVariable.getSelfUnit(UnitType.Terran_Wraith)) {
				if (Wraith.isCloaked() == true) {
					Wraith.decloak();
					CommandUtil.commandHash.add(Wraith);
				}
			}
		} else {
			// 전쟁 상황이면 클로킹
			for (Unit wraith : MyVariable.getSelfUnit(UnitType.Terran_Wraith)) {
				if (wraith.isAttackFrame() == true) {
					if (wraith.canUseTech(TechType.Cloaking_Field)) {
						wraith.useTech(TechType.Cloaking_Field);
						CommandUtil.commandHash.add(wraith);
					}
				}
			}

			// 옵저버가 보이면 바로 공격
			for (Unit unit : MyVariable.getEnemyUnit(UnitType.Protoss_Observer)) {
				if (unit.isDetected() == true) {
					for (Unit wraith : MyVariable.getSelfUnit(UnitType.Terran_Wraith)) {
						if (wraith.isCloaked() == true) {
							CommandUtil.attackUnit(wraith, unit);
						}
					}
				} else {

					MyVariable.needTerran_Science_Vessel = true;

					boolean aroundWraith = false;
					for (Unit wraith : MyVariable.getSelfUnit(UnitType.Terran_Wraith)) {
						if (wraith.getDistance(unit) < 400) {
							aroundWraith = true;
						}
					}
					if (aroundWraith == true) {
						ActionUseScanner.useScanner_Sweep(unit);
					}

				}
			}

			for (Unit photon_cannon : MyVariable.getEnemyUnit(UnitType.Protoss_Photon_Cannon)) {
				for (Unit wraith : MyVariable.getSelfUnit(UnitType.Terran_Wraith)) {
					if (photon_cannon.getDistance(wraith) < photon_cannon.getType().airWeapon().maxRange() + 10) {
						CommandUtil.move(wraith, MyVariable.myStartLocation.toPosition());
					}
				}
			}

			for (Unit Dragoon : MyVariable.getEnemyUnit(UnitType.Protoss_Dragoon)) {
				for (Unit wraith : MyVariable.getSelfUnit(UnitType.Terran_Wraith)) {
					if (wraith.isDetected() == true && Dragoon.getDistance(wraith) < Dragoon.getType().airWeapon().maxRange() + 10) {
						CommandUtil.move(wraith, MyVariable.myStartLocation.toPosition());
					}
				}
			}

			for (Unit Corsair : MyVariable.getEnemyUnit(UnitType.Protoss_Corsair)) {
				for (Unit wraith : MyVariable.getSelfUnit(UnitType.Terran_Wraith)) {
					if (wraith.isDetected() == true && Corsair.getDistance(wraith) < Corsair.getType().airWeapon().maxRange() + 10) {
						CommandUtil.move(wraith, MyVariable.myStartLocation.toPosition());
					}
				}
			}

			for (Unit unit : MyVariable.getEnemyUnit(UnitType.Protoss_Carrier)) {
				for (Unit wraith : MyVariable.getSelfUnit(UnitType.Terran_Wraith)) {
					CommandUtil.attackUnit(wraith, unit);
				}
			}

			for (Unit unit : MyVariable.getEnemyUnit(UnitType.Protoss_Probe)) {
				for (Unit wraith : MyVariable.getSelfUnit(UnitType.Terran_Wraith)) {
					if (wraith.isCloaked() == true) {
						CommandUtil.attackUnit(wraith, unit);
					}
				}
			}
		}
	}

	public void checkBunker() {
		for (Unit unit : MyVariable.getSelfUnit(UnitType.Terran_Bunker)) {
			if (unit.isCompleted()) {
				// 총 공격일 때는 다 꺼냄
				if (MyVariable.isFullScaleAttackStarted == true) {
					if (unit.getLoadedUnits().size() > 0) {
						unit.unloadAll();
					}
				} else {
					if (unit.getLoadedUnits().size() < 4) {
						for (Unit unit2 : MyVariable.getSelfUnit(UnitType.Terran_Marine)) {
							if (unit2.isCompleted() && unit2.isLoaded() == false) {
								CommandUtil.commandHash.add(unit2);

								unit2.rightClick(unit);
								MyVariable.bunkerUnit.add(unit);
								MyVariable.attackUnit.remove(unit);
							}
						}
						return;
					}
				}
			}
		}
	}

	public Unit getMostCloseEnemyUnit(UnitType unitType, Unit myUnit) {
		Unit mostCloseEnemyUnit = null;
		double minDistance = Double.MAX_VALUE;
		for (Unit enemyUnit : MyVariable.getEnemyUnit(unitType)) {
			double distance = MyUtil.distanceTilePosition(myUnit.getTilePosition(), enemyUnit.getTilePosition());
			if (minDistance > distance) {
				minDistance = distance;
				mostCloseEnemyUnit = enemyUnit;
			}
		}
		return mostCloseEnemyUnit;
	}

}
