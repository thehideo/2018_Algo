import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import bwapi.Race;
import bwapi.TilePosition;
import bwapi.UnitType;
import bwta.BWTA;
import bwta.BaseLocation;

public class GroupPatrol extends GroupAbstract {

	ArrayList<TilePosition> listTilePosition = new ArrayList<TilePosition>();

	static int cnt = 0;

	@Override
	public void action() {
		// 마린은 지정하면 안됨

		if (InformationManager.Instance().enemyRace == Race.Terran) {
			if (MyUtil.indexToGo >= 15) {
				int Terran_Goliath = GroupManager.instance().groupAttack.getUnitTypeCnt(UnitType.Terran_Goliath) / 4;
				if (Terran_Goliath >= 0) {
					this.mapUnitTotal.put(UnitType.Terran_Goliath, Terran_Goliath);
				}
				int Terran_Vulture = GroupManager.instance().groupAttack.getUnitTypeCnt(UnitType.Terran_Vulture) / 2;
				if (Terran_Vulture >= 0) {
					this.mapUnitTotal.put(UnitType.Terran_Vulture, Terran_Vulture);
				}

				if (MyUtil.GetMyTankCnt() > 10) {
					int Tank = MyUtil.GetMyTankCnt() / 10;
					this.mapUnitTotal.put(UnitType.Terran_Siege_Tank_Tank_Mode, Tank);
					this.mapUnitTotal.put(UnitType.Terran_Siege_Tank_Siege_Mode, Tank);
				}
			}
			if (MyVariable.getSelfUnit(UnitType.Terran_Battlecruiser).size() > 4) {
				this.mapUnitTotal.put(UnitType.Terran_Battlecruiser, MyVariable.getSelfUnit(UnitType.Terran_Battlecruiser).size() / 4);
			} else {
				this.mapUnitTotal.put(UnitType.Terran_Battlecruiser, 0);
			}
		} else {
			if (MyVariable.getSelfUnit(UnitType.Terran_Vulture).size() >= 10) {
				this.mapUnitTotal.put(UnitType.Terran_Vulture, 1);
			}
			if (MyVariable.getSelfUnit(UnitType.Terran_Goliath).size() >= 20) {
				this.mapUnitTotal.put(UnitType.Terran_Goliath, MyVariable.getSelfUnit(UnitType.Terran_Goliath).size() / 10);
			}

			if (MyVariable.getSelfUnit(UnitType.Terran_Marine).size() >= 40) {
				this.mapUnitTotal.put(UnitType.Terran_Marine, MyVariable.getSelfUnit(UnitType.Terran_Marine).size() / 10);
			}
		}

		if (listTilePosition.size() > 0) {
			Iterator<UnitType> unitTypes = this.mapUnit.keySet().iterator();
			while (unitTypes.hasNext()) {
				UnitType unitType = unitTypes.next();
				if (listTilePosition.size() > 0) {					
					for (Integer unitID : mapUnit.get(unitType)) {
						if (MyUtil.distancePosition(MyVariable.mapUnitIDUnit.get(unitID).getPosition(), listTilePosition.get(0).toPosition()) < 32 * 1) {
							listTilePosition.remove(0);
							break;
						}
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
			if (tilePosition.equals(bl1.getTilePosition()) || tilePosition.equals(bl2.getTilePosition()) && tilePosition.equals(bl3.getTilePosition()) || tilePosition.equals(bl4.getTilePosition()) || MyVariable.mapSelfMainBuilding.contains(tilePosition)) {
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

			cnt++;
			if (cnt % 2 == 0) {
				Collections.reverse(listTilePosition);
			}
		}

		if (MyVariable.isFullScaleAttackStarted == true) {
			mapTargetUnit.clear();
			for (TilePosition tilePosition : MyVariable.enemyBuildingUnit) {
				targetPosition = tilePosition.toPosition();
				targetPositionForTank = tilePosition.toPosition();
				break;
			}
		} else if (listTilePosition.size() > 0) {
			this.targetPosition = listTilePosition.get(0).toPosition();
		}
	}
}
