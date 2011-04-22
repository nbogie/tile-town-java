package net.abstractplain.tiletown;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class TileDisplayPanel extends JPanel {

	private static final long serialVersionUID = -2028505116963217700L;

	@Override
	public void paint(Graphics gAwt) {
		Graphics2D g = (Graphics2D) gAwt;
		g.setColor(Color.white);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		g.drawString("TileTown Test", 1000, 1000);

		Map<String, TileData> allTileData;
		try {
			allTileData = TileData.loadTileData("basic/basic.dat");

			List<String> tileNamesList = new LinkedList<String>();
			tileNamesList.addAll(allTileData.keySet());
			Collections.sort(tileNamesList);

			int i = 0;
			for (String tileName : tileNamesList) {

				BufferedImage img = FileHelp.loadImage(tileName);
				int x = 100 * (i % 10);
				int y = 100 * (i / 10);
				g.drawImage(img, x, y, null);
				g.setColor(Color.black);
				Font font = new Font("Helvetica", Font.BOLD, 10);
				g.setFont(font);
				g.drawString(tileName, x + 10, (y + 25));

				TileData tileData = allTileData.get(tileName);
				Tile tile = new Tile(tileName, tileData);
				tile.determineRoadSectionsOnTile();
				g.drawString(tileData.getNumberOfOccurrencesInSet() + " occs", x + 10, (y + 50));
				g.drawString("Rds: " + tile.discreteRoadSections().size() + "", x + 10, (y + 70));
				i++;
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public static void main(String[] args) throws IOException {

		JFrame frame = new JFrame("tiles display");
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setBounds(new Rectangle(100, 100, 2000, 2000));
		JPanel tileDisplayPanel = new TileDisplayPanel();
		frame.add(tileDisplayPanel);
		frame.setVisible(true);
	}

}
