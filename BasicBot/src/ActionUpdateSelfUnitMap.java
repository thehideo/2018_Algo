import java.util.ArrayList;

import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;

public class ActionUpdateSelfUnitMap implements ActionInterface {

	int refreshIndex = 0;

	TilePosition myStartLocation = MyBotModule.Broodwar.self().getStartLocation().getPoint();

	@Override
	public void action() {
		MyVariable.clearSelfUnit();

		for (Unit unit : MyBotModule.Broodwar.self().getUnits()) {
			// 벙커 안에 있는 것은 스킵
			if (unit.isLoaded() == true) {
				continue;
			}
			// 발견되지 않는 Type 추가
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
				else if (unit.canAttack()) {
					MyVariable.attackUnit.add(unit);

					MyVariable.enemyBuildingUnit.remove(unit.getTilePosition());

					// 그 위치에 갔지만 인식이 안되는 경우를 대비해서
					refreshIndex++;
					if (refreshIndex % 50 == 0) {
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
					MyVariable.mapSelfAttackUnit.get(unit.getType()).add(unit);
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

}
