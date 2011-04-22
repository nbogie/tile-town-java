package net.abstractplain.tiletown;

import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

import org.apache.log4j.Logger;

public class ModePlaceTile extends ModeBase {

	private static Logger log = Logger.getLogger(ModePlaceTile.class);

	public ModePlaceTile(Game game) {
		super(game);
		setBothMouseListeners(new PlacingMouseListener());
		setKeyListener(new PlacingKeyListener());
		setShortAdvice("Right click to rotate tile, left click to place it");
	}

	@Override
	void enterSpecial() {
		//TODO: discard tile if it cannot be played anywhere on the board.
		gui().setFloatingTile(game().pickNextTile());
		if (gui().getFloatingTile() == null) {
			log.info("NO MORE TILES!  game state should already have been changed");
		} else {
			gui().setFloatingTilePosition(new Point(0, 0));
		}

	}

	@Override
	void leaveSpecial() {
	}

	public class PlacingMouseListener extends MouseInputAdapter {

		@Override
		public void mousePressed(MouseEvent e) {
			Point gridPos = grid().convertToGrid(e.getPoint());

			if (wasRightClick(e)) {
				rotatePieceUnderMouse(e, wasRightClick(e));
			} else {
				if (game().canPlayTileAt(gui().floatingTile(), gridPos)) {
					game().board().placeTile(gui().floatingTile(), gridPos);
					gui().setFloatingTile(null);
					game().changeMode(new ModePlaceMeeple(game()));
				}
			}
			gui().repaint();
		}

		private void rotatePieceUnderMouse(MouseEvent e, boolean clockwise) {

			if (gui().floatingTile() != null) {

				if (clockwise) {
					gui().floatingTile().rotateCW();
				} else {
					gui().floatingTile().rotateCCW();
				}
				updateFloatingTileIsPlayableHereHint(e.getPoint());
			}
			gui().repaint();

		}

		private void updateFloatingTileIsPlayableHereHint(Point p) {
			if (gui().floatingTile() == null) {
				return;
			}
			Point gridPos = grid().convertToGrid(p);
			boolean isLegal = game().canPlayTileAt(gui().floatingTile(), gridPos);
			gui().floatingTile().setIsFloatingAtLegalPlayPosition(isLegal);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			game().addToDebugMap("mouse", e.getPoint().toString());
			gui().displayInfoOnTileUnderMouseIfAny(e.getPoint());
			gui().setFloatingTilePosition(e.getPoint());
			updateFloatingTileIsPlayableHereHint(e.getPoint());
			gui().repaint();
		}

	}

	public class PlacingKeyListener extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent ke) {
			int code = ke.getKeyCode();
			if (code == KeyEvent.VK_ESCAPE) {
			}
		}

	}

}
