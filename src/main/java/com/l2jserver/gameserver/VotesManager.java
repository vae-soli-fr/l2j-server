package com.l2jserver.gameserver;

import java.net.ProxySelector;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.l2jserver.Config;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.util.Hmac;

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

		try (CloseableHttpClient httpclient = HttpClientBuilder.create().disableCookieManagement()
				.setRoutePlanner(new SystemDefaultRoutePlanner(ProxySelector.getDefault())).build()) {

			HttpPost httpPost = new HttpPost(Config.API_BASE_URL + "/reward.php");
			List<NameValuePair> nvps = new ArrayList<>();
			nvps.add(new BasicNameValuePair("login", player.getAccountName()));
			if (withdraw != null) {
				nvps.add(new BasicNameValuePair("withdraw", String.valueOf(withdraw)));
			}
			HttpEntity entity = new UrlEncodedFormEntity(nvps);
			httpPost.setEntity(entity);
			httpPost.setHeader("X-Api-Signature", Hmac.sha256(Config.API_SECRET, EntityUtils.toByteArray(entity)));

			try (CloseableHttpResponse response = httpclient.execute(httpPost)) {

				switch (response.getStatusLine().getStatusCode()) {

				case 200:
					return EntityUtils.toString(response.getEntity());

				default:
				case 400:
					throw new Exception("Votes: API did not respond OK.");
				}
			}

		}
	}

}
