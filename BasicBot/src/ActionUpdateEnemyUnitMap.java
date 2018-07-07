import java.util.ArrayList;

import bwapi.TechType;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;

public class ActionUpdateEnemyUnitMap implements ActionInterface {

	@Override
	public void action() {
		MyVariable.clearEnemyUnit();

		TilePosition myStartLocation = MyBotModule.Broodwar.self().getStartLocation().getPoint();

		for (Unit unit : MyBotModule.Broodwar.enemy().getUnits()) {
			// 정상 유닛이면
			if (unit.exists() && unit.getType() != UnitType.Unknown && unit.getPosition().isValid()) {

				if (!MyVariable.mapEnemyUnit.containsKey(unit.getType())) {
					MyVariable.mapEnemyUnit.put(unit.getType(), new ArrayList<Unit>());
				}
				MyVariable.mapEnemyUnit.get(unit.getType()).add(unit);

				// 건물
				if (unit.getType().isBuilding()) {
					if (!unit.getType().isRefinery()) {
						MyVariable.enemyBuildingUnit.add(unit.getTilePosition());
					}
				}
				// 일반 공격
				else {
					MyVariable.enemyAttactUnit.add(unit);
				}
				// 지상
				if (unit.isFlying() == false) {
					MyVariable.enemyGroundUnit.add(unit);
				}

				// 내 본진 근처 적유닛
				double distance = MyUtil.distanceTilePosition(unit.getTilePosition(), myStartLocation);
				if (distance < 20) {
					MyVariable.enemyUnitAroundMyStartPoint.add(unit);
				}
			}
		}

	}

}