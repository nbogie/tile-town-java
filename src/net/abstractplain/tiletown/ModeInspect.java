package net.abstractplain.tiletown;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

import org.apache.log4j.Logger;

public class ModeInspect extends ModeBase {
	private static Logger log = Logger.getLogger(ModeInspect.class);

	public ModeInspect(Game game) {
		super(game);
		setBothMouseListeners(new InspectingMouseListener());
		setKeyListener(new InspectingKeyListener());
		setShortAdvice("Hover to inspect, click to get new tile");
	}

	public class InspectingMouseListener extends MouseInputAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			if (game().tilesRemainingCount() != 0) {
				game().changeMode(new ModePlaceTile(game()));
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			game().addToDebugMap("mouse", e.getPoint().toString());
			gui().displayInfoOnTileUnderMouseIfAny(e.getPoint());
			gui().repaint();
		}

	}

	public class InspectingKeyListener extends KeyAdapter {

		@Override
		public void keyPressed(KeyEvent ke) {
			int code = ke.getKeyCode();
			if (code == KeyEvent.VK_ESCAPE) {
			}
		}
	}

	@Override
	void enterSpecial() {
		log.debug("you are now in inspect mode only!");
	}

	@Override
	void leaveSpecial() {
	}
}
