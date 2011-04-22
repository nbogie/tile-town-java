package net.abstractplain.tiletown;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class GameTest extends TileTownTestCaseBase {

	@Test
	public void testScoreCompletedRoadsWhenCompletedRoadIsUnmeepled() {
		h().playTestTiles(new Pl[] { new Pl(Tns.TEE1, 4, 4), new Pl(Tns.TEE1, 5, 4) });
		Tile lastTile = board().tileAtGP(new Point(5, 4));
		// TODO: move this scoring out so it isn't in a class that knows about
		// the gui, etc.
		Game game = new Game();
		game.setBoard(board());
		game.scoreCompletedRoads(lastTile);
	}

	public void testScoreCompletedCities() {
		assertScoreCompletedCity(18, new Pl[] { new Pl(16, 4, 4, 0), new Pl(7, 4, 3, 3), new Pl(4, 3, 3, 0), new Pl(9, 2, 3, 1),
				new Pl(17, 2, 2, 2), new Pl(8, 3, 2, 2), new Pl(14, 4, 2, 3) });
	}

	private void assertScoreCompletedCity(int expectedScore, Pl[] placements) {
		List<PlayerI> players = setupPlayersForTest();
		PlayerI p1 = players.get(0);

		h().playTestTiles(placements);
		Tile lastTile = board().getLastPlayedTile();
		CitySection citySection = lastTile.findCitySectionAtPosition(new Point(5, 9));
		// TODO: shouldn't be allowed to hack meeple placement - this should be
		// done through the game.
		citySection.addMeeple(new MeepleNormal(p1));
		Game game = new Game();
		game.setBoard(board());
		game.setPlayers(players);
		game.scoreCompletedCities(lastTile);
		assertEquals(expectedScore, p1.score());
		assertTrue("meeples should have been removed from completed citysection", citySection.meeples().isEmpty());
	}

	private ArrayList<PlayerI> setupPlayersForTest() {
		ArrayList<PlayerI> players = new ArrayList<PlayerI>();
		players.add(new Player("p1", Color.red));
		players.add(new Player("p2", Color.yellow));
		return players;
	}

}
