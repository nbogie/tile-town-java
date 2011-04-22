package net.abstractplain.tiletown;

import java.awt.Graphics2D;
import java.awt.Point;

public interface Meeple {
	public Point placedPosition();

	public Point floatingPosition();

	public void setFloatingPosition(Point pos);

	public void setPlacedPosition(Point pos);

	public void paint(Graphics2D g);

	public String title();

	public void setPlaceable(boolean b);

	public PlayerI owner();

	void returnToBase();
}
