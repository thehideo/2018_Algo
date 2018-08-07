import java.util.ArrayList;
import java.util.List;

import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;
import bwta.BaseLocation;

public class GroupWraith extends GroupAbstract {

	int minPointX = Integer.MAX_VALUE;
	int maxPointX = Integer.MIN_VALUE;
	int minPointY = Integer.MAX_VALUE;
	int maxPointY = Integer.MIN_VALUE;

	List<TilePosition> tpList = null;

	public Position getTargetPosition(Unit unit) {
		Position result = null;

		if (targetPosition == null) {
			if (mapTargetPosition.containsKey(unit.getID())) {
				Position tmp = mapTargetPosition.get(unit.getID());
				if (MyUtil.distanceTilePosition(tmp.toTilePosition(), unit.getTilePosition()) < 5) {
					int x = MyUtil.random.nextInt(maxPointX);
					int y = MyUtil.random.nextInt(maxPointY);
					mapTargetPosition.put(unit.getID(), new TilePosition(x, y).toPosition());
				}
				result = mapTargetPosition.get(unit.getID());
			} else {

				int x = MyUtil.random.nextInt(maxPointX);
				int y = MyUtil.random.nextInt(maxPointY);
				mapTargetPosition.put(unit.getID(), new TilePosition(x, y).toPosition());

				result = mapTargetPosition.get(unit.getID());
			}
		} else {
			if (unit.getType() == UnitType.Terran_Siege_Tank_Siege_Mode || unit.getType() == UnitType.Terran_Siege_Tank_Tank_Mode) {
				result = targetPositionForTank;
			} else {
				result = targetPosition;
			}
		}

		return result;
	}

	@Override
	public void action() {

		if (MyVariable.enemyBuildingUnit.size() == 0 && MyVariable.isFullScaleAttackStarted == true) {

			targetPosition = null;
			targetPositionForTank = null;
		} else if (true) {
			return;
		}
		if (tpList == null) {
			tpList = new ArrayList<TilePosition>();
			List<BaseLocation> blList = BWTA.getBaseLocations();
			for (BaseLocation bl : blList) {
				TilePosition tp = bl.getTilePosition();
				TilePosition myStartLocation = MyVariable.myStartLocation;
				TilePosition myFirstExpansionLocation = InformationManager.Instance().getFirstExpansionLocation(MyBotModule.Broodwar.self()).getTilePosition();
				if (tp != myStartLocation && tp != myFirstExpansionLocation) {
					tpList.add(tp);
				}
			}
		}

		TilePosition enemyStartLocation = InformationManager.Instance().getMainBaseLocation(MyBotModule.Broodwar.enemy()).getTilePosition();
		TilePosition enemyFirstExpansionLocation = InformationManager.Instance().getFirstExpansionLocation(MyBotModule.Broodwar.enemy()).getTilePosition();

		targetPosition = enemyStartLocation.toPosition();
		/*
		 * Position position = MyUtil.GetMyBunkerPosition();
		 * 
		 * if (MyVariable.enemyUnitAroundMyStartPoint.size() > 0) { for (Unit enemyUnit
		 * : MyVariable.enemyUnitAroundMyStartPoint) { targetPosition =
		 * enemyUnit.getPosition(); break; } } else { if
		 * (MyVariable.isFullScaleAttackStarted == true || position == null) {
		 * targetPosition = MyVariable.myStartLocation.toPosition(); } else {
		 * targetPosition = MyUtil.getSaveChokePoint().getPoint(); } }
		 */
	}
}
