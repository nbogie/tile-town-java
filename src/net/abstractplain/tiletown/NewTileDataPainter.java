package net.abstractplain.tiletown;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class NewTileDataPainter {
	private final static int LINE_HEIGHT = 10;

	private final TerrainDetails _data;

	private final Point _displayPos;

	private String _label;

	NewTileDataPainter(TerrainDetails data, String label, Point displayPos) {
		_data = data;
		_label = label;
		_displayPos = displayPos;
	}

	public void Paint(Graphics g) {
		Matrix detailMatrix = _data.getDetail();
		int dim = detailMatrix.getHeight() * LINE_HEIGHT;
		g.setColor(new Color(1f, 1f, 1f, 0.5f));
		g.fillRect(_displayPos.x, _displayPos.y, dim, dim);
		g.setColor(Color.black);
		g.drawString(_label, _displayPos.x, _displayPos.y);
		for (int row = 0; row < detailMatrix.getHeight(); row++) {
			StringBuffer line = new StringBuffer();
			for (int col = 0; col < detailMatrix.getWidth(); col++) {
				line.append((String) detailMatrix.get(col, row));
			}
			g.drawString(line.toString(), _displayPos.x, _displayPos.y + (row + 1) * LINE_HEIGHT);

		}
	}
}
