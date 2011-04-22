package net.abstractplain.tiletown;

import java.awt.Point;

/**
 * just generates primitive maps of the board for printing to console during debugging (or for inclusion in bug report, say)
 * 
 */
public class BoardPrinter {

	public String[] boardToString(Board board) {

		StringBuffer wholeTerrainDetailMap = new StringBuffer();
		StringBuffer wholeTileNumbersMap = new StringBuffer();
		int minY, minX, maxY, maxX;
		minY = board.getExtentsOrdinate(Direction8.NORTH);
		maxY = board.getExtentsOrdinate(Direction8.SOUTH);
		minX = board.getExtentsOrdinate(Direction8.WEST);
		maxX = board.getExtentsOrdinate(Direction8.EAST);

		for (int y = minY; y <= maxY; y++) {
			StringBuffer[] destLines = new StringBuffer[10];
			for (int i = 0; i < destLines.length; i++) {
				destLines[i] = new StringBuffer();
			}
			for (int x = minX; x <= maxX; x++) {
				Point p = new Point(x, y);
				Tile t = board.tileAtGP(p);
				if (t == null) {
					copyBlankSpaceToBuffers(destLines);
					wholeTileNumbersMap.append("..");
				} else {
					TerrainDetails terrain = t.terrainDetails();
					copyTerrainLinesToBuffers(terrain.toStrings(), destLines);
					wholeTileNumbersMap.append(t.tileType());
				}
			}

			for (int i = 0; i < destLines.length; i++) {
				wholeTerrainDetailMap.append(destLines[i] + "\n");
			}
			wholeTileNumbersMap.append("\n");
		}
		return new String[] { wholeTileNumbersMap.toString(), wholeTerrainDetailMap.toString() };
	}

	private void copyBlankSpaceToBuffers(StringBuffer[] destLines) {
		String blankChar = ".";
		for (int i = 0; i < destLines.length; i++) {
			for (int x = 0; x < TerrainDetails.RESOLUTION; x++) {
				destLines[i].append(blankChar);
				destLines[i].append(",");
			}
		}
	}

	private void copyTerrainLinesToBuffers(String[] srcLines, StringBuffer[] destLines) {
		assert (TerrainDetails.RESOLUTION == srcLines.length);
		for (int i = 0; i < srcLines.length; i++) {
			destLines[i].append(srcLines[i]);
		}
	}
}
