import java.util.HashSet;

import bwapi.Race;
import bwapi.Unit;
import bwta.BWTA;
import bwta.BaseLocation;
import bwta.Chokepoint;

public class ActionCombat implements ActionInterface {
	CommandUtil commandUtil = new CommandUtil();
	HashSet<Unit> firstFullScaleAttack = new HashSet<Unit>();

	@Override
	public void action() {
		// 공격 모드가 아닐 때에는 전투유닛들을 아군 진영 길목에 집결시켜서 방어
		if (MyVariable.isFullScaleAttackStarted == false) {
			Chokepoint firstChokePoint = BWTA.getNearestChokepoint(InformationManager.Instance().getMainBaseLocation(InformationManager.Instance().selfPlayer).getTilePosition());

			for (Unit unit : MyVariable.attackUnit) {
				if (unit.canAttack() && unit.getType() != InformationManager.Instance().getWorkerType() && unit.isIdle()) {
					commandUtil.attackMove(unit, firstChokePoint.getCenter());
				}
			}

			// 전투 유닛이 2개 이상 생산되었고, 적군 위치가 파악되었으면 총공격 모드로 전환
			if (MyVariable.attackUnit.size() > 30) {
				if (InformationManager.Instance().enemyPlayer != null && InformationManager.Instance().enemyRace != Race.Unknown && InformationManager.Instance().getOccupiedBaseLocations(InformationManager.Instance().enemyPlayer).size() > 0) {
					MyVariable.isFullScaleAttackStarted = true;
				}
			}
		}
		// 공격 모드가 되면, 모든 전투유닛들을 적군 Main BaseLocation 로 공격 가도록 합니다
		else {
			if (MyVariable.attackUnit.size() < 10) {
				MyVariable.isFullScaleAttackStarted = false;
			}

			if (InformationManager.Instance().enemyPlayer != null && InformationManager.Instance().enemyRace != Race.Unknown && InformationManager.Instance().getOccupiedBaseLocations(InformationManager.Instance().enemyPlayer).size() > 0) {
				// 공격 대상 지역 결정
				BaseLocation targetBaseLocation = null;
				double closestDistance = 100000000;

				for (BaseLocation baseLocation : InformationManager.Instance().getOccupiedBaseLocations(InformationManager.Instance().enemyPlayer)) {
					double distance = BWTA.getGroundDistance(InformationManager.Instance().getMainBaseLocation(InformationManager.Instance().selfPlayer).getTilePosition(), baseLocation.getTilePosition());

					if (distance < closestDistance) {
						closestDistance = distance;
						targetBaseLocation = baseLocation;
					}
				}

				if (targetBaseLocation != null) {
					for (Unit unit : MyVariable.attackUnit) {
						// 건물은 제외
						if (unit.getType().isBuilding()) {
							continue;
						}
						// 모든 일꾼은 제외
						if (unit.getType().isWorker()) {
							continue;
						}
						// canAttack 유닛은 attackMove Command 로 공격을 보냅니다
						if (unit.canAttack()) {
							if (!firstFullScaleAttack.contains(unit)) {
								firstFullScaleAttack.add(unit);
								commandUtil.attackMove(unit, targetBaseLocation.getPosition());
							}
						}
					}
				}
			}
		}
	}
}
