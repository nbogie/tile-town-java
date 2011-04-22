package net.abstractplain.tiletown;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.NotImplementedException;

public class FarmSection implements SectionI {

	private HashSet<FarmTP> _connectedFarmTPs;

	private Tile _tile;

	private final List<Point> _tixelPoints;

	private List<Meeple> _meeples = new LinkedList<Meeple>();

	private List<Point> _touchingCityTixelPoints;

	private Set<CitySection> _citySectionsTouched;

	public Set<CitySection> getCitySectionsTouched() {
		return new HashSet<CitySection>(_citySectionsTouched);
	}

	// TODO: is this almost-empty FarmSection constructor necessary?
	public FarmSection(Tile t) {
		this(t, new HashSet<FarmTP>(), new LinkedList<Point>(), new LinkedList<Point>());
	}

	public FarmSection(Tile t, Set<FarmTP> farmTPs, List<Point> tixelPoints, List<Point> touchingCityTixelPoints) {
		_tixelPoints = tixelPoints;
		_touchingCityTixelPoints = touchingCityTixelPoints;
		_connectedFarmTPs = new HashSet<FarmTP>(farmTPs);
		_tile = t;
	}

	public Set<FarmTP> connectedFarmTPs() {
		return _connectedFarmTPs;
	}

	// TODO: remove this. needed by SectionI but that's wrong.
	public Set<Edge> connectedEdges() {
		throw new NotImplementedException("farm doesn't have connected EDGES!");
	}

	public Tile tile() {
		return _tile;
	}

	// TODO: tixelPoints or detailPoints - one or the other
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

	// TODO: if FarmSection is mutable, is storing it in a hashset a bad bad
	// idea? if the hashcode is used to place it and later to find it, it may
	// never be found once it has been placed, no?
	@Override
	public int hashCode() {
		int result = 19;
		result = 17 * result + tile().getPlayCount();
		List<FarmTP> farmTPsSorted = new ArrayList<FarmTP>(connectedFarmTPs());
		Collections.sort(farmTPsSorted);
		for (FarmTP e : farmTPsSorted) {
			result = 17 * result + e.hashCode();
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof FarmSection))
			return false;
		FarmSection other = (FarmSection) obj;
		Set<FarmTP> otherFarmTPs = other._connectedFarmTPs;
		Set<FarmTP> myFarmTPs = this._connectedFarmTPs;
		return ((this.tile().getPlayCount() == other.tile().getPlayCount()) && myFarmTPs.containsAll(otherFarmTPs) && myFarmTPs.size() == otherFarmTPs
				.size());
	}

	@Override
	public String toString() {
		return "FarmSection in tile " + _tile.getName() + " on farmTP(s) " + connectedFarmTPs();
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

	public List<Point> getTouchingCityDetailPoints() {
		return _touchingCityTixelPoints;
	}

	public void setCitySectionsTouched(Set<CitySection> citySectionsTouched) {
		_citySectionsTouched = citySectionsTouched;
	}

}
