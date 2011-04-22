package net.abstractplain.tiletown;

import java.awt.Point;

public class PointUtil {

	public static String xyString(Point gridPos) {
		if (gridPos == null) {
			return "" + null;
		}
		return gridPos.x + "," + gridPos.y;
	}

}
