package net.abstractplain.tiletown;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class TileData {
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(TileData.class);

	private String[] _featurePoints;

	private LinkedList<EdgeOrCentre> _connectionTerminalsList;

	private int _numberOfOccurrences;

	private boolean _isStartTileType;

	private final TerrainDetails _terrainDetails;

	private final String _tileFilename;

	private boolean _hasCloister;

	private boolean _hasPennant;

	public TileData(String tileFilename, int numberOfOccurrences, String featurePoints, TerrainDetails terrainDetails,
			boolean isStartTileType, boolean hasPenant) {
		_tileFilename = tileFilename;
		_featurePoints = featurePoints.split(" ");
		_isStartTileType = isStartTileType;
		_connectionTerminalsList = new LinkedList<EdgeOrCentre>();
		_numberOfOccurrences = numberOfOccurrences;
		_terrainDetails = terrainDetails;
		_hasPennant = hasPenant;
		computeIfHasCloister();
	}

	private void computeIfHasCloister() {
		_hasCloister = _terrainDetails.getDetail().contains("" + Feature.CLOISTER);
	}

	public boolean hasCloister() {
		return _hasCloister;
	}

	private String featurePointsAsString() {
		StringBuffer sb = new StringBuffer();
		for (String s : _featurePoints) {
			sb.append(s + " ");
		}
		return sb.toString();
	}

	public TileData(TileData other) {
		this(other._tileFilename, other._numberOfOccurrences, other.featurePointsAsString(), new TerrainDetails(other.terrainDetails()
				.getDetail()), other._isStartTileType, other._hasPennant);
	}

	public int featureAt(Edge edge) {
		return Integer.parseInt(_featurePoints[edge.asArrayIndexForNESW()]);
	}

	public static Map<String, TileData> loadTileData(String filename) {

		List<String> lines;
		try {
			lines = FileHelp.loadLines(filename);
		} catch (IOException e) {
			throw new RuntimeException("Problem loading tile data from file: " + filename + "  Problem: " + e, e);
		}

		HashMap<String, TileData> map = new HashMap<String, TileData>();
		for (Iterator<String> iter = lines.iterator(); iter.hasNext();) {
			String tileFilename = iter.next();
			int numberOfOccurrences = new Integer(iter.next());
			String featurePoints = iter.next();
			String extras = iter.next();
			boolean isStartTileType = (extras.toLowerCase().contains("start"));
			boolean hasPennant = (extras.toLowerCase().contains("pennant"));

			ArrayList<String> detailLines = new ArrayList<String>();
			for (int i = 0; i < 10; i++) {
				detailLines.add(iter.next());
			}
			Matrix detailMatrix = Matrix.createFromStringLines(detailLines);
			TerrainDetails terrainDetails = new TerrainDetails(detailMatrix);
			TileData tileData = new TileData(tileFilename, numberOfOccurrences, featurePoints, terrainDetails, isStartTileType, hasPennant);
			map.put(tileFilename, tileData);
		}
		return map;
	}

	public TerrainDetails terrainDetails() {
		return _terrainDetails;
	}

	@Override
	public String toString() {
		return "featurePoints: n: " + featureAt(Edge.NORTH) + " e: " + featureAt(Edge.EAST) + " s: " + featureAt(Edge.SOUTH) + " w: "
				+ featureAt(Edge.WEST);
	}

	public LinkedList<EdgeOrCentre> getConnectionTerminalsList() {
		return _connectionTerminalsList;
	}

	public int getNumberOfOccurrencesInSet() {
		return _numberOfOccurrences;
	}

	public boolean isStartTileType() {
		return _isStartTileType;
	}

	protected void rotateFeaturePointsCW() {
		String last = _featurePoints[3];
		String[] newFeatures = new String[4];
		newFeatures[0] = last;
		for (int i = 0; i < 3; i++) {
			newFeatures[i + 1] = _featurePoints[i];
		}
		_featurePoints = newFeatures;
	}

	public void rotateCW() {
		rotateFeaturePointsCW();
		_terrainDetails.rotateCW();

	}

	public void rotateCCW() {
		rotateFeaturePointsCW();
		rotateFeaturePointsCW();
		rotateFeaturePointsCW();
		_terrainDetails.rotateCCW();
	}

	public static TileData fetchOneTileDataForTest(int tileNumber, Map<String, TileData> allTileData) {
		String tileName = makeTileNameForTileNumber(tileNumber);
		// must clone here, to avoid creating many tiles with references to the
		// same TileData object.
		return new TileData(allTileData.get(tileName));
	}

	static String makeTileNameForTileNumber(int tileNumber) {
		String tileName = String.format("basic/bg%03d.png", new Integer(tileNumber));
		return tileName;
	}

	public String imageName() {
		return _tileFilename;
	}

	public static TileData createTestInstance() {
		int occurrences = 1;
		String featurePointsString = "1 3 3 6";
		return new TileData("imageFilename", occurrences, featurePointsString, TerrainDetails.createTestInstance(), false, false);
	}

	public boolean hasPennant() {
		return _hasPennant;
	}

}
