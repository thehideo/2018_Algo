import bwapi.Unit;
import bwapi.UnitType;

public class ControlGoliath extends ControlAbstract {
	void actionMain(Unit unit, GroupAbstract groupAbstract) {

		// 주위에 캐리어가 있고, 각 골리앗 마다 가장 가까운 녀석을 공격한다.
		if (MyVariable.getEnemyUnit(UnitType.Protoss_Carrier).size() > 0) {
			Unit mostCloseCarrier = MyUtil.getMostCloseEnemyUnit(UnitType.Protoss_Carrier, unit);
			if (mostCloseCarrier != null) {
				if (mostCloseCarrier.getDistance(unit) < 1200) {
					CommandUtil.attackUnit(unit, mostCloseCarrier);
					setSpecialAction(unit);
				}
			}
		}
		CommandUtil.attackMove(unit, groupAbstract.getTargetPosition(unit));
	}
}
