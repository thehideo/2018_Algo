import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import bwapi.Color;
import bwapi.Position;
import bwapi.Race;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;
import bwta.BaseLocation;
import bwta.Region;

/// 게임 초반에 일꾼 유닛 중에서 정찰 유닛을 하나 지정하고, 정찰 유닛을 이동시켜 정찰을 수행하는 class<br>
/// 적군의 BaseLocation 위치를 알아내는 것까지만 개발되어있습니다
public class ScoutManager {

	private Unit currentScoutUnit;
	private int currentScoutStatus;
	
	public enum ScoutStatus {
		NoScout, /// < 정찰 유닛을 미지정한 상태
		MovingToAnotherBaseLocation, /// < 적군의 BaseLocation 이 미발견된 상태에서 정찰 유닛을 이동시키고 있는 상태
		MoveAroundEnemyBaseLocation, /// < 적군의 BaseLocation 이 발견된 상태에서 정찰 유닛을 이동시키고 있는 상태
		MoveAroundEnemyExpansionLocation /// < 적군의 BaseLocation 이 발견된 상태에서 정찰 유닛을 ExpansionLocation으로 이동시키고 있는 상태
	};
	
	private BaseLocation currentScoutTargetBaseLocation = null;
	private Vector<Position> enemyBaseRegionVertices = new Vector<Position>();
	private int currentScoutFreeToVertexIndex = -1;
	private Position currentScoutTargetPosition = Position.None;

	private CommandUtil commandUtil = new CommandUtil();

	private static ScoutManager instance = new ScoutManager();

	/// static singleton 객체를 리턴합니다
	public static ScoutManager Instance() {
		return instance;
	}

	/// 정찰 유닛을 지정하고, 정찰 상태를 업데이트하고, 정찰 유닛을 이동시킵니다
	public void update() {
		// 1초에 4번만 실행합니다
		if (MyBotModule.Broodwar.getFrameCount() % 6 != 0)
			return;

		// scoutUnit 을 지정하고, scoutUnit 의 이동을 컨트롤함.
		assignScoutIfNeeded();
		moveScoutUnit();

//		System.out.println("CurrentScoutStatus:"+ currentScoutStatus);
		// 참고로, scoutUnit 의 이동에 의해 발견된 정보를 처리하는 것은 InformationManager.update() 에서 수행함
	}

