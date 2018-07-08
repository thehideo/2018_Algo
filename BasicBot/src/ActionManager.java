import java.util.ArrayList;
import java.util.HashMap;

public class ActionManager {
	HashMap<Integer, ArrayList<ActionInterface>> map = new HashMap<Integer, ArrayList<ActionInterface>>();

	private static ActionManager instance = new ActionManager();

	ActionMicroControl actionMicroControl = new ActionMicroControl();

	public static ActionManager Instance() {
		return instance;
	}

	public ActionManager() {
		for (int i = 0; i < 100; i++) {
			if (!map.containsKey(i)) {
				map.put(i, new ArrayList<ActionInterface>());
			}
		}
	}

	public void addAction(ActionInterface action, int index) {
		map.get(index).add(action);
	}

	public void action() {
		int frame = MyBotModule.Broodwar.getFrameCount() % 24;
		for (ActionInterface action : map.get(frame)) {
			action.action();
		}
		actionMicroControl.action();
	}
}
