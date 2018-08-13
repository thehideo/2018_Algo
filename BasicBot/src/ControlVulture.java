import java.util.HashSet;
import java.util.List;

import bwapi.TechType;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BaseLocation;

public class ControlVulture extends ControlAbstract {

	BaseLocation bl = InformationManager.Instance().getFirstExpansionLocation(MyBotModule.Broodwar.self());

	public void actionMain(Unit unit, GroupAbstract groupAbstract) {
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
				int enemyVultureCnt = 0;
				int selfVultureCnt = 0;

				for (Unit u : ourUnits) {
					if (u.getType() == UnitType.Terran_Vulture) {
						if (u.getPlayer() == MyBotModule.Broodwar.enemy()) {
							enemyVultureCnt++;
						}
						if (u.getPlayer() == MyBotModule.Broodwar.self()) {
							selfVultureCnt++;
						}
					}
				}

				// 적이 있을 때
				if (enemyVultureCnt > 0)
				{
					// 내가 상대방 보다 많으면 공격하고
					if (selfVultureCnt > enemyVultureCnt) {
						Unit mostCloseEnemyAttackUnit = MyUtil.getMostCloseEnemyUnit(unit, MyVariable.enemyAttactUnit);
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
				if (MyVariable.findTank == false && MyVariable.findBunker == false) {
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
