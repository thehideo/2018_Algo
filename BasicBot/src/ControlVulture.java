import java.util.HashMap;

import bwapi.TechType;
import bwapi.TilePosition;
import bwapi.Unit;

public class ControlVulture extends ControlAbstract {

	HashMap<Integer, Integer> mapSpecialActionTime = new HashMap<Integer, Integer>();

	public void actionMain(Unit unit, Group groupAbstract) {

		if (mapSpecialActionTime.get(unit.getID()) != null) {
			if (MyBotModule.Broodwar.getFrameCount() - mapSpecialActionTime.get(unit.getID()) < 12) {
				return;
			}
		}

		if (unit.getGroundWeaponCooldown() != 0 && MyVariable.mostFarTank != null) {
			CommandUtil.attackMove(unit, MyVariable.mostFarTank.getPosition());
		}

		if (MyUtil.distanceTilePosition(MyVariable.myStartLocation, unit.getTilePosition()) > 40) {
			if (unit.canUseTech(TechType.Spider_Mines, unit.getPosition())) {
				if (!MyVariable.spinderMinePosition.contains(unit.getPoint().toTilePosition())) {
					if (!CommandUtil.commandHash.contains(unit)) {
						CommandUtil.commandHash.add(unit);
						unit.useTech(TechType.Spider_Mines, unit.getPosition());
						mapSpecialActionTime.put(unit.getID(), MyBotModule.Broodwar.getFrameCount());
					}
				}
			}
		}

		if (MyVariable.isFullScaleAttackStarted == false && unit.getSpiderMineCount() > 0 && unit.canUseTech(TechType.Spider_Mines, unit.getPosition())) {
			for (TilePosition tilePosition : MyVariable.enemyBuildingUnit) {
				CommandUtil.attackMove(unit, tilePosition.toPosition());
				return;
			}

		}

		CommandUtil.attackMove(unit, groupAbstract.getTarget(unit));
	}
}
