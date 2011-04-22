package net.abstractplain.tiletown;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class CitySection implements SectionI {

	private HashSet<Edge> _connectedEdges;

	private Tile _tile;

	private final List<Point> _tixelPoints;

	private List<Meeple> _meeples = new LinkedList<Meeple>();

	public CitySection(Tile t) {
		this(t, new HashSet<Edge>(), new LinkedList<Point>());
	}

	public CitySection(Tile t, Set<Edge> edges, List<Point> tixelPoints) {
		_tixelPoints = tixelPoints;
		_connectedEdges = new HashSet<Edge>(edges);
		_tile = t;
	}

	public Set<Edge> connectedEdges() {
		return _connectedEdges;
	}

	public Tile tile() {
		return _tile;
	}

	public List<Point> tixelPoints() {
		return _tixelPoints;
	}

	// TODO: have RoadSection share this implementation
	public boolean containsTixelPoint(Point tixelPosition) {
		for (Point rsPoint : tixelPoints()) {
			if (rsPoint.equals(tixelPosition)) {
				return true;
			}
		}
		return false;
	}

	public List<Point> detailPoints() {
		return tixelPoints();
	}

	public Collection<? extends Meeple> meeples() {
		return _meeples;
	}

	public void addMeeple(Meeple m) {
		_meeples.add(m);
	}

	// TODO: if CitySection is mutable, is storing it in a hashset a bad bad
	// idea? if the hashcode is used to place it and later to find it, it may
	// never be found once it has been placed, no?
	@Override
	public int hashCode() {
		int result = 19;
		result = 17 * result + tile().getPlayCount();
		List<Edge> edgesSorted = new ArrayList<Edge>(connectedEdges());
		Collections.sort(edgesSorted);
		for (Edge e : edgesSorted) {
			result = 17 * result + e.hashCode();
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CitySection))
			return false;
		CitySection other = (CitySection) obj;
		Set<Edge> otherEdges = other._connectedEdges;
		Set<Edge> myEdges = this._connectedEdges;
		return ((this.tile().getPlayCount() == other.tile().getPlayCount()) && myEdges.containsAll(otherEdges) && myEdges.size() == otherEdges
				.size());
	}

	@Override
	public String toString() {
		return "CitySection in tile " + _tile.getName() + " on edge(s) " + connectedEdges();
	}

	public int compareTo(SectionI other) {
		return (new Integer(hashCode()).compareTo(new Integer(other.hashCode())));
	}

	public void returnAnyMeeplesToBase() {
		for (Meeple m : _meeples) {
			m.returnToBase();
		}
		_meeples.clear();
	}
}
