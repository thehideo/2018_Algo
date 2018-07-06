import java.util.Random;

import bwapi.TilePosition;

public class MyUtil {
	
	
	static Random random = new Random();
	
	static double distance(TilePosition a, TilePosition b) {
		return Math.sqrt((a.getX() - b.getX()) * (a.getX() - b.getX()) + (a.getY() - b.getY()) * (a.getY() - b.getY()));
	}
	
	
}
