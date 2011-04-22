package net.abstractplain.tiletown;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

public class Board {
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(Board.class);

	private List<Tile> _tilesPlayed;

	private List<Meeple> _meeplesPlayed;

	private Tile _lastPlayedTile;

	private List<DebuggingScoreMarker> _debuggingScoreMarkers;

	public Board() {
		_tilesPlayed = new LinkedList<Tile>();
		_meeplesPlayed = new LinkedList<Meeple>();
		_lastPlayedTile = null;
		_debuggingScoreMarkers = new LinkedList<DebuggingScoreMarker>();
	}

	public List<Tile> getTilesPlayed() {
		return _tilesPlayed;
	}

	public void setTilesPlayed(List<Tile> tilesPlayed) {
		_tilesPlayed = tilesPlayed;
	}

	public List<Meeple> getMeeplesPlayed() {
		return _meeplesPlayed;
	}

	public Tile getLastPlayedTile() {
		return _lastPlayedTile;
	}

	public void setLastPlayedTile(Tile lastPlayedTile) {
		_lastPlayedTile = lastPlayedTile;
	}

	public List<DebuggingScoreMarker> getDebuggingScoreMarkers() {
		return _debuggingScoreMarkers;
	}

	public void setDebuggingScoreMarkers(List<DebuggingScoreMarker> debuggingScoreMarkers) {
		_debuggingScoreMarkers = debuggingScoreMarkers;
	}

	Tile tileAtGP(int gridX, int gridY) {
		return tileAtGP(new Point(gridX, gridY));
	}

	Tile tileAtGP(Point gridPos) {
		for (Tile tile : tilesPlayed()) {
			if (gridPos.equals(tile.getGridPos())) {
				return tile;
			}
		}
		return null;
	}

	List<Tile> getPlayedTilesAdjoiningToGridPos(Point gridPos) {
		return getPlayedTilesSurroundingGridPos(gridPos, Direction8.neswOnly());
	}

	List<Tile> getPlayedTilesSurroundingGridPos(Point gridPos, Direction8[] directions) {
		List<Tile> tiles = new LinkedList<Tile>();
		for (Direction8 dir : directions) {
			Point adjacentPos = dir.computeAdjacentPoint(gridPos);
			Tile t = tileAtGP(adjacentPos);
			if (t != null) {
				tiles.add(t);
			}
		}
		return tiles;
	}

	boolean hasTileAt(Point gridPos) {
		return (null != tileAtGP(gridPos));
	}

	public void placeTile(Tile tile, Point gridPos) {
		if (tile == null) {
			throw new NullPointerException("tile should never be null!  attempted placement at " + gridPos);
		}
		tile.place(gridPos, tilesPlayed().size() + 1);
		tile.computeFeatures();
		setLastPlayedTile(tile);
		tilesPlayed().add(tile);
	}

	public List<Tile> tilesPlayed() {
		return getTilesPlayed();
	}

	public List<Meeple> meeplesPlayed() {
		return getMeeplesPlayed();
	}

	void addDebuggingScoreMarker(PlayerI player, Tile tile, int score) {
		DebuggingScoreMarker marker = new DebuggingScoreMarker(player, tile, score, new Date());
		getDebuggingScoreMarkers().add(marker);
	}

	/**
	 * @param baseTile
	 *            tile whose neighbour to look for
	 * @param edge
	 *            edge of baseTile to look against
	 * @return Tile adjacent to given tile on its given edge, or null if no tile. E.g. tile at 5, 10, West edge, yields a tile at 4,10 if
	 *         present.
	 */
	Tile getTileOnAdjacentEdge(Tile baseTile, Edge edge) {
		Direction8 dir = Direction8.fromEdge(edge);
		Point adjacentPos = dir.computeAdjacentPoint(baseTile.getGridPos());
		return tileAtGP(adjacentPos);
	}

	Tile getTileOnAdjacentFarmTP(Tile baseTile, FarmTP farmTP) {
		Direction8 dir = farmTP.nextTileDirection();
		Point adjacentPos = dir.computeAdjacentPoint(baseTile.getGridPos());
		return tileAtGP(adjacentPos);
	}

	public void setMeeplesPlayed(List<Meeple> meeplesPlayed) {
		_meeplesPlayed = meeplesPlayed;
	}

