package net.abstractplain.tiletown;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class RoadSection implements SectionI {

	private final List<Point> _detailPoints;

	private final Tile _tile;

	private final Set<Edge> _connectedEdges;

	private List<Meeple> _meeples = new LinkedList<Meeple>();

	public Set<Edge> connectedEdges() {
		return new HashSet<Edge>(_connectedEdges);
	}

	public RoadSection(Tile tile, List<Point> detailPoints, Set<Edge> connectedEdgesList) {
		_tile = tile;
		_detailPoints = detailPoints;
		_connectedEdges = connectedEdgesList;
	}

	public List<Point> detailPoints() {
		return _detailPoints;
	}

	public Tile tile() {
		return _tile;
	}

	public boolean hasTerminus() {
		return (connectedEdges().size() == 1);
	}

	public List<Meeple> meeples() {
		return _meeples;
	}

	public void addMeeple(Meeple m) {
		_meeples.add(m);
	}

	@Override
	public String toString() {
		return "RoadSection: tile: " + tile().getName() + " edges " + _connectedEdges.toString();
	}

	public void returnAnyMeeplesToBase() {
		for (Meeple m : _meeples) {
			m.returnToBase();
		}
		_meeples.clear();
	}

	@Override
	public int hashCode() {
		int result = 18;
		result = 30 * result + tile().getPlayCount();
		List<Edge> edgesSorted = new ArrayList<Edge>(connectedEdges());
		Collections.sort(edgesSorted);
		for (Edge e : edgesSorted) {
			result = 30 * result + e.hashCode();
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof RoadSection))
			return false;
		RoadSection rs = (RoadSection) obj;
		Set<Edge> otherEdges = rs._connectedEdges;
		Set<Edge> myEdges = this._connectedEdges;
		return ((this.tile().getPlayCount() == rs.tile().getPlayCount()) && myEdges.containsAll(otherEdges) && myEdges.size() == otherEdges
				.size());
	}

	public int compareTo(SectionI o) {
		return new Integer(o.hashCode()).compareTo(new Integer(this.hashCode()));
	}

}
