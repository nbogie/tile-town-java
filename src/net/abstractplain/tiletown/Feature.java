//TODO: don't use this class for the identification of feature type, only for parsing the terrain detail matrix.
//We may have extensible "feature" set so don't use enum.
//Use enums for "sets where you know all possible values at compile time".
package net.abstractplain.tiletown;

import java.util.HashMap;

public class Feature {
	public static final int CITY = 1;

	public static final int CLOISTER = 2;

	public static final int FARM = 3;

	public static final int TERMINUS = 4;

	public static final int ROAD = 6;

	public static HashMap<Integer, String> map = new HashMap<Integer, String>();

	public static String intToName(int featureInt) {
		map.put(1, "City");
		map.put(2, "Monastery");
		map.put(3, "Grass");
		map.put(4, "Terminus");
		map.put(6, "Road");
		return map.get(featureInt);
	}

}
