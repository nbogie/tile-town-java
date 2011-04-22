package net.abstractplain.tiletown;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.LinkedList;

public class ModeGetPlayers extends ModeBase {

	private LinkedList<PlayerI> _playersList;

	public ModeGetPlayers(Game game) {
		super(game);
		// setBothMouseListeners(new MyMouseListener());
		setKeyListener(new MyKeyListener());
		setShortAdvice("Configure players for game.  Press Space to Start");
		_playersList = new LinkedList<PlayerI>();
	}

	@Override
	void enterSpecial() {
	}

	@Override
	void leaveSpecial() {
	}

	private void finishThisMode() {
		game().setPlayersAndStartTurns(_playersList);
		gui().repaint();
	}

	// TODO: what happens if we handle a key event and mouse event nearly
	// simultaneously? How many threads are there? And can we screw up the game
	// logic?
	public class MyKeyListener extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent ke) {
			int code = ke.getKeyCode();
			if (code == KeyEvent.VK_SPACE) {
				_playersList.add(new Player("Vizzini", Color.blue));
				_playersList.add(new Player("Fezzik", Color.yellow));
				finishThisMode();
			}
		}

	}
}
