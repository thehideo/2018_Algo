import java.util.List;
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

	static Position GetMyBunkerDonthaveTurretPosition() {
		Position bunkerPosition = null;
		for (Unit unit : MyVariable.getSelfUnit(UnitType.Terran_Bunker)) {
			boolean find = false;
			for (Unit unit2 : MyVariable.getSelfUnit(UnitType.Terran_Missile_Turret)) {
				if (distanceTilePosition(unit.getTilePosition(), unit2.getTilePosition()) < 4) {
					find = true;
					break;
				}
			}
			if (find == false) {
				bunkerPosition = unit.getPoint();
			}
		}
		return bunkerPosition;
	}

	static Position GetMyBunkerPosition() {
		Position bunkerPosition = null;
		if (MyVariable.mostCloseBunker != null) {
			bunkerPosition = MyVariable.mostCloseBunker.getPosition();
		}
		return bunkerPosition;
	}

	static Position GetMyBunkerBuildPosition() {
		Position bunkerPosition = null;
		List<TilePosition> ltp = BWTA.getShortestPath(MyVariable.myFirstchokePoint, MyVariable.myStartLocation);

		if (ltp.size() > 5) {
			bunkerPosition = ltp.get(3).toPosition();
		}

		if (bunkerPosition == null)
			bunkerPosition = MyVariable.myFirstchokePoint.toPosition();
		return bunkerPosition;
	}

	static int getCommandCenterCount() {
		return MyVariable.getSelfUnit(UnitType.Terran_Command_Center).size();
	}

	// 방어할 ChokePoint를 구한다.
	public static Chokepoint getSaveChokePoint() {
		// 기본은 첫번째 초크 포인트
		Chokepoint chokePoint = BWTA.getNearestChokepoint(InformationManager.Instance().getMainBaseLocation(InformationManager.Instance().selfPlayer).getTilePosition());

		// 확장했으면 확장부분을 지킨다.
		if (MyVariable.getSelfUnit(UnitType.Terran_Command_Center).size() > 1) {
			chokePoint = InformationManager.Instance().getSecondChokePoint(InformationManager.Instance().selfPlayer);
		}

		return chokePoint;
	}

}
