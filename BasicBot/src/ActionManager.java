import java.util.ArrayList;
import java.util.HashMap;

import bwapi.TilePosition;
import bwta.BWTA;

public class ActionManager {
	HashMap<Integer, ArrayList<ActionControlAbstract>> map = new HashMap<Integer, ArrayList<ActionControlAbstract>>();

	private static ActionManager instance = new ActionManager();

	ActionMicroControl actionMicroControl = new ActionMicroControl();

	public static ActionManager Instance() {
		return instance;
	}

	public ActionManager() {
		for (int i = 0; i < 100; i++) {
			if (!map.containsKey(i)) {
				map.put(i, new ArrayList<ActionControlAbstract>());
			}
		}
	}

	public void onStart() {
		addAction(new ActionCreateUnit(), 1);
		addAction(new ActionUpgrade(), 2);

		addAction(new ActionCreateBuilding(), 5);
		addAction(new ActionSetUnitRatio(), 6);
		addAction(new ActionControlBunker(), 6);
		addAction(new ActionControlGroup(),7);
		addAction(new ActionSupplyManagement(), 8);

		// Scanner 체크는 2번
		addAction(new ActionUseScanner(), 2);
		addAction(new ActionUseScanner(), 9);

	}

	public void addAction(ActionControlAbstract action, int index) {
		map.get(index).add(action);
	}

	public void update() {
		try {
			// if (MyBotModule.Broodwar.getFrameCount() % 12 == 0)
			{
				// 매 프레임마다 유닛 중복 명령을 방지하는 hash초기화 한다.
				
				// Action Micro Control이 모든 명령에 우선이다.
				actionMicroControl.action();
			}

			int frame = MyBotModule.Broodwar.getFrameCount() % 24;
			for (ActionControlAbstract action : map.get(frame)) {
				action.action();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
