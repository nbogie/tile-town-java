package net.abstractplain.tiletown;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TileTest extends TileTownTestCaseBase {

	public void testRotateFeaturePointsCW() {
		TileData td = new TileData("img", 1, "1 2 3 4", TerrainDetails.createTestInstance(), false, false);
		assertEquals(1, td.featureAt(Edge.NORTH));
		assertEquals(2, td.featureAt(Edge.EAST));
		assertEquals(3, td.featureAt(Edge.SOUTH));
		assertEquals(4, td.featureAt(Edge.WEST));

		td.rotateFeaturePointsCW();

		assertEquals(4, td.featureAt(Edge.NORTH));
		assertEquals(1, td.featureAt(Edge.EAST));
		assertEquals(2, td.featureAt(Edge.SOUTH));
		assertEquals(3, td.featureAt(Edge.WEST));
	}

	public void testParse() throws IOException {
		Map<String, TileData> allTileData;

		allTileData = TileData.loadTileData("basic/basic.dat");
		List<String> tileNamesList = new LinkedList<String>();
		tileNamesList.addAll(allTileData.keySet());
		Collections.sort(tileNamesList);
	}

	public void testParseTileNumberFromImageName() {
		assertEquals(1, Tile.parseTileTypeFromImageName("basic/bg001.png"));
		assertEquals(987, Tile.parseTileTypeFromImageName("bg987other.png"));

	}

	public void testFindAllFarmTPsHavingFeature() {
		assertTileHasTheseFarmTPs(Tns.CITY_TWO_NS, new FarmTP[] { FarmTP.EN, FarmTP.ES, FarmTP.WS, FarmTP.WN });
		assertTileHasTheseFarmTPs(Tns.CITY_TWO_NW, new FarmTP[] { FarmTP.EN, FarmTP.ES, FarmTP.SE, FarmTP.SW });
		assertTileHasTheseFarmTPs(Tns.CITY_WE, new FarmTP[] { FarmTP.NE, FarmTP.NW, FarmTP.SE, FarmTP.SW });
		assertTileHasTheseFarmTPs(Tns.CLOISTER, FarmTP.values());
		assertTileHasTheseFarmTPs(Tns.STOPVERT, FarmTP.values());
		assertTileHasTheseFarmTPs(Tns.CITY_WNE, new FarmTP[] { FarmTP.SE, FarmTP.SW });
		assertTileHasTheseFarmTPs(6, new FarmTP[] { FarmTP.SE, FarmTP.SW });
		assertTileHasTheseFarmTPs(7, new FarmTP[] { FarmTP.SE, FarmTP.SW });
		assertTileHasTheseFarmTPs(Tns.CITY_NW, new FarmTP[] { FarmTP.EN, FarmTP.ES, FarmTP.SE, FarmTP.SW });
		assertTileHasTheseFarmTPs(Tns.CITY_NW_AND_ROADBEND, new FarmTP[] { FarmTP.EN, FarmTP.ES, FarmTP.SE, FarmTP.SW });
		FarmTP[] allButNorthEdge = new FarmTP[] { FarmTP.EN, FarmTP.ES, FarmTP.SE, FarmTP.SW, FarmTP.WS, FarmTP.WN };
		assertTileHasTheseFarmTPs(Tns.CITY_N, allButNorthEdge);
		assertTileHasTheseFarmTPs(Tns.CITY_N_AND_HORIZ, allButNorthEdge);
		assertTileHasTheseFarmTPs(Tns.CITY_N_AND_TEE, allButNorthEdge);
		assertTileHasTheseFarmTPs(Tns.CROSS, FarmTP.values());
		assertTileHasTheseFarmTPs(Tns.TEE1, FarmTP.values());
	}

	private void assertTileHasTheseFarmTPs(int tileNumber, FarmTP... tps) {
		{
			Tile t = h().playTestTile(tileNumber, 4, 4);

			Set<FarmTP> expectedTPs = new HashSet<FarmTP>();
			for (FarmTP farmTP : tps) {
				expectedTPs.add(farmTP);
			}

			// run the code under test
			Set<FarmTP> actualTPs = t.findAllFarmTPsHavingFeature(Feature.FARM);

			// assert expectations
			assertEquals(expectedTPs, actualTPs);
		}

	}

	public void testDetermineCitySectionsOnTile() {
		assertTileHasTheseCitySections(Tns.CITY_TWO_NW, new Edge[] { Edge.NORTH }, new Edge[] { Edge.WEST });
		assertTileHasTheseCitySections(Tns.CITY_TWO_NS, new Edge[] { Edge.NORTH }, new Edge[] { Edge.SOUTH });
		assertTileHasTheseCitySections(Tns.CITY_WNE, new Edge[] { Edge.NORTH, Edge.EAST, Edge.WEST });
		assertTileHasTheseCitySections(Tns.CITY_NW, new Edge[] { Edge.NORTH, Edge.WEST });
		assertTileHasTheseCitySections(Tns.CITY_FULL, new Edge[] { Edge.NORTH, Edge.EAST, Edge.SOUTH, Edge.WEST });
		// when there are no city sections on the tile
		assertTileHasTheseCitySections(Tns.VERT1);
	}

	public void testDetermineFarmSectionsOnTile() {
		// when there are no farm sections on the tile
		assertTileHasTheseFarmSections(Tns.CITY_FULL, new FarmTP[] {});

		assertTileHasTheseFarmSections(Tns.CITY_TWO_NS, new FarmTP[] { FarmTP.EN, FarmTP.ES, FarmTP.WS, FarmTP.WN });
		assertTileHasTheseFarmSections(Tns.CITY_TWO_NW, new FarmTP[] { FarmTP.EN, FarmTP.ES, FarmTP.SE, FarmTP.SW });
		assertTileHasTheseFarmSections(Tns.CITY_WE, new FarmTP[] { FarmTP.NE, FarmTP.NW }, new FarmTP[] { FarmTP.SE, FarmTP.SW });
		assertTileHasTheseFarmSections(Tns.CLOISTER, FarmTP.values());
		assertTileHasTheseFarmSections(Tns.STOPVERT, FarmTP.values());
		assertTileHasTheseFarmSections(Tns.CITY_WNE, new FarmTP[] { FarmTP.SE, FarmTP.SW });
		assertTileHasTheseFarmSections(Tns.CITY_WNE_AND_ROAD_S, new FarmTP[] { FarmTP.SW }, new FarmTP[] { FarmTP.SE });
		assertTileHasTheseFarmSections(Tns.CITY_WNE_AND_ROAD_S_AND_PENNANT, new FarmTP[] { FarmTP.SW }, new FarmTP[] { FarmTP.SE });
		assertTileHasTheseFarmSections(Tns.CITY_NW, new FarmTP[] { FarmTP.EN, FarmTP.ES, FarmTP.SE, FarmTP.SW });
		assertTileHasTheseFarmSections(Tns.CITY_NW_AND_ROADBEND, new FarmTP[] { FarmTP.EN, FarmTP.SW },
				new FarmTP[] { FarmTP.SE, FarmTP.ES });
		FarmTP[] allButNorthEdge = new FarmTP[] { FarmTP.EN, FarmTP.ES, FarmTP.SE, FarmTP.SW, FarmTP.WS, FarmTP.WN };
		assertTileHasTheseFarmSections(Tns.CITY_N, allButNorthEdge);
		assertTileHasTheseFarmSections(Tns.CITY_N_AND_HORIZ, new FarmTP[] { FarmTP.WN, FarmTP.EN }, new FarmTP[] { FarmTP.WS, FarmTP.SW,
				FarmTP.SE, FarmTP.ES });
		assertTileHasTheseFarmSections(Tns.CITY_N_AND_TEE, new FarmTP[] { FarmTP.WN, FarmTP.EN }, new FarmTP[] { FarmTP.WS, FarmTP.SW },
				new FarmTP[] { FarmTP.SE, FarmTP.ES });
		assertTileHasTheseFarmSections(Tns.CROSS, new FarmTP[] { FarmTP.WS, FarmTP.SW }, new FarmTP[] { FarmTP.SE, FarmTP.ES },
				new FarmTP[] { FarmTP.WN, FarmTP.NW }, new FarmTP[] { FarmTP.NE, FarmTP.EN });
		assertTileHasTheseFarmSections(Tns.VERT1, new FarmTP[] { FarmTP.NW, FarmTP.WN, FarmTP.WS, FarmTP.SW }, new FarmTP[] { FarmTP.NE,
				FarmTP.EN, FarmTP.ES, FarmTP.SE });
		assertTileHasTheseFarmSections(Tns.TEE1, new FarmTP[] { FarmTP.NW, FarmTP.WN, FarmTP.NE, FarmTP.EN }, new FarmTP[] { FarmTP.SE,
				FarmTP.ES }, new FarmTP[] { FarmTP.WS, FarmTP.SW });
		assertTileHasTheseFarmSections(Tns.WSCURVE1, new FarmTP[] { FarmTP.WN, FarmTP.NW, FarmTP.NE, FarmTP.EN, FarmTP.ES, FarmTP.SE },
				new FarmTP[] { FarmTP.SW, FarmTP.WS });
	}

	public void testFarmSectionTouchesTheseCitySections() {
		assertFarmSectionTouchesTheseCitySections(Tns.CITY_TWO_NS, FarmTP.EN, Edge.NORTH, Edge.SOUTH);
		assertFarmSectionTouchesTheseCitySections(Tns.CITY_TWO_NW, FarmTP.EN, Edge.NORTH, Edge.WEST);
		assertFarmSectionTouchesTheseCitySections(Tns.CITY_WNE, FarmTP.SE, Edge.NORTH);
		assertFarmSectionTouchesTheseCitySections(Tns.CITY_WNE_AND_ROAD_S, FarmTP.SE, Edge.NORTH);
		assertFarmSectionTouchesTheseCitySections(Tns.CITY_WNE_AND_ROAD_S, FarmTP.SW, Edge.NORTH);
		assertFarmSectionTouchesTheseCitySections(Tns.CLOISTER, FarmTP.EN);
		assertFarmSectionTouchesTheseCitySections(Tns.STOPVERT, FarmTP.EN);
		assertFarmSectionTouchesTheseCitySections(Tns.START, FarmTP.WN, Edge.NORTH);
		assertFarmSectionTouchesTheseCitySections(Tns.START, FarmTP.WS);
		assertFarmSectionTouchesTheseCitySections(Tns.CITY_N_AND_TEE, FarmTP.WN, Edge.NORTH);
		assertFarmSectionTouchesTheseCitySections(Tns.CITY_N_AND_TEE, FarmTP.WS);
		assertFarmSectionTouchesTheseCitySections(Tns.CITY_N_AND_TEE, FarmTP.ES);
		assertFarmSectionTouchesTheseCitySections(Tns.CITY_NW_AND_ROADBEND, FarmTP.SW, Edge.NORTH);
		assertFarmSectionTouchesTheseCitySections(Tns.CITY_NW_AND_ROADBEND, FarmTP.SE);
		assertFarmSectionTouchesTheseCitySections(Tns.CITY_N_AND_ROADBEND_ES, FarmTP.SW, Edge.NORTH);
		assertFarmSectionTouchesTheseCitySections(Tns.CITY_N_AND_ROADBEND_ES, FarmTP.SE);
		assertFarmSectionTouchesTheseCitySections(Tns.CITY_N_AND_ROADBEND_WS, FarmTP.SE, Edge.NORTH);
		assertFarmSectionTouchesTheseCitySections(Tns.CITY_N_AND_ROADBEND_WS, FarmTP.SW);
		assertFarmSectionTouchesTheseCitySections(Tns.CROSS, FarmTP.EN);
	}

	private void assertFarmSectionTouchesTheseCitySections(int tileNumber, FarmTP farmTP, Edge... edgesIdentifyingCitySections) {
		Tile t = h().playTestTile(tileNumber, 4, 4);
		Set<CitySection> expectedCitySectionsTouched = new HashSet<CitySection>();
		for (Edge edge : edgesIdentifyingCitySections) {
			CitySection expectedCitySection = t.getCitySectionOnEdgeOrFail(edge);
			expectedCitySectionsTouched.add(expectedCitySection);
		}
		//code under test 
		//  - done! -  (it already ran when tile was placed on board)

		// assert expectations
		FarmSection farmSection = t.getFarmSectionOnFarmTPOrFail(farmTP);
		assertEquals(expectedCitySectionsTouched, farmSection.getCitySectionsTouched());
	}

	private void assertTileHasTheseCitySections(int tileNumber, Edge[]... edgeArrays) {
		{
			Tile t = h().playTestTile(tileNumber, 4, 4);

			HashSet<CitySection> expectedSections = new HashSet<CitySection>();
			// setup expectations
			for (Edge[] edgesForSection : edgeArrays) {
				if (edgesForSection.length != 0) {
					CitySection cs = new CitySection(t, Edge.toSet(edgesForSection), null);
					expectedSections.add(cs);
				}
			}
			// run the code under test
			t.determineCitySectionsOnTile();
			Set<CitySection> actualSections = t.discreteCitySections();

			// assert expectations
			assertEquals(expectedSections, actualSections);
		}

	}

	private void assertTileHasTheseFarmSections(int tileNumber, FarmTP[]... edgeArrays) {
		{
			Tile t = h().playTestTile(tileNumber, 4, 4);
			Set<FarmSection> expectedSections = new HashSet<FarmSection>();
			// setup expectations
			for (FarmTP[] edgesForSection : edgeArrays) {
				if (edgesForSection.length != 0) {
					FarmSection s = new FarmSection(t, FarmTP.toSet(edgesForSection), null, null);
					expectedSections.add(s);
				}
			}
			// run the code under test
			t.determineFarmSectionsOnTile();
			Set<FarmSection> actualSections = t.discreteFarmSections();

			// assert expectations
			assertEquals(expectedSections, actualSections);
		}

	}
}
