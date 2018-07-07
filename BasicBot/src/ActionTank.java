import java.util.ArrayList;

import bwapi.Unit;
import bwapi.UnitType;

public class ActionTank implements ActionInterface {

	public static final int SIEGE_MODE_MIN_RANGE = UnitType.Terran_Siege_Tank_Siege_Mode.groundWeapon().minRange(); // 64
	public static final int SIEGE_MODE_MAX_RANGE = UnitType.Terran_Siege_Tank_Siege_Mode.groundWeapon().maxRange(); // 384

	@Override
	public void action() {
		ArrayList<Unit> Terran_Siege_Tank_Siege_Mode = MyVariable.mapSelfUnit.get(UnitType.Terran_Siege_Tank_Siege_Mode);
		ArrayList<Unit> Terran_Siege_Tank_Tank_Mode = MyVariable.mapSelfUnit.get(UnitType.Terran_Siege_Tank_Tank_Mode);

		if (Terran_Siege_Tank_Siege_Mode != null)
			for (Unit unit : Terran_Siege_Tank_Siege_Mode) {
				if (needSiege(unit) == false) {
					unit.unsiege();
				}
			}
		if (Terran_Siege_Tank_Tank_Mode != null)
			for (Unit unit : Terran_Siege_Tank_Tank_Mode) {
				if (needSiege(unit) == true) {
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
