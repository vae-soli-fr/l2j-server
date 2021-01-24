package com.l2jserver.gameserver.taskmanager.tasks;

import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import com.google.common.base.Charsets;
import com.l2jserver.Config;
import com.l2jserver.commons.database.pool.impl.ConnectionFactory;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.taskmanager.Task;
import com.l2jserver.gameserver.taskmanager.TaskManager;
import com.l2jserver.gameserver.taskmanager.TaskManager.ExecutedTask;
import com.l2jserver.gameserver.taskmanager.TaskTypes;

/**
 * Analytics task.
 * @author Melua
 */
public class TaskAnalytics extends Task
{
	private static final int BLUE_EVA = 4355;
	private static final String DELAY = "900000";
	private static final String INTERVAL = "900000";

	private static final String NAME = "analytics";
	private static final String ITEM_TOTAL = "SELECT SUM(count) as total FROM items i JOIN characters c ON i.owner_id = c.charId WHERE c.accesslevel = 0 AND i.item_id = ?";

	@Override
	public String getName()
	{
		return NAME;
	}

	@Override
	public void onTimeElapsed(ExecutedTask task)
	{
		saveAnalytics();
	}

	private void saveAnalytics()
	{
		int onlinePlayers = 0;
		int blueEvas = getItemTotal(BLUE_EVA);

		List<String> offlineChars = new LinkedList<>();
		List<String> onlineGMs = new LinkedList<>();
		List<String> onlineChars = new LinkedList<>();

		for (L2PcInstance player : L2World.getInstance().getPlayersSortedBy(Comparator.comparingLong(L2PcInstance::getUptime))) {
			if (player.isInOfflineMode()) {
				offlineChars.add(player.getName());
				continue;
			}

			onlinePlayers++;

			if (player.isGM()) {
				onlineGMs.add(player.getName());
				continue;
			}

			onlineChars.add(player.getName());
		}

		try (CloseableHttpClient httpclient = HttpClientBuilder.create().disableCookieManagement().build()) {

			HttpPost httpPost = new HttpPost("https://api.vae-soli.fr/stats.php");
			List<NameValuePair> nvps = new ArrayList<>();
			nvps.add(new BasicNameValuePair("onlinePlayers", String.valueOf(onlinePlayers)));
			nvps.add(new BasicNameValuePair("blueEvas", String.valueOf(blueEvas)));
			nvps.add(new BasicNameValuePair("onlineChars", String.join(";", onlineChars)));
			nvps.add(new BasicNameValuePair("offlineChars", String.join(";", offlineChars)));
			nvps.add(new BasicNameValuePair("onlineGMs", String.join(";", onlineGMs)));
			nvps.add(new BasicNameValuePair("secret", Config.API_SECRET));
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, Charsets.UTF_8));

			try (CloseableHttpResponse response = httpclient.execute(httpPost)) {

				if (response.getStatusLine().getStatusCode() != 200) {
					_log.info("API: " + response.getStatusLine() + " for stats.");
				}

			}

		} catch (Exception e) {
			_log.log(Level.WARNING, "Exception while calling API stats.", e);
		}
	}

	private int getItemTotal(int itemId) {
		int total = 0;
		try
		{
			try (Connection con = ConnectionFactory.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement(ITEM_TOTAL))
			{
				ps.setInt(1, itemId);
				try (ResultSet rs = ps.executeQuery())
				{
					if (rs.next())
					{
						total = rs.getInt("total");
					}
				}
			}
		}
		catch (Exception e)
		{
			_log.warning("Error while counting item " + itemId + " ! " + e);
		}
		return total;
	}

	@Override
	public void initializate()
	{
		super.initializate();
		TaskManager.addUniqueTask(NAME, TaskTypes.TYPE_FIXED_SHEDULED, DELAY, INTERVAL, "");
	}
}
