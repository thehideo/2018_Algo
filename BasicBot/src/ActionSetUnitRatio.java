import bwapi.Race;
import bwapi.UnitType;

public class ActionSetUnitRatio extends ActionControlAbstract {

	@Override
	public void action() {
		MyVariable.clearUnitRaio();
		if (InformationManager.Instance().enemyRace == Race.Protoss) {
			if (MyVariable.findCarrier == true) {
				unitProtossFastCarrier();
			} else {
				unitProtoss();
			}
		} else if (InformationManager.Instance().enemyRace == Race.Terran) {
			unitTerran();
		} else {
			unitZerg();
		}
	}

	public void unitProtossFastCarrier() {
		if (MyVariable.getSelfUnit(UnitType.Terran_Goliath).size() < 12) {
			MyVariable.attackUnitRatio.put(UnitType.Terran_Goliath, 12);
		} else {
			MyVariable.attackUnitRatio.put(UnitType.Terran_Goliath, 4);
			if (MyVariable.getSelfAttackUnit(UnitType.Terran_Goliath).size() / 4 > MyUtil.GetMyTankCnt()) {
				MyVariable.attackUnitRatio.put(UnitType.Terran_Siege_Tank_Tank_Mode, 1);
			}
		}

	}

	public void unitProtoss() {
		if (MyVariable.getSelfUnit(UnitType.Terran_Vulture).size() < 6) {
			MyVariable.attackUnitRatio.put(UnitType.Terran_Vulture, 4);
		} else {

			MyVariable.attackUnitRatio.put(UnitType.Terran_Vulture, 4);
			if (MyVariable.getSelfAttackUnit(UnitType.Terran_Vulture).size() / 4 > MyUtil.GetMyTankCnt()) {
				MyVariable.attackUnitRatio.put(UnitType.Terran_Siege_Tank_Tank_Mode, 1);
			}
		}
	}

	public void unitTerran() {
		if (MyVariable.getSelfUnit(UnitType.Terran_Vulture).size() < 6) {
			MyVariable.attackUnitRatio.put(UnitType.Terran_Vulture, 4);
		} else {
			MyVariable.attackUnitRatio.put(UnitType.Terran_Vulture, 4);
			if (MyVariable.getSelfAttackUnit(UnitType.Terran_Vulture).size() / 4 > MyUtil.GetMyTankCnt()) {
				MyVariable.attackUnitRatio.put(UnitType.Terran_Siege_Tank_Tank_Mode, 1);
			}
			if (MyVariable.findWraith == true) {
				MyVariable.attackUnitRatio.put(UnitType.Terran_Goliath, 1);
			}
		}
	}

	public void unitZerg() {
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
