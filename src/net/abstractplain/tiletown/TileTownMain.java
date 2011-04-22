package net.abstractplain.tiletown;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

public class TileTownMain {

	public static void main(String[] args) throws IOException {

		JFrame frame = new JFrame("tiletown test");
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		Game game = new Game();
		// use one of these to test with a fixed ordering of tiles

		Integer[] cloisterSet = new Integer[] { Tns.VERT1, Tns.VERT1, Tns.VERT1, Tns.VERT1, Tns.STOPVERT, Tns.CLOISTER, Tns.VERT2,
				Tns.VERT2, Tns.VERT2, Tns.WSCURVE1, Tns.WSCURVE2 };
		Integer[] roadLoop = new Integer[] { Tns.WSCURVE1, Tns.WSCURVE1, Tns.WSCURVE1, Tns.WSCURVE2, };
		Integer[] roadMixSmall = new Integer[] { Tns.START, Tns.STOPVERT, Tns.TEE1, Tns.CLOISTER, Tns.VERT1, Tns.TEE1, Tns.CLOISTER,
				Tns.VERT1, Tns.CROSS, Tns.STOPVERT, Tns.VERT2, Tns.CLOISTER, Tns.WSCURVE1 };
		Integer[] misc2 = new Integer[] { Tns.STOPVERT, Tns.TEE1, Tns.VERT1, Tns.TEE1, Tns.VERT1, Tns.CROSS, Tns.STOPVERT };
		Integer[] bugSet = new Integer[] { Tns.TEE1, Tns.TEE1 };
		Integer[] citySmall = new Integer[] { 16, 7, 4, 9, 17, 8, 14 };

		// needed only when debugging, currently
		@SuppressWarnings("unused")
		Integer[][] tileStackConfigs = new Integer[][] { cloisterSet, roadLoop, roadMixSmall, misc2, bugSet, citySmall };

		// game.setUseOnlyRoadPieces();

		// game.setOrderOfTilesToPlay(roadMixSmall);
		// game.setOrderOfTilesToPlay(citySmall);
		game.setup();
		PlayAreaPanel playAreaPanel = new PlayAreaPanel(game);
		game.setGUI(playAreaPanel);
		JTextArea textArea = new JTextArea(3, 80);
		textArea.setMaximumSize(new Dimension(100, 100));
		playAreaPanel.setLoggingTextArea(textArea);
		textArea.setEditable(false);
		java.awt.Dimension ss = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		ss.getWidth();
		JScrollPane scrollPaneForTextArea = new JScrollPane(textArea);
		frame.add(playAreaPanel);
		frame.add(scrollPaneForTextArea, BorderLayout.SOUTH);
		frame.setBounds(new Rectangle(50, 50, (int) ss.getWidth() - 100, (int) ss.getHeight() - 100));
		frame.setVisible(true);
		playAreaPanel.requestFocus();
		game.start();

	}
}
