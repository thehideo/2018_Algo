import bwapi.Unit;

// 내 본진 근처에 있는 적을 공격한다.
public class ActionAttackEnemyUnitInMyRegion implements ActionInterface {
	CommandUtil commandUtil = new CommandUtil();

	@Override
	public void action() {
		if (MyVariable.enemyUnitAroundMyStartPoint.size() > 0) {
			if (MyVariable.defenceUnit.size() > 0) {
				for (Unit unit : MyVariable.defenceUnit) {
					commandUtil.attackMove(unit, MyVariable.enemyUnitAroundMyStartPoint.get(0).getPoint());
				}
			} else {
				for (Unit unit : MyVariable.attackUnit) {
					commandUtil.attackMove(unit, MyVariable.enemyUnitAroundMyStartPoint.get(0).getPoint());
				}
			}
		}
		if (MyVariable.enemyUnitAroundMyStartPoint.size() > 10) {
			for (Unit unit : MyVariable.attackUnit) {
				commandUtil.attackMove(unit, MyVariable.enemyUnitAroundMyStartPoint.get(0).getPoint());
			}
		}
	}

}
