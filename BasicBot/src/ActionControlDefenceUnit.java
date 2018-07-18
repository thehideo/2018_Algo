import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;

public class ActionControlDefenceUnit implements ActionInterface {
	CommandUtil commandUtil = new CommandUtil();

	@Override
	public void action() {
		Position position = MyUtil.GetMyBunkerPosition();
		TilePosition myStartLocation = MyBotModule.Broodwar.self().getStartLocation().getPoint();
		if (MyVariable.enemyUnitAroundMyStartPoint.size() > 0) {
			for (Unit unit : MyVariable.defenceUnit) {
				commandUtil.attackMove(unit, MyVariable.enemyUnitAroundMyStartPoint.get(0).getPosition());
			}
		} else {
			for (Unit unit : MyVariable.defenceUnit) {
				if (unit.isIdle() == true) {
					if (MyVariable.isFullScaleAttackStarted == true || position == null) {
						commandUtil.attackMove(unit, myStartLocation.toPosition());
					} else {
						commandUtil.attackMove(unit, MyUtil.getSaveChokePoint().getPoint());
					}
				}
			}
		}
	}
}
