import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.StringTokenizer;

import bwapi.Race;
import bwapi.TechType;
import bwapi.Unit;
import bwapi.UnitType;
import bwapi.UpgradeType;
import bwta.BWTA;
import bwta.BaseLocation;
import bwta.Chokepoint;

/// 상황을 판단하여, 정찰, 빌드, 공격, 방어 등을 수행하도록 총괄 지휘를 하는 class <br>
/// InformationManager 에 있는 정보들로부터 상황을 판단하고, <br>
/// BuildManager 의 buildQueue에 빌드 (건물 건설 / 유닛 훈련 / 테크 리서치 / 업그레이드) 명령을 입력합니다.<br>
/// 정찰, 빌드, 공격, 방어 등을 수행하는 코드가 들어가는 class
public class StrategyManager {

	private static StrategyManager instance = new StrategyManager();

	private CommandUtil commandUtil = new CommandUtil();

	// BasicBot 1.1 Patch Start ////////////////////////////////////////////////
	// 경기 결과 파일 Save / Load 및 로그파일 Save 예제 추가를 위한 변수 및 메소드 선언

	/// 한 게임에 대한 기록을 저장하는 자료구조
	private class GameRecord {
		String mapName;
		String enemyName;
		String enemyRace;
		String enemyRealRace;
		String myName;
		String myRace;
		int gameFrameCount = 0;
		int myWinCount = 0;
		int myLoseCount = 0;
	}

	/// 과거 전체 게임들의 기록을 저장하는 자료구조
	ArrayList<GameRecord> gameRecordList = new ArrayList<GameRecord>();

	// BasicBot 1.1 Patch End //////////////////////////////////////////////////

	/// static singleton 객체를 리턴합니다
	public static StrategyManager Instance() {
		return instance;
	}

	/// 경기가 시작될 때 일회적으로 전략 초기 세팅 관련 로직을 실행합니다
	public void onStart() {

		// BasicBot 1.1 Patch Start ////////////////////////////////////////////////
		// 경기 결과 파일 Save / Load 및 로그파일 Save 예제 추가

		// 과거 게임 기록을 로딩합니다
		loadGameRecordList();

		// BasicBot 1.1 Patch End //////////////////////////////////////////////////

		setInitialBuildOrder();
	}

	/// 경기가 종료될 때 일회적으로 전략 결과 정리 관련 로직을 실행합니다
	public void onEnd(boolean isWinner) {

		// BasicBot 1.1 Patch Start ////////////////////////////////////////////////
		// 경기 결과 파일 Save / Load 및 로그파일 Save 예제 추가

		// 과거 게임 기록 + 이번 게임 기록을 저장합니다
		saveGameRecordList(isWinner);

		// BasicBot 1.1 Patch End //////////////////////////////////////////////////
	}

	/// 경기 진행 중 매 프레임마다 경기 전략 관련 로직을 실행합니다
	public void update() {
		if (BuildManager.Instance().buildQueue.isEmpty()) {
			MyVariable.isInitialBuildOrderFinished = true;
		}

		int frame = MyBotModule.Broodwar.getFrameCount() % 24;
		if (frame == 0) {
			MyStrategyManager.getInstance().actionUpdateSelfUnitMap();
		} else if (frame == 1) {
			if (MyVariable.isInitialBuildOrderFinished) {
				MyStrategyManager.getInstance().actionCreateUnit();
			}
		} else if (frame == 2) {
			MyStrategyManager.getInstance().actionCheckUnitUpdate();
		} else if (frame == 3) {
			MyStrategyManager.getInstance().actionUnitControl();
		} else if (frame == 4) {
			MyStrategyManager.getInstance().actionUpdateEnemyUnitMap();
		} else if (frame == 5) {
			MyStrategyManager.getInstance().actionCheckBunker();
		} else if (frame == 6) {
			if (MyVariable.isInitialBuildOrderFinished == true) {
				MyStrategyManager.getInstance().actionCheckBuilding();
			}
		} else if (frame == 7) {
			MyStrategyManager.getInstance().actionControlScanUnit();
		} else if (frame == 8) {
			if (MyVariable.isFullScaleAttackStarted == true) {
				MyStrategyManager.getInstance().actionCheckOtherPoint();
			}
		} else if (frame == 9) {
			MyStrategyManager.getInstance().actionAttackEnemyUnitAroundMyStartPoint();
		} else if (frame == 10) {
			executeSupplyManagement();
		} else if (frame == 11) {
			MyStrategyManager.getInstance().actionSetUnitCountRatio();
		} else if (frame == 12) {
		} else if (frame == 13) {
			executeCombat();
		} else {
			executeWorkerTraining();
		}

		// BasicBot 1.1 Patch Start ////////////////////////////////////////////////
		// 경기 결과 파일 Save / Load 및 로그파일 Save 예제 추가

		// 이번 게임의 로그를 남깁니다
		// saveGameLog();

		// BasicBot 1.1 Patch End //////////////////////////////////////////////////
	}

