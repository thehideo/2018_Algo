import java.util.HashSet;

import bwapi.Position;
import bwapi.TechType;
import bwapi.Unit;
import bwapi.UnitCommand;
import bwapi.UnitCommandType;

public class CommandUtil {
	// 한 프레임에 여러 명령을 받지 않도록 hash로 관리함
	static public HashSet<Unit> commandHash = new HashSet<Unit>();

	// 매 프레임 마다 초기화 한다.
	static public void clearCommandHash() {
		commandHash.clear();
	}

	static public void useStim_Packs(Unit unit) {
		if (commandHash.contains(unit)) {
			return;
		} else {
			commandHash.add(unit);
		}
		unit.useTech(TechType.Stim_Packs);
	}

	static public void siege(Unit unit) {
		if (commandHash.contains(unit)) {
			return;
		} else {
			commandHash.add(unit);
		}
		unit.siege();
	}

	static public void unsiege(Unit unit) {
		if (commandHash.contains(unit)) {
			return;
		} else {
			commandHash.add(unit);
		}
		unit.unsiege();
	}

	static public void attackUnit(Unit attacker, Unit target) {
		// UnitCommand currentCommand = attacker.getLastCommand();
		// if (currentCommand.getTarget() == target && attacker.isIdle() != false) {
		// return;
		// }

		if (attacker == null || target == null || target.isDetected() == false) {
			return;
		}

		if (commandHash.contains(attacker)) {
			return;
		} else {
			commandHash.add(attacker);
		}

		attacker.attack(target);
	}

	static public void attackMove(Unit attacker, final Position targetPosition) {
		// UnitCommand currentCommand = attacker.getLastCommand();
		//
		// if (currentCommand.getTargetPosition().equals(targetPosition) &&
		// attacker.isIdle() != false) {
		// return;
		// }

		if (targetPosition == null || attacker == null || !targetPosition.isValid() || attacker.isDetected() == false) {
			return;
		}

		if (commandHash.contains(attacker)) {
			return;
		} else {
			commandHash.add(attacker);
		}

		attacker.attack(targetPosition);
	}

	static public void move(Unit attacker, final Position targetPosition) {

		// UnitCommand currentCommand = attacker.getLastCommand();
		//
		// if (currentCommand.getTargetPosition().equals(targetPosition) &&
		// attacker.isIdle() != false) {
		// return;
		// }

		if (targetPosition == null || attacker == null || !targetPosition.isValid()) {
			return;
		}

		if (commandHash.contains(attacker)) {
			return;
		} else {
			commandHash.add(attacker);
		}

		attacker.move(targetPosition);
	}

	static public void rightClick(Unit unit, Unit target) {
		// UnitCommand currentCommand = unit.getLastCommand();
		//
		// if (currentCommand.getTarget() == target && unit.isIdle() != false) {
		// return;
		// }

		if (unit == null || target == null) {
			return;
		}

		if (commandHash.contains(unit)) {
			return;
		} else {
			commandHash.add(unit);
		}

		unit.rightClick(target);
	}

	static public void repair(Unit unit, Unit target) {
		// UnitCommand currentCommand = unit.getLastCommand();
		//
		// if (currentCommand.getTarget() == target && unit.isIdle() != false) {
		// return;
		// }
		if (unit == null || target == null) {
			return;
		}

		if (commandHash.contains(unit)) {
			return;
		} else {
			commandHash.add(unit);
		}

		unit.repair(target);
	}
}