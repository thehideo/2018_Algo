import java.util.HashMap;

import bwapi.TechType;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;

public class ControlVulture extends ControlAbstract {

	public void actionMain(Unit unit, Group groupAbstract) {
		/*
		 * if (unit.getGroundWeaponCooldown() != 0) { CommandUtil.move(unit,
		 * MyVariable.myStartLocation.toPosition()); }
		 */
		if (MyUtil.distanceTilePosition(MyVariable.myStartLocation, unit.getTilePosition()) > MyUtil.distanceTilePosition(MyVariable.myStartLocation, MyVariable.myFirstchokePoint)) {
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

		if (MyVariable.isFullScaleAttackStarted == false && unit.getSpiderMineCount() > 0 && unit.canUseTech(TechType.Spider_Mines, unit.getPosition())) {
			for (TilePosition tilePosition : MyVariable.enemyBuildingUnit) {
				if (unit.getHitPoints() == UnitType.Terran_Vulture.maxHitPoints()) {
					CommandUtil.attackMove(unit, tilePosition.toPosition());
				}
				break;

			}

		}

		CommandUtil.attackMove(unit, groupAbstract.getTargetPosition(unit));
	}
}
