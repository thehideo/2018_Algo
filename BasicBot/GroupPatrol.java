import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import bwapi.TilePosition;
import bwapi.UnitType;
import bwta.BWTA;
import bwta.BaseLocation;

public class GroupPatrol extends GroupAbstract {

	ArrayList<TilePosition> listTilePosition = new ArrayList<TilePosition>();

	@Override
	public void action() {
		// 정찰은 벌처가 한다.
		// 마린은 지정하면 안됨
		this.mapUnitTotal.put(UnitType.Terran_Vulture, 1);

		if (listTilePosition.size() > 0) {
			Iterator<UnitType> UnitTypes = this.mapUnit.keySet().iterator();
			while (UnitTypes.hasNext()) {
				UnitType unitType = UnitTypes.next();
				for (Integer unitID : mapUnit.get(unitType)) {
					if (MyUtil.distancePosition(MyVariable.mapUnitIDUnit.get(unitID).getPosition(), listTilePosition.get(0).toPosition()) < 32 * 1) {
						listTilePosition.remove(0);
						break;
					}

				}
			}
		}

		BaseLocation bl1 = InformationManager.Instance().getMainBaseLocation(MyBotModule.Broodwar.self());
		BaseLocation bl2 = InformationManager.Instance().getFirstExpansionLocation(MyBotModule.Broodwar.self());
		BaseLocation bl3 = InformationManager.Instance().getMainBaseLocation(MyBotModule.Broodwar.enemy());
		BaseLocation bl4 = InformationManager.Instance().getFirstExpansionLocation(MyBotModule.Broodwar.enemy());

		if (listTilePosition.size() > 0) {
			TilePosition tilePosition = listTilePosition.get(0);
			if (tilePosition.equals(bl1.getTilePosition()) || tilePosition.equals(bl2.getTilePosition()) && tilePosition.equals(bl3.getTilePosition()) || tilePosition.equals(bl4.getTilePosition()) || MyVariable.mapSelfMainBuilding.contains(tilePosition) || MyVariable.mapEnemyMainBuilding.contains(tilePosition) || MyVariable.enemyBuildingUnit.contains(tilePosition)) {
				listTilePosition.remove(0);
			}

		}

		if (listTilePosition.size() == 0) {
			List<BaseLocation> listBaseLocation = BWTA.getBaseLocations();

			if (bl3 != null && bl4 != null) {
				for (BaseLocation bl : listBaseLocation) {
					if (!bl.getTilePosition().equals(bl1.getTilePosition()) && !bl.getTilePosition().equals(bl2.getTilePosition()) && !bl.getTilePosition().equals(bl3.getTilePosition()) && !bl.getTilePosition().equals(bl4.getTilePosition()) && !MyVariable.mapSelfMainBuilding.contains(bl.getTilePosition()) && !MyVariable.mapEnemyMainBuilding.contains(bl.getTilePosition()) && !MyVariable.enemyBuildingUnit.contains(bl.getTilePosition())) {
						listTilePosition.add(bl.getTilePosition());
					}
				}
				Collections.sort(listTilePosition, new ComparatorBaseLocation());
			}
		}

		if (listTilePosition.size() > 0) {
			this.targetPosition = listTilePosition.get(0).toPosition();
		}
	}
}
