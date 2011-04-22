package net.abstractplain.tiletown;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * hardly used.
 * 
 */
public class Cloister implements HighlightableFeatureI, SectionI {

	private final List<Point> _detailPoints;

	private Tile _tile;

	public Cloister(Tile tile, List<Point> detailPoints) {
		_tile = tile;
		_detailPoints = detailPoints;
	}

	public void setIndicateAllowedMeeple(boolean dontcare) {

	}

	public void paintHighlit(Grid grid, Graphics2D g) {
		DetailPointsHighlighter detailPointsHighlighter = new DetailPointsHighlighter(tile(), _detailPoints, new Color(1f, 1f, 1f, 0.5f));
		detailPointsHighlighter.paint(grid, g);
	}

	public Tile tile() {
		return _tile;
	}

	public void applyHighlit(Grid grid, BufferedImage bufferedImage) {
		DetailPointsHighlighter detailPointsHighlighter = new DetailPointsHighlighter(tile(), _detailPoints, new Color(1f, 1f, 1f, 0.5f));
		detailPointsHighlighter.apply(grid, bufferedImage);
	}

	public List<SectionI> sections() {
		ArrayList<SectionI> list = new ArrayList<SectionI>();
		list.add(this);
		return list;
	}

	public List<Point> detailPoints() {
		return _detailPoints;
	}

	public Collection<? extends Meeple> meeples() {
		Set<Meeple> ms = new HashSet<Meeple>();
		ms.add(tile().getMeepleOnCloister());
		return ms;
	}

	// TODO: consider that Cloister shouldn't implement SectionI - it has no
	// connected edges (though it has a Tile and DetailPoints)
	public Set<Edge> connectedEdges() {
		return new HashSet<Edge>();
	}

	public int compareTo(SectionI o) {
		return new Integer(o.hashCode()).compareTo(new Integer(this.hashCode()));
	}

}
