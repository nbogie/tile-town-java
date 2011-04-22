package net.abstractplain.tiletown;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Road implements HighlightableFeatureI, HasMeeplesI {

	private int _score;

	private List<RoadSection> _roadSections = new LinkedList<RoadSection>();

	private Set<RoadEnd> _ends = new HashSet<RoadEnd>();

	private MeepleMgr _meepleMgr;

	private Highlighter _highlighter;

	public Road() {
		_meepleMgr = new MeepleMgr(this);
		_highlighter = new Highlighter(this);
	}

	public boolean isComplete(Board board) {
		boolean result = (terminalRoadSections().size() == 2) || (terminalRoadSections().size() == 0 && isCyclic(board));
		return result;
	}

	boolean isCyclic(Board board) {

		// TODO: relax this optimisation - perhaps later tiles will allow cycles
		// within fewer than 4 tiles
		if (numberOfSections() < 4) {
			return false;
		}
		if (terminalRoadSections().size() != 0) {
			return false;
		}
		RoadSection rsFirst = _roadSections.get(0);
		Set<Edge> connectedEdgesOnFirstRS = rsFirst.connectedEdges();
		assert (connectedEdgesOnFirstRS.size() == 2);
		Edge first = (Edge) connectedEdgesOnFirstRS.toArray()[0];
		return board.roadIsTraceableRoundToSecondEdge(rsFirst, first);
	}

	private int numberOfSections() {
		return _roadSections.size();
	}

	public void setScore(int i) {
		_score = i;

	}

	public int getScore() {
		return _score;
	}

	public boolean hasAtLeastOneMeeple() {
		return _meepleMgr.hasAtLeastOneMeeple();
	}

	/**
	 * @return a (shallow) copy of the list of road sections
	 */
	List<RoadSection> roadSections() {
		return new ArrayList<RoadSection>(_roadSections);
	}

	public List<Meeple> meeples() {
		LinkedList<Meeple> results = new LinkedList<Meeple>();
		for (SectionI section : sections()) {
			results.addAll(section.meeples());
		}
		return results;
	}

	public List<PlayerI> getMajorityOwners() {
		return meepleMgr().getMajorityOwners();
	}

	private MeepleMgr meepleMgr() {
		return _meepleMgr;
	}

	public void add(RoadSection rs) {
		if (_roadSections.contains(rs)) {
			throw new IllegalArgumentException("trying to add duplicate road section " + rs + " to road " + this);
		}
		_roadSections.add(rs);
	}

	/**
	 * @return a list of zero, one or two RoadSections where this road terminates.
	 */
	public List<RoadSection> terminalRoadSections() {
		LinkedList<RoadSection> results = new LinkedList<RoadSection>();
		for (RoadSection rs : _roadSections) {
			if (rs.hasTerminus()) {
				results.add(rs);
			}
		}
		return results;
	}

	public int countDistinctTiles() {
		return distinctTiles().size();
	}

	public List<Tile> distinctTiles() {
		LinkedList<Tile> distinct = new LinkedList<Tile>();
		for (RoadSection rs : _roadSections) {
			if (!distinct.contains(rs.tile())) {
				distinct.add(rs.tile());
			}
		}
		Comparator<Tile> tileNumComparator = new Comparator<Tile>() {
			public int compare(Tile o1, Tile o2) {
				Integer n1 = new Integer((o1).getPlayCount());
				Integer n2 = new Integer((o2).getPlayCount());
				return n1.compareTo(n2);
			}
		};
		Collections.sort(distinct, tileNumComparator);
		return distinct;
	}

	public boolean alreadyIncludesTile(Tile t) {
		return distinctTiles().contains(t);
	}

	@Override
	public String toString() {
		return "Road: length " + countDistinctTiles() + " with " + meeples().size() + " meeple(s) in tiles "
				+ Tile.mapTilePlayCounts(distinctTiles());
	}

	public String toTestCaseString() {
		StringBuffer buf = new StringBuffer();
		buf.append("assertConfigurationContainsNoCompleteRoads(\"gend\", new Pl[] {");
		for (Tile t : distinctTiles()) {
			buf.append(String.format("new Pl(%d, %d, %d, %d), ", t.tileType(), t.getGridPos().x, t.getGridPos().y, t.rotation()));
		}
		buf.delete(buf.length() - 2, buf.length());
		buf.append("});");
		return buf.toString();
	}

	// TODO: make generic to SectionI
	public boolean alreadyIncludesRoadSection(RoadSection rs) {
		return _roadSections.contains(rs);
	}

	public void returnAllMeeples() {
		for (RoadSection rs : _roadSections) {
			rs.returnAnyMeeplesToBase();
		}
	}

	// TODO: move to road display class (as roads can otherwise know nothing
	// about guis, and grids)
	public void applyHighlit(Grid grid, BufferedImage bufferedImage) {
		_highlighter.applyHighlightAsNecessary(grid, bufferedImage);
	}

	public void paintHighlit(Grid grid, Graphics2D g) {
		_highlighter.paintAsNecessary(grid, g);
	}

	public Set<RoadEnd> getEnds() {
		return new HashSet<RoadEnd>(_ends);
	}

	public Set<RoadEnd> getOpenEnds() {
		Set<RoadEnd> openEnds = new HashSet<RoadEnd>();
		for (RoadEnd end : getEnds()) {
			if (end.isOpen()) {
				openEnds.add(end);
			}
		}
		return openEnds;
	}

	private void addEnd(RoadEnd roadEnd) {
		_ends.add(roadEnd);
	}

	public void addClosedEnd(RoadSection rs, Edge nonEndingEdge) {
		RoadEnd roadEnd = new RoadEnd(rs);
		addEnd(roadEnd);
	}

	public void addOpenEnd(RoadSection rs, Edge edge) {
		RoadEnd roadEnd = new RoadEnd(rs, edge);
		addEnd(roadEnd);
	}

	@Override
	public int hashCode() {
		int result = 16;
		List<RoadEnd> endsSorted = new ArrayList<RoadEnd>(_ends);
		Collections.sort(endsSorted);
		for (RoadEnd end : endsSorted) {
			result = 30 * result + end.hashCode();
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Road))
			return false;
		Road r = (Road) obj;
		Set<RoadEnd> otherEnds = r.getEnds();
		Set<RoadEnd> myEnds = this.getEnds();

		return (myEnds.equals(otherEnds));
	}

	public List<? extends SectionI> sections() {
		return roadSections();
	}

	public void setIndicateAllowedMeeple(boolean b) {
		_highlighter.setIndicateAllowedMeeple(b);
	}

}
