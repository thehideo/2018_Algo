import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;

public class MyVariable {

	/////////////////////////////////////////////
	// 전체 전략 판단
	/////////////////////////////////////////////

	public static boolean isFullScaleAttackStarted = false;

	public static boolean isInitialBuildOrderFinished = false;

	// 싸이언스 베슬이 필요한지
	public static boolean needTerran_Science_Vessel = false;
	// 싸이언스 베슬이 있는지
	public static boolean haveTerran_Science_Vessel = false;
	// 러커를 찾았는지
	public static boolean findLucker = false;
	// 뮤탈리스트를 찾았는지
	public static boolean findMutal = false;

	/////////////////////////////////////////////
	// 내 유닛 정보
	/////////////////////////////////////////////

	// 전체
	public static HashMap<UnitType, ArrayList<Unit>> mapSelfUnit = new HashMap<UnitType, ArrayList<Unit>>();

	public static HashMap<UnitType, ArrayList<Unit>> mapSelfAttackUnit = new HashMap<UnitType, ArrayList<Unit>>();

	// 공격 유닛
	public static ArrayList<Unit> attackUnit = new ArrayList<Unit>();

	// 공격 당하는 유닛
	public static ArrayList<Unit> attackedUnit = new ArrayList<Unit>();

	// 방어 유닛
	public static ArrayList<Unit> defenceUnit = new ArrayList<Unit>();

	// 스캔할 수 있는 유닛 (싸이언스 베슬)
	public static ArrayList<Unit> scanUnit = new ArrayList<Unit>();

	// 벙커 지키는 유닛
	public static ArrayList<Unit> bunkerUnit = new ArrayList<Unit>();

	// 본진 근처에 유닛
	public static ArrayList<Unit> enemyUnitAroundMyStartPoint = new ArrayList<>();

	/////////////////////////////////////////////
	// 유닛 구성
	/////////////////////////////////////////////

	public static HashMap<UnitType, Integer> attackUnitRatio = new HashMap<UnitType, Integer>();

	public static HashMap<UnitType, Integer> defenceUnitCountTotal = new HashMap<UnitType, Integer>();

	public static HashMap<UnitType, Integer> defenceUnitCount = new HashMap<UnitType, Integer>();

	/////////////////////////////////////////////
	// 전체 전략 판단
	/////////////////////////////////////////////

	// 전체
	public static HashMap<UnitType, ArrayList<Unit>> mapEnemyUnit = new HashMap<UnitType, ArrayList<Unit>>();

	// 적건물
	public static HashSet<TilePosition> enemyBuildingUnit = new HashSet<TilePosition>();

	// 적공격 유닛
	public static ArrayList<Unit> enemyAttactUnit = new ArrayList<Unit>();

}
