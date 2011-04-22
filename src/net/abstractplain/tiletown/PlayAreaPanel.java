package net.abstractplain.tiletown;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

public class PlayAreaPanel extends JPanel {
	private static final long serialVersionUID = -499251703856378050L;

	Game _game = null;

	private Grid _grid;

	private JTextArea _textAreaForLogging;

	private Tile _lastTileUnderMouse;

	private Tile _floatingTile = null;

	private Meeple _floatingMeeple;

	private Font _gameFont;

	private HighlightableFeatureI _highlitFeature;

	public PlayAreaPanel(Game game) {
		super(true);
		this.setMinimumSize(new Dimension(1000, 800));
		_game = game;
		_grid = new Grid(game.getTileSize(), 20, 20);
		_gameFont = new Font("Helvetica", Font.BOLD, 10);
		setKeyBindings();
	}

	/**
	 * http://java.sun.com/docs/books/tutorial/uiswing/misc/keybinding.html#
	 */
	private void setKeyBindings() {
		getInputMap().put(KeyStroke.getKeyStroke("F2"), "togglePaintDebug");
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "quit");
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), "dumpboard");
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F, 0), "finishearly");
		@SuppressWarnings("serial")
		Action togglePaintDebugAction = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				_game.togglePaintDebug();
			}
		};
		getActionMap().put("togglePaintDebug", togglePaintDebugAction);
		@SuppressWarnings("serial")
		Action quitAction = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				_game.quit();
			}
		};
		getActionMap().put("quit", quitAction);

		@SuppressWarnings("serial")
		Action dumpBoardForTestAction = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				_game.dumpBoardAsTestCase();
			}
		};
		getActionMap().put("dumpboard", dumpBoardForTestAction);

		@SuppressWarnings("serial")
		Action finishEarlyAction = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				_game.finishEarly();
			}
		};
		getActionMap().put("finishearly", finishEarlyAction);
	}

	@Override
	public void paint(Graphics gAwt) {
		// BufferedImage bufferedImage = new BufferedImage(getWidth(),
		// getHeight(), ColorSpace.TYPE_RGB);
		// Graphics tileGraphics = (Graphics2D)bufferedImage.getGraphics();
		Graphics2D g = (Graphics2D) gAwt;
		g.setFont(_gameFont);
		g.setColor(Color.white);
		g.fillRect(0, 0, getWidth(), getHeight());
		// tileGraphics.fillRect(0, 0, getWidth(), getHeight());

		_grid.paint(g);

		for (Tile tile : _game.board().tilesPlayed()) {
			tile.paint(g);
			if (_game.shouldPaintDebug()) {
				tile.paintDebug(g);
			}
		}

		if (_highlitFeature != null) {
			_highlitFeature.paintHighlit(grid(), g);
		}

		for (Meeple m : _game.board().meeplesPlayed()) {
			m.paint(g);
		}

		if (floatingTile() != null) {
			floatingTile().paint(g);
			if (_game.shouldPaintDebug()) {
				floatingTile().paintDebug(g);
			}
		}

		if (_floatingMeeple != null) {
			_floatingMeeple.paint(g);
		}

		UIThing debugMapDisplay = new UIThing() {
			@Override
			public void paint(Graphics2D g) {
				g.setColor(Color.black);
				int debugLineCount = 0;
				Map<String, String> debugMap = _game.debugMap();
				for (String key : debugMap.keySet()) {
					String val = debugMap.get(key);
					g.drawString(key + ":" + val, x(), y() + debugLineCount * 30);
					debugLineCount++;
				}
			}
		};

		UIThing tileDetailDisplay = new UIThing() {
			@Override
			public void paint(Graphics2D g) {
				if (floatingTile() != null) {
					new NewTileDataPainter(floatingTile().terrainDetails(), "floating", new Point(x(), y())).Paint(g);
				}
				if (_lastTileUnderMouse != null) {
					new NewTileDataPainter(_lastTileUnderMouse.terrainDetails(), "under mouse", new Point(x() + 100, y())).Paint(g);
				}
			}
		};

		UIThing modeDisplay = new UIThing() {
			@Override
			public void paint(Graphics2D g) {
				g.setColor(Color.black);
				g.drawString("Tiles Remaining: " + _game.tilesRemainingCount(), x(), y());
				g.drawString("Mode: " + _game.mode().getShortAdvice(), x(), y() + 20);
			}
		};

		UIThing playerDisplay = new UIThing() {
			@Override
			public void paint(Graphics2D g) {
				int count = 0;
				for (PlayerI player : _game.players()) {
					if (player.equals(_game.currentPlayer())) {
						g.setColor(Color.black);
						g.fillRect(x() - 2, y() - 2 + (14 * count), 14, 14);
						g.setColor(Color.white);
						g.fillRect(x() - 1, y() - 1 + (14 * count), 12, 12);
					}
					g.setColor(player.color());
					g.fillRect(x(), y() + (14 * count), 10, 10);
					g.setColor(Color.black);
					g.drawString("Player: " + player.name() + "(" + player.score() + ")", x() + 14, y() + 9 + (14 * count));
					count++;
				}
			}
		};

		List<UIThing> uithings = new LinkedList<UIThing>();
		modeDisplay.setPosition(new Point(50, 100));
		playerDisplay.setPosition(new Point(50, 30));
		debugMapDisplay.setPosition(new Point(100, 200));
		tileDetailDisplay.setPosition(new Point(400, 50));
		uithings.add(debugMapDisplay);
		uithings.add(playerDisplay);
		uithings.add(modeDisplay);
		uithings.add(tileDetailDisplay);
		for (UIThing w : uithings) {
			w.paint(g);
		}

		for (DebuggingScoreMarker marker : _game.debuggingScoreMarkers()) {
			marker.paint(g);
		}
	}

	abstract class UIThing {
		Point _position = new Point(0, 0);

		public abstract void paint(Graphics2D g);

		public void setPosition(Point p) {
			_position = p;
		}

		public int x() {
			return _position.x;
		}

		public int y() {
			return _position.y;
		}
	}

	public void setLoggingTextArea(JTextArea textArea) {
		_textAreaForLogging = textArea;
	}

	public JTextArea getTextAreaForLogging() {
		return _textAreaForLogging;
	}

	void setFloatingTilePosition(Point point) {
		if (floatingTile() != null) {
			int halfEdge = floatingTile().getSize() / 2;
			Point tilePositionCentredOnMouse = new Point(point.x - halfEdge, point.y - halfEdge);
			floatingTile().setPixelPos(tilePositionCentredOnMouse);
			Point gridPos = _grid.convertToGrid(point);
			floatingTile().setGridPos(gridPos);
		}
	}

	private Point getMouseTileDetailGridPosition(Point mousePoint) {
		Point[] points = _grid.convertToGridAndDetailGrid(mousePoint);
		Point inTileGridPos = points[1];
		return inTileGridPos;
	}

	private Tile getPlayedTileUnderMouseIfAny(Point mousePoint) {
		Point mouseGridPos = _grid.convertToGrid(mousePoint);
		Tile tileUnderMouse = null;
		for (Tile t : _game.board().tilesPlayed()) {
			if (t.getGridPos().equals(mouseGridPos)) {
				tileUnderMouse = t;
				break;
			}
		}
		return tileUnderMouse;
	}

	public void displayInfoOnTileUnderMouseIfAny(Point mousePoint) {
		Tile tileUnderMouse = getPlayedTileUnderMouseIfAny(mousePoint);
		if (tileUnderMouse != null) {
			Point mouseDetailGridPosition = getMouseTileDetailGridPosition(mousePoint);
			_game.addToDebugMap("mousedetpos", mouseDetailGridPosition.toString());
			_lastTileUnderMouse = tileUnderMouse;
			int feature = tileUnderMouse.terrainDetails().featureAtPosition(mouseDetailGridPosition);
			_game.addToDebugMap("feat", Feature.intToName(feature) + " " + feature);
		}
	}

	public void setFloatingTile(Tile t) {
		_floatingTile = t;
	}

	public Grid grid() {
		return _grid;
	}

	public Tile getFloatingTile() {
		return floatingTile();
	}

	public void setFloatingMeeple(Meeple floatingMeeple) {
		_floatingMeeple = floatingMeeple;
	}

	Tile floatingTile() {
		return _floatingTile;
	}

	public void setHighlight(HighlightableFeatureI thing) {
		_highlitFeature = thing;
	}

}
