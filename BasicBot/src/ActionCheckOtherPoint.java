import bwapi.TilePosition;
import bwapi.Unit;

public class ActionCheckOtherPoint implements ActionInterface {
	CommandUtil commandUtil = new CommandUtil();

	int minPointX = Integer.MAX_VALUE;
	int maxPointX = Integer.MIN_VALUE;
	int minPointY = Integer.MAX_VALUE;
	int maxPointY = Integer.MIN_VALUE;

	public ActionCheckOtherPoint() {
		for (int i = 0; i < MyBotModule.Broodwar.getStartLocations().size(); i++) {
			minPointX = Math.min(minPointX, MyBotModule.Broodwar.getStartLocations().get(i).getPoint().getX());
			maxPointX = Math.max(maxPointX, MyBotModule.Broodwar.getStartLocations().get(i).getPoint().getX());
			minPointY = Math.min(minPointY, MyBotModule.Broodwar.getStartLocations().get(i).getPoint().getY());
			maxPointY = Math.max(maxPointY, MyBotModule.Broodwar.getStartLocations().get(i).getPoint().getY());
		}
	}

	@Override
	public void action() {
		if (MyVariable.isFullScaleAttackStarted == false) {
			return;
		}

		for (Unit unit : MyVariable.attackUnit) {
			// 더 이상 발견한 건물이 없다면 아무 곳으로 이동
			if (MyVariable.enemyBuildingUnit.size() == 0) {
				int xValue = minPointX + MyUtil.random.nextInt(maxPointX - minPointX);
				int yValue = minPointY + MyUtil.random.nextInt(maxPointY - minPointY);
				TilePosition position = new TilePosition(xValue, yValue);
				commandUtil.attackMove(unit, position.toPosition());
			}
			// 발견한 건물이 있다면 그쪽으로 이동
			else {
				for (TilePosition tilePosition : MyVariable.enemyBuildingUnit) {
					commandUtil.attackMove(unit, tilePosition.toPosition());
					break;
				}
			}
		}

	}

}
