package net.abstractplain.tiletown;

import java.awt.Graphics2D;
import java.util.Date;

public class DebuggingScoreMarker {

	private final Tile _tile;

	private final int _score;

	private final Date _timeAdded;

	private final PlayerI _player;

	public DebuggingScoreMarker(PlayerI player, Tile tile, int score, Date timeAdded) {
		_player = player;
		_tile = tile;
		_score = score;
		_timeAdded = timeAdded;

	}

	public long age() {
		return (new Date().getTime() - _timeAdded.getTime());
	}

	public void paint(Graphics2D g) {

		long age = age();
		if (age > 5 * 1000) {
			return;
		}
		int midTileOffset = _tile.getSizeInt() / 2;
		g.setColor(_player.color());
		int x = _tile.getPixelPos().x + midTileOffset;
		int y = _tile.getPixelPos().y + midTileOffset;
		int dim = (int) Math.max((3000 - age) / 150.0, 0);
		g.fillRect(x, y, 5 + dim, dim);
		g.drawString("" + _score, x + 10, y);
	}
}
