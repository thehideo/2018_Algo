import bwapi.Race;
import bwapi.Unit;
import bwapi.UnitType;

public class ActionSetUnitRatio extends ActionControlAbstract {

	@Override
	public void action() {
		MyVariable.clearUnitRaio();

		// 적이 다 파괴된것 같은데 계속 게임지 지속될 때
		if (MyVariable.enemyBuildingUnit.size() == 0 && MyVariable.isFullScaleAttackStarted == true) {
			MyVariable.attackUnitRatio.put(UnitType.Terran_Wraith, 9999);
		} else {
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
	}

	public void unitProtossFastCarrier() {
		if (MyVariable.getSelfUnit(UnitType.Terran_Goliath).size() < 12) {
			if (MyUtil.GetMyTankCnt() == 0) {
				MyVariable.attackUnitRatio.put(UnitType.Terran_Siege_Tank_Tank_Mode, 12);
			} else {
				MyVariable.attackUnitRatio.put(UnitType.Terran_Goliath, 12);
			}
		} else {
			MyVariable.attackUnitRatio.put(UnitType.Terran_Goliath, 4);
			if (MyVariable.getSelfAttackUnit(UnitType.Terran_Goliath).size() / 4 > MyUtil.GetMyTankCnt()) {
				MyVariable.attackUnitRatio.put(UnitType.Terran_Siege_Tank_Tank_Mode, 1);
			}
		}

		if (MyBotModule.Broodwar.self().minerals() > 400) {
			MyVariable.attackUnitRatio.put(UnitType.Terran_Marine, 4);
			if (MyVariable.getSelfAttackUnit(UnitType.Terran_Medic).size() * 4 <= MyVariable.getSelfAttackUnit(UnitType.Terran_Marine).size()) {
				MyVariable.attackUnitRatio.put(UnitType.Terran_Medic, 1);
			}
		}

	}

	public void unitProtoss() {
		MyVariable.attackUnitRatio.put(UnitType.Terran_Marine, 4);
		if (MyVariable.getSelfAttackUnit(UnitType.Terran_Medic).size() * 4 <= MyVariable.getSelfAttackUnit(UnitType.Terran_Marine).size()) {
			MyVariable.attackUnitRatio.put(UnitType.Terran_Medic, 1);
		}
		MyVariable.attackUnitRatio.put(UnitType.Terran_Vulture, 4);
		MyVariable.attackUnitRatio.put(UnitType.Terran_Siege_Tank_Tank_Mode, 1);
	}

	public void unitTerran() {
		if (MyVariable.getSelfUnit(UnitType.Terran_Vulture).size() < 6 && MyUtil.GetMyTankCnt() <= 4) {
			MyVariable.attackUnitRatio.put(UnitType.Terran_Vulture, 4);
		} else {
			MyVariable.attackUnitRatio.put(UnitType.Terran_Siege_Tank_Tank_Mode, 4);
			if (MyVariable.getSelfAttackUnit(UnitType.Terran_Goliath).size() <= 15) {
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
			if (MyUtil.GetMyTankCnt() * 4 < MyVariable.getSelfAttackUnit(UnitType.Terran_Marine).size()) {
				MyVariable.attackUnitRatio.put(UnitType.Terran_Siege_Tank_Tank_Mode, 1);
			}
		}
	}

}
