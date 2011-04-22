package net.abstractplain.tiletown;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Farm implements HasMeeplesI, HighlightableFeatureI {

	public Farm() {
		_meepleMgr = new MeepleMgr(this);
		_highlighter = new Highlighter(this);
		_farmSections = new ArrayList<FarmSection>();
	}

	private MeepleMgr _meepleMgr;

	private Highlighter _highlighter;

	private List<FarmSection> _farmSections;

	public int countDistinctTiles() {
		return distinctTiles().size();
	}

	public List<Tile> distinctTiles() {
		LinkedList<Tile> distinct = new LinkedList<Tile>();
		for (FarmSection s : _farmSections) {
			if (!distinct.contains(s.tile())) {
				distinct.add(s.tile());
			}
		}
		return distinct;
	}

	public boolean isComplete(Board board) {
		for (FarmSection s : _farmSections) {
			for (FarmTP e : s.connectedFarmTPs()) {
				if (null == board.getTileOnAdjacentFarmTP(s.tile(), e)) {
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

	public List<FarmSection> sections() {
		return new ArrayList<FarmSection>(_farmSections);
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
		return "Farm: tile count " + countDistinctTiles() + ", sections: " + sections() + ", meeples: " + meeples();
	}

	public void add(FarmSection section) {
		_farmSections.add(section);
	}

	public boolean alreadyIncludesSection(FarmSection s) {
		return sections().contains(s);
	}

	@Override
	public int hashCode() {
		int result = 15;
		List<FarmSection> sectionsSorted = new ArrayList<FarmSection>(_farmSections);
		Collections.sort(sectionsSorted);
		for (FarmSection section : sectionsSorted) {
			result = 30 * result + section.hashCode();
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Farm))
			return false;
		Farm r = (Farm) obj;
		List<FarmSection> otherSections = r._farmSections;
		List<FarmSection> mySections = this._farmSections;
		// TODO: use set for this stuff
		return (otherSections.size() == mySections.size() && otherSections.containsAll(mySections));
	}

	public void returnAllMeeples() {
		for (FarmSection rs : _farmSections) {
			rs.returnAnyMeeplesToBase();
		}
	}

	public Set<FarmSection> getFarmSections() {
		return new HashSet<FarmSection>(_farmSections);
	}

}
