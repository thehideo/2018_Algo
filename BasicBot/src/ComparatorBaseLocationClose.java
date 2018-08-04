import java.util.Comparator;

import bwapi.TilePosition;

public class ComparatorBaseLocationClose implements Comparator<TilePosition> {

	@Override
	public int compare(TilePosition o1, TilePosition o2) {
		double tmp = MyUtil.distanceTilePosition(o1, MyVariable.myStartLocation) - MyUtil.distanceTilePosition(o2, MyVariable.myStartLocation);
		if (tmp > 0) {
			return 1;
		} else if (tmp < 0) {
			return -1;
		}

		return 0;
	}

}
