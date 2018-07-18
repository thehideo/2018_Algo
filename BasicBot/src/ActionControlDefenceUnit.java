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
			for (Unit enemyUnit : MyVariable.enemyUnitAroundMyStartPoint) {
				for (Unit unit : MyVariable.defenceUnit) {
					// 적 유닛이 보이거나 공격을 할 수 있는 것일 때 따라간다.
					if (enemyUnit.isCloaked() == false || enemyUnit.canAttack()) {
						commandUtil.attackMove(unit, enemyUnit.getPosition());
						break;
					}
				}
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
