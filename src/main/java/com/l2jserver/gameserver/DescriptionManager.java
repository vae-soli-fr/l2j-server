package com.l2jserver.gameserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.api.ApiClient;
import com.l2jserver.api.StringResponse;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Description API implementation.
 * @author Melua
 */
public class DescriptionManager {

	private static final Logger LOG = LoggerFactory.getLogger(DescriptionManager.class);

	public static void load(L2PcInstance player) {

		StringResponse response = ApiClient.desc(player.getAccountName(), player.getCharSlot());

		switch (response.getStatus()) {

		case 200:
			player.setDescription(response.getEntity());
			break;

		default:
		case 204:
			LOG.info("PHPBBDesc: No description for login '" + player.getAccountName() + "' and slot '" + player.getCharSlot() + "'.");
		}

	}

}
