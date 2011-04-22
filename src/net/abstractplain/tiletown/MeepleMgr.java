package net.abstractplain.tiletown;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Takes care of stuff like deciding the owner(s) of the majority meeples on a feature.
 * 
 * @author n
 * 
 */
public class MeepleMgr {
	private HasMeeplesI _meepleHolder;

	public MeepleMgr(HasMeeplesI meeplesHolder) {
		_meepleHolder = meeplesHolder;
	}
	
	//in expansions this considers only meeples not pigs or traders
	public List<PlayerI> getMajorityOwners() {
		List<PlayerI> bosses = new LinkedList<PlayerI>();
		if (!hasAtLeastOneMeeple()) {
			return bosses;

		}
		Map<PlayerI, Integer> counts = new HashMap<PlayerI, Integer>();
		for (Meeple m : _meepleHolder.meeples()) {
			assert (m != null);
			PlayerI p = m.owner();
			Integer prevCount = counts.get(p);
			if (prevCount == null) {
				prevCount = new Integer(0);
			}
			counts.put(p, new Integer(prevCount.intValue() + 1));

		}
		Integer max = Collections.max(counts.values());
		for (PlayerI p : counts.keySet()) {
			int c = counts.get(p);
			if (c == max) {
				bosses.add(p);
			}
		}
		return bosses;
	}

	public boolean hasAtLeastOneMeeple() {
		return (_meepleHolder.meeples().size() > 0);
	}

}
