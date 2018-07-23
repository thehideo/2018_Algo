import java.util.ArrayList;

import bwapi.TilePosition;
import bwapi.Unit;
import bwta.BWTA;
import bwta.BaseLocation;

public class ActionPatrolBaseLocation implements ActionInterface {
	ArrayList<TilePosition> tlList = new ArrayList<TilePosition>();

	@Override
	public void action() {
		if (tlList.size() <= 0) {
			BaseLocation main = InformationManager.Instance().getMainBaseLocation(MyBotModule.Broodwar.enemy());
			BaseLocation firstExpansionLocation = InformationManager.Instance().getFirstExpansionLocation(MyBotModule.Broodwar.enemy());
			for (BaseLocation bl : BWTA.getBaseLocations()) {
				if (bl != main && bl != firstExpansionLocation) {
					tlList.add(bl.getTilePosition());
				}
			}
		}

		if (tlList.size() > 0) {
			for (Unit unit : MyVariable.patrolUnit) {
				CommandUtil.attackMove(unit, tlList.get(0).toPosition());
				if (MyUtil.distanceTilePosition(unit.getTilePosition(), tlList.get(0)) < 2) {
					tlList.remove(0);
				}
				if (tlList.size() == 0) {
					break;
				}
			}
		}
	}
}
