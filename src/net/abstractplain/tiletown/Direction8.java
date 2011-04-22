package net.abstractplain.tiletown;

import java.awt.Point;

/**
 * one of 8 compass directions.
 * 
 */
public enum Direction8 {
	NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST, NORTHWEST;

	Point computeAdjacentPoint(Point position) {
		switch (this) {
		case NORTH:
			return new Point(position.x, position.y - 1);
		case NORTHEAST:
			return new Point(position.x + 1, position.y - 1);
		case EAST:
			return new Point(position.x + 1, position.y);
		case SOUTHEAST:
			return new Point(position.x + 1, position.y + 1);
		case SOUTH:
			return new Point(position.x, position.y + 1);
		case SOUTHWEST:
			return new Point(position.x - 1, position.y + 1);
		case WEST:
			return new Point(position.x - 1, position.y);
		case NORTHWEST:
			return new Point(position.x - 1, position.y - 1);
		default:
			throw new IllegalArgumentException("invalid direction: " + this);
		}

	}

	public static Direction8 fromEdge(Edge edge) {
		switch (edge) {
		case NORTH:
			return NORTH;
		case EAST:
			return EAST;
		case SOUTH:
			return SOUTH;
		case WEST:
			return WEST;
		default:
			throw new IllegalArgumentException("Bad edge: " + edge);
		}
	}

	public static Direction8[] neswOnly() {
		return new Direction8[] { NORTH, EAST, SOUTH, WEST };
	}

	public static Direction8[] all8dirs() {
		return values();
	}

	public int relevantOrdinate(Point p) {
		int result;
		switch (this) {
		case NORTH:
		case SOUTH:
			result = p.y;
			break;
		case EAST:
		case WEST:
			result = p.x;
			break;
		default:
			throw new IllegalArgumentException("dir should be only N E S or W");
		}
		return result;
	}

}
