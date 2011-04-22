package net.abstractplain.tiletown;

import java.awt.Color;

public class Player implements PlayerI {
	private String _name;

	private Color _color;

	private int _score = 0;

	public Player(String name, Color color) {
		_name = name;
		_color = color;
		_score = 0;
	}

	public String name() {
		return _name;
	}

	public Color color() {
		return _color;
	}

	@Override
	public String toString() {
		return name();
	}

	public void incrementScore(int score) {
		_score += score;
	}

	public int score() {
		return _score;
	}
}
