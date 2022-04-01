package com.l2jserver.gameserver;

import com.l2jserver.api.ApiClient;
import com.l2jserver.api.StringResponse;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Votes DAO MySQL implementation.
 * @author Melua
 */
public class VotesManager {

	private VotesManager() {

	}

	public static String available(L2PcInstance player) throws Exception {
		return call(player, null);
	}

	public static String exchange(L2PcInstance player, Integer value) throws Exception {
		return call(player, value);
	}

	private static String call(L2PcInstance player, Integer withdraw) throws Exception {

		StringResponse response = ApiClient.reward(player.getAccountName(), withdraw);

		switch (response.getStatus()) {

		case 200:
			return response.getEntity();

		default:
		case 400:
			throw new Exception("Votes: API did not respond OK.");
		}
	}

}
