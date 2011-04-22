package net.abstractplain.tiletown;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

public class Game {

	private static Logger log = Logger.getLogger(Game.class);

	List<Tile> _tilesUnplayed = null;

	private boolean _shouldPaintDebug;

	Map<String, String> debugMap = new HashMap<String, String>();

	List<Tile> _tilesAll = null;

	InteractionMode _mode = null;

	private int _tileSize;

	private PlayAreaPanel _panel;

	private PlayerI _currentPlayer;

	private List<PlayerI> _playersList = new ArrayList<PlayerI>();

	private Board _board = new Board();

	private List<Integer> _orderOfTileNumbersToPlay = new ArrayList<Integer>();

	private boolean _useOnlyRoadPieces;

	private boolean _finishGameAtEndOfTurn = false;

	public void setGUI(PlayAreaPanel panel) {
		_panel = panel;
	}

	public Game() {

	}

	public void setup() {
		// note: might not have a gui at this point...
		loadTiles();
		Tile exampleTile = _tilesAll.get(0);
		setTileSize(exampleTile.getSize());
	}

	private void setTileSize(int size) {
		_tileSize = size;

	}

	public void setOrderOfTilesToPlay(Integer[] tileNumbers) {
		_orderOfTileNumbersToPlay = new ArrayList<Integer>(tileNumbers.length);
		_orderOfTileNumbersToPlay.addAll(Arrays.asList(tileNumbers));
	}

	public int getTileSize() {
		return _tileSize;
	}

	private Tile getStartTileOrTopTile() {
		Tile firstStartTileFound = null;
		for (Tile t : _tilesUnplayed) {
			if (t.tileData().isStartTileType()) {
				firstStartTileFound = t;
				break;
			}
		}
		if (firstStartTileFound != null) {
			if (!_tilesUnplayed.remove(firstStartTileFound)) {
				throw new RuntimeException("couldn't find the tile in the pack we previously found it in!");
			}
			return firstStartTileFound;
		} else {
			log.warn("WARNING: no tiles found marked as Start tiles, so starting with random pick.");
			return pickTopTile();
		}

	}

	private Tile pickTopTile() {
		if (_tilesUnplayed.isEmpty()) {
			return null;
		}
		return _tilesUnplayed.remove(0);
	}

	// create all the tiles and store in a collection.
	public void loadTiles() {
		_tilesAll = new LinkedList<Tile>();
		_tilesUnplayed = new LinkedList<Tile>();
		board().setTilesPlayed(new LinkedList<Tile>());
		Map<String, TileData> allTileData = TileData.loadTileData("basic/basic.dat");
		Set<String> tileNames = allTileData.keySet();

		for (String tileName : tileNames) {
			TileData tileData = allTileData.get(tileName);
			int occurrences = tileData.getNumberOfOccurrencesInSet();
			for (int i = 0; i < occurrences; i++) {
				TileData clonedTileData = new TileData(tileData);
				Tile t = new Tile(tileName, clonedTileData);
				t.loadImage();
				_tilesAll.add(t);
			}
		}

		prepareDeck(_tilesAll);

	}

	/**
	 * sets up _tilesUnplayed (perhaps by shuffling the given available set, or perhaps by stacking the deck according to some tile order
	 * for debugging / demo playback / challenge)
	 */
	private void prepareDeck(List<Tile> givenTileSet) {
		if (_useOnlyRoadPieces) {
			_tilesUnplayed.addAll(findOnlyRoadPieces(_tilesAll));
			Collections.shuffle(_tilesUnplayed);
		} else {
			if (_orderOfTileNumbersToPlay.isEmpty()) {
				_tilesUnplayed.addAll(_tilesAll);
				Collections.shuffle(_tilesUnplayed);
			} else {
				for (Integer i : _orderOfTileNumbersToPlay) {
					_tilesUnplayed.add(removeTileTypeNumberFromSetOrFail(i, givenTileSet));
				}
			}

		}
	}

	private List<Tile> findOnlyRoadPieces(List<Tile> tiles) {
		List<Tile> results = new ArrayList<Tile>();
		for (Tile tile : tiles) {
			if (tile.hasNoCityEdges()) {
				results.add(tile);
			}
		}
		return results;
	}

	private Tile removeTileTypeNumberFromSetOrFail(Integer soughtNum, List<Tile> givenTileSet) {
		for (Tile tile : givenTileSet) {
			if (soughtNum.intValue() == tile.tileType()) {
				givenTileSet.remove(tile);
				return tile;
			}
		}
		throw new IllegalArgumentException("No such tile number " + soughtNum + "in given set");
	}

