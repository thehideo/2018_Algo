import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.StringTokenizer;

import javax.swing.ActionMap;

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

	public StrategyManager() {
		ActionManager.Instance().addAction(new ActionCreateUnit(), 1);
		ActionManager.Instance().addAction(new ActionCheckUnitUpdate(), 2);
		ActionManager.Instance().addAction(new ActionUnitControl(), 3);
		ActionManager.Instance().addAction(new ActionCheckBunker(), 4);
		ActionManager.Instance().addAction(new ActionUpdateEnemyUnitMap(), 5);
		ActionManager.Instance().addAction(new ActionAttackEnemyUnitInMyRegion(), 6);
		ActionManager.Instance().addAction(new ActionControlScanUnit(), 7);
		ActionManager.Instance().addAction(new ActionCheckOtherPoint(), 8);
		ActionManager.Instance().addAction(new ActionCheckBuilding(), 9);
		ActionManager.Instance().addAction(new ActionSetSelfUnitRatio(), 10);
		ActionManager.Instance().addAction(new ActionUpdateSelfUnitMap(), 11);
		ActionManager.Instance().addAction(new ActionCombat(), 12);
		ActionManager.Instance().addAction(new ActionSupplyManagement(), 13);
	}

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
		
		ActionManager.Instance().action();
		
		executeWorkerTraining();		
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