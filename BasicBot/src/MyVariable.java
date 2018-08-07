import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;
import bwta.Chokepoint;

public class MyVariable {

	// 맵에서의 본진 위치
	public static int xx = -1;
	public static int yy = -1;
	// 맵 싸이즈
	public static int map_max_x = 0;
	public static int map_max_y = 0;

	/////////////////////////////////////////////
	// 전체 전략 판단
	/////////////////////////////////////////////

	public static boolean isFullScaleAttackStarted = false;

	public static boolean isInitialBuildOrderFinished = false;

	// public static boolean StopMarinProtossTerran = false;

	// 싸이언스 베슬이 필요한지
	public static boolean needTerran_Science_Vessel = false;

	// 뮤탈리스트를 찾았는지
	public static boolean findMutal = false;

	// 뮤탈리스트를 찾았는지
	public static boolean findLucker = false;

	// 다크템플러를 찾았는지
	public static boolean findDarkTempler = false;

	// 하이템플러를 찾았는지
	public static boolean findHighTempler = false;

	public static boolean findCarrier = false;

	public static boolean findWraith = false;

	// 초당(1000ms) 프레임수(game speed 에 따라 다름)s
	public static int nFrameCntPerMin = Math.round(1000 / Config.SetLocalSpeed);

	/////////////////////////////////////////////
	// 내 유닛 정보
	/////////////////////////////////////////////

	public static HashMap<Integer, Unit> mapUnitIDUnit = new HashMap<Integer, Unit>();

	public static TilePosition myStartLocation = MyBotModule.Broodwar.self().getStartLocation();

	public static TilePosition myFirstchokePoint = BWTA.getNearestChokepoint(InformationManager.Instance().getMainBaseLocation(InformationManager.Instance().selfPlayer).getTilePosition()).getPoint().toTilePosition();

	public static double distanceOfMostFarTank = 0;
	public static Unit mostFarTank = null;

	public static double distanceOfMostFarAttackUnit = 0;
	public static Unit mostFarAttackUnit = null;

	public static double distanceOfMostFarTurret = 0;
	public static Unit mostFarTurret = null;

	public static double distanceOfMostCloseBunker = Double.MAX_VALUE;
	public static Unit mostCloseBunker = null;

	// 애드온 지을자리를 기억해둔다.
	public static HashSet<TilePosition> addonPlace = new HashSet<TilePosition>();

	// 서플 지을자리를 기억해둔다.
	public static HashSet<TilePosition> supplyPlace = new HashSet<TilePosition>();

	// 내 영역을 기억해둔다. 본진에 처들어온 적을 판단하기 위해서.
	public static HashSet<TilePosition> mapMyRegion = new HashSet<TilePosition>();

	// 애드온 지을자리를 기억해둔다.
	public static HashSet<TilePosition> mapChokePointAround = new HashSet<TilePosition>();

	// 가스 지어진 자리를 기억해둔다.
	public static HashSet<TilePosition> mapRefineryPosition = new HashSet<TilePosition>();

	// 상대편 command center, neus, 해처리 개수 파악용
	public static HashSet<TilePosition> mapEnemyMainBuilding = new HashSet<TilePosition>();

	// 상대편 command center, neus, 해처리 개수 파악용
	public static HashSet<TilePosition> mapSelfMainBuilding = new HashSet<TilePosition>();

	public static HashMap<String, ArrayList<Unit>> mapBuildingSizeMap = new HashMap<String, ArrayList<Unit>>();

	// 전체
	private static HashMap<UnitType, ArrayList<Unit>> mapSelfUnit = new HashMap<UnitType, ArrayList<Unit>>();

	public static Set<UnitType> getSelfUnitKey() {
		return mapSelfUnit.keySet();
	}

	public static ArrayList<Unit> getSelfUnit(UnitType unitType) {
		if (!mapSelfUnit.containsKey(unitType)) {
			mapSelfUnit.put(unitType, new ArrayList<Unit>());
		}
		return mapSelfUnit.get(unitType);
	}

	private static HashMap<UnitType, ArrayList<Unit>> mapSelfAttackUnit = new HashMap<UnitType, ArrayList<Unit>>();

