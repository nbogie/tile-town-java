package net.abstractplain.tiletown;

import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MeepleNormal extends MeepleBase {

	public MeepleNormal(PlayerI owner) {
		super(owner);
	}

	public String title() {
		return "meeple";
	}

	// TODO: move out from meeple base. this is gui-specific
	public void loadImage() {
		String imageName = null;
		try {
			Color c = owner().color();
			Map<Color, String> imageNames = new HashMap<Color, String>();
			imageNames.put(Color.red, "common/meeple-red-small.png");
			imageNames.put(Color.yellow, "common/meeple-yellow-small.png");
			imageNames.put(Color.blue, "common/meeple-blue-small.png");
			imageNames.put(Color.green, "common/meeple-green-small.png");
			imageNames.put(Color.black, "common/meeple-black-small.png");
			imageName = imageNames.get(c);
			if (imageName == null) {
				throw new IllegalStateException("unsupported player color: " + c);
			}
			_image = FileHelp.loadImage(imageName);
		} catch (IOException e) {
			throw new RuntimeException("missing image: " + imageName, e);
		}
		assert(_image != null);
	}
}
