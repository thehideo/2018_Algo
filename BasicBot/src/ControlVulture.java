import java.util.Iterator;
import java.util.List;

import bwapi.TechType;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BaseLocation;

public class ControlVulture extends ControlAbstract {

	public static final int SIEGE_MODE_MIN_RANGE = UnitType.Terran_Siege_Tank_Siege_Mode.groundWeapon().minRange(); // 64
	public static final int SIEGE_MODE_MAX_RANGE = UnitType.Terran_Siege_Tank_Siege_Mode.groundWeapon().maxRange(); // 384

	BaseLocation bl = InformationManager.Instance().getFirstExpansionLocation(MyBotModule.Broodwar.self());

	public void actionMain(Unit unit, GroupAbstract groupAbstract) {
		// 어텍 그룹일 때는 탱크를 피해서 움직인다.
		if (groupAbstract == GroupManager.instance().groupAttack) {
			if (MyUtil.GetMyTankCnt() >= 1) {
				Iterator<Integer> tankIDs = MyVariable.mapTankPosition.keySet().iterator();
				while (tankIDs.hasNext()) {
					Integer TankID = tankIDs.next();
					if (MyUtil.distancePosition(unit.getPosition(), MyVariable.mapTankPosition.get(TankID).toPosition()) <= SIEGE_MODE_MAX_RANGE + 32) {
						CommandUtil.move(unit, MyVariable.myStartLocation.toPosition());
						return;
					}
				}
			}
		}

		// Spinder Mines 사용
		if (MyVariable.enemyStartLocation != null) {
			boolean useSpider_Mines = false;

			// 내 본진 밖에 Mine을 심는다.
			if (MyUtil.distanceTilePosition(MyVariable.myStartLocation, unit.getTilePosition()) > MyUtil.distanceTilePosition(MyVariable.myStartLocation, MyVariable.myFirstchokePoint)) {
				useSpider_Mines = true;
			}

			if (useSpider_Mines) {
				boolean findEnemy = false;
				List<Unit> ourUnits = MyBotModule.Broodwar.getUnitsInRadius(unit.getPosition(), 32 * 10);
				for (Unit u : ourUnits) {
					if (u.getPlayer() == MyBotModule.Broodwar.enemy()) {
						findEnemy = true;
						break;
					}
				}
				// 적을 발견하지 않았을 때 마인을 심는다.
				if (findEnemy == false && bl != null && MyUtil.distanceTilePosition(unit.getTilePosition(), bl.getTilePosition()) > 6) {
					if (unit.canUseTech(TechType.Spider_Mines, unit.getPosition())) {
						int X = unit.getTilePosition().getX() / 4;
						int Y = unit.getTilePosition().getY() / 4;
						if (!MyVariable.spinderMinePosition.contains(new TilePosition(X, Y))) {
							CommandUtil.useTech(unit, TechType.Spider_Mines, unit.getPosition());
							setSpecialAction(unit, 0);
						}
					}
				}
			}
		}

		if (groupAbstract == GroupManager.instance().groupAttack) {
			if (MyVariable.isFullScaleAttackStarted == false) {

				List<Unit> ourUnits = MyBotModule.Broodwar.getUnitsInRadius(unit.getPosition(), 32 * 7);
				Double enemyVultureCnt = 0.0;
				Double selfVultureCnt = 0.0;

				for (Unit u : ourUnits) {
					if (u.getPlayer() == MyBotModule.Broodwar.enemy()) {
						if (u.getType() == UnitType.Terran_Vulture) {
							enemyVultureCnt++;
						} else if (u.getType() == UnitType.Terran_Marine) {
							enemyVultureCnt++;
						} else if (u.getType() == UnitType.Terran_Goliath) {
							enemyVultureCnt = enemyVultureCnt + 3;
						} else if (u.getType() == UnitType.Terran_Siege_Tank_Siege_Mode || u.getType() == UnitType.Terran_Siege_Tank_Tank_Mode) {
							enemyVultureCnt = enemyVultureCnt + 3;
						} else if (u.getType() == UnitType.Terran_SCV) {
							enemyVultureCnt = enemyVultureCnt + 0.01;
						}
					}
					if (u.getPlayer() == MyBotModule.Broodwar.self()) {
						if (u.getType() == UnitType.Terran_Vulture) {
							selfVultureCnt++;
						} else if (u.getType() == UnitType.Terran_Marine) {
							selfVultureCnt++;
						} else if (u.getType() == UnitType.Terran_Goliath) {
							selfVultureCnt = selfVultureCnt + 3;
						} else if (u.getType() == UnitType.Terran_Siege_Tank_Siege_Mode || u.getType() == UnitType.Terran_Siege_Tank_Tank_Mode) {
							selfVultureCnt = selfVultureCnt + 3;
						} else if (u.getType() == UnitType.Terran_SCV) {
							selfVultureCnt = selfVultureCnt + 0.01;
						}
					}
				}

				// 적이 있을 때
				if (enemyVultureCnt > 0) {
					// 내가 상대방 보다 많으면 공격하고
					if (selfVultureCnt > enemyVultureCnt) {
						Unit mostCloseEnemyAttackUnit = MyUtil.getMostCloseUnit(unit, MyVariable.enemyAttactUnit);
						if (mostCloseEnemyAttackUnit != null) {
							CommandUtil.attackUnit(unit, mostCloseEnemyAttackUnit);
							return;
						}
					}
					// 그렇지 않으면 도망간다.
					else {
						if (MyUtil.distanceTilePosition(MyVariable.myStartLocation, unit.getTilePosition()) >= 5) {
							CommandUtil.move(unit, MyVariable.myStartLocation.toPosition());
							return;
						}
					}
				}

				// 주위에 적군이 없으면
				// 그렇지 않으면 상대 본진으로 가면서 마인을 심고 돌아온다.

				// 탱크나 벙커가 안보이면 마인을 심고 가서 적 본진 앞에 대기하고
				if (MyVariable.findTank == false && MyVariable.findBunker == false && MyVariable.findWraith == false) {
					CommandUtil.attackMove(unit, InformationManager.Instance().getFirstExpansionLocation(MyBotModule.Broodwar.enemy()).getPoint());
				}
				// 그렇지 않으면 마인만 심는다.
				else if (unit.getSpiderMineCount() > 0) {
					CommandUtil.attackMove(unit, InformationManager.Instance().getFirstExpansionLocation(MyBotModule.Broodwar.enemy()).getPoint());
				} else {
					CommandUtil.attackMove(unit, MyUtil.getSaveTilePosition(0).toPosition());
				}

			}
		}
		CommandUtil.attackMove(unit, groupAbstract.getTargetPosition(unit));
	}
}
