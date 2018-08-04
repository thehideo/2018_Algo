import bwapi.Position;
import bwapi.Unit;
import bwapi.UnitType;

public class GroupDefence extends GroupAbstract {
	@Override
	public void action() {
		// 방어 유닛 비율
		if (MyVariable.findMutal) {
			this.mapUnitTotal.put(UnitType.Terran_Goliath, 4);
			this.mapUnitTotal.put(UnitType.Terran_Marine, 8);
			this.mapUnitTotal.put(UnitType.Terran_Medic, 2);
		}

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
				targetPosition = MyUtil.getSaveTilePosition().toPosition();
			}
		}
	}
}
