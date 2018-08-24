import bwapi.Unit;
import bwapi.UnitType;

public class ActionControlBunker extends ActionControlAbstract {

	static int needMarinCnt = 0;

	@Override
	public void action() {
		needMarinCnt = 0;
		for (Unit unit : MyVariable.getSelfUnit(UnitType.Terran_Bunker)) {
			if (unit.isCompleted()) {
				// 총 공격일 때는 다 꺼냄
				if (MyVariable.isFullScaleAttackStarted == true || MyVariable.distanceOfMostCloseEnemyUnit < MyVariable.distanceOfMostCloseBunker - 5) {
					if (unit.getLoadedUnits().size() > 0) {
						unit.unloadAll();
					}
					needMarinCnt = 0;
				} else {
					if (unit.getLoadedUnits().size() < 4) {
						needMarinCnt = 4 - unit.getLoadedUnits().size();
						return;
					}
				}
			}
		}
	}
}
