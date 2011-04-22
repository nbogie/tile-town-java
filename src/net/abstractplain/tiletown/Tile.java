package net.abstractplain.tiletown;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class Tile {
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(Tile.class);

	BufferedImage _image = null;

	private List<RoadSection> _discreteRoadSections = new LinkedList<RoadSection>();

	private Point _pixelPos = null;

	Point _gridPos = null;

	boolean _placed = false;

	int _rotation = 0;

	private String _imgName;

	private TileData _tileData;

	/**
	 *used when painting the (floating) tile's ok/illegal placement feedback (e.g. colored shadow)
	 */
	private boolean _isFloatingAtLegalPlayPosition;

	private int _playCount;

	private Meeple _meepleOnCloister;

	private Set<CitySection> _discreteCitySections;

	private Set<FarmSection> _discreteFarmSections;

	public Tile(String imgName, TileData tileData) {
		_imgName = imgName;
		setPixelPos(new Point(-1, -1));
		_gridPos = new Point(-1, -1);
		_rotation = 0;
		_tileData = tileData;
	}

	public void loadImage() {
		try {
			_image = FileHelp.loadImage(_imgName);
		} catch (IOException e) {
			throw new RuntimeException("missing image: " + _imgName, e);
		}
	}

	public int getSize() {
		return 100;
	}

	public int getSizeInt() {
		return getSize();
	}

	public TerrainDetails terrainDetails() {
		return _tileData.terrainDetails();
	}

	private void paintFeaturesDebug(Graphics2D g, int x, int y) {
		Matrix detailMatrix = terrainDetails().getDetail();

		Map<Integer, Color> colorMap = new HashMap<Integer, Color>();
		float alpha = 0.5f;
		colorMap.put(new Integer(1), new Color(1f, 0, 0, alpha));
		colorMap.put(new Integer(2), new Color(1f, 1f, 0, alpha));
		colorMap.put(new Integer(3), new Color(1f, 1f, 0, alpha));
		colorMap.put(new Integer(4), new Color(0, 0, 1f, alpha));
		colorMap.put(new Integer(5), new Color(0.5f, 1f, 0.5f, alpha));
		colorMap.put(new Integer(6), new Color(0.7f, 0.7f, 0.7f, alpha));
		for (int col = 0; col < detailMatrix.getHeight(); col++) {
			for (int row = 0; row < detailMatrix.getWidth(); row++) {
				int feature = Integer.parseInt((String) detailMatrix.get(row, col));
				Color c = colorMap.get(feature);
				g.setColor(c);
				g.fillRect(x + row * 10, y + col * 10, 10, 10);
			}
		}
		float roadAlpha = 1f;
		Color[] linkedRoadColors = new Color[] { new Color(0.7f, 0f, 0.0f, roadAlpha), new Color(0f, 0.7f, 0.0f, roadAlpha),
				new Color(0f, 0f, 0.7f, roadAlpha), new Color(1f, 0f, 0.5f, roadAlpha) };
		int colorIndex = 0;
		List<RoadSection> rsToPaint = this.discreteRoadSections();
		for (RoadSection roadSection : rsToPaint) {
			g.setColor(linkedRoadColors[colorIndex % 4]);
			for (Point p : roadSection.detailPoints()) {
				g.fillRect(x + p.x * 10, y + p.y * 10, 10, 10);
			}
			colorIndex++;
		}

		g.setColor(Color.black);
		g.drawString("" + tileData().featureAt(Edge.NORTH), x + 50, y + 10);
		g.drawString("" + tileData().featureAt(Edge.EAST), x + 90, y + 50);
		g.drawString("" + tileData().featureAt(Edge.SOUTH), x + 50, y + 90);
		g.drawString("" + tileData().featureAt(Edge.WEST), x + 10, y + 50);
		g.drawString("Discretes: " + discreteRoadSections().size(), x + 30, y + 60);
	}

	// TODO: highlight the currently placed tile until meeple placement is
	// finished - otherwise it's possible to look away from the game during
	// meeple placement and not be able to find the tile you just played (and
	// therefor not be able to complete your turn!)
	public void paint(Graphics2D g) {
		double rotRightRadians = Math.PI / 2.0;
		double rotLeftRadians = -Math.PI / 2.0;
		double rotDownRadians = Math.PI;
		double rotUpRadians = 0;

		double rotToApply = 0;
		switch (_rotation) {
		case 0:
			rotToApply = rotUpRadians;
			break;
		case 1:
			rotToApply = rotRightRadians;
			break;
		case 2:
			rotToApply = rotDownRadians;
			break;
		case 3:
			rotToApply = rotLeftRadians;
			break;
		default:
			throw new IllegalStateException("bad rotation value: " + _rotation);
		}

		// first draw potential-placement shadow
		if (!_placed) {
			Point shadowPos = gridPosToPixelPos(_gridPos);
			Color colorForNo = new Color(1.0f, 0, 0, 0.7f);
			Color colorForYes = new Color(0, 0, 1f, 0.7f);
			Color shadowColor = isFloatingAtLegalPlayPosition() ? colorForYes : colorForNo;
			g.setColor(shadowColor);

			g.fillRect(shadowPos.x, shadowPos.y, getSizeInt(), getSizeInt());
		}

		// don't overwrite pixelPos if not placed on grid - mouse will set the
		// floating pos
		if (_placed) {
			setPixelPos(gridPosToPixelPos(_gridPos));
		}

		int x = getPixelPos().x;
		int y = getPixelPos().y;
		g.rotate(rotToApply, x + getSize() / 2.0, y + getSize() / 2.0);
		g.drawImage(_image, x + 0, y + 0, null);
		g.rotate(-rotToApply, x + getSize() / 2.0, y + getSize() / 2.0);

		g.setColor(Color.black);
		g.drawString(_imgName, x + 10, (int) (y + getSize() / 4.0));
		g.drawString("#" + getPlayCount(), x + 80, y + 3 * getSize() / 4);
	}

	public void paintDebug(Graphics2D g) {
		int x = getPixelPos().x;
		int y = getPixelPos().y;
		paintFeaturesDebug(g, x, y);
	}

	private boolean isFloatingAtLegalPlayPosition() {

		return _isFloatingAtLegalPlayPosition;
	}

	// likely to use later.
	@SuppressWarnings("unused")
	private Point getPixelPosRelativeToEdgeOrCentre(EdgeOrCentre sideOrCentre) {
		Point p = null;
		switch (sideOrCentre) {
		case CENTRE:
			p = new Point(49, 49);
			break;
		case NORTH:
			p = new Point(49, 0);
			break;
		case EAST:
			p = new Point(99, 49);
			break;
		case SOUTH:
			p = new Point(49, 99);
			break;
		case WEST:
			p = new Point(0, 49);
			break;

		default:
			throw new RuntimeException("illegal side or centre number: " + sideOrCentre);
		}
		return p;
	}

	private Point gridPosToPixelPos(Point gridPos) {
		return new Point((gridPos.x * getSize()), (gridPos.y * getSize()));
	}

	public static int randomRotation() {
		int x = (int) (Math.random() * 4);
		return x;
	}

	public void rotateCCW() {
		_rotation -= 1;
		if (_rotation < 0) {
			_rotation = 3;
		}
		_tileData.rotateCCW();
	}

	public void rotateCW() {
		_rotation += 1;
		if (_rotation > 3) {
			_rotation = 0;
		}
		_tileData.rotateCW();
	}

	public void setPixelPos(Point point) {
		_pixelPos = point;
	}

	public void place(Point gridPos, int playCount) {
		_gridPos = gridPos;
		_playCount = playCount;
		_placed = true;

	}

	public int getPlayCount() {
		return _playCount;
	}

	public void setGridPos(Point point) {
		_gridPos = point;
	}

	public Point getGridPos() {
		return _gridPos;
	}

	public int featureOnSide(Edge edge) {
		return _tileData.featureAt(edge);
	}

	@Override
	public String toString() {
		return "Tile #" + getPlayCount() + " (type " + tileType() + ") rot: " + _rotation + " grid: " + PointUtil.xyString(_gridPos)
				+ ".  td: " + _tileData + (hasPennantOnSoleCitySectionIfAtAll() ? " (with pennant)" : "");

	}

	/**
	 * fill in the given connected points and connected edges lists by tracing the road pixels starting from the given edge.
	 * 
	 * @param edge
	 *            - edge of tile from which to trace the road.
	 * @param connectedPoints
	 *            - will be filled with coords of the relevant road section.
	 * @param connectedEdgesList
	 *            - will be filled with the edges which the road touches (if the road terminates in this tile this will be 1)
	 */
	public RoadSection roadFloodFillFromEdge(Edge edge) {
		List<Point> connectedPoints = new LinkedList<Point>();
		Set<Edge> connectedEdges = new HashSet<Edge>();
		Point firstRoadPosition = terrainDetails().findFirstFeaturePositionOnEdge(edge, Feature.ROAD);
		terrainDetails().roadFloodFillFromPoint(firstRoadPosition, connectedEdges, connectedPoints);
		RoadSection rs = new RoadSection(this, connectedPoints, connectedEdges);
		return rs;
	}

	/**
	 * 
	 */
	public void determineRoadSectionsOnTile() {
		Set<Edge> edgeList = findAllEdgesHavingFeature(Feature.ROAD);
		while (!edgeList.isEmpty()) {
			Edge startEdge = (Edge) edgeList.toArray()[0];
			RoadSection rs = roadFloodFillFromEdge(startEdge);

			for (Edge e : rs.connectedEdges()) {
				// we don't want to trace the same road starting from the other
				// edge!
				edgeList.remove(e);
			}
			_discreteRoadSections.add(rs);
		}
	}

	public Set<Edge> findAllEdgesHavingFeature(int soughtFeature) {
		Edge edgesToTry[] = Edge.nesw();
		Set<Edge> matchingEdges = new HashSet<Edge>();
		for (Edge edgeToTry : edgesToTry) {

			Point findFirstFeaturePositionOnEdge = terrainDetails().findFirstFeaturePositionOnEdge(edgeToTry, soughtFeature);
			if (findFirstFeaturePositionOnEdge != null) {
				matchingEdges.add(edgeToTry);
			}
		}
		return matchingEdges;
	}

	public TileData tileData() {
		return _tileData;
	}

	// TODO: reuse what's used for RoadSections
	public CitySection getCitySectionOnEdgeOrFail(Edge e) {
		CitySection section = getCitySectionOnEdge(e);
		if (section == null) {
			throw new IllegalStateException("no CitySection where expected on " + e + " edge of tile " + this + " with sections "
					+ this._discreteCitySections);
		}
		return section;
	}

	public FarmSection getFarmSectionOnFarmTPOrFail(FarmTP e) {
		FarmSection section = getFarmSectionOnFarmTP(e);
		if (section == null) {
			throw new IllegalStateException("no Section where expected on " + e + " farmTP of tile " + this + " with sections "
					+ this._discreteFarmSections);
		}
		return section;
	}

	private FarmSection getFarmSectionOnFarmTP(FarmTP e) {
		for (FarmSection section : _discreteFarmSections) {
			if (section.connectedFarmTPs().contains(e)) {
				return section;
			}
		}
		return null;

	}

	/**
	 * @param e
	 * @return RoadSection touching given edge, or null if none applies.
	 */
	public CitySection getCitySectionOnEdge(Edge e) {
		for (CitySection section : _discreteCitySections) {
			if (section.connectedEdges().contains(e)) {
				return section;
			}
		}
		return null;
	}

	/**
	 * @param e
	 * @return RoadSection touching given edge, or null if none applies.
	 */
	public RoadSection getRoadSectionOnEdge(Edge e) {
		for (RoadSection rs : _discreteRoadSections) {
			if (rs.connectedEdges().contains(e)) {
				return rs;
			}
		}
		return null;
	}

	public RoadSection getRoadSectionOnEdgeOrFail(Edge e) {
		RoadSection rs = getRoadSectionOnEdge(e);
		if (rs == null) {
			throw new IllegalStateException("no RoadSection where expected on " + e + " edge of tile " + this + " with sections "
					+ this._discreteRoadSections);
		}
		return rs;
	}

	public String getName() {
		return "#" + getPlayCount();
	}

	public RoadSection findRoadSectionAtPosition(Point inTileDetailPos) {
		for (RoadSection rs : discreteRoadSections()) {
			for (Point rsPoint : rs.detailPoints()) {
				if (rsPoint.equals(inTileDetailPos)) {
					return rs;
				}
			}
		}
		throw new IllegalStateException("expected there to be a road section at position: " + inTileDetailPos);
	}

	// TODO: reuse findSectionAtPosition()
	public CitySection findCitySectionAtPosition(Point tixelPosition) {
		for (CitySection cs : discreteCitySections()) {
			if (cs.containsTixelPoint(tixelPosition)) {
				return cs;
			}
		}
		return null;
	}

	public FarmSection findFarmSectionAtPosition(Point tixelPosition) {
		for (FarmSection fs : discreteFarmSections()) {
			if (fs.containsTixelPoint(tixelPosition)) {
				return fs;
			}
		}
		return null;
	}

	public Cloister findCloisterAtPosition(Point inTileDetailPos) {
		List<Point> detailPoints = terrainDetails().findDetailPoints(Feature.CLOISTER);
		Cloister c = new Cloister(this, detailPoints);
		return c;
	}

	// if it's on a road section, add it to the road section, etc.
	public void addMeepleToFeatureAt(Point inTileDetailPos, Meeple meeple) {
		int featureAtPosition = terrainDetails().featureAtPosition(inTileDetailPos);
		if (featureAtPosition == Feature.ROAD) {
			RoadSection rs = findRoadSectionAtPosition(inTileDetailPos);
			rs.addMeeple(meeple);
			// TODO: make these ALL use just
			// findSectionAtPosition(tixelPosition) (returning something to
			// which we can add a meeple)
		} else if (featureAtPosition == Feature.CITY) {
			CitySection cs = findCitySectionAtPosition(inTileDetailPos);
			cs.addMeeple(meeple);
		} else if (featureAtPosition == Feature.FARM) {
			FarmSection fs = findFarmSectionAtPosition(inTileDetailPos);
			fs.addMeeple(meeple);
		} else if (featureAtPosition == Feature.CLOISTER) {
			setMeepleOnCloister(meeple);
		}
	}

	public void setIsFloatingAtLegalPlayPosition(boolean legality) {
		_isFloatingAtLegalPlayPosition = legality;

	}

	public static Tile createTestInstance() {
		TileData td = TileData.createTestInstance();

		return createTestInstance(td);
	}

	public static Tile createTestInstance(TileData td) {
		return new Tile("imageName", td);
	}

	public int tileType() {
		return parseTileTypeFromImageName(_imgName);
	}

	static int parseTileTypeFromImageName(String givenImageName) {
		Pattern compile = Pattern.compile("\\d+");
		Matcher matcher = compile.matcher(givenImageName);
		if (!matcher.find()) {
			throw new RuntimeException("can't find pattern in image name: " + givenImageName);
		}
		return new Integer(matcher.group());
	}

	public int rotation() {
		return _rotation;
	}

	public boolean hasNoCityEdges() {
		for (Edge e : Edge.nesw()) {
			if (featureOnSide(e) == Feature.CITY) {
				return false;
			}
		}
		return true;
	}

	public boolean hasCloister() {
		return _tileData.hasCloister();
	}

	public void setMeepleOnCloister(Meeple m) {
		if (!hasCloister()) {
			throw new IllegalStateException("can't add meeple - no cloister on tile! " + this);
		}
		if (hasMeepleOnCloister()) {
			throw new IllegalStateException("Already has meeple on cloister! " + this);
		}
		_meepleOnCloister = m;
	}

	public boolean hasMeepleOnCloister() {
		return null != getMeepleOnCloister();
	}

	public boolean hasPennantOnSoleCitySectionIfAtAll() {
		boolean has = tileData().hasPennant();
		if (has && discreteCitySections().size() != 1) {
			throw new IllegalStateException("tile has pennant but also has more than one city section.  nonsensical data." + this
					+ " tile data: " + tileData());
		}
		return has;
	}

	public Meeple getMeepleOnCloister() {
		return _meepleOnCloister;
	}

	public void removeMeepleFromCloister() {
		_meepleOnCloister = null;

	}

	Point getPixelPos() {
		return _pixelPos;
	}

	public static List<Integer> mapTilePlayCounts(List<Tile> distinctTiles) {
		List<Integer> result = new ArrayList<Integer>();
		for (Tile tile : distinctTiles) {
			result.add(new Integer(tile.getPlayCount()));
		}
		return result;
	}

	public String getGridPosStr() {
		return String.format("(%d,%d)", getGridPos().x, getGridPos().y);
	}

	public List<RoadSection> discreteRoadSections() {
		return _discreteRoadSections;
	}

	public Set<CitySection> discreteCitySections() {
		return new HashSet<CitySection>(_discreteCitySections);
	}

	public Set<FarmSection> discreteFarmSections() {
		return _discreteFarmSections;
	}

	public void determineCitySectionsOnTile() {
		_discreteCitySections = new HashSet<CitySection>();
		Set<Edge> edgesToInspect = findAllEdgesHavingFeature(Feature.CITY);
		while (!edgesToInspect.isEmpty()) {
			Edge startEdge = (Edge) edgesToInspect.toArray()[0];
			CitySection cs = cityCreateCitySectionOnEdge(startEdge);
			for (Edge e : cs.connectedEdges()) {
				// if we've found that edges are a part of a feature we've
				// already traced, we don't want to start again with those edges
				edgesToInspect.remove(e);
			}
			_discreteCitySections.add(cs);
		}
	}

	public void determineFarmSectionsOnTile() {
		// TODO: assign the field only once the set is fully built up.
		_discreteFarmSections = new HashSet<FarmSection>();
		Set<FarmTP> farmTPsToInspect = findAllFarmTPsHavingFeature(Feature.FARM);
		while (!farmTPsToInspect.isEmpty()) {
			FarmTP startFarmTP = (FarmTP) farmTPsToInspect.toArray()[0];
			FarmSection cs = farmCreateFarmSectionOnFarmTP(startFarmTP);
			for (FarmTP e : cs.connectedFarmTPs()) {
				// if we've found that farmTPs are a part of a feature we've
				// already traced, we don't want to start again with those
				// farmTPs
				farmTPsToInspect.remove(e);
			}
			// TODO: also handle centre-only (farm restricted to centre, no tps
			// at all)
			_discreteFarmSections.add(cs);
		}
	}

	/**
	 * find the extends of the farm section starting on the given farmTP, in terms of the farmTPs it hits on this tile, and its tixel
	 * coords. Also finds which city sections it touches (requires the general CitySections to have been determined for tile already)
	 * 
	 * @param e
	 *            - farmTP from which to start
	 * @param cityTixelPoints
	 *            - (to be filled in) list of tixel positions comprising the city section
	 * @param connectedFarmTPs
	 *            (to be filled in) list of farmTPs touched by this city section
	 * @return FarmSection created
	 */
	FarmSection farmCreateFarmSectionOnFarmTP(FarmTP e) {
		// TODO: consider moving floodfill stuff to Farm (would be static)
		// or some FarmBuilder class or some FeatureBuilder class, which just
		// was parameterised slightly differently for different features
		Point firstFarmPosition = e.toTixelPosition();
		List<Point> farmTixelPoints = new LinkedList<Point>();
		List<Point> touchingCityTixelPoints = new LinkedList<Point>();

		Set<FarmTP> connectedFarmTPs = new HashSet<FarmTP>();
		terrainDetails().farmFloodFillFromPoint(firstFarmPosition, connectedFarmTPs, farmTixelPoints, touchingCityTixelPoints);

		FarmSection fs = new FarmSection(this, connectedFarmTPs, farmTixelPoints, touchingCityTixelPoints);
		Set<CitySection> citySectionsTouched = determineCitySectionsTouchedByFarmSection(fs);
		fs.setCitySectionsTouched(citySectionsTouched);
		return fs;
	}

	Set<FarmTP> findAllFarmTPsHavingFeature(int soughtFeature) {

		Set<FarmTP> matchingFarmTPs = new HashSet<FarmTP>();
		for (FarmTP farmTPToTry : FarmTP.values()) {

			int feature = terrainDetails().featureAtFarmTP(farmTPToTry);
			if (feature == Feature.FARM) {
				matchingFarmTPs.add(farmTPToTry);
			}
		}
		return matchingFarmTPs;

	}

	/**
	 * find the extends of the city section starting on the given edge, in terms of the edges it hits on this tile, and its tixel coords.
	 * 
	 * @param e
	 *            - edge from which to start
	 * @param cityTixelPoints
	 *            - (to be filled in) list of tixel positions comprising the city section
	 * @param connectedEdges
	 *            (to be filled in) list of edges touched by this city section
	 * @return CitySection created
	 */
	CitySection cityCreateCitySectionOnEdge(Edge e) {
		// TODO: consider moving city floodfill stuff to City (would be static)
		// or some CityBuilder class
		Point firstCityPosition = terrainDetails().findFirstFeaturePositionOnEdge(e, Feature.CITY);
		if (firstCityPosition == null) {
			throw new IllegalStateException("could find no city tixel on edge " + e);
		}
		List<Point> cityTixelPoints = new LinkedList<Point>();
		Set<Edge> connectedEdges = new HashSet<Edge>();
		terrainDetails().cityFloodFillFromPoint(firstCityPosition, connectedEdges, cityTixelPoints);
		CitySection cs = new CitySection(this, connectedEdges, cityTixelPoints);
		return cs;
	}

	public void computeFeatures() {
		determineRoadSectionsOnTile();
		determineCitySectionsOnTile();
		determineFarmSectionsOnTile();
	}

	// TODO: do this once right after farm and city section computation. It doesn't change.
	public Set<CitySection> determineCitySectionsTouchedByFarmSection(FarmSection farmSection) {
		HashSet<CitySection> result = new HashSet<CitySection>();
		List<Point> touchingCityTixelPoints = farmSection.getTouchingCityDetailPoints();
		for (Point point : touchingCityTixelPoints) {
			CitySection touchedCS = findCitySectionAtPosition(point);
			if (!result.contains(touchedCS)) {
				result.add(touchedCS);
			}
		}
		return result;
	}

}