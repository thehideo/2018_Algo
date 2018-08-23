import bwapi.Race;
import bwapi.Unit;
import bwapi.UnitType;

public class ControlMarin extends ControlAbstract {
	void actionMain(Unit unit, GroupAbstract groupAbstract) {
		if (unit.isAttacking() && unit.isStimmed() == false && 1.0 * unit.getHitPoints() / unit.getInitialHitPoints() > 0.5) {
			CommandUtil.useStim_Packs(unit);
			setSpecialAction(unit, 0);
			return;
		}
		if (groupAbstract == GroupManager.instance().groupAttack && ActionControlBunker.needMarinCnt > 0) {
			for (Unit bunker : MyVariable.getSelfUnit(UnitType.Terran_Bunker)) {
				if (bunker.getLoadedUnits().size() < 4) {
					CommandUtil.commandHash.add(unit);
					unit.rightClick(bunker);
					break;
				}
			}
		}
		if (InformationManager.Instance().enemyRace == Race.Terran) {
			if (MyVariable.isFullScaleAttackStarted == false) {
				if (MyVariable.enemyStartLocation != null) {
					CommandUtil.attackMove(unit, MyUtil.getSaveTilePosition(6).toPosition());
					return;
				}
			}
		} else if (InformationManager.Instance().enemyRace == Race.Protoss) {
			for (Unit Protoss_High_Templar : MyVariable.getEnemyUnit(UnitType.Protoss_High_Templar)) {
				if (Protoss_High_Templar.getEnergy() >= 74) {
					if (!MyVariable.mapMyRegion.contains(Protoss_High_Templar.getTilePosition())) {
						if (unit.getDistance(Protoss_High_Templar) < 12 * 32) { // 사정 거리 9
							CommandUtil.move(unit, MyVariable.myStartLocation.toPosition());
							return;
						}
					}
				}
			}
			for (Unit Protoss_Reaver : MyVariable.getEnemyUnit(UnitType.Protoss_Reaver)) {
				if (!MyVariable.mapMyRegion.contains(Protoss_Reaver.getTilePosition())) {
					if (unit.getDistance(Protoss_Reaver) < 12 * 32) {
						CommandUtil.move(unit, MyVariable.myStartLocation.toPosition());
						return;
					}
				}
			}

			if (MyVariable.isFullScaleAttackStarted == true && MyVariable.getEnemyUnit(UnitType.Protoss_Dragoon).size() > 0 && unit.getDistance(MyVariable.myStartLocation.toPosition()) / 32 > MyVariable.distanceOfMostFarTank + 2) {
				CommandUtil.move(unit, MyVariable.myStartLocation.toPosition());
				return;
			}

			// 초반 입구방어 하면서 밖으로 튀어나가지 않도록 수정
			if (MyVariable.isFullScaleAttackStarted == false && MyVariable.getSelfUnit(UnitType.Terran_Command_Center).size() <= 1) {
				if (MyUtil.distanceTilePosition(unit.getTilePosition(), MyVariable.myStartLocation) > MyUtil.distanceTilePosition(groupAbstract.getTargetPosition(unit).toTilePosition(), MyVariable.myStartLocation) + 2) {
					CommandUtil.move(unit, MyVariable.myStartLocation.toPosition());
					return;
				}
			}
		} else if (InformationManager.Instance().enemyRace == Race.Zerg) {
			for (Unit Zerg_Lurker : MyVariable.getEnemyUnit(UnitType.Zerg_Lurker)) {
				if (MyUtil.distancePosition(unit.getPosition(), MyVariable.myStartLocation.toPosition()) < MyUtil.distancePosition(Zerg_Lurker.getPosition(), MyVariable.myStartLocation.toPosition())) {
					if (Zerg_Lurker.isBurrowed() == true) {
						if (unit.getDistance(Zerg_Lurker) < 6 * 32) { // 러커 사거리 6
							CommandUtil.move(unit, MyVariable.myStartLocation.toPosition());
							return;
						}
					}
				}
			}
		}
		CommandUtil.attackMove(unit, groupAbstract.getTargetPosition(unit));
	}
}
