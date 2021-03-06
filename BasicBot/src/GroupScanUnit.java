import bwapi.Unit;
import bwapi.UnitType;

public class GroupScanUnit extends GroupAbstract {
	@Override
	public void action() {
		// 공격 받는 유닛 쪽으로 이동(자기 자신이 아니면), 메딕한테는 안간다.
		if (MyVariable.attackedUnit.size() > 0) {
			for (Unit unit : MyVariable.attackedUnit) {
				if (unit.getType() != UnitType.Terran_Science_Vessel && unit.getType() != UnitType.Terran_SCV && unit.getType() != UnitType.Terran_Medic && unit.getType().isBuilding() == false) {
					targetPosition = unit.getPosition();
					break;
				}
			}
		}
		// 그렇지 않으면 가장 멀리있는 탱크 위치로 이동
		else {
			if (MyVariable.mostFarTank != null) {
				targetPosition = MyVariable.mostFarTank.getPosition();
			}
			// 탱크도 없으면 본진으로 이동
			else {
				targetPosition = MyVariable.myStartLocation.toPosition();
			}
		}
	}
}