	public static ArrayList<Unit> getSelfAttackUnit(UnitType unitType) {
		if (!mapSelfAttackUnit.containsKey(unitType)) {
			mapSelfAttackUnit.put(unitType, new ArrayList<Unit>());
		}
		return mapSelfAttackUnit.get(unitType);
	}

	// 공격 유닛
	public static ArrayList<Unit> attackUnit = new ArrayList<Unit>();

	// 공격 당하는 유닛
	public static ArrayList<Unit> attackedUnit = new ArrayList<Unit>();

	// 방어 유닛
	public static ArrayList<Unit> defenceUnit = new ArrayList<Unit>();

	// 벙커 지키는 유닛
	public static ArrayList<Unit> bunkerUnit = new ArrayList<Unit>();

	// 적 확장 방어 유닛
	public static ArrayList<Unit> patrolUnit = new ArrayList<Unit>();

	public static HashSet<TilePosition> spinderMinePosition = new HashSet<TilePosition>();

	public static ArrayList<Unit> minerals = new ArrayList<Unit>();

	public static void clearSelfUnit() {
		MyVariable.mapSelfUnit.clear();
		MyVariable.mapSelfAttackUnit.clear();

		// 역할별 유닛 목록
		MyVariable.attackUnit.clear();
		MyVariable.attackedUnit.clear();
		MyVariable.defenceUnit.clear();
		MyVariable.patrolUnit.clear();
		MyVariable.bunkerUnit.clear();
		spinderMinePosition.clear();

		distanceOfMostFarTank = 0;
		mostFarTank = null;

		distanceOfMostFarTurret = 0;
		mostFarTurret = null;

		distanceOfMostFarAttackUnit = 0;
		mostFarAttackUnit = null;

		distanceOfMostCloseBunker = Double.MAX_VALUE;
		mostCloseBunker = null;
	}

	/////////////////////////////////////////////
	// 유닛 구성
	/////////////////////////////////////////////

	public static HashMap<UnitType, Integer> attackUnitRatio = new HashMap<UnitType, Integer>();

	public static double distanceOfMostCloseEnemyUnit = Double.MAX_VALUE;
	public static Unit mostCloseEnemyUnit = null;

	public static double distanceOfMostCloseCarrier = Double.MAX_VALUE;
	public static Unit mostCloseCarrier = null;

	public static void clearUnitRaio() {
		MyVariable.attackUnitRatio.clear();
	}

	/////////////////////////////////////////////
	// 전체 전략 판단
	/////////////////////////////////////////////

	// 적건물, 초기화 대상 아님!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	public static HashSet<TilePosition> enemyBuildingUnit = new HashSet<TilePosition>();

	// 전체
	private static HashMap<UnitType, ArrayList<Unit>> mapEnemyUnit = new HashMap<UnitType, ArrayList<Unit>>();

	static public ArrayList<Unit> getEnemyUnit(UnitType unitType) {
		if (!mapEnemyUnit.containsKey(unitType)) {
			mapEnemyUnit.put(unitType, new ArrayList<Unit>());
		}
		return mapEnemyUnit.get(unitType);
	}

	// 적공격 유닛
	public static ArrayList<Unit> enemyAttactUnit = new ArrayList<Unit>();

	// 적공격 중 유닛
	public static ArrayList<Unit> enemyAttactingUnit = new ArrayList<Unit>();

	// 적공격 지상 유닛
	public static ArrayList<Unit> enemyGroundUnit = new ArrayList<Unit>();

	// 본진 근처 적유닛
	public static ArrayList<Unit> enemyUnitAroundMyStartPoint = new ArrayList<>();

	public static void clearEnemyUnit() {
		MyVariable.mapEnemyUnit.clear();
		MyVariable.enemyAttactUnit.clear();
		MyVariable.enemyAttactingUnit.clear();
		MyVariable.enemyGroundUnit.clear();
		MyVariable.enemyUnitAroundMyStartPoint.clear();

		distanceOfMostCloseEnemyUnit = Double.MAX_VALUE;
		mostCloseEnemyUnit = null;

		distanceOfMostCloseCarrier = Double.MAX_VALUE;
		mostCloseCarrier = null;
	}

}