	/// 정찰 유닛을 필요하면 새로 지정합니다
	public void assignScoutIfNeeded() {
		BaseLocation enemyBaseLocation = InformationManager.Instance().getMainBaseLocation(MyBotModule.Broodwar.enemy());

		if (enemyBaseLocation == null) {
			if (currentScoutUnit == null || currentScoutUnit.exists() == false || currentScoutUnit.getHitPoints() <= 0) {
				currentScoutUnit = null;
				currentScoutStatus = ScoutStatus.NoScout.ordinal();

				// first building (Pylon / Supply Depot / Spawning Pool) 을 건설 시작한 후, 가장 가까이에 있는
				// Worker 를 정찰유닛으로 지정한다
				Unit firstBuilding = null;

				for (Unit unit : MyBotModule.Broodwar.self().getUnits()) {
					if (unit.getType().isBuilding() == true && unit.getType().isResourceDepot() == false) {
						firstBuilding = unit;
						break;
					}
				}

				if (firstBuilding != null) {
					// grab the closest worker to the first building to send to scout
					Unit unit = WorkerManager.Instance().getClosestMineralWorkerTo(firstBuilding.getPosition());

					// if we find a worker (which we should) add it to the scout units
					// 정찰 나갈 일꾼이 없으면, 아무것도 하지 않는다
					if (unit != null) {
						// set unit as scout unit
						currentScoutUnit = unit;
						WorkerManager.Instance().setScoutWorker(currentScoutUnit);
						currentScoutStatus = ScoutStatus.MovingToAnotherBaseLocation.ordinal();

						// 참고로, 일꾼의 정찰 임무를 해제하려면, 다음과 같이 하면 된다
						// WorkerManager::Instance().setIdleWorker(currentScoutUnit);
					}
				}
			}
		} else {
			// 적의 기지를 발견한 이후 정찰정책 추가(yh.kim)
			// 첫 번째 정찰일꾼이 죽으면 다른 일꾼을 선정하여 정찰임무를 수행한다. (초기정찰은 2마리 일꾼까지만 수행후 else 아래 후반 정찰임무수행으로 넘어감)
			// 두 번째 정찰일꾼은 적의 첫번째 확장기지로 이동하여 확장타이밍을 체크한다. 
			if(currentScoutStatus == ScoutStatus.MovingToAnotherBaseLocation.ordinal()
					|| currentScoutStatus == ScoutStatus.MoveAroundEnemyBaseLocation.ordinal()) {
				
				if(WorkerManager.Instance().getScoutWorker() != null) {
					// 1) 첫 번째 정찰일꾼이 적기지를 찾은 후 적 기지 주변을 도는 모드로 변경
					if(currentScoutStatus == ScoutStatus.MovingToAnotherBaseLocation.ordinal())
						currentScoutStatus = ScoutStatus.MoveAroundEnemyBaseLocation.ordinal();
				} else {
					// 2) 첫 번째 정찰일꾼이 죽은 후 두 번째 정찰일꾼 새로 할당 후 적의 첫번째 확장기지로 이동.
					// firstChoke point 에서 가장 가까운 곳의 일꾼선정
					Unit unit = WorkerManager.Instance().getClosestMineralWorkerTo(InformationManager.Instance().getFirstChokePoint(MyBotModule.Broodwar.self()).getCenter());
					
					if (unit != null) {
						// set unit as scout unit
						currentScoutUnit = unit;
						WorkerManager.Instance().setScoutWorker(currentScoutUnit);
						currentScoutStatus = ScoutManager.ScoutStatus.MoveAroundEnemyExpansionLocation.ordinal();
					}
				}
			} else {
				// 초기 정찰 이후의 정찰 임무 수행
				// 스캐너가 200이 꽉찬 상태면 스캔을 한 번 뿌려준다.(구현 필요)
//				if(MyBotModule.Broodwar.getFrameCount() - beforeTime > MyVariable.nFrameCntPerMin * 3)
//				ArrayList<Unit> units = MyVariable.getSelfUnit(UnitType.Terran_Comsat_Station);
//				for (int i = 0; i < units.size(); i++) {
//					if (units.get(i).canUseTech(TechType.Scanner_Sweep, unit)) {
//						units.get(i).useTech(TechType.Scanner_Sweep, unit);
//						beforeTime = MyBotModule.Broodwar.getFrameCount();
//						break;
//					}
//				}
			}
		}
	}

