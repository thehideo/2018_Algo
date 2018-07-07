import java.util.ArrayList;

import bwapi.TechType;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;

public class ActionUpdateEnemyUnitMap implements ActionInterface {

	@Override
	public void action() {

		MyVariable.mapEnemyUnit.clear();
		MyVariable.enemyUnitAroundMyStartPoint.clear();
		TilePosition myStartLocation = MyBotModule.Broodwar.self().getStartLocation().getPoint();

		for (Unit unit : MyBotModule.Broodwar.enemy().getUnits()) {
			if (unit.getType().isBuilding() && unit.exists() && unit.getType() != UnitType.Unknown && unit.getPosition().isValid()) {
				if (!unit.getType().isRefinery()) {
					MyVariable.enemyBuildingUnit.add(unit.getTilePosition());
				}
			} else {
				if (unit.canAttack() == true) {
					MyVariable.enemyAttactUnit.add(unit);
				}
			}

			double distance = MyUtil.distance(unit.getTilePosition(), myStartLocation);
			if (distance < 20) {
				MyVariable.enemyUnitAroundMyStartPoint.add(unit);
			}
			if (!MyVariable.mapEnemyUnit.containsKey(unit.getType())) {
				MyVariable.mapEnemyUnit.put(unit.getType(), new ArrayList<Unit>());
			}
			MyVariable.mapEnemyUnit.get(unit.getType()).add(unit);
		}

	}

}
