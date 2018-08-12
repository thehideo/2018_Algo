import java.util.HashMap;

import bwapi.Unit;
import bwapi.UnitType;

public abstract class ControlAbstract {

	HashMap<Integer, Integer> mapMoveBackTime = new HashMap<Integer, Integer>();

	HashMap<Integer, Integer> mapSpecialActionTime = new HashMap<Integer, Integer>();

	void setSpecialAction(Unit unit, int addTime) {
		mapSpecialActionTime.put(unit.getID(), MyBotModule.Broodwar.getFrameCount() + addTime);
	}

	void action(Unit unit, GroupAbstract groupAbstract) {
		// 싸이오닉 스톰 맞으면 뒤로 뺀다.
		if (unit.isUnderStorm()) {
			CommandUtil.move(unit, MyVariable.myStartLocation.toPosition());
			return;
		}

		// 러커 또는 다크템플러가 확인되었는데 스캔할 방법이 없으면 터렛으로 도망
		if (unit.getType() != UnitType.Terran_SCV) {
			if ((MyVariable.findLucker == true || MyVariable.findDarkTempler) && MyUtil.canUseScan() == false && MyVariable.getSelfUnit(UnitType.Terran_Science_Vessel).size() == 0) {
				if (MyVariable.mostFarTurret != null) {
					CommandUtil.attackMove(unit, MyVariable.mostFarTurret.getPosition());
				}
				return;
			}
		}

		if (groupAbstract == GroupManager.instance().groupAttack) {
			if (MyVariable.isFullScaleAttackStarted == true && unit == MyVariable.mostFarAttackUnit) {

				CommandUtil.attackMove(unit, MyVariable.myStartLocation.getPoint().toPosition());

				mapMoveBackTime.put(unit.getID(), MyBotModule.Broodwar.getFrameCount());

				return;
			}
		}

		// 가장 앞에 있어서 뒤로 뺀 경우에는 다른 액션을 하지 않는다.
		if (mapMoveBackTime.get(unit.getID()) != null) {
			if (MyBotModule.Broodwar.getFrameCount() - mapMoveBackTime.get(unit.getID()) < 24) {
				return;
			}
		}

		// 특수 기술을 사용한 경우에는 다른 액션을 하지 않는다.
		if (mapSpecialActionTime.get(unit.getID()) != null) {
			if (MyBotModule.Broodwar.getFrameCount() - mapSpecialActionTime.get(unit.getID()) < 12) {
				return;
			}
		}

		actionMain(unit, groupAbstract);
	}

	abstract void actionMain(Unit unit, GroupAbstract groupAbstract);

}
