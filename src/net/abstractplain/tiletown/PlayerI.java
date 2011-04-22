package net.abstractplain.tiletown;

import java.awt.Color;

public interface PlayerI {
	public String name();

	public Color color();

	public void incrementScore(int score);

	public int score();

}
