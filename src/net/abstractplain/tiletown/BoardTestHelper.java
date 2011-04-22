package net.abstractplain.tiletown;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

public class BoardTestHelper {
	private Map<String, TileData> _allTileData;

	private Board _board = null;

	public BoardTestHelper() {
		_allTileData = TileData.loadTileData("basic/basic.dat");
		reset();
	}

	Tile createTestTile(int tileNumber) {
		TileData td = TileData.fetchOneTileDataForTest(tileNumber, _allTileData);

		Tile t = new Tile(td.imageName(), td);
		return t;
	}

	Tile playTestTile(int tileNumber, int x, int y) {
		return playTestTile(tileNumber, x, y, 0);
	}

	Tile playTestTile(int tileNumber, int x, int y, int rotation) {
		Tile t = createTestTile(tileNumber);
		for (int i = 0; i < rotation; i++) {
			t.rotateCW();
		}
		_board.placeTile(t, new Point(x, y));
		return t;
	}

	/**
	 * Play tiles onto the board following the given list of placements. Note that this will reset the board, as it is assumed it is being
	 * used for quick initial population.
	 * 
	 * @param placements
	 * @return the placed tiles
	 */
	List<Tile> playTestTiles(Pl[] placements) {
		reset();
		List<Tile> all = new ArrayList<Tile>();
		for (Pl pl : placements) {
			all.add(playTestTile(pl.tileNo(), pl.x(), pl.y(), pl.rotation()));
		}
		return all;
	}

	public Board board() {
		return _board;
	}

	public void reset() {
		_board = new Board();
	}

	/**
	 * return the only road at the tile at given grid pos, asserting exactly one road exists on that tile (but maybe 2 road sections (say,
	 * in a figure8)).
	 * 
	 * @param gridPos
	 *            - grid pos of tile from which to return road
	 * @return Road - the one road that exists on the tile at given pos.
	 */
	public Road getOnlyRoadAtGridPosOrFail(Point gridPos) {
		Tile t = board().tileAtGP(gridPos);
		List<Road> roads = board().findDiscreteRoadsOn(t);
		Assert.assertEquals(1, roads.size());
		return roads.get(0);
	}

	public CitySection getSoleCitySectionFromSet(Set<CitySection> sections) {
		Assert.assertEquals(1, sections.size());
		CitySection soleSection = sections.iterator().next();
		return soleSection;
	}

	public CitySection getSoleCitySection(Tile t) {
		return getSoleCitySectionFromSet(t.discreteCitySections());
	}

}
