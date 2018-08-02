import bwapi.Position;

public class ActionUnitData {
	enum ActionType {
		Attack, Defence, None
	};

	ActionType actionType = ActionType.None;

	Position target = null;
}
