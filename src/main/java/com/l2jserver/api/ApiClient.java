package com.l2jserver.api;

import java.net.InetAddress;
import java.net.ProxySelector;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.google.common.base.Charsets;
import com.google.common.net.HttpHeaders;
import com.l2jserver.Config;
import com.l2jserver.util.Hmac;

/**
 * Vae Soli API implementation.
 * @author Melua
 */
public class ApiClient
{
	private static final Logger _log = Logger.getLogger(ApiClient.class.getName());
	private static final SystemDefaultRoutePlanner _planner = new SystemDefaultRoutePlanner(ProxySelector.getDefault());
	private static final RequestConfig _config = RequestConfig.custom().setConnectTimeout(Config.API_TIMEOUT).setSocketTimeout(Config.API_TIMEOUT).build();

	private ApiClient() {
	}

	public static StringResponse auth(InetAddress addr, String login, String password)
	{
		List<NameValuePair> data = new ArrayList<>();
		data.add(new BasicNameValuePair("username", login));
		data.add(new BasicNameValuePair("password", password));
		return invokeApi("/auth", data, new StringResponse(), new BasicHeader(HttpHeaders.X_FORWARDED_FOR, addr.getHostAddress()));
	}

	public static StringResponse date()
	{
		return invokeApi("/date", null, new StringResponse());
	}

	public static StringResponse desc(String account, int slot)
	{
		List<NameValuePair> data = new ArrayList<>();
		data.add(new BasicNameValuePair("login", account));
		data.add(new BasicNameValuePair("slot", String.valueOf(slot)));
		return invokeApi("/desc", data, new StringResponse());
	}

	public static BytesResponse images(String filename)
	{
		List<NameValuePair> data = new ArrayList<>();
		data.add(new BasicNameValuePair("filename", filename));
		return invokeApi("/images", data, new BytesResponse());
	}

	public static StringResponse reward(String account, Integer withdraw)
	{
		List<NameValuePair> data = new ArrayList<>();
		data.add(new BasicNameValuePair("login", account));
		if (withdraw != null) {
			data.add(new BasicNameValuePair("withdraw", String.valueOf(withdraw)));
		}
		return invokeApi("/reward", data, new StringResponse());
	}

	public static VoidResponse stats(int onlinePlayers, int blueEvas, List<String> onlineChars, List<String> offlineChars, List<String> onlineGMs, double cpuLoad)
	{
		List<NameValuePair> data = new ArrayList<>();
		data.add(new BasicNameValuePair("serverId", String.valueOf(Config.SERVER_ID)));
		data.add(new BasicNameValuePair("onlinePlayers", String.valueOf(onlinePlayers)));
		data.add(new BasicNameValuePair("blueEvas", String.valueOf(blueEvas)));
		data.add(new BasicNameValuePair("onlineChars", String.join(";", onlineChars)));
		data.add(new BasicNameValuePair("offlineChars", String.join(";", offlineChars)));
		data.add(new BasicNameValuePair("onlineGMs", String.join(";", onlineGMs)));
		data.add(new BasicNameValuePair("cpu", String.valueOf(cpuLoad)));
		return invokeApi("/stats", data, new VoidResponse());
	}

	private static <T extends ApiResponse<?>> T invokeApi(String endpoint, List<NameValuePair> data, T apiResponse, Header... headers)
	{
		long iat = Instant.now().getEpochSecond();
		long exp = iat + 600;

		try (CloseableHttpClient httpclient = HttpClientBuilder.create().disableCookieManagement()
				.setRoutePlanner(_planner).setDefaultRequestConfig(_config).build()) {

			HttpPost httpPost = new HttpPost(Config.API_BASE_URL + endpoint);

			List<NameValuePair> form = new ArrayList<>();
			// API v3 and greater requires issuedAt and Expires
			form.add(new BasicNameValuePair("iat", String.valueOf(iat)));
			form.add(new BasicNameValuePair("exp", String.valueOf(exp)));
			if (data != null)
			{
				form.addAll(data);
			}

			HttpEntity entity = new UrlEncodedFormEntity(form, Charsets.UTF_8);
			httpPost.setEntity(entity);
			// HMAC signature used to authenticate client
			httpPost.setHeader("X-Api-Signature", Hmac.sha256(Config.API_SECRET, EntityUtils.toByteArray(entity)));
			if (headers != null)
			{
				for (Header header : headers) {
					httpPost.addHeader(header);
				}
			}

			try (CloseableHttpResponse response = httpclient.execute(httpPost)) {
				apiResponse.setStatus(response.getStatusLine().getStatusCode());
				apiResponse.processEntity(response.getEntity());
			}

		} catch (Exception e) {
			_log.log(Level.WARNING, "Exception while invoking API " + endpoint + " !", e);
		}

		return apiResponse;
	}



}
