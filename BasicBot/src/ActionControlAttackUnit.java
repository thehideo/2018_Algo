import java.util.ArrayList;

import bwapi.Race;
import bwapi.TechType;
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

		boolean needToWait = false;

		// scanner가 모두 소진되었지만 베슬이 없으면 대기해야함
		boolean canUseScan = false;
		for (Unit unit : MyVariable.getSelfUnit(UnitType.Terran_Comsat_Station)) {
			if (unit.canUseTechPosition(TechType.Scanner_Sweep)) {
				canUseScan = true;
			}
		}
		if (canUseScan == false && MyVariable.getSelfUnit(UnitType.Terran_Science_Vessel).size() == 0) {
			needToWait = true;
			MyVariable.isFullScaleAttackStarted = false;
		}

		// 공격 모드가 아닐 때에는 전투유닛들을 아군 진영 길목에 집결시켜서 방어
		if (MyVariable.isFullScaleAttackStarted == false || needToWait == true) {
			Chokepoint saveChokePoint = MyUtil.getSaveChokePoint();
			for (Unit unit : MyVariable.attackUnit) {
				if (unit.isIdle()) {
					commandUtil.attackMove(unit, saveChokePoint.getCenter());
				}
			}
			if (InformationManager.Instance().enemyRace == Race.Protoss || InformationManager.Instance().enemyRace == Race.Terran) {
				if (MyVariable.getSelfUnit(UnitType.Terran_Command_Center).size() <= 1) {
					if (MyVariable.attackUnit.size() > 30 && MyVariable.getSelfUnit(UnitType.Terran_Siege_Tank_Tank_Mode).size() + MyVariable.getSelfUnit(UnitType.Terran_Siege_Tank_Siege_Mode).size() >= 2) {
						MyVariable.isFullScaleAttackStarted = true;
					}
				}
				// 확장 기지가 2개 라면
				else {
					if (MyVariable.attackUnit.size() > 40 && MyVariable.getSelfUnit(UnitType.Terran_Siege_Tank_Tank_Mode).size() + MyVariable.getSelfUnit(UnitType.Terran_Siege_Tank_Siege_Mode).size() >= 3) {
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
					if (MyVariable.attackUnit.size() > 40 && MyVariable.getSelfUnit(UnitType.Terran_Siege_Tank_Tank_Mode).size() + MyVariable.getSelfUnit(UnitType.Terran_Siege_Tank_Siege_Mode).size() >= 3) {
						MyVariable.isFullScaleAttackStarted = true;
					}
				}
			}
			if (needToWait == true) {
				MyVariable.isFullScaleAttackStarted = false;
			}
		}
		// 공격 모드가 되면, 모든 전투유닛들을 적군 Main BaseLocation 로 공격 가도록 합니다
		else {
			if (InformationManager.Instance().enemyRace == Race.Protoss || InformationManager.Instance().enemyRace == Race.Terran) {
				if (MyVariable.attackUnit.size() < 10 || MyVariable.getSelfUnit(UnitType.Terran_Siege_Tank_Tank_Mode).size() + MyVariable.getSelfUnit(UnitType.Terran_Siege_Tank_Siege_Mode).size() <= 2) {
					MyVariable.isFullScaleAttackStarted = false;
				}
			} else {
				if (MyVariable.attackUnit.size() < 10) {
					MyVariable.isFullScaleAttackStarted = false;

				}
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
					if (MyVariable.mostFarTank != null && unit.getType() != UnitType.Terran_Siege_Tank_Tank_Mode && unit.getType() != UnitType.Terran_Siege_Tank_Siege_Mode && (MyVariable.distanceOfMostFarTank > 40 || MyVariable.enemyAttactingUnit.size() > 0) && distance > MyVariable.distanceOfMostFarTank) {
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
}
