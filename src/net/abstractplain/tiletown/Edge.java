package net.abstractplain.tiletown;

import java.awt.Point;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

//Enums guide: http://java.sun.com/j2se/1.5.0/docs/guide/language/enums.html 
public enum Edge {
	NORTH, EAST, SOUTH, WEST;

	public static Edge[] nesw() {
		return new Edge[] { NORTH, EAST, SOUTH, WEST };
	}

	public static Set<Edge> neswAsSet() {
		return toSet(nesw());
	}

	public int asArrayIndexForNESW() {
		Edge[] correctOrder = nesw();
		return Arrays.asList(correctOrder).indexOf(this);
	}

	public static Edge parseCompassChar(String compassCharStr) {
		char c = compassCharStr.toUpperCase().charAt(0);
		switch (c) {
		case 'N':
			return NORTH;
		case 'E':
			return EAST;
		case 'S':
			return SOUTH;
		case 'W':
			return WEST;

		default:
			throw new RuntimeException("unrecognised compass char: '" + c + "'");
		}
	}

	public Edge applyRotation(int rotation) {
		if (rotation == 0) {
			return this;
		} else {
			return applySingleRotation(rotation >= 0).applyRotation(rotation > 0 ? rotation - 1 : rotation + 1);
		}
	}

	private Edge applySingleRotation(boolean isClockwise) {
		switch (this) {
		case NORTH:
			return isClockwise ? EAST : WEST;
		case EAST:
			return isClockwise ? SOUTH : NORTH;
		case SOUTH:
			return isClockwise ? WEST : EAST;
		case WEST:
			return isClockwise ? NORTH : SOUTH;
		default:
			throw new IllegalArgumentException("no such edge: " + this);
		}
	}

	/**
	 * these have the name-prefix "other" to make them stand out as unusual uses (we will use Edge.nesw() in many places and want to avoid
	 * accidentally using another order)
	 */
	public static Edge[] otherESWN() {
		return new Edge[] { EAST, SOUTH, WEST, NORTH };
	}

	public static Edge[] otherSWNE() {
		return new Edge[] { SOUTH, WEST, NORTH, EAST };
	}

	public static Edge[] otherWNES() {
		return new Edge[] { WEST, NORTH, EAST, SOUTH };
	}

	/**
	 * Return Edge that will neighbour tile at position B, were we at point A, or return null if points are same (assuming positions refer
	 * to adjacent tiles)
	 * 
	 * @param a
	 * @param b
	 * @return Edge
	 */
	static Edge edgeTowardBFromA(Point a, Point b) {
		if (a.x < b.x) {
			return EAST;
		} else if (a.x > b.x) {
			return WEST;
		} else if (a.y > b.y) {
			return NORTH;
		} else if (a.y < b.y) {
			return SOUTH;
		} else {
			return null;
		}
	}

	public Edge reciprocal() {
		switch (this) {
		case WEST:
			return EAST;
		case EAST:
			return WEST;
		case NORTH:
			return SOUTH;
		case SOUTH:
			return NORTH;
		default:
			throw new IllegalArgumentException("no such edge: " + this);
		}
	}

	public static Set<Edge> toSet(Edge... edges) {
		Set<Edge> set = new HashSet<Edge>();
		for (Edge edge : edges) {
			set.add(edge);
		}
		return set;
	}
}
