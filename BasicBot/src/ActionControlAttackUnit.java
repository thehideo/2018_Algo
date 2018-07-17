import bwapi.Race;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;
import bwta.Chokepoint;

public class ActionControlAttackUnit implements ActionInterface {
	CommandUtil commandUtil = new CommandUtil();

	int minPointX = Integer.MAX_VALUE;
	int maxPointX = Integer.MIN_VALUE;
	int minPointY = Integer.MAX_VALUE;
	int maxPointY = Integer.MIN_VALUE;

	public ActionControlAttackUnit() {
		for (int i = 0; i < MyBotModule.Broodwar.getStartLocations().size(); i++) {
			minPointX = Math.min(minPointX, MyBotModule.Broodwar.getStartLocations().get(i).getPoint().getX());
			maxPointX = Math.max(maxPointX, MyBotModule.Broodwar.getStartLocations().get(i).getPoint().getX());
			minPointY = Math.min(minPointY, MyBotModule.Broodwar.getStartLocations().get(i).getPoint().getY());
			maxPointY = Math.max(maxPointY, MyBotModule.Broodwar.getStartLocations().get(i).getPoint().getY());
		}
	}

	@Override
	public void action() {
		TilePosition myStartLocation = MyBotModule.Broodwar.self().getStartLocation().getPoint();

		// 공격 모드가 아닐 때에는 전투유닛들을 아군 진영 길목에 집결시켜서 방어
		if (MyVariable.isFullScaleAttackStarted == false) {
			Chokepoint saveChokePoint = getSaveChokePoint();
			for (Unit unit : MyVariable.attackUnit) {
				if (unit.isIdle()) {
					commandUtil.attackMove(unit, saveChokePoint.getCenter());
				}
			}
			if (InformationManager.Instance().enemyRace == Race.Protoss || InformationManager.Instance().enemyRace == Race.Terran) {
				if (MyVariable.getSelfUnit(UnitType.Terran_Command_Center).size() <= 1) {
					if (MyVariable.attackUnit.size() > 30 || MyVariable.getSelfUnit(UnitType.Terran_Siege_Tank_Tank_Mode).size() + MyVariable.getSelfUnit(UnitType.Terran_Siege_Tank_Siege_Mode).size() >= 6) {
						MyVariable.isFullScaleAttackStarted = true;
					}
				}
				// 확장 기지가 2개 라면
				else {
					if (MyVariable.attackUnit.size() > 50 || MyVariable.getSelfUnit(UnitType.Terran_Siege_Tank_Tank_Mode).size() + MyVariable.getSelfUnit(UnitType.Terran_Siege_Tank_Siege_Mode).size() >= 8) {
						MyVariable.isFullScaleAttackStarted = true;
					}
				}
			} else {
				if (MyVariable.getSelfUnit(UnitType.Terran_Command_Center).size() <= 1) {
					if (MyVariable.attackUnit.size() > 30) {
						MyVariable.isFullScaleAttackStarted = true;
					}
				}
				// 확장 기지가 2개 라면
				else {
					if (MyVariable.attackUnit.size() > 50) {
						MyVariable.isFullScaleAttackStarted = true;
					}
				}
			}
		}
		// 공격 모드가 되면, 모든 전투유닛들을 적군 Main BaseLocation 로 공격 가도록 합니다
		else {
			if (MyVariable.attackUnit.size() < 10) {
				MyVariable.isFullScaleAttackStarted = false;
			}
			// 메딕 비율만 높아도 방어 모드로 변경

			if (1.0 * MyVariable.getSelfAttackUnit(UnitType.Terran_Medic).size() / MyVariable.attackUnit.size() > 0.5) {
				MyVariable.isFullScaleAttackStarted = false;
			}

			int tank_cnt = MyVariable.getSelfUnit(UnitType.Terran_Siege_Tank_Siege_Mode).size() + MyVariable.getSelfUnit(UnitType.Terran_Siege_Tank_Tank_Mode).size();

			for (Unit unit : MyVariable.attackUnit) {
				// 더 이상 발견한 건물이 없다면 아무 곳으로 이동
				if (MyVariable.enemyBuildingUnit.size() == 0) {
					if (unit.isIdle()) {
						int xValue = minPointX + MyUtil.random.nextInt(maxPointX - minPointX);
						int yValue = minPointY + MyUtil.random.nextInt(maxPointY - minPointY);
						TilePosition position = new TilePosition(xValue, yValue);
						commandUtil.attackMove(unit, position.toPosition());
					}
				}
				// 발견한 건물이 있다면 그쪽으로 이동
				else {
					// 메딕은 치료할 곳으로 이동한다.
					if (unit.getType() == UnitType.Terran_Medic) {
						if (MyVariable.attackedUnit.size() > 0) {
							commandUtil.attackMove(unit, MyVariable.attackedUnit.get(0).getPosition());
						}
					}

					double distance = MyUtil.distanceTilePosition(unit.getTilePosition(), myStartLocation);
					if (MyVariable.mostFarTank != null && unit.getType() != UnitType.Terran_Siege_Tank_Tank_Mode && unit.getType() != UnitType.Terran_Siege_Tank_Siege_Mode && (MyVariable.distanceOfMostFarTank > 50 || MyVariable.enemyAttactingUnit.size() > 0) && distance > MyVariable.distanceOfMostFarTank) {
						commandUtil.attackMove(unit, myStartLocation.toPosition());
					} else

						for (TilePosition tilePosition : MyVariable.enemyBuildingUnit) {
							commandUtil.attackMove(unit, tilePosition.toPosition());
							break;
						}
				}
			}
		}
	}

	// 방어할 ChokePoint를 구한다.
	static Chokepoint getSaveChokePoint() {

		// 기본은 첫번째 초크 포인트
		Chokepoint chokePoint = BWTA.getNearestChokepoint(InformationManager.Instance().getMainBaseLocation(InformationManager.Instance().selfPlayer).getTilePosition());

		// 확장했으면 확장부분을 지킨다.
		if (MyVariable.getSelfUnit(UnitType.Terran_Command_Center).size() > 1) {
			chokePoint = InformationManager.Instance().getSecondChokePoint(InformationManager.Instance().selfPlayer);
		}

		return chokePoint;
	}
}
