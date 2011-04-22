package net.abstractplain.tiletown;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class MatrixTest extends TestCase {

	private Matrix createTestMatrix() {
		List<String> data = new ArrayList<String>();
		data.add("1234");
		data.add("5678");
		data.add("abcd");
		data.add("efgh");
		return Matrix.createFromStringLines(data);
	}

	public void testContains() {

		Matrix m = createTestMatrix();
		assertTrue("should contain. " + m.toString(), m.contains("6"));
		assertFalse("shouldn't contain" + m.toString(), m.contains("z"));
	}

	public void testGetHeightAndWidth() {
		Matrix m = createTestMatrix();
		assertEquals(4, m.getHeight());
		assertEquals(4, m.getWidth());
	}

	public void testRotationCW() {
		Matrix m = createTestMatrix();
		Matrix result = m.rotateCW90();

		List<String> data = new ArrayList<String>();
		data.add("ea51");
		data.add("fb62");
		data.add("gc73");
		data.add("hd84");
		Matrix expected = Matrix.createFromStringLines(data);
		assertEquals(expected.toString(), result.toString());
	}

	public void testToString() {
		Matrix m = createTestMatrix();
		String expected = "Matrix: \n1,2,3,4,\n5,6,7,8,\na,b,c,d,\ne,f,g,h,\n";
		assertEquals(expected, m.toString());
	}

	public void testCreation() {
		Matrix m = createTestMatrix();
		assertEquals("1", m.get(0, 0));
		assertEquals("2", m.get(1, 0));
		assertEquals("3", m.get(2, 0));
		assertEquals("4", m.get(3, 0));

		assertEquals("5", m.get(0, 1));
		assertEquals("6", m.get(1, 1));
		assertEquals("7", m.get(2, 1));
		assertEquals("8", m.get(3, 1));

		assertEquals("a", m.get(0, 2));
		assertEquals("b", m.get(1, 2));
		assertEquals("c", m.get(2, 2));
		assertEquals("d", m.get(3, 2));

		assertEquals("e", m.get(0, 3));
		assertEquals("f", m.get(1, 3));
		assertEquals("g", m.get(2, 3));
		assertEquals("h", m.get(3, 3));
	}
}
