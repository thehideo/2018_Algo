import java.util.Comparator;

import bwapi.TilePosition;

public class ComparatorBaseLocation implements Comparator<TilePosition> {

	@Override
	public int compare(TilePosition o1, TilePosition o2) {
		int midX = MyVariable.map_max_x / 2;
		int midY = MyVariable.map_max_y / 2;

		double tmp=Math.atan2(o1.getX() - midX, o1.getY() - midY) - Math.atan2(o2.getX() - midX, o2.getY() - midY) ;
		if (tmp> 0) {
			return 1;
		}else if(tmp<0) {
			return -1;
		}

		return 0;
	}

}
