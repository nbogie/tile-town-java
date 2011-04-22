package net.abstractplain.tiletown;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TerrainDetails {

	public static final int RESOLUTION = 10;// the resolution of the terrain

	// details matrix. tough to change,
	// for now.

	private Matrix _detailMatrix;

	// use this to create a different one for each occurrence, rather than
	// hitting aliasing problems
	public TerrainDetails(TerrainDetails givenNewTileData) {
		this(givenNewTileData._detailMatrix);
	}

	public TerrainDetails(Matrix detailMatrix) {
		_detailMatrix = detailMatrix;
	}

	@Override
	public String toString() {
		return "" + _detailMatrix;
	}

	public String[] toStrings() {
		return _detailMatrix.toStrings();
	}

	public Matrix getDetail() {
		return _detailMatrix;
	}

	public void rotateCW() {
		_detailMatrix = _detailMatrix.rotateCW90();
	}

	protected void rotateCCW() {
		_detailMatrix = _detailMatrix.rotateCW90().rotateCW90().rotateCW90();
	}

	boolean validPosition(Point p) {
		return !(p.x >= getWidth() || p.y >= getWidth() || p.x < 0 || p.y < 0);
	}

	// doesn't expect position ever to be in corner and therefore in two edges
	// (return val undefined if it is)
	// return null if the position isn't part of any edge
	Edge fromTixelPosition(Point position) {
		if (position.x == 0) {
			return Edge.WEST;
		}
		if (position.x == getWidth() - 1) {
			return Edge.EAST;
		}
		if (position.y == 0) {
			return Edge.NORTH;
		}
		if (position.y == getHeight() - 1) {
			return Edge.SOUTH;
		}
		return null;
	}

	int getWidth() {
		return _detailMatrix.getWidth();
	}

	int getHeight() {
		return _detailMatrix.getHeight();
	}

	public static TerrainDetails createTestInstance() {
		List<String> data = new ArrayList<String>();
		data.add("0123456789");
		data.add("1123456789");
		data.add("2123456789");
		data.add("3123456789");
		data.add("4123456789");
		data.add("5123456789");
		data.add("6123456789");
		data.add("7123456789");
		data.add("8123456789");
		data.add("9123456789");
		Matrix m = Matrix.createFromStringLines(data);
		return new TerrainDetails(m);
	}

	/**
	 * find the position of the first encounter of the given feature on the given edge, but ignoring the 2x2 set of corner tixels where a
	 * city edge may be painted (for aesthetics) on a side it doesn't truly inhabit.
	 * 
	 * @param edge
	 *            - edge to look on
	 * @param soughtFeature
	 *            - feature whose first occurrence on edge should be reported
	 * @return position of first occurrence of soughtFeature on edge
	 */
	public Point findFirstFeaturePositionOnEdge(Edge edge, int soughtFeature) {
		int x = -1;
		int y = -1;
		switch (edge) {
		case NORTH:
		case SOUTH:
			y = (edge == Edge.NORTH) ? 0 : (getHeight() - 1);
			for (x = 0; x < getWidth(); x++) {
				Point pos = new Point(x, y);
				if (isIn2x2CornerPoint(pos)) {
					continue;
				}
				int feature = featureAtPosition(pos);
				if (feature == soughtFeature) {
					return pos;
				}
			}
			break;
		case EAST:
		case WEST:
			x = (edge == Edge.WEST) ? 0 : (getWidth() - 1);
			for (y = 0; y < getHeight(); y++) {
				Point pos = new Point(x, y);
				if (isIn2x2CornerPoint(pos)) {
					continue;
				}
				int feature = featureAtPosition(pos);
				if (feature == soughtFeature) {
					return pos;
				}
			}
			break;
		default:
			break;
		}
		return null;
	}

	/**
	 * Return feature type present at the given grid-position on this tile
	 * 
	 * @param pos
	 *            Point specifying the in-tile detail-grid coords at which the feature is sought
	 */
	public int featureAtPosition(Point pos) {
		int x = pos.x;
		int y = pos.y;
		if (x >= RESOLUTION || y >= RESOLUTION || x < 0 || y < 0) {
			throw new RuntimeException("invalid position requested: " + x + ", " + y);
		}
		return (Integer.parseInt((String) getDetail().get(x, y)));
	}

	public List<Point> findDetailPoints(int soughtFeature) {
		String soughtFeatureStr = soughtFeature + "";
		return _detailMatrix.findAllMatching(soughtFeatureStr);
	}

	private static final Set<Point> cornerPoints2x2 = new HashSet<Point>(Arrays.asList(new Point[] { new Point(0, 0), new Point(1, 0),
			new Point(0, 1), new Point(1, 1), new Point(8, 0), new Point(9, 0), new Point(8, 1), new Point(9, 1), new Point(0, 8),
			new Point(1, 8), new Point(0, 9), new Point(1, 9), new Point(8, 8), new Point(9, 8), new Point(8, 9), new Point(9, 9) }));

	public boolean isIn2x2CornerPoint(Point p) {
		return cornerPoints2x2.contains(p);
	}

	/**
	 * Discover the extents (within this tile) of the given feature existing at the given starting position, by floodfill. Fill in the given
	 * connected points and connected edges by tracing the feature tixels starting from the given terrain detail point.
	 * 
	 * @param position
	 *            - position in terrain detail from which to start tracing the feature
	 * @param connectedPointsList
	 *            - will be filled with coords of the relevant feature section.
	 * @param connectedEdgesList
	 *            - will be filled with the edges which the feature touches (note if the feature is a road and it terminates in this tile
	 *            this will be of size 1)
	 * @param soughtFeature
	 *            - feature to floodfill
	 * @param directionsToLook
	 *            - directions to look (e.g. all 8 compass points (for roads), or only NESW (for cities))
	 */
	public void featureFloodFillFromPoint(Point position, List<Point> connectedPointsList, Set<Edge> connectedEdgesList, int soughtFeature,
			Direction8[] directionsToLook) {
		if (connectedPointsList.contains(position)) {
			// already evaluated
			return;
		}
		if (isIn2x2CornerPoint(position)) {
			// don't flood fill to the corners for cities
			return;
		}

		int feature = featureAtPosition(position);
		if (feature != soughtFeature) {
			return;
		}
		connectedPointsList.add(position);
		Edge edge = fromTixelPosition(position);// if any
		if (edge != null) {
			if (!connectedEdgesList.contains(edge)) {
				connectedEdgesList.add(edge);
			}
		}
		for (Direction8 dir : directionsToLook) {
			Point posToTry = dir.computeAdjacentPoint(position);
			if (validPosition(posToTry)) {
				featureFloodFillFromPoint(posToTry, connectedPointsList, connectedEdgesList, soughtFeature, directionsToLook);
			}
		}
	}

	void farmFloodFillFromPoint(Point startPoint, Set<FarmTP> connectedFarmTPs, List<Point> tixelPositions,
			List<Point> touchingCityTixelPoints) {
		farmFloodFillFromPoint(startPoint, tixelPositions, connectedFarmTPs, touchingCityTixelPoints, Feature.FARM, Direction8.neswOnly());
	}

	// TODO: consolidate with other flood fills.
	// differences: farm ff allowed to look right into corners (or at least through x=1, y=1 which may be it's only route between edge and
	// road.
	// collects list of touching city points, too.
	// connects to FarmTPs not to tile edges. (so FarmTP and Edge need both a common interface to find a related FarmTP/Edge at given Tixel
	// position, if any.
	public void farmFloodFillFromPoint(Point position, List<Point> connectedPointsList, Set<FarmTP> connectedFarmTPs,
			List<Point> touchingCityTixelPoints, int soughtFeature, Direction8[] directionsToLook) {
		if (connectedPointsList.contains(position)) {
			// already evaluated
			return;
		}

		// if (isIn2x2CornerPoint(position)) - allowed for farms. tile creators will have to paint right to corners where they don't want
		// leak.

		int feature = featureAtPosition(position);
		if (feature != soughtFeature) {
			// record touching city points...
			if (feature == Feature.CITY) {
				// ...but ignore city points those in the corners as they are ambiguous
				if (!isIn2x2CornerPoint(position)) {
					touchingCityTixelPoints.add(position);
				}
			}
			// in any case, don't flood fill further when we're not on Farm tixel
			return;
		}
		connectedPointsList.add(position);
		FarmTP farmTP = getRelatedFarmTP(position);// if any
		if (farmTP != null) {
			if (!connectedFarmTPs.contains(farmTP)) {
				connectedFarmTPs.add(farmTP);
			}
		}
		for (Direction8 dir : directionsToLook) {
			Point posToTry = dir.computeAdjacentPoint(position);
			if (validPosition(posToTry)) {
				farmFloodFillFromPoint(posToTry, connectedPointsList, connectedFarmTPs, touchingCityTixelPoints, soughtFeature,
						directionsToLook);
			}
		}
	}

	private FarmTP getRelatedFarmTP(Point tixelPosition) {
		return FarmTP.fromTixelPosition(tixelPosition);
	}

	void cityFloodFillFromPoint(Point startPoint, Set<Edge> connectedEdges, List<Point> tixelPositions) {
		featureFloodFillFromPoint(startPoint, tixelPositions, connectedEdges, Feature.CITY, Direction8.neswOnly());
	}

	void roadFloodFillFromPoint(Point startPoint, Set<Edge> connectedEdgesList, List<Point> connectedPointsList) {
		featureFloodFillFromPoint(startPoint, connectedPointsList, connectedEdgesList, Feature.ROAD, Direction8.values());
	}

	public int featureAtFarmTP(FarmTP farmTP) {
		return featureAtPosition(farmTP.toTixelPosition());
	}

}
