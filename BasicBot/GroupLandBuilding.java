import bwapi.Position;
import bwapi.Race;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.Chokepoint;

public class GroupLandBuilding extends GroupAbstract {

	@Override
	void action() {
		targetPosition = MyUtil.getSaveTilePosition(4).toPosition();
	}

}