	/// 정찰 유닛을 이동시킵니다
	// 상대방 MainBaseLocation 위치를 모르는 상황이면, StartLocation 들에 대해 아군의 MainBaseLocation에서
	/// 가까운 것부터 순서대로 정찰
	// 상대방 MainBaseLocation 위치를 아는 상황이면, 해당 BaseLocation 이 있는 Region의 가장자리를 따라 계속
	/// 이동함 (정찰 유닛이 죽을때까지)
	public void moveScoutUnit() {
		if (currentScoutUnit == null || currentScoutUnit.exists() == false || currentScoutUnit.getHitPoints() <= 0) {
			currentScoutUnit = null;
			currentScoutStatus = ScoutStatus.NoScout.ordinal();
			return;
		}

		BaseLocation enemyBaseLocation = InformationManager.Instance().getMainBaseLocation(InformationManager.Instance().enemyPlayer);
		BaseLocation myBaseLocation = InformationManager.Instance().getMainBaseLocation(MyBotModule.Broodwar.self());
		
		// 정찰유닛이 정해져있고, 타겟이 정해져있고 아직 정찰하지 않은 BaseLocation 이라면 계속 Move 한다.
		if (currentScoutUnit != null && currentScoutTargetBaseLocation != null && MyBotModule.Broodwar.isExplored(currentScoutTargetBaseLocation.getTilePosition()) == false) {
			commandUtil.move(currentScoutUnit, currentScoutTargetBaseLocation.getPoint());
		}

		// 적의 기지를 아직 발견하지 못한 상태
		if (enemyBaseLocation == null) {
			// currentScoutTargetBaseLocation 가 null 이거나 정찰 유닛이
			// currentScoutTargetBaseLocation 에 도착했으면
			// 아군 MainBaseLocation 으로부터 가장 가까운 미정찰 BaseLocation 을 새로운 정찰 대상
			// currentScoutTargetBaseLocation 으로 잡아서 이동
			if (currentScoutTargetBaseLocation == null || currentScoutUnit.getDistance(currentScoutTargetBaseLocation.getPosition()) < 5 * Config.TILE_SIZE) {
				currentScoutStatus = ScoutStatus.MovingToAnotherBaseLocation.ordinal();

				double closestDistance = 1000000000;
				double tempDistance = 0;
				BaseLocation closestBaseLocation = null;
				for (BaseLocation startLocation : BWTA.getStartLocations()) {
					// if we haven't explored it yet (방문했었던 곳은 다시 가볼 필요 없음)
					if (MyBotModule.Broodwar.isExplored(startLocation.getTilePosition()) == false) {
						// GroundDistance 를 기준으로 가장 가까운 곳으로 선정
						tempDistance = (double) (InformationManager.Instance().getMainBaseLocation(MyBotModule.Broodwar.self()).getGroundDistance(startLocation) + 0.5);

						if (tempDistance > 0 && tempDistance < closestDistance) {
							closestBaseLocation = startLocation;
							closestDistance = tempDistance;
						}
					}
				}

				if (closestBaseLocation != null) {
					// assign a scout to go scout it
					commandUtil.move(currentScoutUnit, closestBaseLocation.getPosition());
					currentScoutTargetBaseLocation = closestBaseLocation;
				}
			}
		}
		// if we know where the enemy region is
		else {
			// if scout is exist, move scout into enemy region
			if (currentScoutUnit != null) {

				currentScoutTargetBaseLocation = enemyBaseLocation;

				if (MyBotModule.Broodwar.isExplored(currentScoutTargetBaseLocation.getTilePosition()) == false) {
					// 이 case 로 들어오는 경우는 어떤 경우인지? 
					currentScoutStatus = ScoutStatus.MovingToAnotherBaseLocation.ordinal();
					currentScoutTargetPosition = currentScoutTargetBaseLocation.getPosition();
					commandUtil.move(currentScoutUnit, currentScoutTargetPosition);

				} else {
					// 적 기지를 발견한 이후 적 기지 경계선을 따라 돈다. 
					if(currentScoutStatus == ScoutManager.ScoutStatus.MoveAroundEnemyBaseLocation.ordinal()) {
//						currentScoutStatus = ScoutStatus.MoveAroundEnemyBaseLocation.ordinal();
						currentScoutTargetPosition = getScoutFleePositionFromEnemyRegionVertices();
						commandUtil.move(currentScoutUnit, currentScoutTargetPosition);			
					} else if(currentScoutStatus == ScoutManager.ScoutStatus.MoveAroundEnemyExpansionLocation.ordinal()) {
						// 두 번째 일꾼은 적의 확장기지로 이동
						currentScoutTargetPosition = InformationManager.Instance().getFirstExpansionLocation(MyBotModule.Broodwar.enemy()).getPosition();
//						if(currentScoutUnit.getDistance(currentScoutTargetPosition) >= 5 ) { // 도착했으면 move 하지 않도록 hold 처리
							commandUtil.move(currentScoutUnit, currentScoutTargetPosition);	
					}
					
//					WorkerManager.Instance().setIdleWorker(currentScoutUnit);
//					currentScoutStatus = ScoutStatus.NoScout.ordinal();
//					currentScoutTargetPosition = myBaseLocation.getPosition();
				}
			}
		}
	}

