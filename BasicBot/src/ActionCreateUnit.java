import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import bwapi.UnitType;

public class ActionCreateUnit implements ActionInterface {
	@Override
	public void action() {
		if (MyVariable.isInitialBuildOrderFinished == false) {
			return;
		}

		if (MyBotModule.Broodwar.self().minerals() >= 200) {

			HashMap<UnitType, Double> tmp = new HashMap<UnitType, Double>();

			for (UnitType unitType : MyVariable.attackUnitRatio.keySet()) {
				// 0은 계산 불가
				if (MyVariable.attackUnitRatio.get(unitType) == 0 && MyBotModule.Broodwar.canMake(unitType) == false) {
					continue;
				}
				int count = 0;
				if (MyVariable.mapSelfAttackUnit.containsKey(unitType)) {
					count = MyVariable.mapSelfAttackUnit.get(unitType).size();
				}
				tmp.put(unitType, (double) (1.0 * count / MyVariable.attackUnitRatio.get(unitType)));
			}

			ArrayList<Double> tmp2 = new ArrayList<Double>();
			tmp2.addAll(tmp.values());
			Collections.sort(tmp2);

			for (int i = 0; i < tmp2.size(); i++) {
				for (UnitType unitType : MyVariable.attackUnitRatio.keySet()) {
					if (tmp.get(unitType) == tmp2.get(i) && MyBotModule.Broodwar.canMake(unitType)) {
						if (BuildManager.Instance().buildQueue.getItemCount(unitType) == 0) {
							BuildManager.Instance().buildQueue.queueAsLowestPriority(unitType, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
						}
						return;
					}
				}
			}
		}
	}
}