	public boolean roadIsTraceableRoundToSecondEdge(RoadSection rsFirst, Edge first) {
		boolean done = false;
		Tile firstTile = rsFirst.tile();
		Tile thisTile = firstTile;
		Edge edgeToExplore = first;
		while (!done) {
			Tile t = getTileOnAdjacentEdge(thisTile, edgeToExplore);
			if (t == null) {
				return false;
			}
			Edge backwardsEdge = edgeToExplore.reciprocal();
			RoadSection adjacentRS = t.getRoadSectionOnEdge(backwardsEdge);
			if (adjacentRS == rsFirst) {
				return true;
			} else {
				Set<Edge> edgesOfAdjacentRS = adjacentRS.connectedEdges();
				assert (edgesOfAdjacentRS.size() == 2);
				boolean wasRemovedOK = edgesOfAdjacentRS.remove(backwardsEdge);
				assert (wasRemovedOK);

				thisTile = adjacentRS.tile();
				edgeToExplore = (Edge) edgesOfAdjacentRS.toArray()[0];
			}
		}
		return false;
	}

	/**
	 * @param tile
	 *            from which to find discrete roads.
	 * 
	 * @return list of discrete Roads (potentially multi-tile), whether they are complete or not.
	 */
	public List<Road> findDiscreteRoadsOn(Tile tile) {
		List<RoadSection> sections = tile.discreteRoadSections();
		List<Road> result = new LinkedList<Road>();
		for (RoadSection section : sections) {
			Road road = new Road();
			extendRoadFromSection(road, section);
			// TODO: eliminate duplicates from road list (in case of figure8
			// loop, etc)
			result.add(road);
		}
		return result;
	}

	void extendRoadFromSection(Road road, RoadSection section) {
		Set<Edge> edges = section.connectedEdges();
		if (edges.size() == 1) {
			road.addClosedEnd(section, (Edge) edges.toArray()[0]);
		}
		road.add(section);
		for (Edge edge : edges) {
			Tile t = getTileOnAdjacentEdge(section.tile(), edge);
			if (t != null) {
				RoadSection adjacentRS = t.getRoadSectionOnEdgeOrFail(edge.reciprocal());
				if (road.alreadyIncludesRoadSection(adjacentRS)) {
					continue;
				}
				extendRoadFromSection(road, adjacentRS);
			} else {
				road.addOpenEnd(section, edge);
			}
		}
	}

	void extendCityFromSection(City city, CitySection section) {
		Set<Edge> edges = section.connectedEdges();
		city.add(section);
		for (Edge edge : edges) {
			Tile t = getTileOnAdjacentEdge(section.tile(), edge);
			if (t != null) {
				CitySection adjacentCS = t.getCitySectionOnEdgeOrFail(edge.reciprocal());
				if (city.alreadyIncludesSection(adjacentCS)) {
					continue;
				}
				extendCityFromSection(city, adjacentCS);
			} else {
			}
		}
	}

	void extendFarmFromSection(Farm farm, FarmSection section) {
		Set<FarmTP> farmTPs = section.connectedFarmTPs();
		farm.add(section);
		for (FarmTP farmTP : farmTPs) {
			Tile t = getTileOnAdjacentFarmTP(section.tile(), farmTP);
			if (t != null) {
				FarmSection adjacentCS = t.getFarmSectionOnFarmTPOrFail(farmTP.reciprocal());
				if (farm.alreadyIncludesSection(adjacentCS)) {
					continue;
				}
				extendFarmFromSection(farm, adjacentCS);
			} else {
			}
		}
	}

	// can't put this in farm without it knowing about the cities already on the board.
	public Set<City> getDistinctCitiesTouchingFarm(Farm farm) {
		Set<City> distinctTouchingCities = new HashSet<City>();

		Set<City> allDistinctCitiesOnBoard = getAllDistinctCities();
		for (FarmSection fs : farm.getFarmSections()) {			
			Set<CitySection> citySectionsTouched = fs.getCitySectionsTouched();
			for (CitySection citySection : citySectionsTouched) {
				City touchedCity = getCityForCitySectionOrFail(allDistinctCitiesOnBoard, citySection);
				if (!distinctTouchingCities.contains(touchedCity)) {
					distinctTouchingCities.add(touchedCity);
				}
			}
		}
		return distinctTouchingCities;
	}

	private City getCityForCitySectionOrFail(Set<City> citiesToSearchThrough, CitySection citySectionToFind) {
		for (City city : citiesToSearchThrough) {
			if (city.sections().contains(citySectionToFind)) {
				return city;
			}
		}
		throw new IllegalStateException("Couldn't find city containing city section: " + citySectionToFind);
	}

