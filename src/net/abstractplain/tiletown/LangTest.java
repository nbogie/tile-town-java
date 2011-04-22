package net.abstractplain.tiletown;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

public class LangTest extends TestCase {

	class Foozor {
		private final int _i;

		public Foozor(int i) {
			_i = i;
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Foozor))
				return false;
			Foozor f = (Foozor) o;
			return f._i == _i;
		}

		@Override
		public int hashCode() {
			// very bad don't do this. kills hashing and forces call to equals.
			return 17;
		}
	}

	class MyException extends RuntimeException {
		/**
		 * 
		 */
		private static final long serialVersionUID = -5234641505945554735L;

		public MyException(String string) {
			super(string);
		}

	}

	public void testEqualsAssumptions() {
		Set<Integer> s = new HashSet<Integer>();
		s.add(new Integer(3));
		s.add(new Integer(3));
		s.add(new Integer(4));
		assertEquals(2, s.size());

		Set<Foozor> sf = new HashSet<Foozor>();
		Foozor fa1 = new Foozor(1);
		Foozor fa2 = new Foozor(1);
		Foozor fb1 = new Foozor(2);

		sf.add(fa1);
		sf.add(fa1);
		sf.add(fa2);
		sf.add(fb1);
		assertEquals(2, sf.size());
	}

	public void testHashCodeOrder() {
		int[] nums1 = new int[] { 3, 8, 6 };
		int[] nums2 = new int[] { 8, 3, 6 };
		assertFalse("codes should be different", computeHashFor(nums1) == computeHashFor(nums2));
	}

	private int computeHashFor(int[] nums) {
		int result = 9;
		for (int i : nums) {
			result = 17 * result + i;
		}
		return result;
	}

}
