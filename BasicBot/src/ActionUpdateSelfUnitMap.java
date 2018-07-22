import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;

public class ActionUpdateSelfUnitMap implements ActionInterface {

	int refreshIndex = 0;

	TilePosition myStartLocation = MyBotModule.Broodwar.self().getStartLocation().getPoint();

	static boolean findMineral = false;

	@Override
	public void action() {
		MyVariable.clearSelfUnit();

		if (findMineral == false) {
			findMineral = true;
			for (Unit unit : MyBotModule.Broodwar.getAllUnits()) {
				if (unit.getType() == UnitType.Resource_Mineral_Field) {
					MyVariable.minerals.add(unit);
				}
			}
		}

		for (Unit unit : MyBotModule.Broodwar.self().getUnits()) {
			// 벙커 안에 있는 것은 스킵

			if (unit.getType() == UnitType.Terran_Vulture_Spider_Mine) {
				if (!MyVariable.spinderMinePosition.contains(unit.getPosition())) {
					MyVariable.spinderMinePosition.add(unit.getPosition());
				}
			}

			// 발견되지 않는 Type 추가
			MyVariable.getSelfUnit(unit.getType()).add(unit);

			// defenceUnit에 할당
			if (!setUnitAsDefence(unit)) {
				if (!setUnitAsPatrol(unit)) {
					// scanUnit에 할당
					if (unit.getType() == UnitType.Terran_Science_Vessel) {
						MyVariable.scanUnit.add(unit);
					}
					// 본진 밖을 벗어난 SCV는 다시 본진으로 위치 시킨다
					else if (unit.getType() == UnitType.Terran_SCV) {
						if (unit.isAttackFrame() == true) {
							double distance = MyUtil.distanceTilePosition(unit.getTilePosition(), myStartLocation);
							if (distance > 20) {
								unit.move(myStartLocation.toPosition());
							}
						}
					}
					// 공격 유닛
					// else if (unit.isLoaded() == false && (unit.canAttack() || unit.getType() ==
					// UnitType.Terran_Medic)) {
					else if (unit.isLoaded() == false && unit.getType().isBuilding() == false) {
						MyVariable.attackUnit.add(unit);
						MyVariable.enemyBuildingUnit.remove(unit.getTilePosition());
						// 그 위치에 갔지만 인식이 안되는 경우를 대비해서
						refreshIndex++;
						if (refreshIndex % 3 == 0) {
							int x = unit.getTilePosition().getX();
							int y = unit.getTilePosition().getY();

							MyVariable.enemyBuildingUnit.remove(new TilePosition(x - 1, y - 1));
							MyVariable.enemyBuildingUnit.remove(new TilePosition(x - 1, y - 0));
							MyVariable.enemyBuildingUnit.remove(new TilePosition(x - 1, y + 1));

							MyVariable.enemyBuildingUnit.remove(new TilePosition(x, y - 1));
							MyVariable.enemyBuildingUnit.remove(new TilePosition(x, y + 1));

							MyVariable.enemyBuildingUnit.remove(new TilePosition(x + 1, y - 1));
							MyVariable.enemyBuildingUnit.remove(new TilePosition(x + 1, y - 0));
							MyVariable.enemyBuildingUnit.remove(new TilePosition(x + 1, y + 1));
						}

						MyVariable.getSelfAttackUnit(unit.getType()).add(unit);

						// 가장 멀리있는 공격 유닛 확인
						double distance = MyUtil.distanceTilePosition(unit.getTilePosition(), myStartLocation);
						if (MyVariable.distanceOfMostFarAttackUnit < distance) {
							MyVariable.distanceOfMostFarAttackUnit = distance;
							MyVariable.mostFarAttackUnit = unit;
						}

					}
					if (unit.getType() == UnitType.Terran_Siege_Tank_Siege_Mode || unit.getType() == UnitType.Terran_Siege_Tank_Tank_Mode) {
						double distance = MyUtil.distanceTilePosition(unit.getTilePosition(), myStartLocation);
						if (MyVariable.distanceOfMostFarTank < distance) {
							MyVariable.distanceOfMostFarTank = distance;

							MyVariable.mostFarTank = unit;
						}

					}

					if (unit.getType() == UnitType.Terran_Missile_Turret) {
						double distance = MyUtil.distanceTilePosition(unit.getTilePosition(), myStartLocation);
						if (MyVariable.distanceOfMostFarTurret < distance) {
							MyVariable.distanceOfMostFarTurret = distance;
							MyVariable.mostFarTurret = unit;
						}
					}

					if (unit.getType() == UnitType.Terran_Bunker) {
						double distance = MyUtil.distanceTilePosition(unit.getTilePosition(), myStartLocation);
						if (MyVariable.distanceOfMostCloseBunker > distance) {
							MyVariable.distanceOfMostCloseBunker = distance;

							MyVariable.mostCloseBunker = unit;
						}

					}
				}
			}

			// 공격당하고 있는 유닛
			if (unit.isUnderAttack()) {
				MyVariable.attackedUnit.add(unit);
			}
		}
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

	// 방어 유닛 구성
	boolean setUnitAsPatrol(Unit unit) {
		boolean result = false;
		if (MyVariable.attackUnit.size() > 30) {
			if (MyVariable.patrolUnitCountTotal.containsKey(unit.getType())) {
				if (!MyVariable.patrolUnitCount.containsKey(unit.getType())) {
					MyVariable.patrolUnitCount.put(unit.getType(), 0);
				}
				int total = MyVariable.patrolUnitCountTotal.get(unit.getType());
				int now = MyVariable.patrolUnitCount.get(unit.getType());
				if (total > now) {
					MyVariable.patrolUnit.add(unit);
					MyVariable.patrolUnitCount.put(unit.getType(), now + 1);
					result = true;
				}
			}
		}
		return result;
	}
}
