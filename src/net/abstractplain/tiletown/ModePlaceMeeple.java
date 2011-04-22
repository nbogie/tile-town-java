package net.abstractplain.tiletown;

import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

public class ModePlaceMeeple extends ModeBase {

	private Meeple _selectedMeeple;

	public ModePlaceMeeple(Game game) {
		super(game);
		setBothMouseListeners(new MeeplePlacingMouseListener());
		setKeyListener(new MeeplePlacingKeyListener());
		setShortAdvice("Place a meeple (click with no meeple selected to pass)");
	}

	@Override
	void enterSpecial() {
		setSelectedMeeple(null);
	}

	private void setSelectedMeeple(Meeple selected) {
		_selectedMeeple = selected;
	}

	@Override
	void leaveSpecial() {
	}

	Meeple selectedMeeple() {
		return _selectedMeeple;
	}

	private void finishThisMode() {
		gui().setFloatingMeeple(null);
		gui().setHighlight(null);
		gui().repaint();
		game().scoreLastMove();
		game().finishTurn();
	}

	private void placeMeeple(Meeple selectedMeeple, Point point) {
		game().placeMeeple(selectedMeeple, point);
	}

	public class MeeplePlacingMouseListener extends MouseInputAdapter {

		@Override
		public void mousePressed(MouseEvent e) {
			gui().setHighlight(null);

			if (wasRightClick(e)) {
				if (selectedMeeple() != null) {
					setSelectedMeeple(null);
				} else {
					setSelectedMeeple(game().getNextNormalMeepleForCurrentPlayer());
				}
				gui().setFloatingMeeple(selectedMeeple());
			} else {
				Meeple m = selectedMeeple();
				if (m != null) {
					if (game().canPlaceMeepleAtPosition(m, e.getPoint())) {
						placeMeeple(selectedMeeple(), e.getPoint());
						finishThisMode();
					} else {

					}
				} else {
					finishThisMode();
				}

			}

			gui().repaint();
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			gui().setHighlight(null);

			game().addToDebugMap("mouse", e.getPoint().toString());

			if (selectedMeeple() != null) {
				selectedMeeple().setFloatingPosition(e.getPoint());
				if (game().canPlaceMeepleAtPosition(selectedMeeple(), e.getPoint())) {
					selectedMeeple().setPlaceable(true);
				} else {
					selectedMeeple().setPlaceable(false);
				}
			} else {
			}

			gui().repaint();
		}

	}

	// TODO: what happens if we handle a key event and mouse event nearly
	// simultaneously? How many threads are there? And can we screw up the game
	// logic?
	public class MeeplePlacingKeyListener extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent ke) {
			int code = ke.getKeyCode();
			if (code == KeyEvent.VK_SPACE) {
				finishThisMode();
			}
		}

	}
}