	public String toFullDetailString() {
		BoardPrinter boardPrinter = new BoardPrinter();
		String[] maps = boardPrinter.boardToString(this);
		return toString() + " full detail (over extents n,e,s,w " + getExtentsNESWAsString() + " ): \n" + maps[0] + "\n" + maps[1];
	}

	@Override
	public String toString() {
		return "num tiles played: " + _tilesPlayed.size() + " extents [n,e,s,w]: " + getExtentsNESWAsString();

	}

	public String getExtentsNESWAsString() {
		int[] es = getExtentsNESW();
		return Arrays.toString(es);
	}

	public int[] getExtentsNESW() {
		Direction8[] nesw = Direction8.neswOnly();
		int[] results = new int[nesw.length];
		int i = 0;
		for (Direction8 dir : nesw) {
			results[i] = getExtentsOrdinate(dir);
			i++;
		}
		return results;
	}

	int getExtentsOrdinate(Direction8 dir) {
		return dir.relevantOrdinate(getExtentsCoordinate(dir));
	}

	Point getExtentsCoordinate(Direction8 dir) {
		return getFurthestPlayedTile(dir).getGridPos();
	}

	private Tile getFurthestPlayedTile(Direction8 dirRequested) {
		return getFurthestPlayedTiles().get(dirRequested);
	}

	public Map<Direction8, Tile> getFurthestPlayedTiles() {
		if (_tilesPlayed.size() == 0) {
			throw new IllegalStateException("no tiles played yet!");
		}

		Tile minYTile, minXTile, maxYTile, maxXTile;
		minYTile = minXTile = maxYTile = maxXTile = _tilesPlayed.get(0);

		for (Tile t : _tilesPlayed) {
			Point p = t.getGridPos();
			if (p.y < minYTile.getGridPos().y) {
				minYTile = t;
			}
			if (p.y > maxYTile.getGridPos().y) {
				maxYTile = t;
			}
			if (p.x < minXTile.getGridPos().x) {
				minXTile = t;
			}
			if (p.x > maxXTile.getGridPos().x) {
				maxXTile = t;
			}
		}
		Map<Direction8, Tile> results = new HashMap<Direction8, Tile>();
		results.put(Direction8.NORTH, minYTile);
		results.put(Direction8.EAST, maxXTile);
		results.put(Direction8.SOUTH, maxYTile);
		results.put(Direction8.WEST, minXTile);
		return results;
	}

	public Object countOfPlayedTiles() {
		return tilesPlayed().size();
	}

	public String toTestCaseTilePlacementString() {
		StringBuffer buf = new StringBuffer();
		buf.append("assertConfigurationContainsNoCompleteRoads(\"gend\", ");
		buf.append("new Pl[] {");
		for (Tile t : _tilesPlayed) {
			buf.append(String.format("new Pl(%d, %d, %d, %d), ", t.tileType(), t.getGridPos().x, t.getGridPos().y, t.rotation()));
		}
		buf.delete(buf.length() - 2, buf.length());
		buf.append("});");
		return buf.toString();
	}

	/**
	 * Return the played tile with the given play number
	 * 
	 * @param playNumber
	 *            - play number of tile to retrieve (e.g. 3 would be the 3rd tile played)
	 */
	public Tile getTileByPlayNumber(int soughtNum) {
		for (Tile t : tilesPlayed()) {
			if (soughtNum == t.getPlayCount()) {
				return t;
			}
		}
		throw new IllegalArgumentException("no tile has play count: " + soughtNum);
	}

	public List<Tile> getPlayedCloisterTiles() {
		List<Tile> results = new ArrayList<Tile>();
		for (Tile t : _tilesPlayed) {
			if (t.hasCloister()) {
				results.add(t);
			}
		}
		return results;
	}

	public List<Tile> getPlayedCloisterTilesSurroundingOrOn(Tile tile) {
		List<Tile> tiles = getPlayedTilesSurroundingGridPos(tile.getGridPos(), Direction8.all8dirs());
		tiles.add(tile);
		List<Tile> nearbyCloisters = new ArrayList<Tile>();
		for (Tile candidateTile : tiles) {
			if (candidateTile.hasCloister()) {
				nearbyCloisters.add(candidateTile);
			}
		}
		return nearbyCloisters;
	}

	public int getSurroundingTileCount(Point gridPos) {
		return getPlayedTilesSurroundingGridPos(gridPos, Direction8.all8dirs()).size();
	}

