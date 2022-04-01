package com.l2jserver.gameserver;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.api.ApiClient;
import com.l2jserver.api.BytesResponse;

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

		BytesResponse response = ApiClient.images(filename);

		switch (response.getStatus()) {

		case 200:
			return response.getEntity();

		default:
		case 404:
			LOG.info("Images: No data for file '" + filename + "'.");
		}

		return new byte[0];
	}

}
