package net.abstractplain.tiletown;

import java.awt.Point;

import org.junit.Test;

public class CitySectionTest extends TileTownTestCaseBase {

	@Test
	public void testContainsTixelPoint() {
		Tile t = h().createTestTile(Tns.CITY_FULL);
		t.computeFeatures();
		CitySection cs = h().getSoleCitySection(t);
		assertTrue(cs.containsTixelPoint(new Point(2, 2)));
		assertTrue(cs.containsTixelPoint(new Point(2, 7)));
		assertTrue(cs.containsTixelPoint(new Point(7, 2)));
		assertTrue(cs.containsTixelPoint(new Point(7, 7)));
	}

}
