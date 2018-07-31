import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;

public class GroupDefence extends Group {
	@Override
	public void action() {
		Position position = MyUtil.GetMyBunkerPosition();
		if (MyVariable.enemyUnitAroundMyStartPoint.size() > 0) {
			for (Unit enemyUnit : MyVariable.enemyUnitAroundMyStartPoint) {
				target = enemyUnit.getPosition();
				break;
			}
		} else {
			if (MyVariable.isFullScaleAttackStarted == true || position == null) {
				target = MyVariable.myStartLocation.toPosition();
			} else {
				target = MyUtil.getSaveChokePoint().getPoint();
			}
		}
	}
}
