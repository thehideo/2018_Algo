import bwapi.UnitType;

public class QConstants {
    
	/**
     * 학습여부
     * true : 랜덤으로 Action한다.
     * false : Max Q값으로 Action한다.
     */
    public static final boolean MLTraning = true;
    
	public static final int SINGHT_DIVIDE_CNT = 2;
	
	/**
	 * 클로킹 기준 에너지
	 */
	public static final int CLOAKING_STD_ENG = 50;
	
	/**
	 * 공격을 시작하기위한 최소의 유닛
	 */
	public static final int Q_LEARNING_START_UNIT_CNT = 4;
	
	/**
	 * 모든 학습Action 경우의 수
	 */
	public static final int RANDOM_ACTION_CNT = 17;
	
	/**
	 * 나의 공격범위
	 */
	public static final int QUNIT_GROUNDWEAPON_ATTCK_RANGE = UnitType.Terran_Wraith.groundWeapon().maxRange() + 30;
	
	/**
	 * 나의 시야범위
	 */
	public static final int QUNIT_SINGHT_RANGE = UnitType.Terran_Wraith.sightRange() + 100; // 224 + 60
	
	/**
	 * 이동Action을 완료하는 거리
	 */
	public static final int MOVE_ACTION_DISTANCE = 30;
	
	public static final double ALPHA = 0.1;
	
	public static final double GAMMA = 0.9;

	/**
	 * 스타크래프트 게임 Frame으로 인해 아래와 같이 Flag로 게임Frame을 제어한다.
	 * @author SDS
	 *
	 */
	class StateFlagType {
		/**
		 * Action을 시작한상태
		 */
	    static final String START = "START";
	    /**
	     * Action이 끝난상태
	     */
	    static final String END = "END";
	    /**
	     * Action을 시작 및 종료를 진행중인 상태
	     */
	    static final String ING = "ING";
	    /**
	     * 학습시작전 상태
	     */
	    static final String STANDBY = "STANDBY";
	}
	
	/**
	 * Action Type을 정의한 클래스
	 * @author SDS
	 *
	 */
	class ActionType {
	    static final String Attack = "Attack";
	    static final String Move = "Move";
	    static final String Cloak = "Cloaking";
	}
	
}