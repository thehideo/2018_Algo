import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;

public class ActionControlDefenceUnit implements ActionInterface {
	@Override
	public void action() {
		Position position = MyUtil.GetMyBunkerPosition();
		TilePosition myStartLocation = MyBotModule.Broodwar.self().getStartLocation().getPoint();

		if (MyVariable.enemyUnitAroundMyStartPoint.size() > 0) {
			for (Unit enemyUnit : MyVariable.enemyUnitAroundMyStartPoint) {
				for (Unit unit : MyVariable.defenceUnit) {
					CommandUtil.attackMove(unit, enemyUnit.getPosition());
					break;
				}
			}
		} else {
			for (Unit unit : MyVariable.defenceUnit) {
				if (unit.isIdle() == true) {
					if (MyVariable.isFullScaleAttackStarted == true || position == null) {
						CommandUtil.attackMove(unit, myStartLocation.toPosition());
					} else {
						CommandUtil.attackMove(unit, MyUtil.getSaveChokePoint().getPoint());
					}
				}
			}
		}
	}
}
