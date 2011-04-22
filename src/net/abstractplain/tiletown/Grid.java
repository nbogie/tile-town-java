package net.abstractplain.tiletown;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

public class Grid {
	int _spacingInPixels = -1;

	private int _heightSquares;

	private int _widthSquares;

	public Grid(int spacing, int widthSquares, int heightSquares) {
		_spacingInPixels = spacing;
		_widthSquares = widthSquares;
		_heightSquares = heightSquares;
	}

	public Point convertToGrid(Point pixelPoint) {
		return new Point((int) Math.floor(pixelPoint.x / getSpacing()), (int) Math.floor(pixelPoint.y / getSpacing()));
	}

	public Point[] convertToGridAndDetailGrid(Point pixelPoint) {
		Point mainGridPoint = convertToGrid(pixelPoint);
		int xPixelInTile = pixelPoint.x % getSpacing();
		int yPixelInTile = pixelPoint.y % getSpacing();
		int xGridInTile = (10 * xPixelInTile / getSpacing());
		int yGridInTile = (10 * yPixelInTile / getSpacing());
		Point inTileGridPoint = new Point(xGridInTile, yGridInTile);
		Point[] retVals = new Point[2];
		retVals[0] = mainGridPoint;
		retVals[1] = inTileGridPoint;
		return retVals;
	}

	public Point roundToGrid(Point pixelPoint) {
		return new Point(Math.round(pixelPoint.x / getSpacing()), Math.round(pixelPoint.y / getSpacing()));
	}

	private int getSpacing() {
		return _spacingInPixels;
	}

	public int h() {
		return _heightSquares;
	}

	public void setHeightSquares(int heightSquares) {
		_heightSquares = heightSquares;
	}

	public int w() {
		return _widthSquares;
	}

	public void setWidthSquares(int widthSquares) {
		_widthSquares = widthSquares;
	}

	public void paint(Graphics2D g) {
		g.setColor(Color.black);

		for (int y = 0; y < h(); y++) {
			g.drawLine(0, (y * getSpacing()), (w() * getSpacing()), (y * getSpacing()));
		}
		for (int x = 0; x < w(); x++) {
			g.drawLine(x * getSpacing(), 0, x * getSpacing(), h() * getSpacing());
			for (int y = 0; y < h(); y++) {
				g.drawString(x + "," + y + " (above)", 2 + (x * getSpacing()), 10 + (y + 1) * getSpacing());
			}
		}

	}

	public Point convertTerrainPosToPixelOffset(Point p) {
		return new Point(p.x * TerrainDetails.RESOLUTION, p.y * TerrainDetails.RESOLUTION);
	}

}
