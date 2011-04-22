package net.abstractplain.tiletown;

import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

public class EdgeTest extends TestCase {

	public void testApplyRotation() {
		applyRotationAndTest(1, new String[] { "n", "e", "s", "w" }, Edge.otherESWN());
		applyRotationAndTest(2, new String[] { "n", "e", "s", "w" }, Edge.otherSWNE());
		applyRotationAndTest(3, new String[] { "n", "e", "s", "w" }, Edge.otherWNES());
		applyRotationAndTest(4, new String[] { "n", "e", "s", "w" }, Edge.nesw());
		applyRotationAndTest(5, new String[] { "n", "e", "s", "w" }, Edge.otherESWN());
		applyRotationAndTest(-4, new String[] { "n", "e", "s", "w" }, Edge.nesw());
		applyRotationAndTest(-3, new String[] { "n", "e", "s", "w" }, Edge.otherESWN());
		applyRotationAndTest(-2, new String[] { "n", "e", "s", "w" }, Edge.otherSWNE());
		applyRotationAndTest(-1, new String[] { "n", "e", "s", "w" }, Edge.otherWNES());
		applyRotationAndTest(-5, new String[] { "n", "e", "s", "w" }, Edge.otherWNES());
	}

	private void applyRotationAndTest(int rotation, String[] inputs, Object[] expectedEdges) {
		List<Edge> actualOutputs = new LinkedList<Edge>();
		for (String input : inputs) {
			actualOutputs.add(Edge.parseCompassChar(input).applyRotation(rotation));
		}
		Object[] actualEdges = actualOutputs.toArray();
		for (int i = 0; i < actualEdges.length; i++) {
			assertEquals("input rotation was " + rotation + " on input " + inputs[i], expectedEdges[i], actualEdges[i]);
		}
	}
}
