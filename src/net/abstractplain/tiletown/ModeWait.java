package net.abstractplain.tiletown;

import org.apache.log4j.Logger;

public class ModeWait extends ModeBase {
	private static Logger log = Logger.getLogger(ModeWait.class);

	public ModeWait(Game game) {
		super(game);
	}

	@Override
	void enterSpecial() {
		log.debug("show 'tiles loading -- please wait' dialog here (or splash)");
	}

	@Override
	void leaveSpecial() {
	}
}
