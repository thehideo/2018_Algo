import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import bwapi.UnitType;

public class ActionCreateUnit extends ActionControlAbstract {

	// 설정된 비율에 따라서 현재 비율이 낮은 유닛을 생산
	public void action() {
		if (MyVariable.isInitialBuildOrderFinished == false) {
			return;
		}

		if (BuildManager.Instance().getBuildQueue().getItemCount(UnitType.Terran_Command_Center) > 0 && MyBotModule.Broodwar.self().minerals() <= 450) {
			return;
		}

		if (MyVariable.attackUnit.size() < 4 || MyBotModule.Broodwar.self().minerals() >= 200) {
			HashMap<UnitType, Double> tmp = new HashMap<UnitType, Double>();
			Iterator<UnitType> attackUnitRatioKey = MyVariable.attackUnitRatio.keySet().iterator();
			while (attackUnitRatioKey.hasNext()) {
				// 0은 계산 불가
				UnitType unitType = attackUnitRatioKey.next();

				if (MyVariable.attackUnitRatio.get(unitType) == 0 && MyBotModule.Broodwar.canMake(unitType) == false) {
					continue;
				}
				int count = 0;
				if (unitType == UnitType.Terran_Siege_Tank_Siege_Mode || unitType == UnitType.Terran_Siege_Tank_Tank_Mode) {
					count = MyVariable.getSelfAttackUnit(UnitType.Terran_Siege_Tank_Siege_Mode).size() + MyVariable.getSelfAttackUnit(UnitType.Terran_Siege_Tank_Tank_Mode).size();
					tmp.put(UnitType.Terran_Siege_Tank_Tank_Mode, (double) (1.0 * count / MyVariable.attackUnitRatio.get(UnitType.Terran_Siege_Tank_Tank_Mode)));
				} else {
					count = MyVariable.getSelfAttackUnit(unitType).size();
					tmp.put(unitType, (double) (1.0 * count / MyVariable.attackUnitRatio.get(unitType)) - 0.0000001 * MyVariable.attackUnitRatio.get(unitType));
				}
			}
			ArrayList<Double> tmp2 = new ArrayList<Double>();
			tmp2.addAll(tmp.values());
			Collections.sort(tmp2);
			/// 여기 까지 정렬 완료
			for (int i = 0; i < tmp2.size(); i++) {
				Iterator<UnitType> attackUnitRatioKeys = MyVariable.attackUnitRatio.keySet().iterator();
				while (attackUnitRatioKeys.hasNext()) {
					UnitType unitType = attackUnitRatioKeys.next();
					if (tmp.get(unitType) == tmp2.get(i)) {
						if (MyBotModule.Broodwar.canMake(unitType)) {
							if (BuildManager.Instance().buildQueue.getItemCount(unitType) == 0) {
								// 높은 우선 순위로 하면 벌처만 생산될 수도 있음
								BuildManager.Instance().buildQueue.queueAsLowestPriority(unitType, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
								break;
							}

						}

					}
				}
			}
		}
	}
}
