package net.abstractplain.tiletown;

public class RoadEnd implements Comparable<RoadEnd> {

	private RoadSection _roadSection;

	private EdgeOrCentre _endingEdgeOrCentre;

	private boolean _isOpen;

	public RoadEnd(RoadSection section, Edge edge) {
		this(section, EdgeOrCentre.fromEdge(edge));
	}

	public RoadEnd(RoadSection section) {
		this(section, EdgeOrCentre.CENTRE);
	}

	private RoadEnd(RoadSection section, EdgeOrCentre edgeOrCentre) {
		_roadSection = section;
		_endingEdgeOrCentre = edgeOrCentre;
		setOpen(edgeOrCentre != EdgeOrCentre.CENTRE);

	}

	private RoadSection getRoadSection() {
		return _roadSection;
	}

	public boolean isOpen() {
		return _isOpen;
	}

	public Tile getTile() {
		return getRoadSection().tile();
	}

	public EdgeOrCentre getEndingEdgeOrCentre() {
		return _endingEdgeOrCentre;
	}

	public void setOpen(boolean open) {
		_isOpen = open;
	}

	@Override
	public String toString() {
		return "RoadEnd tile " + getTile().getPlayCount() + getTile().getGridPosStr() + " edge: " + getEndingEdgeOrCentre() + " open? "
				+ isOpen();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof RoadEnd))
			return false;
		RoadEnd r = (RoadEnd) obj;
		return (r.getRoadSection().hashCode() == this.getRoadSection().hashCode() && (r.getEndingEdgeOrCentre().equals(this
				.getEndingEdgeOrCentre())));
	}

	@Override
	public int hashCode() {
		int result = 15;
		result = result * 30 + getRoadSection().hashCode();
		result = result * 30 + getEndingEdgeOrCentre().hashCode();
		return result;
	}

	// TODO: implement compareTo correctly. This is just so we can sort RoadEnds when making HashCode for Road!
	public int compareTo(RoadEnd o) {
		return (new Integer(hashCode()).compareTo(new Integer(o.hashCode())));
	}
}
