import bwapi.Unit;

public class ActionControlScanUnit implements ActionInterface {
	@Override
	public void action() {
		for (Unit unit : MyVariable.scanUnit) {
			// 공격 받는 유닛 쪽으로 이동(자기 자신이 아니면)
			if (MyVariable.attackedUnit.size() > 0 && MyVariable.attackedUnit.get(0) != unit) {
				CommandUtil.move(unit, MyVariable.attackedUnit.get(0).getPosition());
			}
			// 그렇지 않으면 가장 멀리있는 탱크 위치로 이동
			else if (MyVariable.mostFarTank != null) {
				CommandUtil.move(unit, MyVariable.mostFarTank.getPosition());
			}
		}
	}
}
