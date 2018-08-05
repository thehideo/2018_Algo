import java.util.ArrayList;
import java.util.List;

import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;
import bwta.BWTA;
import bwta.BaseLocation;

public class GroupWraith extends GroupAbstract {

	List<TilePosition> tpList = null;

	@Override
	public void action() {
		
		if(true) {
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
