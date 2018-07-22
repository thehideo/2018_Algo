import bwapi.Color;
import bwapi.Position;
import bwapi.Unit;
import bwapi.UnitCommand;
import bwapi.UnitCommandType;
import bwapi.UnitType;
import bwapi.UpgradeType;
import bwapi.WeaponType;

public class CommandUtil {

	public void attackUnit(Unit attacker, Unit target) {		
		UnitCommand currentCommand = attacker.getLastCommand();
		if (currentCommand.getUnitCommandType() == UnitCommandType.Attack_Unit && currentCommand.getTarget() == target) {
			return;
		}
		attacker.attack(target);
	}

	public void attackMove(Unit attacker, final Position targetPosition) {
		UnitCommand currentCommand = attacker.getLastCommand();
		if (currentCommand.getTargetPosition().equals(targetPosition)) {
			return;
		}
		attacker.attack(targetPosition);
	}

	public void move(Unit attacker, final Position targetPosition) {
		attacker.move(targetPosition);
	}

	public void rightClick(Unit unit, Unit target) {
		UnitCommand currentCommand = unit.getLastCommand();	
		if (currentCommand.getUnitCommandType() == UnitCommandType.Right_Click_Unit && target.getPosition().equals(currentCommand.getTargetPosition())) {
			return;
		}
		unit.rightClick(target);
	}

	public void repair(Unit unit, Unit target) {
		UnitCommand currentCommand = unit.getLastCommand();
		if (currentCommand.getUnitCommandType() == UnitCommandType.Repair && currentCommand.getTarget() == target) {
			return;
		}
		unit.repair(target);
	}
}