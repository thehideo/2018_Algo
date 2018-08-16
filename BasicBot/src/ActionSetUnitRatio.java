import bwapi.Race;
import bwapi.UnitType;

public class ActionSetUnitRatio extends ActionControlAbstract {

	@Override
	public void action() {
		MyVariable.clearUnitRaio();

		// 적이 다 파괴된것 같은데 계속 게임이 지속될 때
		if (MyVariable.enemyBuildingUnit.size() == 0 && MyVariable.isFullScaleAttackStarted == true && MyVariable.getSelfUnit(UnitType.Terran_Wraith).size() <= 10) {
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
		// 벌처 생산 조건
		if (MyUtil.GetMyTankCnt() <= 4 && MyVariable.getSelfUnit(UnitType.Terran_Vulture).size() <= 6) {
			MyVariable.attackUnitRatio.put(UnitType.Terran_Vulture, 4);
		}
		// 탱크 생산 조건
		MyVariable.attackUnitRatio.put(UnitType.Terran_Siege_Tank_Tank_Mode, 4);

		// 레이스 생산 조건
		if (MyVariable.getSelfUnit(UnitType.Terran_Wraith).size() < 10 && MyVariable.getSelfUnit(UnitType.Terran_Wraith).size() * 4 <= MyUtil.GetMyTankCnt()) {
			MyVariable.attackUnitRatio.put(UnitType.Terran_Wraith, 1);
		}

		// 골리앗이 적을 때는 바로 업그레이드
		// if (MyVariable.getSelfAttackUnit(UnitType.Terran_Goliath).size() <= 2) {
		// MyVariable.attackUnitRatio.put(UnitType.Terran_Goliath, 8);
		// } else if (MyVariable.getSelfAttackUnit(UnitType.Terran_Goliath).size() * 1
		// <= MyUtil.GetMyTankCnt()) {
		// MyVariable.attackUnitRatio.put(UnitType.Terran_Goliath, 1);
		// }

		// 배틀쿠르저 생산 조건
		if (MyVariable.getSelfUnit(UnitType.Terran_Physics_Lab).size() > 0) {
			MyVariable.attackUnitRatio.put(UnitType.Terran_Battlecruiser, 2);
		}
	}

	public void unitZerg() {
		// 공격 유닛 비율
		MyVariable.attackUnitRatio.put(UnitType.Terran_Marine, 8);
		if (MyVariable.getSelfAttackUnit(UnitType.Terran_Medic).size() * 4 < MyVariable.getSelfAttackUnit(UnitType.Terran_Marine).size()) {
			MyVariable.attackUnitRatio.put(UnitType.Terran_Medic, 2);
		}
		if (MyUtil.GetMyTankCnt() * 4 < MyVariable.getSelfAttackUnit(UnitType.Terran_Marine).size()) {
			MyVariable.attackUnitRatio.put(UnitType.Terran_Siege_Tank_Tank_Mode, 1);
		}

		if (MyVariable.findMutal) {
			if (MyVariable.getSelfAttackUnit(UnitType.Terran_Science_Vessel).size() < 3) {
				MyVariable.attackUnitRatio.put(UnitType.Terran_Science_Vessel, 1);
			}
			MyVariable.attackUnitRatio.put(UnitType.Terran_Goliath, 1);
		}

	}

}
