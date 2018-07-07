import java.util.Random;

import bwapi.Position;
import bwapi.TilePosition;
import bwapi.UnitType;
import bwta.BWTA;
import bwta.Chokepoint;

public class MyUtil {

	static Random random = new Random();

	static double distanceTilePosition(TilePosition a, TilePosition b) {
		return Math.sqrt((a.getX() - b.getX()) * (a.getX() - b.getX()) + (a.getY() - b.getY()) * (a.getY() - b.getY()));
	}

	static double distancePosition(Position a, Position b) {
		return Math.sqrt((a.getX() - b.getX()) * (a.getX() - b.getX()) + (a.getY() - b.getY()) * (a.getY() - b.getY()));
	}

	static Position GetMyBunkerPosition() {
		Position bunkerPosition = null;
		if (MyVariable.mapSelfUnit.get(UnitType.Terran_Bunker) != null) {
			bunkerPosition = MyVariable.mapSelfUnit.get(UnitType.Terran_Bunker).get(0).getPosition();
		}
		return bunkerPosition;
	}

	static int getCommandCenterCount() {
		int result = 0;
		if (MyVariable.mapSelfUnit.get(UnitType.Terran_Command_Center) != null) {
			result = MyVariable.mapSelfUnit.get(UnitType.Terran_Command_Center).size();
		}
		return result;
	}
	
	

}
