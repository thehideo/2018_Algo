import bwapi.Race;
import bwapi.TechType;
import bwapi.UnitType;

public class ActionSetUnitRatio implements ActionInterface {

	@Override
	public void action() {
		MyVariable.clearUnitRaio();
		if (InformationManager.Instance().enemyRace == Race.Protoss) {
			MyVariable.attackUnitRatio.put(UnitType.Terran_Marine, 4);
			if (MyVariable.getSelfAttackUnit(UnitType.Terran_Medic).size() * 4 <= MyVariable.getSelfAttackUnit(UnitType.Terran_Marine).size()) {
				MyVariable.attackUnitRatio.put(UnitType.Terran_Medic, 1);
			}
			if (MyVariable.getSelfAttackUnit(UnitType.Terran_Firebat).size() * 4 <= MyVariable.getSelfAttackUnit(UnitType.Terran_Marine).size()) {
				MyVariable.attackUnitRatio.put(UnitType.Terran_Firebat, 1);
			}
			if (MyVariable.findCarrier == true) {
				MyVariable.attackUnitRatio.put(UnitType.Terran_Goliath, 3);
			} else {
				MyVariable.attackUnitRatio.put(UnitType.Terran_Goliath, 1);
				MyVariable.attackUnitRatio.put(UnitType.Terran_Siege_Tank_Tank_Mode, 1);
			}
		}
		// 프로토스 이면
		else if (InformationManager.Instance().enemyRace == Race.Terran) {
			MyVariable.attackUnitRatio.put(UnitType.Terran_Siege_Tank_Tank_Mode, 1);
			MyVariable.attackUnitRatio.put(UnitType.Terran_Marine, 4);
			if (MyVariable.getSelfAttackUnit(UnitType.Terran_Medic).size() * 4 <= MyVariable.getSelfAttackUnit(UnitType.Terran_Marine).size()) {
				MyVariable.attackUnitRatio.put(UnitType.Terran_Medic, 1);
			}
		}
		// 저그 이면
		else {
			// 공격 유닛 비율
			MyVariable.attackUnitRatio.put(UnitType.Terran_Marine, 8);
			if (MyVariable.getSelfAttackUnit(UnitType.Terran_Medic).size() * 4 < MyVariable.getSelfAttackUnit(UnitType.Terran_Marine).size()) {
				MyVariable.attackUnitRatio.put(UnitType.Terran_Medic, 2);
			}
			if (MyVariable.findMutal) {
				MyVariable.attackUnitRatio.put(UnitType.Terran_Goliath, 1);
			} else {
				MyVariable.attackUnitRatio.put(UnitType.Terran_Siege_Tank_Tank_Mode, 1);
			}

			// 방어 유닛 비율
			if (MyVariable.findMutal) {
				MyVariable.defenceUnitCountTotal.put(UnitType.Terran_Goliath, 4);
				MyVariable.defenceUnitCountTotal.put(UnitType.Terran_Marine, 8);
				MyVariable.defenceUnitCountTotal.put(UnitType.Terran_Medic, 2);
			}
		}
	}
}
