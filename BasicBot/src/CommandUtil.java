import java.util.HashSet;

import bwapi.Position;
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

	static public void attackUnit(Unit attacker, Unit target) {
		UnitCommand currentCommand = attacker.getLastCommand();
		if (currentCommand.getUnitCommandType() == UnitCommandType.Attack_Unit && currentCommand.getTarget() == target) {
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
		UnitCommand currentCommand = attacker.getLastCommand();
		if (currentCommand.getTargetPosition().equals(targetPosition)) {
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

		if (commandHash.contains(attacker)) {
			return;
		} else {
			commandHash.add(attacker);
		}

		attacker.move(targetPosition);
	}

	static public void rightClick(Unit unit, Unit target) {
		UnitCommand currentCommand = unit.getLastCommand();
		if (currentCommand.getUnitCommandType() == UnitCommandType.Right_Click_Unit && target.getPosition().equals(currentCommand.getTargetPosition())) {
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
		UnitCommand currentCommand = unit.getLastCommand();
		if (currentCommand.getUnitCommandType() == UnitCommandType.Repair && currentCommand.getTarget() == target) {
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