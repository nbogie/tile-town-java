package net.abstractplain.tiletown;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Highlighter {
	private final HighlightableFeatureI _highlit;

	private boolean _indicateAllowedMeeple;

	public Highlighter(HighlightableFeatureI highlit) {
		_highlit = highlit;
	}

	public void paintAsNecessary(Grid grid, Graphics2D g) {
		Color color = _indicateAllowedMeeple ? new Color(1f, 1f, 1f, 0.5f) : new Color(1f, 0f, 0f, 0.3f);
		for (SectionI rs : _highlit.sections()) {
			assert (rs.detailPoints() != null);
			DetailPointsHighlighter detailPointsHighlighter = new DetailPointsHighlighter(rs.tile(), rs.detailPoints(), color);
			detailPointsHighlighter.paint(grid, g);
		}
	}

	public void applyHighlightAsNecessary(Grid grid, BufferedImage bufferedImage) {
		Color color = _indicateAllowedMeeple ? new Color(1f, 1f, 1f, 0.5f) : new Color(1f, 0f, 0f, 0.3f);
		for (SectionI rs : _highlit.sections()) {
			DetailPointsHighlighter detailPointsHighlighter = new DetailPointsHighlighter(rs.tile(), rs.detailPoints(), color);
			detailPointsHighlighter.apply(grid, bufferedImage);
		}
	}

	public void setIndicateAllowedMeeple(boolean val) {
		_indicateAllowedMeeple = val;
	}

}
