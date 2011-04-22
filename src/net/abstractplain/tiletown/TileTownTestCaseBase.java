package net.abstractplain.tiletown;

import junit.framework.TestCase;

public abstract class TileTownTestCaseBase extends TestCase {

	private BoardTestHelper _boardTestHelper;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		_boardTestHelper = new BoardTestHelper();
	}

	public Board board() {
		return h().board();
	}

	public BoardTestHelper h() {
		return _boardTestHelper;
	}
}
