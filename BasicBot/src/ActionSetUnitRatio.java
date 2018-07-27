import bwapi.Race;
import bwapi.Unit;
import bwapi.UnitType;

public class ActionSetUnitRatio implements ActionInterface {

	@Override
	public void action() {
		MyVariable.clearUnitRaio();
		if (InformationManager.Instance().enemyRace == Race.Protoss) {
			// 팩토리가 있는지 확인
			boolean canMakeVulture = false;
			for (Unit unit : MyVariable.getSelfUnit(UnitType.Terran_Factory)) {
				if (unit.isCompleted()) {
					canMakeVulture = true;
					break;
				}
			}

			// 캐리어 발견했을 때
			if (MyVariable.findCarrier == true) {
				if (MyBotModule.Broodwar.self().minerals() > 500) {
					MyVariable.attackUnitRatio.put(UnitType.Terran_Marine, 4);
					MyVariable.attackUnitRatio.put(UnitType.Terran_Medic, 1);
				}
				MyVariable.attackUnitRatio.put(UnitType.Terran_Goliath, 4);
				if ((MyVariable.getSelfAttackUnit(UnitType.Terran_Siege_Tank_Siege_Mode).size() + MyVariable.getSelfAttackUnit(UnitType.Terran_Siege_Tank_Tank_Mode).size()) * 4 <= MyVariable.getSelfAttackUnit(UnitType.Terran_Goliath).size()) {
					MyVariable.attackUnitRatio.put(UnitType.Terran_Siege_Tank_Siege_Mode, 1);
				}
			} else {
				// 벌처 생산 가능하면 바이오닉은 생산하지 않는다.
				if (canMakeVulture == false || MyVariable.getSelfUnit(UnitType.Terran_Marine).size() < 12 || MyBotModule.Broodwar.self().minerals() > 500) {
					MyVariable.attackUnitRatio.put(UnitType.Terran_Marine, 4);
					if (MyVariable.getSelfAttackUnit(UnitType.Terran_Medic).size() * 4 <= MyVariable.getSelfAttackUnit(UnitType.Terran_Marine).size()) {
						MyVariable.attackUnitRatio.put(UnitType.Terran_Medic, 1);
					}
				}
				if (MyVariable.getSelfAttackUnit(UnitType.Terran_Vulture).size() / 4 < MyVariable.getSelfAttackUnit(UnitType.Terran_Siege_Tank_Tank_Mode).size() + MyVariable.getSelfAttackUnit(UnitType.Terran_Siege_Tank_Siege_Mode).size()) {
					MyVariable.attackUnitRatio.put(UnitType.Terran_Vulture, 4);
				}
				MyVariable.attackUnitRatio.put(UnitType.Terran_Siege_Tank_Tank_Mode, 1);
			}

		} else if (InformationManager.Instance().enemyRace == Race.Terran) {
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
