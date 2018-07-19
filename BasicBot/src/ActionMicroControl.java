import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;

public class ActionMicroControl implements ActionInterface {
	CommandUtil commandUtil = new CommandUtil();

	@Override
	public void action() {
		TilePosition myStartLocation = MyBotModule.Broodwar.self().getStartLocation().getPoint();
		int selfCnt = MyVariable.attackUnit.size() + MyVariable.defenceUnit.size();
		int enemyCnt = MyVariable.enemyUnitAroundMyStartPoint.size();

		// 내 본진 주위에 다크템플러가 있고, 보이는 경우에 가장 먼저 공격한다.
		if (MyVariable.enemyUnitAroundMyStartPoint.size() > 0 && MyVariable.getEnemyUnit(UnitType.Protoss_Dark_Templar).size() > 0) {
			for (Unit enemyUnit : MyVariable.getEnemyUnit(UnitType.Protoss_Dark_Templar)) {
				if (enemyUnit.isCloaked() == false) {
					for (Unit selfUnit : MyVariable.attackUnit) {
						commandUtil.attackUnit(selfUnit, enemyUnit);
					}
					for (Unit selfUnit : MyVariable.defenceUnit) {
						commandUtil.attackUnit(selfUnit, enemyUnit);
					}
					return;
				}
			}
		}

		// 적과 나의 숫자가 작을 경우에만 동작
		if (enemyCnt >= 1 && selfCnt * enemyCnt < 200) {
			// if (enemyCnt >= 1) {
			double minDistance = Double.MAX_VALUE;
			Unit minUnit = null;
			Unit enemyUnit = null;

			if (enemyUnit == null) {
				for (Unit unit : MyVariable.attackUnit) {
					for (Unit unit2 : MyVariable.enemyUnitAroundMyStartPoint) {
						double distance = MyUtil.distancePosition(unit.getPosition(), unit2.getPosition());
						if (unit.getType().groundWeapon() != null && unit.getType().groundWeapon().maxRange() > distance)
							if (minDistance > distance) {
								minDistance = distance;
								minUnit = unit;
								enemyUnit = unit2;
							}
					}
				}

				for (Unit unit : MyVariable.defenceUnit) {
					for (Unit unit2 : MyVariable.enemyUnitAroundMyStartPoint) {
						double distance = MyUtil.distancePosition(unit.getPosition(), unit2.getPosition());
						if (unit.getType().groundWeapon() != null && unit.getType().groundWeapon().maxRange() > distance)
							if (minDistance > distance) {
								minDistance = distance;
								minUnit = unit;
								enemyUnit = unit2;
							}
					}
				}
			}

			if (selfCnt < enemyCnt && enemyUnit != null) {
				// SCV가 더 가까우면 제외한다.
				for (Unit unit : MyVariable.getSelfUnit(UnitType.Terran_SCV)) {
					double distance = MyUtil.distancePosition(unit.getPosition(), enemyUnit.getPosition());
					if (minDistance + 10 > distance) {
						minDistance = distance;
						minUnit = null;
					}
				}
			}

			// 가장 가까운 유닛은 도망간다.
			if (minUnit != null) {
				commandUtil.attackMove(minUnit, MyVariable.myFirstchokePoint.getPoint().toPosition());
			}

			// 적의 숫자가 많으면 SCV를 동원한다.
			if (selfCnt < enemyCnt * 2 && selfCnt < 10) {
				int cnt = 0;
				for (Unit unit : MyVariable.getSelfUnit(UnitType.Terran_SCV)) {
					double distance1 = 50;

					if (MyVariable.mostCloseBunker != null) {
						distance1 = MyUtil.distanceTilePosition(MyVariable.mostCloseBunker.getTilePosition(), myStartLocation.getPoint());
					}
					double distance2 = MyUtil.distanceTilePosition(myStartLocation.getPoint(), unit.getPoint().toTilePosition());

					if (distance1 + 3 <= distance2) {
						commandUtil.attackMove(unit, myStartLocation.getPoint().toPosition());
					} else {
						cnt++;
						if (cnt > 10) {
							break;
						}
						enemyUnit = null;
						minDistance = Double.MAX_VALUE;
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
							commandUtil.attackUnit(unit, enemyUnit);
						}
					}
				}

			}

		} else {
			selfCnt = MyVariable.attackUnit.size();
			enemyCnt = MyVariable.enemyAttactUnit.size();

			// 적과 나의 숫자가 작을 경우에만 동작
			if (enemyCnt >= 1 && selfCnt * enemyCnt < 200) {
				// if (enemyCnt >= 1) {
				double minDistance = Double.MAX_VALUE;
				Unit minUnit = null;
				for (Unit unit : MyVariable.attackUnit) {
					for (Unit unit2 : MyVariable.enemyAttactingUnit) {
						double distance = MyUtil.distancePosition(unit.getPosition(), unit2.getPosition());
						if (unit.getType().groundWeapon() != null && unit.getType().groundWeapon().maxRange() > distance)
							if (minDistance > distance) {
								minDistance = distance;
								minUnit = unit;
							}
					}
				}

				// 가장 가까운 유닛은 도망간다.
				if (minUnit != null) {
					double distance = MyUtil.distancePosition(minUnit.getPosition(), InformationManager.Instance().selfPlayer.getStartLocation().toPosition());
					if (distance > 20) {
						minUnit.move(InformationManager.Instance().selfPlayer.getStartLocation().toPosition());
					}
				}
			}
		}
	}

}