	public void setInitialBuildOrder() {
		if (MyBotModule.Broodwar.self().getRace() == Race.Terran) {
			// SCV 생산
			BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(), BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
			BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(), BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
			BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(), BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
			BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(), BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

			// Supply 건설
			BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getBasicSupplyProviderUnitType(), BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

			// SCV 생산
			BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(), BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
			BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(), BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

			// Barrack 건설
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Barracks, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

			// SCV 생산
			BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(), BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
			BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(), BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
			BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(), BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

			// Supply 건설
			BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getBasicSupplyProviderUnitType(), BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

			// BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Bunker,
			// BuildOrderItem.SeedPositionStrategy.FirstChokePoint, false);
			// Marine 생산
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Marine, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

			// SCV 생산
			BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(), BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

			// Engineering Bay 건설
			BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getRefineryBuildingType());
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Engineering_Bay);

			// SCV 생산
			BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(), BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

			// Marine 생산
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Marine, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

			// SCV 생산
			BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(), BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

			// Marine 생산
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Marine, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

			// 지상유닛 방어력 업그레이드
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.Terran_Infantry_Armor, false);

			// Comsat Station 건설
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Comsat_Station, false);

			// Academy 건설
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Academy);

			// SCV 생산
			BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(), BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

			// Marine 생산
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Marine, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

			// Barrack 건설
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Barracks, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

			// SCV 생산
			BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(), BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

			// Marine 생산
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Marine, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

			// SCV 생산
			BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(), BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

			// Supply 건설
			BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getBasicSupplyProviderUnitType(), BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

			// Marine & Medic 생산
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Marine, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Medic, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Medic, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

			// Barrack 건설
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Barracks, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

			// Medic 생산
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Medic, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

			// Stim Pack 업그레이드
			BuildManager.Instance().buildQueue.queueAsLowestPriority(TechType.Stim_Packs, false);

			// Marine & SCV 생산
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Marine, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
			BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(), BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

			// Supply 건설
			BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getBasicSupplyProviderUnitType(), BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

			// Marine 생산
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Marine, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Marine, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

			// Supply 건설
			BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getBasicSupplyProviderUnitType(), BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

			// Marine 생산
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Marine, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Marine, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

			// Marine 사거리 업그레이드
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.U_238_Shells, false);

			// Barrack 건설
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Barracks, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

			// Supply 건설
			BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getBasicSupplyProviderUnitType(), BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

			// Firebat & Marine 생산
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Firebat, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Firebat, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Marine, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Marine, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

			// Supply 건설
			BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getBasicSupplyProviderUnitType(), BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

			// Marine 생산
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Marine, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Marine, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Marine, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Marine, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Missile_Turret, BuildOrderItem.SeedPositionStrategy.FirstChokePoint, true);

			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Marine, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Marine, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Marine, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Terran_Marine, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

			/*
			 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
			 * Terran_Factory, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
			 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
			 * Terran_Starport, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
			 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
			 * Terran_Control_Tower, BuildOrderItem.SeedPositionStrategy.MainBaseLocation,
			 * true); BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
			 * Terran_Physics_Lab, BuildOrderItem.SeedPositionStrategy.MainBaseLocation,
			 * true); BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
			 * Terran_Science_Facility,
			 * BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
			 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
			 * Terran_Science_Vessel, BuildOrderItem.SeedPositionStrategy.MainBaseLocation,
			 * true);
			 * 
			 */
			// BuildManager.Instance().buildQueue.getItemCount(UnitType.Terran_Science_Facility);

			/*
			 * // 가스 리파이너리
			 * BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.
			 * Instance().getRefineryBuildingType());
			 * 
			 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
			 * Terran_Barracks, BuildOrderItem.SeedPositionStrategy.MainBaseLocation);
			 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
			 * Terran_Bunker, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
			 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
			 * Terran_Marine);
			 * 
			 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
			 * Terran_Academy);
			 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
			 * Terran_Comsat_Station);
			 * 
			 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
			 * Terran_Medic);
			 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
			 * Terran_Firebat);
			 * 
			 * // 지상유닛 업그레이드
			 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
			 * Terran_Engineering_Bay);
			 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.
			 * Terran_Infantry_Weapons, false);
			 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.
			 * Terran_Infantry_Armor, false);
			 * 
			 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
			 * Terran_Missile_Turret);
			 * 
			 * // 마린 스팀팩
			 * BuildManager.Instance().buildQueue.queueAsLowestPriority(TechType.Stim_Packs,
			 * false); // 마린 사정거리 업
			 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.
			 * U_238_Shells, false);
			 * 
			 * // 메딕 BuildManager.Instance().buildQueue.queueAsLowestPriority(TechType.
			 * Optical_Flare, false);
			 * BuildManager.Instance().buildQueue.queueAsLowestPriority(TechType.
			 * Restoration, false); // 메딕 에너지 업
			 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.
			 * Caduceus_Reactor, false);
			 * 
			 * // 팩토리 BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
			 * Terran_Factory);
			 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
			 * Terran_Machine_Shop); // 벌쳐 스파이더 마인
			 * BuildManager.Instance().buildQueue.queueAsLowestPriority(TechType.
			 * Spider_Mines, false); // 벌쳐 이동속도 업
			 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.
			 * Ion_Thrusters, false); // 시즈탱크 시즈모드
			 * BuildManager.Instance().buildQueue.queueAsLowestPriority(TechType.
			 * Tank_Siege_Mode, false);
			 * 
			 * // 벌쳐 BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
			 * Terran_Vulture);
			 * 
			 * // 시즈탱크 BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
			 * Terran_Siege_Tank_Tank_Mode);
			 * 
			 * // 아머니 BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
			 * Terran_Armory); // 지상 메카닉 유닛 업그레이드
			 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.
			 * Terran_Vehicle_Plating, false);
			 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.
			 * Terran_Vehicle_Weapons, false); // 공중 유닛 업그레이드
			 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.
			 * Terran_Ship_Plating, false);
			 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.
			 * Terran_Ship_Weapons, false); // 골리앗 사정거리 업
			 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.
			 * Charon_Boosters, false);
			 * 
			 * // 골리앗 BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
			 * Terran_Goliath);
			 * 
			 * // 스타포트 BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
			 * Terran_Starport);
			 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
			 * Terran_Control_Tower); // 레이쓰 클러킹
			 * BuildManager.Instance().buildQueue.queueAsLowestPriority(TechType.
			 * Cloaking_Field, false); // 레이쓰 에너지 업
			 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.
			 * Apollo_Reactor, false);
			 * 
			 * // 레이쓰 BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
			 * Terran_Wraith);
			 * 
			 * // 발키리 BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
			 * Terran_Valkyrie);
			 * 
			 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
			 * Terran_Command_Center);
			 * 
			 * // 사이언스 퍼실리티
			 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
			 * Terran_Science_Facility); // 사이언스 베슬 - 기술
			 * BuildManager.Instance().buildQueue.queueAsLowestPriority(TechType.Irradiate,
			 * false); BuildManager.Instance().buildQueue.queueAsLowestPriority(TechType.
			 * EMP_Shockwave, false); // 사이언스 베슬 에너지 업
			 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.
			 * Titan_Reactor, false);
			 * 
			 * // 사이언스 베슬 BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
			 * Terran_Science_Vessel); // 사이언스 퍼실리티 - 배틀크루저 생산 가능
			 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
			 * Terran_Physics_Lab);
			 * BuildManager.Instance().buildQueue.queueAsLowestPriority(TechType.Yamato_Gun,
			 * false); // 배틀크루저 에너지 업
			 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.
			 * Colossus_Reactor, false); // 배틀크루저
			 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
			 * Terran_Battlecruiser);
			 * 
			 * // 사이언스 퍼실리티 - 고스트 생산 가능
			 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
			 * Terran_Science_Facility);
			 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
			 * Terran_Covert_Ops);
			 * BuildManager.Instance().buildQueue.queueAsLowestPriority(TechType.Lockdown,
			 * false); BuildManager.Instance().buildQueue.queueAsLowestPriority(TechType.
			 * Personnel_Cloaking, false); // 고스트 가시거리 업
			 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.
			 * Ocular_Implants, false); // 고스트 에너지 업
			 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.
			 * Moebius_Reactor, false);
			 * 
			 * // 고스트 BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
			 * Terran_Ghost);
			 * 
			 * // 핵폭탄 BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
			 * Terran_Command_Center);
			 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
			 * Terran_Nuclear_Silo);
			 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
			 * Terran_Nuclear_Missile);
			 */
		}
	}

	// 일꾼 계속 추가 생산
	public void executeWorkerTraining() {

		// InitialBuildOrder 진행중에는 아무것도 하지 않습니다
		if (MyVariable.isInitialBuildOrderFinished == false) {
			return;
		}

		if (MyBotModule.Broodwar.self().minerals() >= 50) {
			// workerCount = 현재 일꾼 수 + 생산중인 일꾼 수
			int workerCount = MyBotModule.Broodwar.self().allUnitCount(InformationManager.Instance().getWorkerType());

			if (MyBotModule.Broodwar.self().getRace() == Race.Zerg) {
				for (Unit unit : MyBotModule.Broodwar.self().getUnits()) {
					if (unit.getType() == UnitType.Zerg_Egg) {
						// Zerg_Egg 에게 morph 명령을 내리면 isMorphing = true,
						// isBeingConstructed = true, isConstructing = true 가 된다
						// Zerg_Egg 가 다른 유닛으로 바뀌면서 새로 만들어진 유닛은 잠시
						// isBeingConstructed = true, isConstructing = true 가
						// 되었다가,
						if (unit.isMorphing() && unit.getBuildType() == UnitType.Zerg_Drone) {
							workerCount++;
						}
					}
				}
			} else {
				for (Unit unit : MyBotModule.Broodwar.self().getUnits()) {
					if (unit.getType().isResourceDepot()) {
						if (unit.isTraining()) {
							workerCount += unit.getTrainingQueue().size();
						}
					}
				}
			}

			if (workerCount < 30) {
				for (Unit unit : MyBotModule.Broodwar.self().getUnits()) {
					if (unit.getType().isResourceDepot()) {
						if (unit.isTraining() == false || unit.getLarva().size() > 0) {
							// 빌드큐에 일꾼 생산이 1개는 있도록 한다
							if (BuildManager.Instance().buildQueue.getItemCount(InformationManager.Instance().getWorkerType(), null) == 0) {
								// std.cout + "worker enqueue" + std.endl;
								BuildManager.Instance().buildQueue.queueAsLowestPriority(new MetaType(InformationManager.Instance().getWorkerType()), false);
							}
						}
					}
				}
			}
		}
	}

	// Supply DeadLock 예방 및 SupplyProvider 가 부족해질 상황 에 대한 선제적 대응으로서<br>
	// SupplyProvider를 추가 건설/생산한다
	public void executeSupplyManagement() {

		// BasicBot 1.1 Patch Start ////////////////////////////////////////////////
		// 가이드 추가 및 콘솔 출력 명령 주석 처리

		// InitialBuildOrder 진행중 혹은 그후라도 서플라이 건물이 파괴되어 데드락이 발생할 수 있는데, 이 상황에 대한 해결은
		// 참가자께서 해주셔야 합니다.
		// 오버로드가 학살당하거나, 서플라이 건물이 집중 파괴되는 상황에 대해 무조건적으로 서플라이 빌드 추가를 실행하기 보다 먼저 전략적 대책
		// 판단이 필요할 것입니다

		// BWAPI::Broodwar->self()->supplyUsed() >
		// BWAPI::Broodwar->self()->supplyTotal() 인 상황이거나
		// BWAPI::Broodwar->self()->supplyUsed() + 빌드매니저 최상단 훈련 대상 유닛의
		// unit->getType().supplyRequired() > BWAPI::Broodwar->self()->supplyTotal() 인
		// 경우
		// 서플라이 추가를 하지 않으면 더이상 유닛 훈련이 안되기 때문에 deadlock 상황이라고 볼 수도 있습니다.
		// 저그 종족의 경우 일꾼을 건물로 Morph 시킬 수 있기 때문에 고의적으로 이런 상황을 만들기도 하고,
		// 전투에 의해 유닛이 많이 죽을 것으로 예상되는 상황에서는 고의적으로 서플라이 추가를 하지 않을수도 있기 때문에
		// 참가자께서 잘 판단하셔서 개발하시기 바랍니다.

		// InitialBuildOrder 진행중에는 아무것도 하지 않습니다
		if (MyVariable.isInitialBuildOrderFinished == false) {
			return;
		}
		// 게임에서는 서플라이 값이 200까지 있지만, BWAPI 에서는 서플라이 값이 400까지 있다
		// 저글링 1마리가 게임에서는 서플라이를 0.5 차지하지만, BWAPI 에서는 서플라이를 1 차지한다
		if (MyBotModule.Broodwar.self().supplyTotal() <= 400) {

			// 서플라이가 다 꽉찼을때 새 서플라이를 지으면 지연이 많이 일어나므로, supplyMargin (게임에서의 서플라이 마진 값의 2배)만큼
			// 부족해지면 새 서플라이를 짓도록 한다
			// 이렇게 값을 정해놓으면, 게임 초반부에는 서플라이를 너무 일찍 짓고, 게임 후반부에는 서플라이를 너무 늦게 짓게 된다
			int supplyMargin = 12;

			// currentSupplyShortage 를 계산한다
			int currentSupplyShortage = MyBotModule.Broodwar.self().supplyUsed() + supplyMargin - MyBotModule.Broodwar.self().supplyTotal();

			if (currentSupplyShortage > 0) {

				// 생산/건설 중인 Supply를 센다
				int onBuildingSupplyCount = 0;

				// 저그 종족인 경우, 생산중인 Zerg_Overlord (Zerg_Egg) 를 센다. Hatchery 등 건물은 세지 않는다
				if (MyBotModule.Broodwar.self().getRace() == Race.Zerg) {
					for (Unit unit : MyBotModule.Broodwar.self().getUnits()) {
						if (unit.getType() == UnitType.Zerg_Egg && unit.getBuildType() == UnitType.Zerg_Overlord) {
							onBuildingSupplyCount += UnitType.Zerg_Overlord.supplyProvided();
						}
						// 갓태어난 Overlord 는 아직 SupplyTotal 에 반영안되어서, 추가 카운트를 해줘야함
						if (unit.getType() == UnitType.Zerg_Overlord && unit.isConstructing()) {
							onBuildingSupplyCount += UnitType.Zerg_Overlord.supplyProvided();
						}
					}
				}
				// 저그 종족이 아닌 경우, 건설중인 Protoss_Pylon, Terran_Supply_Depot 를 센다. Nexus, Command
				// Center 등 건물은 세지 않는다
				else {
					onBuildingSupplyCount += ConstructionManager.Instance().getConstructionQueueItemCount(InformationManager.Instance().getBasicSupplyProviderUnitType(), null) * InformationManager.Instance().getBasicSupplyProviderUnitType().supplyProvided();
				}

				// 주석처리
				// System.out.println("currentSupplyShortage : " + currentSupplyShortage + "
				// onBuildingSupplyCount : " + onBuildingSupplyCount);

				if (currentSupplyShortage > onBuildingSupplyCount) {

					// BuildQueue 최상단에 SupplyProvider 가 있지 않으면 enqueue 한다
					boolean isToEnqueue = true;
					if (!BuildManager.Instance().buildQueue.isEmpty()) {
						BuildOrderItem currentItem = BuildManager.Instance().buildQueue.getHighestPriorityItem();
						if (currentItem.metaType.isUnit() && currentItem.metaType.getUnitType() == InformationManager.Instance().getBasicSupplyProviderUnitType()) {
							isToEnqueue = false;
						}
					}
					if (isToEnqueue) {
						// 주석처리
						// System.out.println("enqueue supply provider "
						// + InformationManager.Instance().getBasicSupplyProviderUnitType());
						BuildManager.Instance().buildQueue.queueAsHighestPriority(new MetaType(InformationManager.Instance().getBasicSupplyProviderUnitType()), true);
					}
				}
			}
		}

		// BasicBot 1.1 Patch End ////////////////////////////////////////////////
	}

	public void executeCombat() {

		// 공격 모드가 아닐 때에는 전투유닛들을 아군 진영 길목에 집결시켜서 방어
		if (MyVariable.isFullScaleAttackStarted == false) {
			Chokepoint firstChokePoint = BWTA.getNearestChokepoint(InformationManager.Instance().getMainBaseLocation(InformationManager.Instance().selfPlayer).getTilePosition());

			for (Unit unit : MyVariable.attackUnit) {
				if (unit.canAttack() && unit.getType() != InformationManager.Instance().getWorkerType() && unit.isIdle()) {
					commandUtil.attackMove(unit, firstChokePoint.getCenter());
				}
			}

			// 전투 유닛이 2개 이상 생산되었고, 적군 위치가 파악되었으면 총공격 모드로 전환
			if (MyVariable.attackUnit.size() > 30) {
				if (InformationManager.Instance().enemyPlayer != null && InformationManager.Instance().enemyRace != Race.Unknown && InformationManager.Instance().getOccupiedBaseLocations(InformationManager.Instance().enemyPlayer).size() > 0) {
					MyVariable.isFullScaleAttackStarted = true;
				}
			}
		}
		// 공격 모드가 되면, 모든 전투유닛들을 적군 Main BaseLocation 로 공격 가도록 합니다
		else {
			if (MyVariable.attackUnit.size() < 10) {
				MyVariable.isFullScaleAttackStarted = false;
			}

			if (InformationManager.Instance().enemyPlayer != null && InformationManager.Instance().enemyRace != Race.Unknown && InformationManager.Instance().getOccupiedBaseLocations(InformationManager.Instance().enemyPlayer).size() > 0) {
				// 공격 대상 지역 결정
				BaseLocation targetBaseLocation = null;
				double closestDistance = 100000000;

				for (BaseLocation baseLocation : InformationManager.Instance().getOccupiedBaseLocations(InformationManager.Instance().enemyPlayer)) {
					double distance = BWTA.getGroundDistance(InformationManager.Instance().getMainBaseLocation(InformationManager.Instance().selfPlayer).getTilePosition(), baseLocation.getTilePosition());

					if (distance < closestDistance) {
						closestDistance = distance;
						targetBaseLocation = baseLocation;
					}
				}

				if (targetBaseLocation != null) {
					for (Unit unit : MyVariable.attackUnit) {
						// 건물은 제외
						if (unit.getType().isBuilding()) {
							continue;
						}
						// 모든 일꾼은 제외
						if (unit.getType().isWorker()) {
							continue;
						}
						// canAttack 유닛은 attackMove Command 로 공격을 보냅니다
						if (unit.canAttack()) {
							if (!firstFullScaleAttack.contains(unit)) {
								firstFullScaleAttack.add(unit);
								commandUtil.attackMove(unit, targetBaseLocation.getPosition());
							}
						}
					}
				}
			}
		}
	}

	HashSet<Unit> firstFullScaleAttack = new HashSet<Unit>();

	// BasicBot 1.1 Patch Start ////////////////////////////////////////////////
	// 경기 결과 파일 Save / Load 및 로그파일 Save 예제 추가

	/// 과거 전체 게임 기록을 로딩합니다
	void loadGameRecordList() {

		// 과거의 게임에서 bwapi-data\write 폴더에 기록했던 파일은 대회 서버가 bwapi-data\read 폴더로 옮겨놓습니다
		// 따라서, 파일 로딩은 bwapi-data\read 폴더로부터 하시면 됩니다

		// TODO : 파일명은 각자 봇 명에 맞게 수정하시기 바랍니다
		String gameRecordFileName = "c:\\starcraft\\bwapi-data\\read\\NoNameBot_GameRecord.dat";

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(gameRecordFileName));

			System.out.println("loadGameRecord from file: " + gameRecordFileName);

			String currentLine;
			StringTokenizer st;
			GameRecord tempGameRecord;
			while ((currentLine = br.readLine()) != null) {

				st = new StringTokenizer(currentLine, " ");
				tempGameRecord = new GameRecord();
				if (st.hasMoreTokens()) {
					tempGameRecord.mapName = st.nextToken();
				}
				if (st.hasMoreTokens()) {
					tempGameRecord.myName = st.nextToken();
				}
				if (st.hasMoreTokens()) {
					tempGameRecord.myRace = st.nextToken();
				}
				if (st.hasMoreTokens()) {
					tempGameRecord.myWinCount = Integer.parseInt(st.nextToken());
				}
				if (st.hasMoreTokens()) {
					tempGameRecord.myLoseCount = Integer.parseInt(st.nextToken());
				}
				if (st.hasMoreTokens()) {
					tempGameRecord.enemyName = st.nextToken();
				}
				if (st.hasMoreTokens()) {
					tempGameRecord.enemyRace = st.nextToken();
				}
				if (st.hasMoreTokens()) {
					tempGameRecord.enemyRealRace = st.nextToken();
				}
				if (st.hasMoreTokens()) {
					tempGameRecord.gameFrameCount = Integer.parseInt(st.nextToken());
				}

				gameRecordList.add(tempGameRecord);
			}
		} catch (FileNotFoundException e) {
			System.out.println("loadGameRecord failed. Could not open file :" + gameRecordFileName);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/// 과거 전체 게임 기록 + 이번 게임 기록을 저장합니다
	void saveGameRecordList(boolean isWinner) {

		// 이번 게임의 파일 저장은 bwapi-data\write 폴더에 하시면 됩니다.
		// bwapi-data\write 폴더에 저장된 파일은 대회 서버가 다음 경기 때 bwapi-data\read 폴더로 옮겨놓습니다

		// TODO : 파일명은 각자 봇 명에 맞게 수정하시기 바랍니다
		String gameRecordFileName = "c:\\starcraft\\bwapi-data\\write\\NoNameBot_GameRecord.dat";

		System.out.println("saveGameRecord to file: " + gameRecordFileName);

		String mapName = MyBotModule.Broodwar.mapFileName();
		mapName = mapName.replace(' ', '_');
		String enemyName = MyBotModule.Broodwar.enemy().getName();
		enemyName = enemyName.replace(' ', '_');
		String myName = MyBotModule.Broodwar.self().getName();
		myName = myName.replace(' ', '_');

		/// 이번 게임에 대한 기록
		GameRecord thisGameRecord = new GameRecord();
		thisGameRecord.mapName = mapName;
		thisGameRecord.myName = myName;
		thisGameRecord.myRace = MyBotModule.Broodwar.self().getRace().toString();
		thisGameRecord.enemyName = enemyName;
		thisGameRecord.enemyRace = MyBotModule.Broodwar.enemy().getRace().toString();
		thisGameRecord.enemyRealRace = InformationManager.Instance().enemyRace.toString();
		thisGameRecord.gameFrameCount = MyBotModule.Broodwar.getFrameCount();
		if (isWinner) {
			thisGameRecord.myWinCount = 1;
			thisGameRecord.myLoseCount = 0;
		} else {
			thisGameRecord.myWinCount = 0;
			thisGameRecord.myLoseCount = 1;
		}
		// 이번 게임 기록을 전체 게임 기록에 추가
		gameRecordList.add(thisGameRecord);

		// 전체 게임 기록 write
		StringBuilder ss = new StringBuilder();
		for (GameRecord gameRecord : gameRecordList) {
			ss.append(gameRecord.mapName + " ");
			ss.append(gameRecord.myName + " ");
			ss.append(gameRecord.myRace + " ");
			ss.append(gameRecord.myWinCount + " ");
			ss.append(gameRecord.myLoseCount + " ");
			ss.append(gameRecord.enemyName + " ");
			ss.append(gameRecord.enemyRace + " ");
			ss.append(gameRecord.enemyRealRace + " ");
			ss.append(gameRecord.gameFrameCount + "\n");
		}

		Common.overwriteToFile(gameRecordFileName, ss.toString());
	}
}