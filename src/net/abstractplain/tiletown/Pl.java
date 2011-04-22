//TODO: restrict to test only
package net.abstractplain.tiletown;

public class Pl {
	private final int _y;

	private final int _tileNo;

	private final int _x;

	private int _rotation;

	public int y() {
		return _y;
	}

	public int tileNo() {
		return _tileNo;
	}

	public int x() {
		return _x;
	}

	public Pl(int tileNo, int x, int y, int rotation) {
		_tileNo = tileNo;
		_x = x;
		_y = y;
		_rotation = rotation;

	}

	public Pl(int tileNo, int x, int y) {
		this(tileNo, x, y, 0);
	}

	public int rotation() {
		return _rotation;
	}
}
