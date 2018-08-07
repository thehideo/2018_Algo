import java.util.ArrayList;

import bwapi.Player;
import bwapi.Position;
import bwapi.Race;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;
import bwta.BaseLocation;
import bwta.Chokepoint;

/// 실제 봇프로그램의 본체가 되는 class<br>
/// 스타크래프트 경기 도중 발생하는 이벤트들이 적절하게 처리되도록 해당 Manager 객체에게 이벤트를 전달하는 관리자 Controller 역할을 합니다
public class GameCommander {

	private boolean bTimeDPFlg = false; // 매 프레임마다 소요시간 콘솔 출력할지 여부(true:매 프레임마다 출력, false:55ms 초과시에만 출력)
	private long lTotalSpendTime = 0L; // 프레임당 평균 소요시간 계산용(전체 소요시간)

	/// 경기가 시작될 때 일회적으로 발생하는 이벤트를 처리합니다
	public void onStart() {
		System.out.println("Protoss_Photon_Cannon.airWeapon=" + UnitType.Protoss_Photon_Cannon.airWeapon().maxRange());
		System.out.println("Protoss_Photon_Cannon.groundWeapon=" + UnitType.Protoss_Photon_Cannon.groundWeapon().maxRange());
		System.out.println("Protoss_Dragoon.airWeapon=" + UnitType.Protoss_Dragoon.airWeapon().maxRange());
		System.out.println("Protoss_Dragoon.groundWeapon=" + UnitType.Protoss_Dragoon.groundWeapon().maxRange());

		// 맵의 사이즈 확인
		// int max_x = 0;
		for (int i = 0; i < 1000; i++) {
			TilePosition tp = new TilePosition(i, 0);
			if (tp.isValid() == true) {
				MyVariable.map_max_x = i;
			} else {
				break;
			}
		}
		// int max_y = 0;
		for (int i = 0; i < 1000; i++) {
			TilePosition tp = new TilePosition(0, i);
			if (tp.isValid() == true) {
				MyVariable.map_max_y = i;
			} else {
				break;
			}
		}

		// 서플라이 지을 위치 예약
		for (int x = 0; x <= MyVariable.map_max_x; x++) {
			for (int y = 0; y <= 6; y++) {
				MyVariable.supplyPlace.add(new TilePosition(x, y));
			}

			for (int y = MyVariable.map_max_y - 6; y <= MyVariable.map_max_y + 1; y++) {
				MyVariable.supplyPlace.add(new TilePosition(x, y));
			}
		}

		int totalX = 0;
		int totalY = 0;

		int avgX = 0;
		int avgY = 0;

		int minx = Integer.MAX_VALUE;
		int miny = Integer.MAX_VALUE;

		for (int i = 0; i < MyBotModule.Broodwar.getStartLocations().size(); i++) {
			totalX += MyBotModule.Broodwar.getStartLocations().get(i).getPoint().getX();
			totalY += MyBotModule.Broodwar.getStartLocations().get(i).getPoint().getY();
			if (minx > MyBotModule.Broodwar.getStartLocations().get(i).getPoint().getX()) {
				minx = MyBotModule.Broodwar.getStartLocations().get(i).getPoint().getX();
			}
			if (miny > MyBotModule.Broodwar.getStartLocations().get(i).getPoint().getY()) {
				miny = MyBotModule.Broodwar.getStartLocations().get(i).getPoint().getY();
			}
		}
		avgX = totalX / MyBotModule.Broodwar.getStartLocations().size();
		avgY = totalY / MyBotModule.Broodwar.getStartLocations().size();

		if (avgX < MyVariable.myStartLocation.getX()) {
			MyVariable.xx = 1;
		}
		if (avgY < MyVariable.myStartLocation.getY()) {
			MyVariable.yy = 1;
		}

		System.out.println("map_max_x=" + MyVariable.map_max_x);
		System.out.println("map_max_y=" + MyVariable.map_max_y);

		TilePosition startLocation = MyBotModule.Broodwar.self().getStartLocation();
		if (startLocation == TilePosition.None || startLocation == TilePosition.Unknown) {
			return;
		}

		for (BaseLocation bl : BWTA.getBaseLocations()) {
			TilePosition tp = bl.getTilePosition();
			int width = UnitType.Terran_Command_Center.tileWidth() + 2;
			int height = UnitType.Terran_Command_Center.tileHeight();
			for (int x = tp.getX(); x < tp.getX() + width + 1; x++) {
				for (int y = tp.getY(); y < tp.getY() + height; y++) {
					TilePosition tp2 = new TilePosition(x, y);
					MyVariable.addonPlace.add(tp2);
				}
			}
		}

		int cp_range = 3;
		for (Chokepoint cp : BWTA.getChokepoints()) {
			TilePosition tp = cp.getPoint().toTilePosition();
			for (int i = -cp_range; i <= cp_range; i++) {
				for (int j = -cp_range; j <= cp_range; j++) {
					int X = tp.getX();
					int Y = tp.getY();
					TilePosition t2 = new TilePosition(X + i, Y + j);
					MyVariable.mapChokePointAround.add(t2);
				}
			}
		}

		StrategyManager.Instance().onStart();
		ActionManager.Instance().onStart();

	}

