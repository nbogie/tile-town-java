package net.abstractplain.tiletown;

import java.awt.event.InputEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public abstract class ModeBase implements InteractionMode {
	private Game _game;

	private MouseMotionListener _mouseMotionListener;

	private MouseListener _mouseListener;

	private KeyListener _keyListener;

	private String _shortAdvice;

	public PlayAreaPanel gui() {
		return game().playAreaPanel();
	}

	public ModeBase(Game game) {
		setGame(game);
		setShortAdvice("");
	}

	public Game game() {
		return _game;
	}

	// override these, not enter() and leave()
	abstract void enterSpecial();

	abstract void leaveSpecial();

	public void enter() {
		if (getMouseListener() != null) {
			gui().addMouseListener(getMouseListener());
		}
		if (getMouseMotionListener() != null) {
			gui().addMouseMotionListener(getMouseMotionListener());
		}
		if (getKeyListener() != null) {
			gui().addKeyListener(getKeyListener());
		}
		enterSpecial();
	}

	public void leave() {
		if (getMouseListener() != null) {
			gui().removeMouseListener(getMouseListener());
		}
		if (getMouseMotionListener() != null) {
			gui().removeMouseMotionListener(getMouseMotionListener());
		}
		if (getKeyListener() != null) {
			gui().removeKeyListener(getKeyListener());
		}
		leaveSpecial();
	}

	public Grid grid() {
		return game().playAreaPanel().grid();
	}

	private KeyListener getKeyListener() {
		return _keyListener;
	}

	protected void setBothMouseListeners(Object mouseListener) {
		setMouseListener((MouseListener) mouseListener);
		setMouseMotionListener((MouseMotionListener) mouseListener);
	}

	public void setMouseMotionListener(MouseMotionListener mml) {
		_mouseMotionListener = mml;
	}

	public void setMouseListener(MouseListener ml) {
		_mouseListener = ml;
	}

	public void setKeyListener(KeyListener kl) {
		_keyListener = kl;
	}

	private MouseMotionListener getMouseMotionListener() {
		return _mouseMotionListener;
	}

	private MouseListener getMouseListener() {
		return _mouseListener;
	}

	public String getShortAdvice() {
		return _shortAdvice;
	}

	protected void setShortAdvice(String string) {
		_shortAdvice = string;
	}

	public boolean wasRightClick(MouseEvent e) {
		return (e.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

	private void setGame(Game game) {
		_game = game;
	}
}
