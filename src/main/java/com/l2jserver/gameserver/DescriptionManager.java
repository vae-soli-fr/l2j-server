package com.l2jserver.gameserver;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.Config;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.util.Hmac;

/**
 * Description API implementation.
 * @author Melua
 */
public class DescriptionManager {

	private static final Logger LOG = LoggerFactory.getLogger(DescriptionManager.class);

	public static void load(L2PcInstance player) {
		try (CloseableHttpClient httpclient = HttpClientBuilder.create().disableCookieManagement().build()) {

			HttpPost httpPost = new HttpPost(Config.API_BASE_URL + "/desc.php");
			List<NameValuePair> nvps = new ArrayList<>();
			nvps.add(new BasicNameValuePair("login", player.getAccountName()));
			nvps.add(new BasicNameValuePair("slot", String.valueOf(player.getCharSlot())));
			HttpEntity entity = new UrlEncodedFormEntity(nvps);
			httpPost.setEntity(entity);
			httpPost.setHeader("X-Api-Signature", Hmac.sha256(Config.API_SECRET, EntityUtils.toByteArray(entity)));

			try (CloseableHttpResponse response = httpclient.execute(httpPost)) {

				String description = null;

				switch (response.getStatusLine().getStatusCode()) {

				case 200:
					description = EntityUtils.toString(response.getEntity());
					break;

				default:
				case 204:
					LOG.info("PHPBBDesc: No description for login '" + player.getAccountName() + "' and slot '" + player.getCharSlot() + "'.");
				}

				player.setDescription(description);

			}

		} catch (Exception e) {
			LOG.error("Failed loading description. {}", e);
		}
	}

}
