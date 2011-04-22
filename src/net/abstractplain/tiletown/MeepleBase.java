package net.abstractplain.tiletown;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

public abstract class MeepleBase implements Meeple {

	BufferedImage _image = null;

	private Point _placedPosition;

	private Point _floatingPosition;

	private boolean _placeable;

	private final PlayerI _owner;

	public void setPlacedPosition(Point pos) {
		_placedPosition = pos;
	}

	public MeepleBase(PlayerI owner) {
		_owner = owner;
	}

	public Point placedPosition() {
		return _placedPosition;
	}

	public void paint(Graphics2D g) {
		if (_image == null) {
			loadImage();
		}
		Point pos = placedPosition();

		String marker = _placeable ? "o" : "X";
		g.setColor(_owner.color());
		int xOffset = _image.getWidth() / 2;
		int yOffset = _image.getHeight() / 2;
		if (pos != null) {
			g.drawString(marker, pos.x, pos.y);

			g.drawImage(_image, pos.x - xOffset, pos.y - yOffset, null);
		} else {
			Point floatingPos = floatingPosition();
			if (floatingPos != null) {
				g.drawString(marker, floatingPos.x, floatingPos.y);
				g.drawImage(_image, floatingPos.x - xOffset, floatingPos.y - yOffset, null);
			}
		}
	}

	abstract void loadImage();

	@Override
	public String toString() {
		return title() + " floating at " + floatingPosition() + " placed at " + placedPosition();
	}

	public Point floatingPosition() {
		return _floatingPosition;
	}

	public void setFloatingPosition(Point pos) {
		_floatingPosition = pos;
	}

	public void setPlaceable(boolean b) {
		_placeable = b;
	}

	public PlayerI owner() {
		return _owner;
	}

	public void returnToBase() {
		setPlacedPosition(null);
		setFloatingPosition(null);
		setPlaceable(false);
	}

}
