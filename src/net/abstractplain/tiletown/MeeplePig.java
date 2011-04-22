package net.abstractplain.tiletown;

import org.apache.commons.lang.NotImplementedException;

public class MeeplePig extends MeepleBase {
	public String title() {
		return "pig";
	}

	
	public MeeplePig(PlayerI owner) {
		super(owner);
	}


	@Override
	void loadImage() {
		throw new NotImplementedException();
		
	}

}
