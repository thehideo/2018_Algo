import bwapi.Unit;

public class ActionControlScanUnit implements ActionInterface {
	@Override
	public void action() {
		for (Unit unit : MyVariable.scanUnit) {
			if (MyVariable.mostFarTank != null) {
				CommandUtil.move(unit, MyVariable.mostFarTank.getPosition());
			} else if (unit.isIdle() == true && MyVariable.attackedUnit.size() > 0) {
				CommandUtil.move(unit, MyVariable.attackedUnit.get(0).getPosition());
			}
		}
	}
}
