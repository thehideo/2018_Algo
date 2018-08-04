import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import bwapi.Position;
import bwapi.Race;
import bwapi.TechType;
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

	static String getBuildingSizeKey(UnitType unitType) {
		int x = unitType.tileWidth();
		int y = unitType.tileHeight();
		boolean addon = unitType.canBuildAddon();
		String key = "" + x + "|" + y + "|" + addon + (unitType == UnitType.Terran_Supply_Depot);
		return key;
	}

	static double distanceTilePosition(Unit a_u, Unit b_u) {
		TilePosition a = a_u.getTilePosition();
		TilePosition b = b_u.getTilePosition();
		return Math.sqrt((a.getX() - b.getX()) * (a.getX() - b.getX()) + (a.getY() - b.getY()) * (a.getY() - b.getY()));
	}

	static double distancePosition(Position a, Position b) {
		return Math.sqrt((a.getX() - b.getX()) * (a.getX() - b.getX()) + (a.getY() - b.getY()) * (a.getY() - b.getY()));
	}

	static int GetMyTankCnt() {
		if (GroupManager.instance().groupAttack.mapUnit.get(UnitType.Terran_Siege_Tank_Tank_Mode) == null) {
			GroupManager.instance().groupAttack.mapUnit.put(UnitType.Terran_Siege_Tank_Tank_Mode, new HashSet<Integer>());
		}
		if (GroupManager.instance().groupAttack.mapUnit.get(UnitType.Terran_Siege_Tank_Siege_Mode) == null) {
			GroupManager.instance().groupAttack.mapUnit.put(UnitType.Terran_Siege_Tank_Siege_Mode, new HashSet<Integer>());
		}
		return GroupManager.instance().groupAttack.mapUnit.get(UnitType.Terran_Siege_Tank_Tank_Mode).size() + GroupManager.instance().groupAttack.mapUnit.get(UnitType.Terran_Siege_Tank_Siege_Mode).size();
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

	static int indexToGo = 0;

	static int goTimer = 0;

	static HashMap<TilePosition, List<TilePosition>> mapShortestPath = new HashMap<TilePosition, List<TilePosition>>();

	// 방어할 ChokePoint를 구한다.
	// 기본은 첫번째 초크 포인트
	public static TilePosition getSaveTilePosition() {

		// 전진 위치 초기화
		if (MyUtil.GetMyTankCnt() == 0 && MyVariable.isFullScaleAttackStarted == true) {
			indexToGo = 0;
		}

		TilePosition target = null;
		if (InformationManager.Instance().enemyRace == Race.Terran || InformationManager.Instance().enemyRace == Race.Protoss) {
			target = InformationManager.Instance().getSecondChokePoint(InformationManager.Instance().selfPlayer).getPoint().toTilePosition();
		} else {
			target = BWTA.getNearestChokepoint(InformationManager.Instance().getMainBaseLocation(InformationManager.Instance().selfPlayer).getTilePosition()).getPoint().toTilePosition();
			// 확장했으면 확장부분을 지킨다.
			if (MyVariable.getSelfUnit(UnitType.Terran_Command_Center).size() >= 2 || MyVariable.mapEnemyMainBuilding.size() >= 2 || MyVariable.attackUnit.size() > 30) {
				target = InformationManager.Instance().getSecondChokePoint(InformationManager.Instance().selfPlayer).getPoint().toTilePosition();
			}
		}

		// 탱크가 4마리 이상이면 앞으로 서서히 전진
		if (MyUtil.GetMyTankCnt() > 4) {
			if (!mapShortestPath.containsKey(target)) {
				mapShortestPath.put(target, BWTA.getShortestPath(target, InformationManager.Instance().getMainBaseLocation(MyBotModule.Broodwar.enemy()).getTilePosition()));
			}
			List<TilePosition> shortestPath = mapShortestPath.get(target);
			target = shortestPath.get(indexToGo);

			// 전진후 일정 시간이 지나면 한칸 더 앞으로 이동한다.
			if (MyBotModule.Broodwar.getFrameCount() > goTimer + 100) {
				if (indexToGo >= shortestPath.size() - 30) {
					MyVariable.isFullScaleAttackStarted=true;
				} else {
					indexToGo = indexToGo + 1;
					goTimer = MyBotModule.Broodwar.getFrameCount();
				}

			}

			// 적이 있으면 전진하지 않는다.
			if (MyVariable.enemyAttactUnit.size() > 0) {
				goTimer = MyBotModule.Broodwar.getFrameCount();
			}
		}

		return target;
	}

	public static boolean canUseScan() {
		boolean canUseScan = false;
		for (Unit unit : MyVariable.getSelfUnit(UnitType.Terran_Comsat_Station)) {
			if (unit.isCompleted() && unit.canUseTechPosition(TechType.Scanner_Sweep)) {
				canUseScan = true;
			}
		}
		return canUseScan;
	}

	public static boolean haveCompletedScienceVessle() {
		boolean result = false;
		for (Unit unit : MyVariable.getSelfUnit(UnitType.Terran_Science_Vessel)) {
			if (unit.isCompleted() == true) {
				result = true;
				break;
			}
		}
		return result;
	}

	public static Unit getMostCloseEnemyUnit(UnitType unitType, Unit myUnit) {
		Unit mostCloseEnemyUnit = null;
		double minDistance = Double.MAX_VALUE;
		for (Unit enemyUnit : MyVariable.getEnemyUnit(unitType)) {
			double distance = MyUtil.distanceTilePosition(myUnit.getTilePosition(), enemyUnit.getTilePosition());
			if (minDistance > distance) {
				minDistance = distance;
				mostCloseEnemyUnit = enemyUnit;
			}
		}
		return mostCloseEnemyUnit;
	}

}