	// TODO: don't build the roads each time we ask for them (max should be on
	// each tile placement)
	public Road roadAtPosition(Tile tile, Point inTileDetailPos) {
		RoadSection rs = tile.findRoadSectionAtPosition(inTileDetailPos);
		Road road = new Road();
		extendRoadFromSection(road, rs);
		return road;
	}

	public Cloister cloisterAtPosition(Tile tile, Point inTileDetailPos) {
		return tile.findCloisterAtPosition(inTileDetailPos);
	}

	public Set<Road> getAllDistinctRoads() {
		Set<Road> roads = new HashSet<Road>();
		for (Tile tile : tilesPlayed()) {
			List<Road> roadsFromTile = findDiscreteRoadsOn(tile);
			roads.addAll(roadsFromTile);
		}
		return roads;
	}

	public Set<Road> getAllIncompleteRoads() {
		Set<Road> incompleteRoads = new HashSet<Road>();
		Set<Road> allDistinctRoads = getAllDistinctRoads();
		for (Road road : allDistinctRoads) {
			if (!road.isComplete(this)) {
				incompleteRoads.add(road);
			}
		}
		return incompleteRoads;
	}

	public Set<City> findDiscreteCitiesOn(Tile tile) {
		Set<City> result = new HashSet<City>();
		for (CitySection section : tile.discreteCitySections()) {
			assert (section != null);
			City city = new City();
			extendCityFromSection(city, section);
			// TODO: eliminate duplicates from city list (in case of figure8 loop, etc)
			result.add(city);
		}
		return result;
	}

	private Set<Farm> findDiscreteFarmsOn(Tile tile) {
		Set<Farm> result = new HashSet<Farm>();
		for (FarmSection section : tile.discreteFarmSections()) {
			assert (section != null);
			Farm farm = new Farm();
			extendFarmFromSection(farm, section);
			// TODO: eliminate duplicates from road list (in case of figure8 loop, etc)
			result.add(farm);
		}
		return result;
	}

	public City cityAtPosition(Tile tile, Point inTileDetailPos) {
		CitySection cs = tile.findCitySectionAtPosition(inTileDetailPos);
		assert (cs != null);
		City city = new City();
		extendCityFromSection(city, cs);
		return city;
	}

	public Farm farmAtPosition(Tile tile, Point inTileDetailPos) {
		FarmSection fs = tile.findFarmSectionAtPosition(inTileDetailPos);
		assert (fs != null);
		Farm farm = new Farm();
		extendFarmFromSection(farm, fs);
		return farm;
	}

	public Set<City> getAllIncompleteCities() {
		Set<City> incompleteCities = new HashSet<City>();
		Set<City> allDistinctCities = getAllDistinctCities();
		for (City city : allDistinctCities) {
			if (!city.isComplete(this)) {
				incompleteCities.add(city);
			}
		}
		return incompleteCities;
	}

	Set<City> getAllDistinctCities() {
		Set<City> cities = new HashSet<City>();
		for (Tile tile : tilesPlayed()) {
			Set<City> citiesFromTile = findDiscreteCitiesOn(tile);
			cities.addAll(citiesFromTile);
		}
		return cities;
	}

	public Set<Farm> getAllDistinctFarms() {
		Set<Farm> allFarms = new HashSet<Farm>();
		for (Tile tile : tilesPlayed()) {
			Set<Farm> farmsOnTile = findDiscreteFarmsOn(tile);
			allFarms.addAll(farmsOnTile); // because it's a set we don't have to check if it's there already
		}
		return allFarms;
	}

	// TODO: this is used during testing only
	public Farm farmAtGPOrFail(Point gridPos, FarmTP farmTP) {
		Tile tile = tileAtGP(gridPos);
		if (tile == null) {
			throw new IllegalStateException("No farm where expected - NO TILE at " + PointUtil.xyString(gridPos));
		}
		// TODO: do NOT recompute these!
		Set<Farm> farmsOnTile = findDiscreteFarmsOn(tile);
		for (Farm farm : farmsOnTile) {
			for (FarmSection fs : farm.getFarmSections()) {
				if (fs.connectedFarmTPs().contains(farmTP)) {
					return farm;
				}
			}
		}
		throw new IllegalStateException("No farm where expected.  No farm section contained farmTP " + farmTP + ".  Farms on tile at "
				+ PointUtil.xyString(gridPos) + " were: " + farmsOnTile);

	}

}
