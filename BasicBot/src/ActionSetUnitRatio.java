import bwapi.Race;
import bwapi.TechType;
import bwapi.UnitType;

public class ActionSetUnitRatio implements ActionInterface {

	@Override
	public void action() {
		MyVariable.clearUnitRaio();
		if (InformationManager.Instance().enemyRace == Race.Protoss || InformationManager.Instance().enemyRace == Race.Terran) {
			// if (MyVariable.isFullScaleAttackStarted) {
			// 방어 유닛 구성
			// MyVariable.defenceUnitCountTotal.put(UnitType.Terran_Marine, 4);
			MyVariable.defenceUnitCountTotal.put(UnitType.Terran_Marine, 8);
			MyVariable.defenceUnitCountTotal.put(UnitType.Terran_Siege_Tank_Tank_Mode, 1);
			MyVariable.defenceUnitCountTotal.put(UnitType.Terran_Goliath, 2);
			// }

			// 공격 유닛 비율
			// if (MyVariable.getSelfUnit(UnitType.Terran_Vulture).size() < 10) {
			// MyVariable.attackUnitRatio.put(UnitType.Terran_Vulture, 2);
			// }
			MyVariable.attackUnitRatio.put(UnitType.Terran_Siege_Tank_Tank_Mode, 1);
			MyVariable.attackUnitRatio.put(UnitType.Terran_Vulture, 3);
			MyVariable.attackUnitRatio.put(UnitType.Terran_Goliath, 1);

			if (MyVariable.getSelfUnit(UnitType.Terran_Marine).size() <= 20) {
				MyVariable.attackUnitRatio.put(UnitType.Terran_Marine, 10);
			}

			// if (MyVariable.getSelfUnit(UnitType.Terran_Marine).size() < 12) {
			// MyVariable.attackUnitRatio.put(UnitType.Terran_Marine, 3);
			// }
		} else {
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
			if (MyVariable.getSelfUnit(UnitType.Terran_Medic).size() <= 10) {
				MyVariable.attackUnitRatio.put(UnitType.Terran_Medic, 4);
			}
			MyVariable.attackUnitRatio.put(UnitType.Terran_Siege_Tank_Tank_Mode, 1);
			MyVariable.attackUnitRatio.put(UnitType.Terran_Goliath, 1);
		}

	}
}