	boolean canPlayTileAt(Tile candidateTile, final Point candidateGridPos) {
		if (board().hasTileAt(candidateGridPos)) {
			return false;
		}

		List<Tile> borderingTiles = board().getPlayedTilesAdjoiningToGridPos(candidateGridPos);
		if (borderingTiles.isEmpty()) {
			return false;
		}

		for (Tile neighbour : borderingTiles) {
			if (neighbour != null) {
				if (!featuresMatch(candidateTile, candidateGridPos, neighbour)) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean featuresMatch(Tile c, Point candidateGridPos, Tile n) {
		Edge sideOfCandidate = Edge.edgeTowardBFromA(candidateGridPos, n.getGridPos());
		Edge sideOfNeighbour = sideOfCandidate.reciprocal();
		int featureOnCandidateSide = c.featureOnSide(sideOfCandidate);
		int featureOnNeighbourSide = n.featureOnSide(sideOfNeighbour);
		return featureOnCandidateSide == featureOnNeighbourSide;
	}

	public void addToDebugMap(String key, String val) {
		debugMap.put(key, val);

	}

	public int tilesRemainingCount() {
		return _tilesUnplayed.size();
	}

	public Map<String, String> debugMap() {

		return debugMap;
	}

	public Tile pickNextTile() {
		return pickTopTile();
	}

	public void changeMode(InteractionMode newMode) {
		log.debug("Changing mode.  was " + _mode + " and will be " + newMode);
		if (_mode != null) {
			_mode.leave();
		}
		newMode.enter();
		_mode = newMode;
	}

	public PlayAreaPanel playAreaPanel() {
		return _panel;
	}

	public boolean shouldPaintDebug() {
		return _shouldPaintDebug;
	}

	public void quit() {
		log.info("User requested Quit");
		System.exit(0);
	}

	public void togglePaintDebug() {
		_shouldPaintDebug = !_shouldPaintDebug;
		playAreaPanel().repaint();
	}

	public InteractionMode mode() {
		return _mode;
	}

	// infinite supply currently!
	/**
	 * may return null if none left
	 */
	public Meeple getNextNormalMeepleForCurrentPlayer() {
		return new MeepleNormal(currentPlayer());
	}

	PlayerI currentPlayer() {
		return _currentPlayer;
	}

	public Grid grid() {
		return playAreaPanel().grid();
	}

	public void placeMeeple(Meeple meeple, Point point) {
		Point[] results = grid().convertToGridAndDetailGrid(point);
		Point gridPos = results[0];
		Point inTileDetailPos = results[1];
		Tile tile = board().tileAtGP(gridPos);
		tile.addMeepleToFeatureAt(inTileDetailPos, meeple);
		meeple.setPlacedPosition(point);
		board().meeplesPlayed().add(meeple);
	}

	public boolean canPlaceMeepleAtPosition(Meeple m, Point pixelPos) {
		playAreaPanel().setHighlight(null);
		Point[] results = grid().convertToGridAndDetailGrid(pixelPos);
		Point gridPos = results[0];
		Point inTileDetailPos = results[1];
		Tile tile = board().tileAtGP(gridPos);
		if (tile == null) {
			return false;
		}
		if (tile != board().getLastPlayedTile()) {
			// can only play meeple on last played tile
			return false;
		}
		if (tile.terrainDetails().isIn2x2CornerPoint(inTileDetailPos)) {
			return false;
		}
		int feature = tile.terrainDetails().featureAtPosition(inTileDetailPos);

		if (feature == Feature.ROAD) {
			Road road = board().roadAtPosition(tile, inTileDetailPos);
			assert (road != null);
			playAreaPanel().setHighlight(road);
			road.setIndicateAllowedMeeple(!road.hasAtLeastOneMeeple());
			if (!road.hasAtLeastOneMeeple()) {
				return true;
			} else {
				return false;
			}
		}
		if (feature == Feature.CITY) {
			City city = board().cityAtPosition(tile, inTileDetailPos);
			assert (city != null);
			playAreaPanel().setHighlight(city);
			city.setIndicateAllowedMeeple(!city.hasAtLeastOneMeeple());
			if (!city.hasAtLeastOneMeeple()) {
				return true;
			} else {
				return false;
			}
		}

		if (feature == Feature.FARM) {
			Farm farm = board().farmAtPosition(tile, inTileDetailPos);
			assert (farm != null);
			playAreaPanel().setHighlight(farm);
			farm.setIndicateAllowedMeeple(!farm.hasAtLeastOneMeeple());
			if (!farm.hasAtLeastOneMeeple()) {
				return true;
			} else {
				return false;
			}
		}
		if (feature == Feature.CLOISTER) {
			Cloister cloister = board().cloisterAtPosition(tile, inTileDetailPos);
			playAreaPanel().setHighlight(cloister);
			cloister.setIndicateAllowedMeeple(true);
			return true;
		}
		return false;
	}

	void scoreCompletedRoads(Tile tile) {

		List<Road> roads = _board.findDiscreteRoadsOn(tile);
		for (Road road : roads) {

			if (road.isComplete(board())) {
				List<PlayerI> majorityOwners = road.getMajorityOwners();
				assert (majorityOwners != null);
				int tileCount = road.countDistinctTiles();
				log.info("SCORING road: " + road + " which has " + tileCount + " tiles");
				int score = tileCount * 1;
				road.setScore(score);
				if (road.isComplete(board())) {
					for (PlayerI playerI : majorityOwners) {
						log.info("awarded " + score + " pts to " + playerI + " for completed road " + road);
						board().addDebuggingScoreMarker(playerI, tile, score);
						playerI.incrementScore(score);
					}
				}

				road.returnAllMeeples();
			}
		}
	}

	void scoreCompletedCities(Tile tile) {
		Set<City> cities = board().findDiscreteCitiesOn(tile);
		for (City city : cities) {
			if (city.isComplete(board())) {
				if (city.hasAtLeastOneMeeple()) {
					List<PlayerI> majorityOwners = city.getMajorityOwners();
					// TODO: correct the scoring for pennants (and for
					// cathedrals
					// for fun? not in basic tile set)
					int tileCount = city.countDistinctTiles();
					int pennantCount = city.countPennantsOnDistinctTiles();
					int score = tileCount * 2 + pennantCount * 2;
					for (PlayerI playerI : majorityOwners) {
						log.debug("awarded completed city score " + score + " to " + playerI + " for city " + city);
						playerI.incrementScore(score);
						board().addDebuggingScoreMarker(playerI, tile, score);
					}
				} else {
					log.debug("city went to waste - complete with no meeples. " + city);
				}
				city.returnAllMeeples();
			} else {
				log.debug("incomplete city: " + city);
			}

		}
	}

	public void scoreLastMove() {
		Tile tile = board().getLastPlayedTile();
		scoreCompletedRoads(tile);
		scoreCompletedCities(tile);
		scoreCompletedCloisters(tile);
	}

	public List<PlayerI> players() {
		return _playersList;
	}

	public void setPlayersAndStartTurns(List<PlayerI> playersList) {
		setPlayers(playersList);
		startTurns();
	}

	void setPlayers(List<PlayerI> playersList) {
		_playersList = playersList;
	}

	void startTurns() {
		Tile startTile = getStartTileOrTopTile();
		// TODO: ensure all normal placement and feature computation gets done
		// on this tile. (except(?) scoring)
		board().placeTile(startTile, new Point(4, 4));

		changeToNextPlayer();
		changeMode(new ModeInspect(this));
	}

	public void start() {
		changeMode(new ModeGetPlayers(this));
	}

	public PlayerI changeToNextPlayer() {
		if (_playersList.size() < 1) {
			throw new IllegalStateException("Need at least 1 player but have " + _playersList.size());
		}
		if (_currentPlayer == null) {
			_currentPlayer = _playersList.get(0);
		} else {
			_currentPlayer = _playersList.get((1 + _playersList.indexOf(_currentPlayer)) % _playersList.size());
		}
		return _currentPlayer;
	}

	public void finishTurn() {
		if (tilesRemainingCount() == 0 || _finishGameAtEndOfTurn) {
			changeMode(new ModeInspect(this));
			log.info("all tiles played (or early finish requested)!");
			scoreEndGameFeatures();
		} else {
			changeToNextPlayer();
			changeMode(new ModePlaceTile(this));
		}
	}

	private void scoreEndGameFeatures() {
		scoreIncompleteCloisters();
		scoreIncompleteRoads();
		scoreIncompleteCities();
		scoreFarms();
		playAreaPanel().repaint();
	}

	private void scoreFarms() {
		Set<Farm> farms = board().getAllDistinctFarms();
		log.debug("distinct farms on board: " + farms.size());
		for (Farm farm : farms) {
			if (!farm.hasAtLeastOneMeeple()) {
				log.debug("no meeple on farm, ignoring. " + farm);
				continue;
			}
			log.debug("farm is meepled: " + farm + " so will score it");
			Set<City> completedTouchingCities = new HashSet<City>();
			Set<City> touchingCities = board().getDistinctCitiesTouchingFarm(farm);

			for (City city : touchingCities) {
				if (city.isComplete(board())) {
					completedTouchingCities.add(city);
				}
			}
			int scoreForFarm = completedTouchingCities.size() * 3;
			log.debug("farm: " + farm + " has " + touchingCities.size() + " touching cities, of which " + completedTouchingCities.size()
					+ " are complete.  would score: " + scoreForFarm);
			List<PlayerI> majorityOwners = farm.getMajorityOwners();
			for (PlayerI player : majorityOwners) {
				log.debug("awarding farm score " + scoreForFarm + " to " + player);
			}
		}
	}

	// TODO: share with scoreIncompleteRoads
	private void scoreIncompleteCities() {
		Set<City> incompleteCities = board().getAllIncompleteCities();
		for (City city : incompleteCities) {
			int tileCount = city.countDistinctTiles();
			int pennantCount = city.countPennantsOnDistinctTiles();
			int scoreIfOwned = tileCount * 1 + pennantCount * 1;
			if (city.hasAtLeastOneMeeple()) {
				List<PlayerI> majorityOwners = city.getMajorityOwners();
				for (PlayerI owner : majorityOwners) {
					owner.incrementScore(scoreIfOwned);
					log.info("added " + scoreIfOwned + " to " + owner + "for " + city);
					board().addDebuggingScoreMarker(owner, city.distinctTiles().get(0), scoreIfOwned);
				}
			}
		}

	}

	private void scoreIncompleteRoads() {
		Set<Road> incompleteRoads = board().getAllIncompleteRoads();
		for (Road road : incompleteRoads) {
			int tileCount = road.countDistinctTiles();
			int scoreIfOwned = tileCount * 1;
			if (road.hasAtLeastOneMeeple()) {
				List<PlayerI> majorityOwners = road.getMajorityOwners();
				for (PlayerI owner : majorityOwners) {
					owner.incrementScore(scoreIfOwned);
					log.info("added " + scoreIfOwned + " to " + owner + "for road " + road);
					board().addDebuggingScoreMarker(owner, road.distinctTiles().get(0), scoreIfOwned);
				}
			}
		}
	}

	private void scoreCompletedCloisters(Tile tile) {
		List<Tile> allCloistersNearby = board().getPlayedCloisterTilesSurroundingOrOn(tile);

		List<Tile> completedCloistersNearby = new ArrayList<Tile>();

		for (Tile cloisterTile : allCloistersNearby) {
			int surroundCount = board().getSurroundingTileCount(cloisterTile.getGridPos());
			if (surroundCount == 8) {
				completedCloistersNearby.add(cloisterTile);
			}
		}
		scoreCloistersInAnyState(completedCloistersNearby);
	}

	private void scoreCloistersInAnyState(List<Tile> cloisters) {
		for (Tile cloisterTile : cloisters) {
			int surroundCount = board().getSurroundingTileCount(cloisterTile.getGridPos());
			int score = 1 + surroundCount * 1;
			Meeple monk = cloisterTile.getMeepleOnCloister();
			if (monk != null) {
				PlayerI owner = monk.owner();
				board().addDebuggingScoreMarker(owner, cloisterTile, score);
				owner.incrementScore(score);
				if (surroundCount == 8) {
					cloisterTile.removeMeepleFromCloister();
					returnMeepleToOwner(monk);
				}
				log.info("award cloister score " + score + " for the tile(s) surrounding cloister " + cloisterTile.getName()
						+ " to player " + owner.name());
				playAreaPanel().repaint();
			}
		}

	}

	private void returnMeepleToOwner(Meeple monk) {
		monk.returnToBase();
	}

	private void scoreIncompleteCloisters() {
		// TODO: strictly should remove those completely surrounded
		List<Tile> cloisters = board().getPlayedCloisterTiles();
		scoreCloistersInAnyState(cloisters);
	}

	public List<DebuggingScoreMarker> debuggingScoreMarkers() {

		return board().getDebuggingScoreMarkers();
	}

	void setBoard(Board board) {
		_board = board;
	}

	Board board() {
		return _board;
	}

	public void dumpBoardAsTestCase() {
		String dump = board().toTestCaseTilePlacementString();
		log.info(dump);
		System.out.println(dump);
	}

	public void setUseOnlyRoadPieces() {
		_useOnlyRoadPieces = true;
	}

	public void finishEarly() {
		log.info("user requested early finish to the game - cheating!");
		_finishGameAtEndOfTurn = true;
	}

}
