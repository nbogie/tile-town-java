package net.abstractplain.tiletown;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

public interface HighlightableFeatureI {

	void paintHighlit(Grid grid, Graphics2D g);

	void applyHighlit(Grid grid, BufferedImage bufferedImage);

	List<? extends SectionI> sections();
}
