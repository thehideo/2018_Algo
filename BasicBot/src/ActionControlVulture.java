import java.util.ArrayList;

import bwapi.Position;
import bwapi.TechType;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;

public class ActionControlVulture implements ActionInterface {

	TilePosition myStartLocation = MyBotModule.Broodwar.self().getStartLocation().getPoint();

	@Override
	public void action() {
		/*
		ArrayList<Unit> Terran_Vulture = MyVariable.getSelfUnit(UnitType.Terran_Vulture);

		for (Unit unit : Terran_Vulture) {
			double distance = MyUtil.distanceTilePosition(unit.getTilePosition(), myStartLocation);
			if (distance > 40) {
				Position position = unit.getPosition();
				if (!MyVariable.spinderMinePosition.contains(new Position(position.getX() + 1, position.getY() + 1)) && !MyVariable.spinderMinePosition.contains(new Position(position.getX() + 1, position.getY() + 0)) && !MyVariable.spinderMinePosition.contains(new Position(position.getX() + 1, position.getY() - 1)) && !MyVariable.spinderMinePosition.contains(new Position(position.getX() + 0, position.getY() + 1)) && !MyVariable.spinderMinePosition.contains(new Position(position.getX() + 0, position.getY() + 0)) && !MyVariable.spinderMinePosition.contains(new Position(position.getX() + 0, position.getY() - 1)) && !MyVariable.spinderMinePosition.contains(new Position(position.getX() - 1, position.getY() + 1)) && !MyVariable.spinderMinePosition.contains(new Position(position.getX() - 1, position.getY() + 0)) && !MyVariable.spinderMinePosition.contains(new Position(position.getX() - 1, position.getY() - 1)) && unit.canUseTech(TechType.Spider_Mines, unit.getPosition())) {
					MyVariable.spinderMinePosition.add(unit.getPosition());
					unit.useTech(TechType.Spider_Mines, unit.getPosition());
				}
			}
		}*/
	}
}
