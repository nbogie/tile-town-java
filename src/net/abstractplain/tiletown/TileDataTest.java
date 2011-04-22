package net.abstractplain.tiletown;

import junit.framework.TestCase;

public class TileDataTest extends TestCase {

	public void testMakeTileNameForTileNumber() {
		assertEquals("basic/bg001.png", TileData.makeTileNameForTileNumber(1));
		assertEquals("basic/bg020.png", TileData.makeTileNameForTileNumber(20));
		assertEquals("basic/bg987.png", TileData.makeTileNameForTileNumber(987));
	}

}
