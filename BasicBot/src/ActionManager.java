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

	public void onStart() {
		addAction(new ActionCreateUnit(), 1);
		addAction(new ActionUpgrade(), 2);
		addAction(new ActionControlMarin(), 3);
		addAction(new ActionControlScanUnit(), 4);
		addAction(new ActionCreateBuilding(), 5);
		addAction(new ActionSetUnitRatio(), 6);
		addAction(new ActionControlAttackUnit(), 7);
		addAction(new ActionSupplyManagement(), 8);
		addAction(new ActionControlDefenceUnit(), 9);		
		addAction(new ActionPatrolBaseLocation(), 10);
		
		// Scanner 체크는 2번
		addAction(new ActionUseScanner(), 2);
		addAction(new ActionUseScanner(), 9);
		
		// Tank 체크는 3번
		addAction(new ActionControlTank(), 6);
		addAction(new ActionControlTank(), 14);
		addAction(new ActionControlTank(), 22);
		
	}

	public void addAction(ActionInterface action, int index) {
		map.get(index).add(action);
	}

	public void update() {
		try {
			//if (MyBotModule.Broodwar.getFrameCount() % 12 == 0) 
			{
				// 매 프레임마다 유닛 중복 명령을 방지하는 hash초기화 한다.
				CommandUtil.clearCommandHash();
				// Action Micro Control이 모든 명령에 우선이다.
				actionMicroControl.action();
			}

			int frame = MyBotModule.Broodwar.getFrameCount() % 24;
			for (ActionInterface action : map.get(frame)) {
				action.action();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
