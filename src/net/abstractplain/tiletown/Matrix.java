package net.abstractplain.tiletown;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Matrix {
	private Object data[][] = null;

	public Matrix(int x, int y) {
		data = new Object[y][x];
	}

	public static Matrix createFromLines(List<List<Object>> list) {
		int gy = list.size();
		int gx = list.get(0).size();
		Matrix m = new Matrix(gx, gy);
		int y = 0;
		for (List<Object> row : list) {
			int x = 0;
			for (Object obj : row) {
				m.set(x, y, obj);
				x++;
			}
			y++;
		}
		return m;
	}

	public static Matrix createFromStringLines(List<String> list) {
		int gy = list.size();
		int gx = list.get(0).length();
		Matrix m = new Matrix(gx, gy);
		int y = 0;
		for (String row : list) {
			int x = 0;
			String[] cells = row.split("");
			for (String cell : cells) {
				if (cell.isEmpty()) {
					continue;
				}
				m.set(x, y, cell);
				x++;
			}
			y++;
		}
		return m;
	}

	private void set(int x, int y, Object obj) {
		data[y][x] = obj;
	}

	Object get(int x, int y) {
		return data[y][x];
	}

	public Matrix rotateCW90() {

		Matrix m = new Matrix(getHeight(), getWidth());
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				int newX = getHeight() - 1 - y;
				int newY = x;
				m.set(newX, newY, get(x, y));
			}
		}
		return m;
	}

	public int getHeight() {
		return data.length;
	}

	public int getWidth() {
		return data[0].length;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer("Matrix: \n");
		for (int row = 0; row < getHeight(); row++) {
			for (int col = 0; col < getWidth(); col++) {
				buf.append(data[row][col] + ",");
			}
			buf.append("\n");
		}
		return buf.toString();
	}

	public String[] toStrings() {
		String[] strings = new String[getHeight()];
		for (int row = 0; row < getHeight(); row++) {
			StringBuffer buf = new StringBuffer();
			for (int col = 0; col < getWidth(); col++) {
				buf.append(data[row][col] + ",");
			}
			strings[row] = buf.toString();
		}
		return strings;
	}

	public boolean contains(String sought) {
		for (int row = 0; row < getHeight(); row++) {
			for (int col = 0; col < getWidth(); col++) {
				if (data[row][col].equals(sought)) {
					return true;
				}
			}
		}
		return false;
	}

	public List<Point> findAllMatching(String soughtFeatureStr) {
		List<Point> results = new ArrayList<Point>();
		for (int row = 0; row < getHeight(); row++) {
			for (int col = 0; col < getWidth(); col++) {
				if (data[row][col].equals(soughtFeatureStr)) {
					results.add(new Point(row, col));
				}
			}
		}
		return results;
	}

}