	public Position getScoutFleePositionFromEnemyRegionVertices() {
		// calculate enemy region vertices if we haven't yet
		if (enemyBaseRegionVertices.isEmpty()) {
			calculateEnemyRegionVertices();
		}

		if (enemyBaseRegionVertices.isEmpty()) {
			return MyBotModule.Broodwar.self().getStartLocation().toPosition();
		}

		// if this is the first flee, we will not have a previous perimeter index
		if (currentScoutFreeToVertexIndex == -1) {
			// so return the closest position in the polygon
			int closestPolygonIndex = getClosestVertexIndex(currentScoutUnit);

			if (closestPolygonIndex == -1) {
				return MyBotModule.Broodwar.self().getStartLocation().toPosition();
			} else {
				// set the current index so we know how to iterate if we are still fleeing later
				currentScoutFreeToVertexIndex = closestPolygonIndex;
				return enemyBaseRegionVertices.get(closestPolygonIndex);
			}
		}
		// if we are still fleeing from the previous frame, get the next location if we
		// are close enough
		else {
			double distanceFromCurrentVertex = enemyBaseRegionVertices.get(currentScoutFreeToVertexIndex).getDistance(currentScoutUnit.getPosition());

			// keep going to the next vertex in the perimeter until we get to one we're far
			// enough from to issue another move command
			while (distanceFromCurrentVertex < 128) {
				currentScoutFreeToVertexIndex = (currentScoutFreeToVertexIndex + 1) % enemyBaseRegionVertices.size();
				distanceFromCurrentVertex = enemyBaseRegionVertices.get(currentScoutFreeToVertexIndex).getDistance(currentScoutUnit.getPosition());
			}

			return enemyBaseRegionVertices.get(currentScoutFreeToVertexIndex);
		}
	}

