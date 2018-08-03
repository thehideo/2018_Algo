import bwapi.Unit;
import bwapi.UnitType;

public class GroupWorker extends GroupAbstract {
	public void action() {
		mapTargetUnit.clear();
		int selfCnt = MyVariable.attackUnit.size() + MyVariable.defenceUnit.size();
		int enemyCnt = MyVariable.enemyUnitAroundMyStartPoint.size();
		// 적의 숫자가 많으면 SCV를 동원한다. (초반에만 동작)
		if (selfCnt < enemyCnt * 1.5 && selfCnt < 10 && MyBotModule.Broodwar.getFrameCount() < 12000 && MyVariable.distanceOfMostCloseEnemyUnit < MyVariable.distanceOfMostCloseBunker + 3) {
			int cnt = 0;
			for (Unit unit : MyVariable.getSelfUnit(UnitType.Terran_SCV)) {
				double distance1 = 50;

				if (MyVariable.mostCloseBunker != null) {
					distance1 = MyUtil.distanceTilePosition(MyVariable.myStartLocation.getPoint(), MyVariable.mostCloseBunker.getTilePosition()) + 3;
				}
				double distance2 = MyUtil.distanceTilePosition(MyVariable.myStartLocation.getPoint(), unit.getPoint().toTilePosition());

				if (distance1 <= distance2) {
					// WorkerManager.Instance().
				} else {
					cnt++;
					if (cnt >= MyVariable.enemyUnitAroundMyStartPoint.size() * 2 || cnt >= MyVariable.getSelfUnit(UnitType.Terran_SCV).size() - 4) {
						break;
					}
					Unit enemyUnit = null;
					Double minDistance = Double.MAX_VALUE;
					for (Unit unit2 : MyVariable.enemyUnitAroundMyStartPoint) {
						if (unit2.isFlying() == false) {
							double distance = MyUtil.distancePosition(unit.getPosition(), unit2.getPosition());
							if (minDistance > distance) {
								minDistance = distance;
								enemyUnit = unit2;
							}
						}
					}

					if (enemyUnit != null) {
						mapTargetUnit.put(unit.getID(), enemyUnit);
					}
				}
			}
		}

	}

}
