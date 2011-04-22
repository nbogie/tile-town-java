package net.abstractplain.tiletown;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.Test;

public class BoardTest extends TileTownTestCaseBase {
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(BoardTest.class);

	@Test
	public void testPlaceTile() {
		Board board = new Board();
		Tile tile = Tile.createTestInstance();
		Point gridPos = new Point(0, 0);
		board.placeTile(tile, gridPos);
		assertTrue(board.hasTileAt(new Point(0, 0)));
		assertEquals(tile, board.tileAtGP(new Point(0, 0)));
	}

	public void testGetAllDistinctRoads() {
		Pl[] placements = new Pl[] { new Pl(26, 4, 4, 0), new Pl(1, 4, 3, 0), new Pl(23, 3, 4, 1), new Pl(24, 2, 4, 2),
				new Pl(25, 3, 3, 1), new Pl(25, 3, 2, 0), new Pl(23, 5, 3, 0), new Pl(27, 2, 3, 0), new Pl(28, 2, 2, 2),
				new Pl(22, 5, 4, 0), new Pl(2, 4, 5, 2), new Pl(29, 1, 2, 0), new Pl(1, 4, 2, 0), new Pl(25, 0, 2, 3), new Pl(29, 2, 1, 0),
				new Pl(23, 3, 1, 1), new Pl(22, 4, 1, 1), new Pl(24, 4, 0, 2), new Pl(22, 0, 1, 1) };
		h().playTestTiles(placements);

		Set<Road> allDistinctRoads = h().board().getAllDistinctRoads();
		assertEquals(14, allDistinctRoads.size());

		Set<Road> incompleteRoads = new HashSet<Road>();
		for (Road road : allDistinctRoads) {
			if (!road.isComplete(board())) {
				incompleteRoads.add(road);
			}
		}
		assertEquals(10, incompleteRoads.size());
	}

	public void testGetDistinctCitiesTouchingFarm() {
		Pl[] placements = new Pl[] { new Pl(21, 4, 4, 0), new Pl(8, 4, 3, 2), new Pl(9, 3, 3, 0), new Pl(23, 3, 4, 1), new Pl(2, 2, 4, 3),
				new Pl(9, 2, 3, 1), new Pl(20, 3, 2, 2), new Pl(14, 5, 3, 0), new Pl(10, 2, 2, 3), new Pl(25, 2, 1, 3),
				new Pl(18, 1, 2, 1), new Pl(4, 5, 2, 1) };
		h().playTestTiles(placements);
		assertEquals(3, board().getAllDistinctCities().size());
		{
			Farm farm = board().farmAtGPOrFail(new Point(4, 4), FarmTP.WN);
			Set<City> touchingCities = board().getDistinctCitiesTouchingFarm(farm);
			assertEquals(2, touchingCities.size());
			for (City city : touchingCities) {
				assertTrue(city.isComplete(board()));
			}
		}
		{
			Farm farm = board().farmAtGPOrFail(new Point(5, 2), FarmTP.WS);
			Set<City> touchingCities = board().getDistinctCitiesTouchingFarm(farm);
			assertEquals(1, touchingCities.size());
			assertFalse(touchingCities.iterator().next().isComplete(board()));
		}
	}

	public void testFindingExtremesOfPlayedTiles() {
		h().playTestTile(Tns.CROSS, 3, 6);
		assertBoardExtents(new int[] { 6, 3, 6, 3 });

		h().playTestTile(Tns.CROSS, 4, 6);
		assertBoardExtents(new int[] { 6, 4, 6, 3 });

		h().playTestTile(Tns.CROSS, 2, 6);
		assertBoardExtents(new int[] { 6, 4, 6, 2 });

		h().playTestTile(Tns.CROSS, 3, 7);
		assertBoardExtents(new int[] { 6, 4, 7, 2 });

		h().playTestTile(Tns.CROSS, 3, 5);
		assertBoardExtents(new int[] { 5, 4, 7, 2 });

	}

	public void testGetPlayedCloisterTiles() {
		Pl[] placements = new Pl[] { new Pl(Tns.VERT1, 3, 4, 0), new Pl(Tns.VERT1, 4, 4, 0), new Pl(Tns.CLOISTER, 5, 4, 0),
				new Pl(Tns.CLOISTER, 6, 4, 0) };
		h().playTestTiles(placements);
		assertEquals(2, h().board().getPlayedCloisterTiles().size());
		h().playTestTile(Tns.STOPVERT, 3, 3);
		assertEquals(3, h().board().getPlayedCloisterTiles().size());

	}

	private void assertBoardExtents(int[] extentsNESW) {
		Direction8[] nesw = Direction8.neswOnly();
		int i = 0;
		for (Direction8 dir : nesw) {
			assertEquals("in dir " + dir, extentsNESW[i], board().getExtentsOrdinate(dir));
			i++;
		}
	}

	@Test
	public void testGetTileOnAdjacentEdge() {
		Board board = new Board();
		Tile tileEast = Tile.createTestInstance();
		Tile tileWest = Tile.createTestInstance();
		board.placeTile(tileEast, new Point(9, 0));
		board.placeTile(tileWest, new Point(8, 0));
		assertEquals(tileWest, board.getTileOnAdjacentEdge(tileEast, Edge.WEST));
	}

}
