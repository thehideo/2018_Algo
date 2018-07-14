import java.util.Random;

import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;
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
		if(MyVariable.mostCloseBunker!=null) {
			bunkerPosition=MyVariable.mostCloseBunker.getPosition();
		}
		return bunkerPosition;
	}

	static int getCommandCenterCount() {
		return MyVariable.getSelfUnit(UnitType.Terran_Command_Center).size();
	}

}
