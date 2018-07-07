import bwapi.Unit;

public class ActionControlScanUnit implements ActionInterface {
	@Override
	public void action() {
		for (Unit unit : MyVariable.scanUnit) {
			if (unit.isIdle() == true && MyVariable.attackedUnit.size() > 0) {
				unit.move(MyVariable.attackedUnit.get(0).getPosition());
			}
		}
	}
}
