package net.abstractplain.tiletown;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class RoadTest extends TileTownTestCaseBase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	Pl[] getRoadLoopPlacements() {
		return new Pl[] { new Pl(Tns.WSCURVE1, 4, 4, 0), new Pl(Tns.WSCURVE1, 3, 4, 3), new Pl(Tns.WSCURVE1, 3, 5, 2),
				new Pl(Tns.WSCURVE2, 4, 5, 1) };
	}

	Pl[] getRoadSimpleOneOpenEnd() {
		return new Pl[] { new Pl(Tns.VERT1, 4, 4, 1), new Pl(Tns.VERT1, 5, 4, 1), new Pl(Tns.TEE1, 6, 4) };
	}

	Pl[] getRoadReturningToTeePlacements() {
		return new Pl[] { new Pl(Tns.TEE1, 4, 4), new Pl(Tns.WSCURVE1, 5, 4), new Pl(Tns.WSCURVE1, 5, 5, 1), new Pl(Tns.WSCURVE1, 4, 5, 2) };
	}

	public void testGetEnds() {

		{
			h().playTestTiles(new Pl[] { new Pl(Tns.VERT1, 4, 4, 1) });
			Road road = h().getOnlyRoadAtGridPosOrFail(new Point(4, 4));
			assertEquals(1, road.countDistinctTiles());
			Set<RoadEnd> ends = road.getEnds();
			assertEquals(2, ends.size());
			List<EdgeOrCentre> expectedEdges = new ArrayList<EdgeOrCentre>();
			expectedEdges.add(EdgeOrCentre.WEST);
			expectedEdges.add(EdgeOrCentre.EAST);
			System.out.println(ends);
			for (RoadEnd roadEnd : ends) {
				boolean found = expectedEdges.remove(roadEnd.getEndingEdgeOrCentre());
				assertTrue("edge/centre should be one of " + expectedEdges + "but was " + roadEnd.getEndingEdgeOrCentre(), found);
			}
			assertEquals(2, road.getOpenEnds().size());
		}
		{
			h().playTestTiles(new Pl[] { new Pl(Tns.TEE1, 4, 4) });
			Tile t = h().board().tileAtGP(new Point(4, 4));
			List<Road> roads = board().findDiscreteRoadsOn(t);
			assertEquals(3, roads.size());
			for (Road road : roads) {
				assertEquals("Wrong number of ends: " + road.getEnds(), 2, road.getEnds().size());
				assertEquals(1, road.getOpenEnds().size());
			}
		}
	}

	public void testGetEndsWhenRoadIsLoop() {
		{
			h().playTestTiles(getRoadLoopPlacements());
			Road r = h().getOnlyRoadAtGridPosOrFail(new Point(4, 4));
			assertEquals(0, r.getEnds().size());
		}

	}

	public void testGetEndsWhenRoadReturnsToTee() {
		{
			h().playTestTiles(getRoadReturningToTeePlacements());
			Road r = h().getOnlyRoadAtGridPosOrFail(new Point(5, 4));
			Set<RoadEnd> ends = r.getEnds();
			assertEquals(2, ends.size());
			Tile teeTile = board().tileAtGP(new Point(4, 4));

			for (RoadEnd end : ends) {
				assertEquals(teeTile, end.getTile());
				assertEquals(EdgeOrCentre.CENTRE, end.getEndingEdgeOrCentre());
			}
		}
	}

	public void testIsComplete() {
		Tile tileWest = h().playTestTile(Tns.CROSS, 5, 5);
		Tile tileEast = h().playTestTile(Tns.CROSS, 6, 5);
		tileWest.getRoadSectionOnEdge(Edge.EAST);
		Tile[] all = new Tile[] { tileWest, tileEast };
		for (Tile tile : all) {
			List<Road> roads = board().findDiscreteRoadsOn(tile);
			assertEquals(4, roads.size());
			for (Road r : roads) {
				if (r.countDistinctTiles() == 2) {
					assertTrue(r.isComplete(board()));
				} else {
					assertFalse(r.isComplete(board()));
				}
			}
		}
	}

	public void testIsCyclic() {
		assertIsCyclic("gend_cycle", new Pl[] { new Pl(24, 4, 4, 0), new Pl(24, 3, 4, 3), new Pl(24, 3, 5, 2), new Pl(25, 4, 5, 1) });
	}

	/**
	 * Assert that on playing the given tiles, the first tile has exactly one road, and it is cyclic.
	 * 
	 * @param description
	 *            - description of the tile placements (e.g. "simple loop" / "figure 8") - used when presenting failing tests
	 * @param placements
	 *            - the first tile must have exactly one road, and this must be the road to assess
	 */
	private void assertIsCyclic(String description, Pl[] placements) {
		h().reset();
		h().playTestTiles(placements);
		Tile t1 = h().board().getTileByPlayNumber(1);
		List<Road> roads = board().findDiscreteRoadsOn(t1);
		assertEquals(1, roads.size());
		Road r1 = roads.get(0);
		assertNotNull(r1);
		assertTrue("should be cyclic", r1.isCyclic(board()));
	}

	public void testIsCompleteWhenNoRoadsComplete() {
		assertConfigurationContainsNoCompleteRoads("-+", new Pl[] { new Pl(Tns.VERT1, 4, 5, 1), new Pl(Tns.CROSS, 5, 5) });
		assertConfigurationContainsNoCompleteRoads("--", new Pl[] { new Pl(Tns.VERT1, 4, 5, 1), new Pl(Tns.VERT1, 5, 5, 1) }); // --
		assertConfigurationContainsNoCompleteRoads("|n|", new Pl[] { new Pl(Tns.VERT1, 5, 4), new Pl(Tns.VERT1, 5, 5) });
		assertConfigurationContainsNoCompleteRoads("+--", new Pl[] { new Pl(Tns.CROSS, 3, 5), new Pl(Tns.VERT1, 4, 5, 1),
				new Pl(Tns.VERT1, 5, 5, 1) });
		assertConfigurationContainsNoCompleteRoads("--+", new Pl[] { new Pl(Tns.VERT1, 3, 5, 1), new Pl(Tns.VERT1, 4, 5, 1),
				new Pl(Tns.CROSS, 5, 5) });
		assertConfigurationContainsNoCompleteRoads("|n+", new Pl[] { new Pl(Tns.VERT1, 5, 4), new Pl(Tns.CROSS, 5, 5) });
		assertConfigurationContainsNoCompleteRoads("-T", new Pl[] { new Pl(Tns.VERT1, 3, 4, 1), new Pl(Tns.TEE1, 4, 4) });
		assertConfigurationContainsNoCompleteRoads("-Tn^", new Pl[] { new Pl(Tns.TEE1, 3, 5), new Pl(Tns.VERT1, 3, 4, 1),
				new Pl(Tns.TEE1, 4, 4) });
		assertConfigurationContainsNoCompleteRoads("gend", new Pl[] { new Pl(27, 5, 3, 0), new Pl(22, 4, 3, 1) });
		assertConfigurationContainsNoCompleteRoads("gend", new Pl[] { new Pl(27, 4, 4, 0), new Pl(22, 4, 3, 1), new Pl(27, 5, 3, 0) });
		// this one crashes under gui construction only, not sure why yet
		assertConfigurationContainsNoCompleteRoads("gend", new Pl[] { new Pl(24, 4, 4, 0), new Pl(24, 4, 3, 1), new Pl(24, 3, 3, 3),
				new Pl(25, 3, 4, 2) });

	}

	public void assertConfigurationContainsNoCompleteRoads(String description, Pl[] placements) {
		h().reset();
		List<Tile> tiles = h().playTestTiles(placements);
		assertConfigurationContainsNoCompleteRoads(description, tiles);
	}

	public void assertConfigurationContainsNoCompleteRoads(String description, List<Tile> startingTiles) {
		try {
			for (Tile tile : startingTiles) {
				List<Road> roads = board().findDiscreteRoadsOn(tile);
				for (Road r : roads) {
					assertFalse("road expected incomplete " + description + " road: " + r + "on board: " + board().toFullDetailString(), r
							.isComplete(board()));
				}
			}
		} catch (RuntimeException e) {
			System.err.println("when working with board: " + board().toFullDetailString());
			throw e;
		}
	}

	public void testCreateTestTile() {
		Tile t = h().createTestTile(1);
		assertNotNull(t);
	}

	public void testCountDistinctTiles() {
		{
			h().playTestTiles(getRoadLoopPlacements());
			Road r = h().getOnlyRoadAtGridPosOrFail(new Point(4, 4));
			assertEquals(4, r.countDistinctTiles());
		}
		{
			h().playTestTiles(new Pl[] { new Pl(Tns.VERT1, 4, 4, 1) });
			Road r = h().getOnlyRoadAtGridPosOrFail(new Point(4, 4));
			assertEquals(1, r.countDistinctTiles());
			h().playTestTile(Tns.VERT1, 5, 4, 1);
			r = h().getOnlyRoadAtGridPosOrFail(new Point(4, 4));
			assertEquals(2, r.countDistinctTiles());
		}
		{
			h().playTestTiles(getRoadReturningToTeePlacements());
			Road r = h().getOnlyRoadAtGridPosOrFail(new Point(5, 4));
			assertEquals(4, r.countDistinctTiles());
		}
	}

	public void testHasAtLeastOneMeeple() {
		h().playTestTiles(getRoadLoopPlacements());
		Road r = h().getOnlyRoadAtGridPosOrFail(new Point(4, 4));
		assertFalse(r.hasAtLeastOneMeeple());
		Player player1 = new Player("fred", Color.red);
		List<RoadSection> roadSections = r.roadSections();
		assertEquals(4, roadSections.size());
		for (RoadSection rs : roadSections) {
			rs.addMeeple(new MeepleNormal(player1));
			assertTrue(r.hasAtLeastOneMeeple());
		}
	}

	public void testGetMajorityOwners() {
		Road road = new Road();
		List<Point> detailPoints = new LinkedList<Point>();
		Set<Edge> connectedEdges = new HashSet<Edge>();

		Tile tile = h().playTestTile(Tns.VERT1, 3, 3);

		RoadSection rs1 = new RoadSection(tile, detailPoints, connectedEdges);
		road.add(rs1);
		PlayerI p1 = new Player("joe", Color.green);
		PlayerI p2 = new Player("mike", Color.red);
		Meeple ma1 = new MeepleNormal(p1);
		Meeple ma2 = new MeepleNormal(p1);
		Meeple mb1 = new MeepleNormal(p2);
		rs1.addMeeple(ma1);
		rs1.addMeeple(ma2);
		rs1.addMeeple(mb1);
		{
			List<PlayerI> result = road.getMajorityOwners();
			assertEquals(1, result.size());
			assertTrue(result.contains(p1));
		}

		{
			Meeple mb2 = new MeepleNormal(p2);
			rs1.addMeeple(mb2);
			List<PlayerI> result = road.getMajorityOwners();
			assertEquals(2, result.size());
			assertTrue(result.contains(p1));
			assertTrue(result.contains(p2));
		}

	}

	public void testAddOpenEnd() {
		// simple horiz road
		h().playTestTile(Tns.VERT1, 4, 4, 1);
		Tile t = board().tileAtGP(4, 4);
		Road r = new Road();
		List<RoadSection> rses = t.discreteRoadSections();

		r.addOpenEnd(rses.get(0), Edge.WEST);
		Set<RoadEnd> ends = r.getEnds();
		assertEquals(1, ends.size());
		RoadEnd westEnd = (RoadEnd) ends.toArray()[0];
		assertTrue(westEnd.isOpen());
		assertEquals(EdgeOrCentre.WEST, westEnd.getEndingEdgeOrCentre());

		r.addOpenEnd(rses.get(0), Edge.EAST);

		assertEquals(2, r.getEnds().size());
		Set<RoadEnd> allEnds = r.getEnds();
		assertTrue(allEnds.remove(westEnd));
		RoadEnd eastEnd = (RoadEnd) allEnds.toArray()[0];
		assertTrue(eastEnd.isOpen());
		assertEquals(EdgeOrCentre.EAST, eastEnd.getEndingEdgeOrCentre());
	}
}
