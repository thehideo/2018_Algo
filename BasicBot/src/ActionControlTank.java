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
				unit.unsiege();
			}
		}

		int cnt = Terran_Siege_Tank_Siege_Mode.size();
		for (Unit unit : Terran_Siege_Tank_Tank_Mode) {
			if (cnt < 5 && needSiege(unit) == true) {
				cnt++;
				unit.siege();
			}
		}
	}

	boolean needSiege(Unit unit) {
		boolean result = false;
		for (Unit enemyUnit : MyVariable.enemyGroundUnit) {
			int distance = unit.getDistance(enemyUnit);
			if (distance > SIEGE_MODE_MIN_RANGE && distance < SIEGE_MODE_MAX_RANGE) {
				result = true;
			}
		}
		return result;
	}
}
