import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;

public class GroupDefence extends Group {
	@Override
	public void action() {
		Position position = MyUtil.GetMyBunkerPosition();
		if (MyVariable.enemyUnitAroundMyStartPoint.size() > 0) {
			for (Unit enemyUnit : MyVariable.enemyUnitAroundMyStartPoint) {
				targetPosition = enemyUnit.getPosition();
				break;
			}
		} else {
			if (MyVariable.isFullScaleAttackStarted == true || position == null) {
				targetPosition = MyVariable.myStartLocation.toPosition();
			} else {
				targetPosition = MyUtil.getSaveChokePoint().getPoint();
			}
		}
	}
}
