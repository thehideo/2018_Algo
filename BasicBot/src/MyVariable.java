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

	// 뮤탈리스트를 찾았는지
	public static boolean findMutal = false;
	
	// 뮤탈리스트를 찾았는지
	public static boolean findLucker = false;

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

	public static void clearSelfUnit() {
		MyVariable.mapSelfUnit.clear();
		MyVariable.mapSelfAttackUnit.clear();

		// 역할별 유닛 목록
		MyVariable.attackUnit.clear();
		MyVariable.attackedUnit.clear();
		MyVariable.defenceUnit.clear();
		MyVariable.scanUnit.clear();
		MyVariable.bunkerUnit.clear();

		// 방어할 유닛 개수 초기화
		MyVariable.defenceUnitCount.clear();

	}

	/////////////////////////////////////////////
	// 유닛 구성
	/////////////////////////////////////////////

	public static HashMap<UnitType, Integer> attackUnitRatio = new HashMap<UnitType, Integer>();

	public static HashMap<UnitType, Integer> defenceUnitCountTotal = new HashMap<UnitType, Integer>();

	// clearSelfUnit에서 초기화함!!!!!!!!!!
	public static HashMap<UnitType, Integer> defenceUnitCount = new HashMap<UnitType, Integer>();

	public static void clearUnitRaio() {
		MyVariable.attackUnitRatio.clear();
		MyVariable.defenceUnitCountTotal.clear();
	}

	/////////////////////////////////////////////
	// 전체 전략 판단
	/////////////////////////////////////////////

	// 적건물, 초기화 대상 아님!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	public static HashSet<TilePosition> enemyBuildingUnit = new HashSet<TilePosition>();

	// 전체
	public static HashMap<UnitType, ArrayList<Unit>> mapEnemyUnit = new HashMap<UnitType, ArrayList<Unit>>();

	// 적공격 유닛
	public static ArrayList<Unit> enemyAttactUnit = new ArrayList<Unit>();

	// 적공격 지상 유닛
	public static ArrayList<Unit> enemyGroundUnit = new ArrayList<Unit>();

	// 본진 근처 적유닛
	public static ArrayList<Unit> enemyUnitAroundMyStartPoint = new ArrayList<>();

	public static void clearEnemyUnit() {
		MyVariable.mapEnemyUnit.clear();
		MyVariable.enemyAttactUnit.clear();
		MyVariable.enemyGroundUnit.clear();
		MyVariable.enemyUnitAroundMyStartPoint.clear();
	}

}
