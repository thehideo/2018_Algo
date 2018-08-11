import java.util.HashSet;

import bwapi.TechType;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BaseLocation;

public class ControlVulture extends ControlAbstract {

	BaseLocation bl = InformationManager.Instance().getFirstExpansionLocation(MyBotModule.Broodwar.self());

	HashSet<Integer> findEnemy = new HashSet<Integer>();

	public void actionMain(Unit unit, GroupAbstract groupAbstract) {
		// Spinder Mines 사용
		if (MyVariable.enemyStartLocation != null) {
			boolean useSpider_Mines = false;

			if (MyUtil.distanceTilePosition(MyVariable.enemyStartLocation, unit.getTilePosition()) < MyUtil.distanceTilePosition(MyVariable.enemyFirstchokePoint, MyVariable.enemyStartLocation) + 10) {
				useSpider_Mines = true;
			}

			if(( MyVariable.findTank || findEnemy.contains(unit.getID())) && MyUtil.distanceTilePosition(MyVariable.myStartLocation, unit.getTilePosition()) > MyUtil.distanceTilePosition(MyVariable.myStartLocation, MyVariable.myFirstchokePoint)) {
				useSpider_Mines = true;
			}

			if (useSpider_Mines) {
				if (bl != null && MyUtil.distanceTilePosition(unit.getTilePosition(), bl.getTilePosition()) > 6) {
					if (unit.canUseTech(TechType.Spider_Mines, unit.getPosition())) {
						int X = unit.getTilePosition().getX() / 4;
						int Y = unit.getTilePosition().getY() / 4;
						if (!MyVariable.spinderMinePosition.contains(new TilePosition(X, Y))) {
							CommandUtil.useTech(unit, TechType.Spider_Mines, unit.getPosition());
							setSpecialAction(unit);
						}
					}
				}
			}
		}

		// 적을 만난 벌처
		if (MyVariable.enemyAttactUnit.size() > 0) {
			findEnemy.add(unit.getID());
		}

		if (groupAbstract == GroupManager.instance().groupAttack && MyVariable.findTank == false) {
			//if (MyVariable.isFullScaleAttackStarted == false && unit.getSpiderMineCount() > 0 && unit.canUseTech(TechType.Spider_Mines, unit.getPosition()) && MyVariable.enemyAttactUnit.size() == 0) {
			if (MyVariable.isFullScaleAttackStarted == false) {
				for (TilePosition tilePosition : MyVariable.enemyBuildingUnit) {
					if (unit.getHitPoints() == UnitType.Terran_Vulture.maxHitPoints()) {
						CommandUtil.attackMove(unit, tilePosition.toPosition());
					}
					break;
				}
			}
		}

		CommandUtil.attackMove(unit, groupAbstract.getTargetPosition(unit));
	}
}