	/// 경기가 종료될 때 일회적으로 발생하는 이벤트를 처리합니다
	public void onEnd(boolean isWinner) {
		StrategyManager.Instance().onEnd(isWinner);

		System.out.println("[Info] 평균 소요시간 : " + lTotalSpendTime / MyBotModule.Broodwar.getFrameCount() + "ms");
	}

	/// 경기 진행 중 매 프레임마다 발생하는 이벤트를 처리합니다
	public void onFrame() {
		if (MyBotModule.Broodwar.isPaused() || MyBotModule.Broodwar.self() == null || MyBotModule.Broodwar.self().isDefeated() || MyBotModule.Broodwar.self().leftGame() || MyBotModule.Broodwar.enemy() == null || MyBotModule.Broodwar.enemy().isDefeated() || MyBotModule.Broodwar.enemy().leftGame()) {
			return;
		}
		if (MyBotModule.Broodwar.getFrameCount() % 2000 == 0) {
			MyBotModule.Broodwar.sendText("FrameCnt=" + MyBotModule.Broodwar.getFrameCount());
		}

		// Time & Memory check
		long startTime = System.currentTimeMillis();
		// long s_memory= Runtime.getRuntime().freeMemory();

		// 아군 베이스 위치. 적군 베이스 위치. 각 유닛들의 상태정보 등을 Map 자료구조에 저장/업데이트
		InformationManager.Instance().update();

		// 각 유닛의 위치를 자체 MapGrid 자료구조에 저장
		MapGrid.Instance().update();

		// economy and base managers
		// 일꾼 유닛에 대한 명령 (자원 채취, 이동 정도) 지시 및 정리
		WorkerManager.Instance().update();

		// 빌드오더큐를 관리하며, 빌드오더에 따라 실제 실행(유닛 훈련, 테크 업그레이드 등)을 지시한다.
		BuildManager.Instance().update();

		// 빌드오더 중 건물 빌드에 대해서는, 일꾼유닛 선정, 위치선정, 건설 실시, 중단된 건물 빌드 재개를 지시한다
		ConstructionManager.Instance().update();

		// 게임 초기 정찰 유닛 지정 및 정찰 유닛 컨트롤을 실행한다
		ScoutManager.Instance().update();

		// 전략적 판단 및 유닛 컨트롤
		StrategyManager.Instance().update();

		// JohnVer만의 추가 Action
		ActionManager.Instance().update();

		// 평균 소요시간 DP Start
		long spendTime = (System.currentTimeMillis() - startTime);
		lTotalSpendTime += spendTime;

		// 예외가 발생할 부분이 아닌데 발생하여 try catch문을 썼다.
		try {
			if (spendTime > 55) { // 44ms 초과 시 Inform
				System.out.println("[Warning][#" + MyBotModule.Broodwar.getFrameCount() + " frame]" + " ### " + spendTime + "ms 소요, 평균 " + lTotalSpendTime / MyBotModule.Broodwar.getFrameCount() + "ms");
			} else {
				if (MyBotModule.Broodwar.getFrameCount() > 0 && bTimeDPFlg) // 1 프레임 부터 계산시작
					System.out.println("[Info][#" + MyBotModule.Broodwar.getFrameCount() + " frame]" + " ### " + spendTime + "ms 소요, 평균 " + lTotalSpendTime / MyBotModule.Broodwar.getFrameCount() + "ms");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		// 평균 소요시간 DP End
	}

	/// 유닛(건물/지상유닛/공중유닛)이 Create 될 때 발생하는 이벤트를 처리합니다
	public void onUnitCreate(Unit unit) {
		InformationManager.Instance().onUnitCreate(unit);
	}

	/// 유닛(건물/지상유닛/공중유닛)이 Destroy 될 때 발생하는 이벤트를 처리합니다
	public void onUnitDestroy(Unit unit) {
		if (unit.getPlayer() == MyBotModule.Broodwar.self() && unit.getType().canBuildAddon()) {
			int width = unit.getType().tileWidth() + 2;
			int height = unit.getType().tileHeight();
			TilePosition tp = unit.getPosition().toTilePosition();
			for (int x = tp.getX() - 2; x < tp.getX() + width - 1; x++) {
				for (int y = tp.getY() - 1; y < tp.getY() + height - 1; y++) {
					TilePosition tp2 = new TilePosition(x, y);
					MyVariable.addonPlace.remove(tp2);
				}
			}
		}

		// ResourceDepot 및 Worker 에 대한 처리
		WorkerManager.Instance().onUnitDestroy(unit);

		InformationManager.Instance().onUnitDestroy(unit);

		if (MyVariable.isInitialBuildOrderFinished == false) {
			if (unit.getPlayer() == MyBotModule.Broodwar.self() && unit.getType().isBuilding() == false) {
				BuildManager.Instance().buildQueue.queueAsHighestPriority(unit.getType(), BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
			}
		}

		MyVariable.enemyBuildingUnit.remove(unit.getTilePosition());

		// 아군 유닛 제거
		if (unit.getPlayer() == MyBotModule.Broodwar.self() && !unit.getType().isBuilding()) {
			GroupManager.instance().remove(unit.getType(), unit.getID());
		}

	}

	/// 유닛(건물/지상유닛/공중유닛)이 Morph 될 때 발생하는 이벤트를 처리합니다<br>
	/// Zerg 종족의 유닛은 건물 건설이나 지상유닛/공중유닛 생산에서 거의 대부분 Morph 형태로 진행됩니다
	public void onUnitMorph(Unit unit) {
		InformationManager.Instance().onUnitMorph(unit);

		// Zerg 종족 Worker 의 Morph 에 대한 처리
		WorkerManager.Instance().onUnitMorph(unit);
	}

	/// 유닛(건물/지상유닛/공중유닛)의 소속 플레이어가 바뀔 때 발생하는 이벤트를 처리합니다<br>
	/// Gas Geyser에 어떤 플레이어가 Refinery 건물을 건설했을 때, Refinery 건물이 파괴되었을 때, Protoss 종족
	/// Dark Archon 의 Mind Control 에 의해 소속 플레이어가 바뀔 때 발생합니다
	public void onUnitRenegade(Unit unit) {
		// Vespene_Geyser (가스 광산) 에 누군가가 건설을 했을 경우
		// MyBotModule.Broodwar.sendText("A %s [%p] has renegaded. It is now owned by
		// %s", unit.getType().c_str(), unit, unit.getPlayer().getName().c_str());

		InformationManager.Instance().onUnitRenegade(unit);
	}

	// BasicBot 1.1 Patch Start ////////////////////////////////////////////////
	// 일꾼 탄생/파괴 등에 대한 업데이트 로직 버그 수정 : onUnitShow 가 아니라 onUnitComplete 에서 처리하도록 수정

	/// 유닛(건물/지상유닛/공중유닛)의 하던 일 (건물 건설, 업그레이드, 지상유닛 훈련 등)이 끝났을 때 발생하는 이벤트를 처리합니다
	public void onUnitComplete(Unit unit) {
		InformationManager.Instance().onUnitComplete(unit);

		// ResourceDepot 및 Worker 에 대한 처리
		WorkerManager.Instance().onUnitComplete(unit);
	}

	// BasicBot 1.1 Patch End //////////////////////////////////////////////////

	/// 유닛(건물/지상유닛/공중유닛)이 Discover 될 때 발생하는 이벤트를 처리합니다<br>
	/// 아군 유닛이 Create 되었을 때 라든가, 적군 유닛이 Discover 되었을 때 발생합니다
	public void onUnitDiscover(Unit unit) {

	}

	/// 유닛(건물/지상유닛/공중유닛)이 Evade 될 때 발생하는 이벤트를 처리합니다<br>
	/// 유닛이 Destroy 될 때 발생합니다
	public void onUnitEvade(Unit unit) {
	}

	// BasicBot 1.1 Patch Start ////////////////////////////////////////////////
	// 일꾼 탄생/파괴 등에 대한 업데이트 로직 버그 수정 : onUnitShow 가 아니라 onUnitComplete 에서 처리하도록 수정

	/// 유닛(건물/지상유닛/공중유닛)이 Show 될 때 발생하는 이벤트를 처리합니다<br>
	/// 아군 유닛이 Create 되었을 때 라든가, 적군 유닛이 Discover 되었을 때 발생합니다
	public void onUnitShow(Unit unit) {
		InformationManager.Instance().onUnitShow(unit);

		if (unit.getType() == UnitType.Resource_Mineral_Field || unit.getType() == UnitType.Resource_Mineral_Field_Type_2 || unit.getType() == UnitType.Resource_Mineral_Field_Type_3) {
			ConstructionPlaceFinder.Instance().getTilesToAvoid().add(unit.getTilePosition());
		}

		if (unit.getPlayer() == MyBotModule.Broodwar.self() && unit.getType().canBuildAddon()) {
			int width = unit.getType().tileWidth() + 2;
			int height = unit.getType().tileHeight();
			TilePosition tp = unit.getPosition().toTilePosition();
			for (int x = tp.getX() - 2; x < tp.getX() + width - 1; x++) {
				for (int y = tp.getY() - 1; y < tp.getY() + height - 1; y++) {
					TilePosition tp2 = new TilePosition(x, y);
					MyVariable.addonPlace.add(tp2);
				}
			}
		}

		// 아군 유닛을 그룹에 등록
		if (unit.getPlayer() == MyBotModule.Broodwar.self()) {
			if (!unit.getType().isBuilding()) {
				if (unit.getType() == UnitType.Terran_SCV) {
					GroupManager.instance().addToGroup(unit.getType(), unit.getID(), GroupManager.instance().groupWorker);
				} 
				//else if (unit.getType() == UnitType.Terran_Wraith) {
				//	GroupManager.instance().addToGroup(unit.getType(), unit.getID(), GroupManager.instance().groupWraith);
				//} 				
				else if (unit.getType() == UnitType.Terran_Science_Vessel) {
					GroupManager.instance().addToGroup(unit.getType(), unit.getID(), GroupManager.instance().groupScanUnit);
				} else if (unit.getType() != UnitType.Terran_Vulture_Spider_Mine) {
					GroupManager.instance().addToGroup(unit.getType(), unit.getID(), GroupManager.instance().groupAttack);
				}
			} else {

				if (InformationManager.Instance().enemyRace == Race.Terran && unit.getType() == UnitType.Terran_Barracks) {
					GroupManager.instance().addToGroup(unit.getType(), unit.getID(), GroupManager.instance().groupLandBuilding);
				}

				if (unit.getType() != UnitType.Terran_Bunker && unit.getType() != UnitType.Terran_Missile_Turret) {
					String key = MyUtil.getBuildingSizeKey(unit.getType());

					if (!MyVariable.mapBuildingSizeMap.containsKey(key)) {
						MyVariable.mapBuildingSizeMap.put(key, new ArrayList<Unit>());
					}
					MyVariable.mapBuildingSizeMap.get(key).add(unit);
				}

				TilePosition tp = unit.getTilePosition();
				int X = tp.getX();
				int Y = tp.getY();
				for (int i = -10; i <= 10; i++) {
					for (int j = -10; j <= 10; j++) {
						TilePosition tp2 = new TilePosition(X + i, Y + j);
						if (MyUtil.distanceTilePosition(tp, tp2) <= 10) {
							MyVariable.mapMyRegion.add(tp2);
						}
					}
				}
			}

		}

		// ResourceDepot 및 Worker 에 대한 처리
		// WorkerManager.Instance().onUnitShow(unit);
	}

	// BasicBot 1.1 Patch End //////////////////////////////////////////////////

	/// 유닛(건물/지상유닛/공중유닛)이 Hide 될 때 발생하는 이벤트를 처리합니다<br>
	/// 보이던 유닛이 Hide 될 때 발생합니다
	public void onUnitHide(Unit unit) {
		InformationManager.Instance().onUnitHide(unit);
	}

	// BasicBot 1.1 Patch Start ////////////////////////////////////////////////
	// onNukeDetect, onPlayerLeft, onSaveGame 이벤트를 처리할 수 있도록 메소드 추가

	/// 핵미사일 발사가 감지되었을 때 발생하는 이벤트를 처리합니다
	public void onNukeDetect(Position target) {
	}

	/// 다른 플레이어가 대결을 나갔을 때 발생하는 이벤트를 처리합니다
	public void onPlayerLeft(Player player) {
	}

	/// 게임을 저장할 때 발생하는 이벤트를 처리합니다
	public void onSaveGame(String gameName) {
	}

	// BasicBot 1.1 Patch End //////////////////////////////////////////////////

	/// 텍스트를 입력 후 엔터를 하여 다른 플레이어들에게 텍스트를 전달하려 할 때 발생하는 이벤트를 처리합니다
	public void onSendText(String text) {
	}

	/// 다른 플레이어로부터 텍스트를 전달받았을 때 발생하는 이벤트를 처리합니다
	public void onReceiveText(Player player, String text) {
	}
}