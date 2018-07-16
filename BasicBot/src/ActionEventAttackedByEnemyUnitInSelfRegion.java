import bwapi.Unit;

// 내 본진 근처에 있는 적을 공격한다.
public class ActionEventAttackedByEnemyUnitInSelfRegion implements ActionInterface {
	CommandUtil commandUtil = new CommandUtil();

	@Override
	public void action() {
		if (MyVariable.enemyUnitAroundMyStartPoint.size() > 0) {
			for (Unit unit : MyVariable.defenceUnit) {
				if (MyVariable.mostCloseEnemyUnit != null && unit.canAttack(MyVariable.mostCloseEnemyUnit)) {
					commandUtil.attackMove(unit, MyVariable.mostCloseEnemyUnit.getPoint());
				} else {
					commandUtil.attackMove(unit, MyVariable.enemyUnitAroundMyStartPoint.get(0).getPoint());
				}
			}
			// 적이 5마리가 넘어가면 공격 유닛도 방어하러 온다.
			if (MyVariable.enemyUnitAroundMyStartPoint.size() > 5 || MyVariable.defenceUnit.size() <= 2) {
				for (Unit unit : MyVariable.attackUnit) {
					if (MyVariable.mostCloseEnemyUnit != null && unit.canAttack(MyVariable.mostCloseEnemyUnit)) {
						commandUtil.attackMove(unit, MyVariable.mostCloseEnemyUnit.getPoint());
					} else {
						commandUtil.attackMove(unit, MyVariable.enemyUnitAroundMyStartPoint.get(0).getPoint());
					}
				}
			}
		}

	}
}
