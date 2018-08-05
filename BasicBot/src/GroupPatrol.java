import java.util.ArrayList;
import java.util.Collections;
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
		this.mapUnitTotal.put(UnitType.Terran_Goliath, MyVariable.getSelfUnit(UnitType.Terran_Goliath).size() / 5);

		if (listTilePosition.size() > 0) {
			for (UnitType unitType : this.mapUnit.keySet()) {
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

		// 이미 점유되고 있는 곳이면 제거한다.
		if (listTilePosition.size() > 0) {
			TilePosition tilePosition = listTilePosition.get(0);
			if (tilePosition.equals(bl1.getTilePosition()) || tilePosition.equals(bl2.getTilePosition()) && tilePosition.equals(bl3.getTilePosition()) || tilePosition.equals(bl4.getTilePosition()) || MyVariable.mapSelfMainBuilding.contains(tilePosition) || MyVariable.mapEnemyMainBuilding.contains(tilePosition) || MyVariable.enemyBuildingUnit.contains(tilePosition)) {
				listTilePosition.remove(0);
			}
		}

		if (listTilePosition.size() == 0) {
			List<BaseLocation> listBaseLocation = BWTA.getBaseLocations();
			if (bl3 != null && bl4 != null) {

				ArrayList<TilePosition> tmpList = new ArrayList<TilePosition>();

				for (BaseLocation bl : listBaseLocation) {
					tmpList.add(bl.getTilePosition());
				}
				Collections.sort(tmpList, new ComparatorBaseLocation());

				int indexB1 = 0;

				for (int i = 0; i < tmpList.size(); i++) {
					if (tmpList.get(i).equals(bl1.getTilePosition())) {
						indexB1 = i;
					}
				}

				for (int i = tmpList.size() - 1; i >= 0; i--) {
					if (tmpList.get(i).getX() >= 50 && tmpList.get(i).getX() <= 70 && tmpList.get(i).getY() >= 50 && tmpList.get(i).getY() <= 70) {
						tmpList.remove(i);
					}
				}

				int index = indexB1;
				boolean findEneemy = false;
				while (findEneemy == false) {
					if (tmpList.get(index).equals(bl3.getTilePosition()) || tmpList.get(index).equals(bl4.getTilePosition())) {
						findEneemy = true;
						break;
					}
					if (!tmpList.get(index).equals(bl1.getTilePosition()) && !tmpList.get(index).equals(bl2.getTilePosition()) && !tmpList.get(index).equals(bl3.getTilePosition()) && !tmpList.get(index).equals(bl4.getTilePosition())) {
						listTilePosition.add(tmpList.get(index));
					}
					index++;
					if (index >= tmpList.size()) {
						index = 0;
					}
				}
				index--;
				if (index < 0)
					index = tmpList.size() - 1;
				findEneemy = false;
				while (findEneemy == false) {
					if (tmpList.get(index).equals(bl3.getTilePosition()) || tmpList.get(index).equals(bl4.getTilePosition())) {
						findEneemy = true;
						break;
					}
					if (!tmpList.get(index).equals(bl1.getTilePosition()) && !tmpList.get(index).equals(bl2.getTilePosition()) && !tmpList.get(index).equals(bl3.getTilePosition()) && !tmpList.get(index).equals(bl4.getTilePosition())) {
						listTilePosition.add(tmpList.get(index));
					}
					index--;
					if (index < 0) {
						index = tmpList.size() - 1;
					}
				}
				index++;
				if (index > tmpList.size() - 1) {
					index = 0;
				}

				boolean findSelf = false;
				while (findSelf == false) {
					if (tmpList.get(index).equals(bl1.getTilePosition()) || tmpList.get(index).equals(bl2.getTilePosition())) {
						findSelf = true;
						break;
					}
					if (!tmpList.get(index).equals(bl1.getTilePosition()) && !tmpList.get(index).equals(bl2.getTilePosition()) && !tmpList.get(index).equals(bl3.getTilePosition()) && !tmpList.get(index).equals(bl4.getTilePosition())) {
						listTilePosition.add(tmpList.get(index));
					}
					index++;
					if (index >= tmpList.size()) {
						index = 0;
					}
				}
			}
		}

		if (listTilePosition.size() > 0) {
			this.targetPosition = listTilePosition.get(0).toPosition();
		}
	}
}
