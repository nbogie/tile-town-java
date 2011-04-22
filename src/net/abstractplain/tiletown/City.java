package net.abstractplain.tiletown;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class City implements HasMeeplesI, HighlightableFeatureI {

	public City() {
		_meepleMgr = new MeepleMgr(this);
		_highlighter = new Highlighter(this);
		_citySections = new ArrayList<CitySection>();
	}

	private MeepleMgr _meepleMgr;

	private Highlighter _highlighter;

	private List<CitySection> _citySections;

	public int countDistinctTiles() {
		return distinctTiles().size();
	}

	public List<Tile> distinctTiles() {
		LinkedList<Tile> distinct = new LinkedList<Tile>();
		for (CitySection s : _citySections) {
			if (!distinct.contains(s.tile())) {
				distinct.add(s.tile());
			}
		}
		return distinct;
	}

	public boolean isComplete(Board board) {
		for (CitySection s : _citySections) {
			for (Edge e : s.connectedEdges()) {
				if (null == board.getTileOnAdjacentEdge(s.tile(), e)) {
					return false;
				}
			}
		}
		return true;
	}

	public List<PlayerI> getMajorityOwners() {
		return meepleMgr().getMajorityOwners();
	}

	private MeepleMgr meepleMgr() {
		return _meepleMgr;
	}

	public void applyHighlit(Grid grid, BufferedImage bufferedImage) {
		_highlighter.applyHighlightAsNecessary(grid, bufferedImage);

	}

	public void paintHighlit(Grid grid, Graphics2D g) {
		_highlighter.paintAsNecessary(grid, g);
	}

	public List<? extends SectionI> sections() {
		return new ArrayList<CitySection>(_citySections);
	}

	public void setIndicateAllowedMeeple(boolean b) {
		_highlighter.setIndicateAllowedMeeple(b);
	}

	public List<Meeple> meeples() {
		LinkedList<Meeple> results = new LinkedList<Meeple>();
		for (SectionI cs : sections()) {
			results.addAll(cs.meeples());
		}
		return results;
	}

	public boolean hasAtLeastOneMeeple() {
		return _meepleMgr.hasAtLeastOneMeeple();
	}

	@Override
	public String toString() {
		return "City: tile count " + countDistinctTiles() + ", sections: " + sections() + ", meeples: " + meeples() + ", pennants: "
				+ countPennantsOnDistinctTiles();
	}

	public void add(CitySection section) {
		_citySections.add(section);
	}

	public boolean alreadyIncludesSection(SectionI s) {
		return sections().contains(s);
	}

	public int countPennantsOnDistinctTiles() {
		int count = 0;
		for (Tile t : distinctTiles()) {
			if (t.hasPennantOnSoleCitySectionIfAtAll())
				count++;
		}
		return count;
	}

	@Override
	public int hashCode() {
		int result = 15;
		List<? extends SectionI> sectionsSorted = sections();
		Collections.sort(sectionsSorted);
		for (SectionI section : sectionsSorted) {
			result = 30 * result + section.hashCode();
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof City))
			return false;
		City r = (City) obj;
		List<? extends SectionI> otherSections = r.sections();
		List<? extends SectionI> mySections = this.sections();
		// TODO: use set for this stuff
		return (otherSections.size() == mySections.size() && otherSections.containsAll(mySections));
	}

	public void returnAllMeeples() {
		for (CitySection rs : _citySections) {
			rs.returnAnyMeeplesToBase();
		}
	}

}
