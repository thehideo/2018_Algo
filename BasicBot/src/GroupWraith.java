import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bwapi.TilePosition;
import bwapi.UnitType;
import bwta.BWTA;
import bwta.BaseLocation;

public class GroupWraith extends GroupAbstract {
	@Override
	public void action() {
		if (MyUtil.GetMyTankCnt() >= 4) {
			this.mapUnitTotal.put(UnitType.Terran_Wraith, MyVariable.getSelfUnit(UnitType.Terran_Wraith).size() / 3);
		}
	}

	static int cnt = 0;

	static ArrayList<TilePosition> makeNewList() {

		BaseLocation bl1 = InformationManager.Instance().getMainBaseLocation(MyBotModule.Broodwar.self());
		BaseLocation bl2 = InformationManager.Instance().getFirstExpansionLocation(MyBotModule.Broodwar.self());
		BaseLocation bl3 = InformationManager.Instance().getMainBaseLocation(MyBotModule.Broodwar.enemy());
		BaseLocation bl4 = InformationManager.Instance().getFirstExpansionLocation(MyBotModule.Broodwar.enemy());

		if (bl1 == null | bl2 == null || bl3 == null || bl4 == null) {
			return new ArrayList<TilePosition>();
		}

		ArrayList<TilePosition> listTilePosition = new ArrayList<TilePosition>();
		if (listTilePosition.size() == 0) {

			List<BaseLocation> listBaseLocation = BWTA.getBaseLocations();
			if (bl3 != null && bl4 != null) {

				ArrayList<TilePosition> tmpList = new ArrayList<TilePosition>();

				for (BaseLocation bl : listBaseLocation) {
					tmpList.add(bl.getTilePosition());
				}
				Collections.sort(tmpList, new ComparatorBaseLocation());

				for (int i = tmpList.size() - 1; i >= 0; i--) {
					if (tmpList.get(i).getX() >= 50 && tmpList.get(i).getX() <= 70 && tmpList.get(i).getY() >= 50 && tmpList.get(i).getY() <= 70) {
						tmpList.remove(i);
					}
				}

				int indexB1 = 0;

				for (int i = 0; i < tmpList.size(); i++) {
					if (tmpList.get(i).equals(bl1.getTilePosition())) {
						indexB1 = i;
					}
				}

				int index = indexB1;
				boolean findEnemy = false;
				while (findEnemy == false) {
					if (tmpList.get(index).equals(bl3.getTilePosition()) || tmpList.get(index).equals(bl4.getTilePosition())) {
						findEnemy = true;
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
				findEnemy = false;
				while (findEnemy == false) {
					if (tmpList.get(index).equals(bl3.getTilePosition()) || tmpList.get(index).equals(bl4.getTilePosition())) {
						findEnemy = true;
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

		cnt++;
		if (cnt % 2 == 0) {
			Collections.reverse(listTilePosition);
		}
		return listTilePosition;
	}

}
