import java.util.ArrayList;
import java.util.HashMap;

import bwapi.TilePosition;
import bwta.BWTA;

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
		try {
			// 매 프레임마다 유닛 중복 명령을 방지하는 hash초기화 한다.
			CommandUtil.clearCommandHash();

			// Action Micro Control이 모든 명령에 우선이다.
			actionMicroControl.action();
			int frame = MyBotModule.Broodwar.getFrameCount() % 24;
			for (ActionInterface action : map.get(frame)) {
				action.action();
			}
		} catch (Exception ex) {

		}
	}
}
