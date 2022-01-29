package com.l2jserver.gameserver;

import java.net.ProxySelector;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.Config;
import com.l2jserver.util.Hmac;

/**
 * Images API implementation.
 * @author Melua
 */
public class ImagesManager {

	private static final Logger LOG = LoggerFactory.getLogger(ImagesManager.class);

	private static final Map<String, byte[]> IMAGES_CACHE = new ConcurrentHashMap<>();
	private static final int MAX_BYTES = 32_896;

	public static byte[] getBytes(String filename) {
		if (!IMAGES_CACHE.containsKey(filename)) {
			cache(filename, load(filename));
		}
		return IMAGES_CACHE.get(filename);
	}

	public static void clearCache() {
		IMAGES_CACHE.clear();
	}

	private static void cache(String filename, byte[] data) {
		if (data.length > MAX_BYTES) {
			IMAGES_CACHE.put(filename, new byte[0]);
			return;
		}
		IMAGES_CACHE.put(filename, data);
	}

	private static byte[] load(String filename) {
		try (CloseableHttpClient httpclient = HttpClientBuilder.create().disableCookieManagement()
				.setRoutePlanner(new SystemDefaultRoutePlanner(ProxySelector.getDefault())).build()) {

			HttpPost httpPost = new HttpPost(Config.API_BASE_URL + "/images.php");
			List<NameValuePair> nvps = new ArrayList<>();
			nvps.add(new BasicNameValuePair("filename", filename));
			HttpEntity entity = new UrlEncodedFormEntity(nvps);
			httpPost.setEntity(entity);
			httpPost.setHeader("X-Api-Signature", Hmac.sha256(Config.API_SECRET, EntityUtils.toByteArray(entity)));

			try (CloseableHttpResponse response = httpclient.execute(httpPost)) {

				switch (response.getStatusLine().getStatusCode()) {

				case 200:
					return EntityUtils.toByteArray(response.getEntity());

				default:
				case 404:
					LOG.info("Images: No data for file '" + filename + "'.");
				}

			}

		} catch (Exception e) {
			LOG.error("Failed loading image. {}", e);
		}
		return new byte[0];
	}

}
