package net.abstractplain.tiletown;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class TerrainDetailsTest extends TileTownTestCaseBase {

	public void testFindFirstFeaturePositionOnEdge() {
		{
			List<String> data = new ArrayList<String>();
			data.add("1133333333");
			data.add("1133333333");
			data.add("1113333333");
			data.add("1111333333");
			data.add("1111333666");
			data.add("1111336333");
			data.add("1111336333");
			data.add("1113336333");
			data.add("1113336333");
			data.add("1133336333");

			Matrix matrix = Matrix.createFromStringLines(data);
			TerrainDetails td = new TerrainDetails(matrix);
			assertEquals(new Point(6, 9), td.findFirstFeaturePositionOnEdge(Edge.SOUTH, Feature.ROAD));
			assertEquals(null, td.findFirstFeaturePositionOnEdge(Edge.WEST, Feature.ROAD));
			assertEquals(null, td.findFirstFeaturePositionOnEdge(Edge.NORTH, Feature.ROAD));
			assertEquals(new Point(9, 4), td.findFirstFeaturePositionOnEdge(Edge.EAST, Feature.ROAD));

			assertEquals(null, td.findFirstFeaturePositionOnEdge(Edge.EAST, Feature.CITY));
			assertEquals(null, td.findFirstFeaturePositionOnEdge(Edge.NORTH, Feature.CITY));
			assertEquals(null, td.findFirstFeaturePositionOnEdge(Edge.SOUTH, Feature.CITY));
			assertEquals(new Point(0, 2), td.findFirstFeaturePositionOnEdge(Edge.WEST, Feature.CITY));

		}
	}

	public void testIsCornerPosition() {
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
		TerrainDetails td = new TerrainDetails(m);

		Set<Point> cornerPoints = new HashSet<Point>(Arrays.asList(new Point[] { new Point(0, 0), new Point(1, 0), new Point(0, 1),
				new Point(1, 1), new Point(8, 0), new Point(9, 0), new Point(8, 1), new Point(9, 1), new Point(0, 8), new Point(1, 8),
				new Point(0, 9), new Point(1, 9), new Point(8, 8), new Point(9, 8), new Point(8, 9), new Point(9, 9) }));

		for (int y = 0; y < TerrainDetails.RESOLUTION; y++) {
			for (int x = 0; x < TerrainDetails.RESOLUTION; x++) {
				if (cornerPoints.contains(new Point(x, y))) {
					assertTrue(x + "," + y + " should be corner pos", td.isIn2x2CornerPoint(new Point(x, y)));
				} else {
					assertFalse(x + "," + y + " should not be corner pos", td.isIn2x2CornerPoint(new Point(x, y)));
				}
			}
		}
	}

	public void testRoadFloodFill() {
		List<Point> connPtsList = new ArrayList<Point>();
		Set<Edge> connEdgesList = new HashSet<Edge>();
		Tile tile = h().createTestTile(18);
		tile.terrainDetails().featureFloodFillFromPoint(new Point(4, 9), connPtsList, connEdgesList, Feature.ROAD, Direction8.all8dirs());
		assertEquals(9, connPtsList.size());
		assertEquals(2, connEdgesList.size());
		assertTrue(connEdgesList.contains(Edge.SOUTH));
		assertTrue(connEdgesList.contains(Edge.WEST));
	}

	public void testRoadFloodFillToTerminus() {
		List<Point> connPtsList = new ArrayList<Point>();
		Set<Edge> connEdgesList = new HashSet<Edge>();
		Tile tile = h().createTestTile(6);
		tile.terrainDetails().featureFloodFillFromPoint(new Point(5, 9), connPtsList, connEdgesList, Feature.ROAD, Direction8.all8dirs());
		assertEquals(3, connPtsList.size());
		assertEquals(1, connEdgesList.size());
		assertTrue(connEdgesList.contains(Edge.SOUTH));

	}

	public void testCityFloodFillFromPoint() {
		{
			Tile t = h().playTestTile(Tns.CITY_FULL, 4, 4);
			Point startPoint = new Point(0, 4);
			Set<Edge> connectedEdges = new HashSet<Edge>();
			List<Point> tixelPositions = new LinkedList<Point>();
			t.terrainDetails().cityFloodFillFromPoint(startPoint, connectedEdges, tixelPositions);
			assertEquals(Edge.neswAsSet(), connectedEdges);
			assertEquals(100 - 4 * 4, tixelPositions.size());
		}
		{
			Tile t = h().playTestTile(Tns.CITY_WE, 4, 4);
			// start on middle of west edge
			Point startPoint = new Point(0, 4);
			Set<Edge> connectedEdges = new HashSet<Edge>();
			List<Point> tixelPositions = new LinkedList<Point>();
			t.terrainDetails().cityFloodFillFromPoint(startPoint, connectedEdges, tixelPositions);
			assertEquals(Edge.toSet(Edge.WEST, Edge.EAST), connectedEdges);
		}
	}

}
