import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;

public class ActionControlScanUnit implements ActionInterface {
	@Override
	public void action() {
		TilePosition myStartLocation = MyBotModule.Broodwar.self().getStartLocation().getPoint();
		for (Unit unit : MyVariable.scanUnit) {
			double distance = MyUtil.distanceTilePosition(unit.getTilePosition(), myStartLocation);
			if (MyVariable.mostFarTank != null && unit.getType() != UnitType.Terran_Siege_Tank_Tank_Mode && unit.getType() != UnitType.Terran_Siege_Tank_Siege_Mode && MyVariable.distanceOfMostFarTank > 50 && distance > MyVariable.distanceOfMostFarTank) {
				CommandUtil.move(unit, MyVariable.mostFarTank.getPosition());
			} else
			
			if (unit.isIdle() == true && MyVariable.attackedUnit.size() > 0) {
				unit.move(MyVariable.attackedUnit.get(0).getPosition());
			}
		}
	}
}
