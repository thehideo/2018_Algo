import java.util.ArrayList;

import bwapi.Unit;
import bwapi.UnitType;

public class ActionControlTank implements ActionInterface {

	public static final int SIEGE_MODE_MIN_RANGE = UnitType.Terran_Siege_Tank_Siege_Mode.groundWeapon().minRange(); // 64
	public static final int SIEGE_MODE_MAX_RANGE = UnitType.Terran_Siege_Tank_Siege_Mode.groundWeapon().maxRange(); // 384

	@Override
	public void action() {
		ArrayList<Unit> Terran_Siege_Tank_Siege_Mode = MyVariable.getSelfUnit(UnitType.Terran_Siege_Tank_Siege_Mode);
		ArrayList<Unit> Terran_Siege_Tank_Tank_Mode = MyVariable.getSelfUnit(UnitType.Terran_Siege_Tank_Tank_Mode);

		for (Unit unit : Terran_Siege_Tank_Siege_Mode) {
			if (needSiege(unit) == false) {
				CommandUtil.unsiege(unit);
			}
		}

		int cnt = Terran_Siege_Tank_Siege_Mode.size();
		for (Unit unit : Terran_Siege_Tank_Tank_Mode) {
			if (cnt < 5 && needSiege(unit) == true) {
				cnt++;
				CommandUtil.siege(unit);
			}
		}
	}

	boolean needSiege(Unit unit) {
		boolean result = false;
		// 방어 모드이면 시즈 모드로 대기
		if (MyVariable.isFullScaleAttackStarted == false) {
			if (MyUtil.distanceTilePosition(MyUtil.getSaveChokePoint().getPoint().toTilePosition(), unit.getTilePosition()) < 5) {
				result = true;
			}
		}
		if (result == false) {
			for (Unit enemyUnit : MyVariable.enemyGroundUnit) {
				int distance = unit.getDistance(enemyUnit);
				if (distance >= SIEGE_MODE_MIN_RANGE && distance <= SIEGE_MODE_MAX_RANGE) {
					result = true;
					break;
				}
			}
		}
		return result;
	}
}
