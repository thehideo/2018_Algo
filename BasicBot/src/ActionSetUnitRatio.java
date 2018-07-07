import bwapi.UnitType;

public class ActionSetUnitRatio implements ActionInterface {

	@Override
	public void action() {
		MyVariable.clearUnitRaio();
		if (MyVariable.isFullScaleAttackStarted) {
			// 방어 유닛 구성
			MyVariable.defenceUnitCountTotal.put(UnitType.Terran_Marine, 4);
			MyVariable.defenceUnitCountTotal.put(UnitType.Terran_Medic, 1);
			if (MyVariable.findMutal) {
				MyVariable.defenceUnitCountTotal.put(UnitType.Terran_Goliath, 4);
			}
		}

		// 공격 유닛 비율
		MyVariable.attackUnitRatio.put(UnitType.Terran_Marine, 12);
		MyVariable.attackUnitRatio.put(UnitType.Terran_Medic, 4);
		MyVariable.attackUnitRatio.put(UnitType.Terran_Siege_Tank_Tank_Mode, 1);
		MyVariable.attackUnitRatio.put(UnitType.Terran_Goliath, 1);

	}
}
