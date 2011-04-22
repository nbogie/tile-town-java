package net.abstractplain.tiletown;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

public enum FarmTP {
	NE, EN, ES, SE, SW, WS, WN, NW;

	public static Set<FarmTP> toSet(FarmTP... edges) {
		Set<FarmTP> set = new HashSet<FarmTP>();
		for (FarmTP edge : edges) {
			set.add(edge);
		}
		return set;
	}

	// TODO: don't hard-code to terrain detail resolution in FarmTP
	public Point toTixelPosition() {
		switch (this) {
		case NE:
			return new Point(7, 0);
		case EN:
			return new Point(9, 2);
		case ES:
			return new Point(9, 7);
		case SE:
			return new Point(7, 9);
		case SW:
			return new Point(2, 9);
		case WS:
			return new Point(0, 7);
		case WN:
			return new Point(0, 2);
		case NW:
			return new Point(2, 0);
		default:
			throw new IllegalStateException("bad value of " + this.getClass().getName() + ": " + this);
		}
	}

	/**
	 * return the FarmTP at the given tixel position, if any.
	 * 
	 * @param tixelPosition
	 * @return FarmTP corresponding to given Tixel Position, or null if none applies.
	 */
	public static FarmTP fromTixelPosition(Point tixelPosition) {
		for (FarmTP tp : FarmTP.values()) {
			if (tixelPosition.equals(tp.toTixelPosition())) {
				return tp;
			}
		}
		return null;
	}

	public FarmTP reciprocal() {
		switch (this) {
		case NE:
			return SE;
		case EN:
			return WN;
		case ES:
			return WS;
		case SE:
			return NE;
		case SW:
			return NW;
		case WS:
			return ES;
		case WN:
			return EN;
		case NW:
			return SW;
		default:
			throw new IllegalStateException("unrecognised farmTP: " + this);
		}
	}

	public Direction8 nextTileDirection() {
		switch (this) {
		case NW:
		case NE:
			return Direction8.NORTH;
		case EN:
		case ES:
			return Direction8.EAST;
		case WN:
		case WS:
			return Direction8.WEST;
		case SW:
		case SE:
			return Direction8.SOUTH;
		default:
			throw new IllegalArgumentException("no such farmTP: " + this);
		}
	}
}