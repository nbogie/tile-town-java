package net.abstractplain.tiletown;

import java.awt.Point;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface SectionI extends Comparable<SectionI> {

	Tile tile();

	List<Point> detailPoints();

	Collection<? extends Meeple> meeples();

	Set<Edge> connectedEdges();

}
