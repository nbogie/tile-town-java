package net.abstractplain.tiletown;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.LookupOp;
import java.awt.image.ShortLookupTable;
import java.util.List;

public class DetailPointsHighlighter {

	private final Tile _tile;

	private final List<Point> _detailPoints;

	private final Color _color;

	private ShortLookupTable _lookupTable;

	private void createBrightenLUT() {
		short brighten[] = new short[256];
		for (int i = 0; i < 256; i++) {
			short pixelValue = (short) (i + 10);
			if (pixelValue > 255)
				pixelValue = 255;
			else if (pixelValue < 0)
				pixelValue = 0;
			brighten[i] = pixelValue;
		}
		_lookupTable = new ShortLookupTable(0, brighten);
	}

	public DetailPointsHighlighter(Tile tile, List<Point> detailPoints, Color color) {
		assert (detailPoints != null);
		assert (tile != null);
		assert (color != null);
		_tile = tile;
		_detailPoints = detailPoints;
		_color = color;
		createBrightenLUT();
	}

	public void paint(Grid grid, Graphics2D g) {
		Tile t = _tile;
		Point tilePixelPos = t.getPixelPos();
		for (Point p : _detailPoints) {
			Point offset = grid.convertTerrainPosToPixelOffset(p);
			Point terrainPixelPos = new Point(tilePixelPos.x + offset.x, tilePixelPos.y + offset.y);
			int size = t.getSize() / TerrainDetails.RESOLUTION;
			g.setColor(_color);
			g.fillRect(terrainPixelPos.x, terrainPixelPos.y, size, size);
		}
	}

	public void apply(Grid grid, BufferedImage image) {
		LookupOp lop = new LookupOp(_lookupTable, null);
		lop.filter(image, image);
	}

}
