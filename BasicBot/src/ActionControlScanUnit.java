import bwapi.TechType;
import bwapi.Unit;
import bwapi.UnitType;

public class ActionControlScanUnit implements ActionInterface {
	@Override
	public void action() {
		for (Unit unit : MyVariable.scanUnit) {
			// 공격 받는 유닛 쪽으로 이동(자기 자신이 아니면), 메딕한테는 안간다.
			if (MyVariable.attackedUnit.size() > 0 && MyVariable.attackedUnit.get(0) != unit && MyVariable.attackedUnit.get(0).getType() != UnitType.Terran_Medic) {
				CommandUtil.move(unit, MyVariable.attackedUnit.get(0).getPosition());
			}
			// 그렇지 않으면 가장 멀리있는 탱크 위치로 이동
			else if (MyVariable.mostFarTank != null) {
				CommandUtil.move(unit, MyVariable.mostFarTank.getPosition());
			}
			// 탱크도 없으면 본진으로 이동
			else {
				CommandUtil.move(unit, MyVariable.myStartLocation.toPosition());
			}
			/*
			 * if (MyVariable.getEnemyUnit(UnitType.Protoss_Carrier).size() > 0) { Unit
			 * carrierMostClose = MyUtil.getMostCloseEnemyUnit(UnitType.Protoss_Carrier,
			 * unit); if (carrierMostClose != null) { if
			 * (unit.canUseTechUnit(TechType.EMP_Shockwave, carrierMostClose)) {
			 * unit.useTech(TechType.EMP_Shockwave, carrierMostClose); } } }
			 */
		}
	}

}
