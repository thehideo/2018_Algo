import bwapi.TechType;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BaseLocation;

public class ControlVulture extends ControlAbstract {

	BaseLocation bl = InformationManager.Instance().getFirstExpansionLocation(MyBotModule.Broodwar.self());

	public void actionMain(Unit unit, GroupAbstract groupAbstract) {
		if (MyUtil.distanceTilePosition(MyVariable.myStartLocation, unit.getTilePosition()) > MyUtil.distanceTilePosition(MyVariable.myStartLocation, MyVariable.myFirstchokePoint)) {
			if (bl != null && MyUtil.distanceTilePosition(unit.getTilePosition(), bl.getTilePosition()) > 6) {
				if (unit.canUseTech(TechType.Spider_Mines, unit.getPosition())) {
					int X = unit.getTilePosition().getX() / 4;
					int Y = unit.getTilePosition().getY() / 4;
					if (!MyVariable.spinderMinePosition.contains(new TilePosition(X, Y))) {
						if (!CommandUtil.commandHash.contains(unit)) {
							CommandUtil.commandHash.add(unit);
							unit.useTech(TechType.Spider_Mines, unit.getPosition());
							setSpecialAction(unit);
						}
					}
				}
			}
		}

		if (MyVariable.isFullScaleAttackStarted == false && unit.getSpiderMineCount() > 0 && unit.canUseTech(TechType.Spider_Mines, unit.getPosition()) && MyVariable.enemyAttactUnit.size() == 0) {
			for (TilePosition tilePosition : MyVariable.enemyBuildingUnit) {
				if (unit.getHitPoints() == UnitType.Terran_Vulture.maxHitPoints()) {
					CommandUtil.attackMove(unit, tilePosition.toPosition());
				}
				break;

			}
		}

		//if (MyVariable.enemyAttactingUnit.size() > 0) {
			CommandUtil.attackMove(unit, groupAbstract.getTargetPosition(unit));
		//}
	}
}