	// Enemy MainBaseLocation 이 있는 Region 의 가장자리를 enemyBaseRegionVertices 에 저장한다
	// Region 내 모든 건물을 Eliminate 시키기 위한 지도 탐색 로직 작성시 참고할 수 있다
	public void calculateEnemyRegionVertices() {
		BaseLocation enemyBaseLocation = InformationManager.Instance().getMainBaseLocation(MyBotModule.Broodwar.enemy());
		if (enemyBaseLocation == null) {
			return;
		}

		Region enemyRegion = enemyBaseLocation.getRegion();
		if (enemyRegion == null) {
			return;
		}
		final Position basePosition = MyBotModule.Broodwar.self().getStartLocation().toPosition();
		final Vector<TilePosition> closestTobase = MapTools.Instance().getClosestTilesTo(basePosition);
		Set<Position> unsortedVertices = new HashSet<Position>();

		// check each tile position
		for (int i = 0; i < closestTobase.size(); ++i) {
			final TilePosition tp = closestTobase.get(i);

			if (BWTA.getRegion(tp) != enemyRegion) {
				continue;
			}

			// a tile is 'surrounded' if
			// 1) in all 4 directions there's a tile position in the current region
			// 2) in all 4 directions there's a buildable tile
			boolean surrounded = true;
			if (BWTA.getRegion(new TilePosition(tp.getX() + 1, tp.getY())) != enemyRegion || !MyBotModule.Broodwar.isBuildable(new TilePosition(tp.getX() + 1, tp.getY())) || BWTA.getRegion(new TilePosition(tp.getX(), tp.getY() + 1)) != enemyRegion || !MyBotModule.Broodwar.isBuildable(new TilePosition(tp.getX(), tp.getY() + 1)) || BWTA.getRegion(new TilePosition(tp.getX() - 1, tp.getY())) != enemyRegion || !MyBotModule.Broodwar.isBuildable(new TilePosition(tp.getX() - 1, tp.getY())) || BWTA.getRegion(new TilePosition(tp.getX(), tp.getY() - 1)) != enemyRegion || !MyBotModule.Broodwar.isBuildable(new TilePosition(tp.getX(), tp.getY() - 1))) {
				surrounded = false;
			}

			// push the tiles that aren't surrounded
			// Region의 가장자리 타일들만 추가한다
			if (!surrounded && MyBotModule.Broodwar.isBuildable(tp)) {
				if (Config.DrawScoutInfo) {
					int x1 = tp.getX() * 32 + 2;
					int y1 = tp.getY() * 32 + 2;
					int x2 = (tp.getX() + 1) * 32 - 2;
					int y2 = (tp.getY() + 1) * 32 - 2;
					MyBotModule.Broodwar.drawTextMap(x1 + 3, y1 + 2, "" + BWTA.getGroundDistance(tp, basePosition.toTilePosition()));
					MyBotModule.Broodwar.drawBoxMap(x1, y1, x2, y2, Color.Green, false);
				}

				unsortedVertices.add(new Position(tp.toPosition().getX() + 16, tp.toPosition().getY() + 16));
			}
		}

		Vector<Position> sortedVertices = new Vector<Position>();
		Position current = unsortedVertices.iterator().next();
		enemyBaseRegionVertices.add(current);
		unsortedVertices.remove(current);

		// while we still have unsorted vertices left, find the closest one remaining to
		// current
		while (!unsortedVertices.isEmpty()) {
			double bestDist = 1000000;
			Position bestPos = null;

			for (final Position pos : unsortedVertices) {
				double dist = pos.getDistance(current);

				if (dist < bestDist) {
					bestDist = dist;
					bestPos = pos;
				}
			}

			current = bestPos;
			sortedVertices.add(bestPos);
			unsortedVertices.remove(bestPos);
		}

		// let's close loops on a threshold, eliminating death grooves
		int distanceThreshold = 100;

		while (true) {
			// find the largest index difference whose distance is less than the threshold
			int maxFarthest = 0;
			int maxFarthestStart = 0;
			int maxFarthestEnd = 0;

			// for each starting vertex
			for (int i = 0; i < (int) sortedVertices.size(); ++i) {
				int farthest = 0;
				int farthestIndex = 0;

				// only test half way around because we'll find the other one on the way back
				for (int j = 1; j < sortedVertices.size() / 2; ++j) {
					int jindex = (i + j) % sortedVertices.size();

					if (sortedVertices.get(i).getDistance(sortedVertices.get(jindex)) < distanceThreshold) {
						farthest = j;
						farthestIndex = jindex;
					}
				}

				if (farthest > maxFarthest) {
					maxFarthest = farthest;
					maxFarthestStart = i;
					maxFarthestEnd = farthestIndex;
				}
			}

			// stop when we have no long chains within the threshold
			if (maxFarthest < 4) {
				break;
			}

			double dist = sortedVertices.get(maxFarthestStart).getDistance(sortedVertices.get(maxFarthestEnd));

			Vector<Position> temp = new Vector<Position>();

			for (int s = maxFarthestEnd; s != maxFarthestStart; s = (s + 1) % sortedVertices.size()) {

				temp.add(sortedVertices.get(s));
			}

			sortedVertices = temp;
		}

		enemyBaseRegionVertices = sortedVertices;
	}

	public int getClosestVertexIndex(Unit unit) {
		int closestIndex = -1;
		double closestDistance = 10000000;

		for (int i = 0; i < enemyBaseRegionVertices.size(); ++i) {
			double dist = unit.getDistance(enemyBaseRegionVertices.get(i));
			if (dist < closestDistance) {
				closestDistance = dist;
				closestIndex = i;
			}
		}

		return closestIndex;
	}

	/// 정찰 유닛을 리턴합니다
	public Unit getScoutUnit() {
		return currentScoutUnit;
	}

	// 정찰 상태를 리턴합니다
	public int getScoutStatus() {
		return currentScoutStatus;
	}

	/// 정찰 유닛의 이동 목표 BaseLocation 을 리턴합니다
	public BaseLocation getScoutTargetBaseLocation() {
		return currentScoutTargetBaseLocation;
	}

	/// 적군의 Main Base Location 이 있는 Region 의 경계선에 해당하는 Vertex 들의 목록을 리턴합니다
	public Vector<Position> getEnemyRegionVertices() {
		return enemyBaseRegionVertices;
	}
}