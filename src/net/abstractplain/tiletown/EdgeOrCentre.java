package net.abstractplain.tiletown;

/**
 * while it's tempting to allow the occasionally used CENTRE into the more frequently used enum Edge, this would weaken it in the majority
 * of its cases. For now, we'll use this special enum when we are dealing with this larger set of possibilities, and leave the Edge enum
 * strict.
 * 
 */
public enum EdgeOrCentre {
	NORTH, EAST, SOUTH, WEST, CENTRE;

	public static EdgeOrCentre fromEdge(Edge e) {
		switch (e) {
		case NORTH:
			return EdgeOrCentre.NORTH;
		case EAST:
			return EdgeOrCentre.EAST;
		case SOUTH:
			return EdgeOrCentre.SOUTH;
		case WEST:
			return EdgeOrCentre.WEST;
		default:
			throw new IllegalArgumentException("illegal edge: " + e);
		}
	}

	public static EdgeOrCentre parseCompassCharOrCentre(String compassCharStr) {
		if (compassCharStr.toUpperCase().charAt(0) == 'C') {
			return CENTRE;
		} else {
			return fromEdge(Edge.parseCompassChar(compassCharStr));
		}
	}
}
