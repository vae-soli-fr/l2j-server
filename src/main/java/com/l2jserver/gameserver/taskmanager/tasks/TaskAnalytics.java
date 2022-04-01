package com.l2jserver.gameserver.taskmanager.tasks;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.l2jserver.api.ApiClient;
import com.l2jserver.api.VoidResponse;
import com.l2jserver.commons.database.pool.impl.ConnectionFactory;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.taskmanager.Task;
import com.l2jserver.gameserver.taskmanager.TaskManager;
import com.l2jserver.gameserver.taskmanager.TaskManager.ExecutedTask;
import com.l2jserver.gameserver.taskmanager.TaskTypes;
import com.sun.management.UnixOperatingSystemMXBean;

/**
 * Analytics task.
 * @author Melua
 */
public class TaskAnalytics extends Task
{
	private final OperatingSystemMXBean mxBean = ManagementFactory.getOperatingSystemMXBean();

	private static final int BLUE_EVA = 4355;
	private static final String DELAY = "900000";
	private static final String INTERVAL = "900000";

	private static final String NAME = "analytics";
	private static final String ITEM_TOTAL = "SELECT ("
			+ "SELECT SUM(count) FROM items i WHERE i.item_id = ?"
			+ ") - ("
			+ "SELECT SUM(count) FROM items i JOIN characters c ON i.owner_id = c.charId WHERE c.accesslevel > ? AND i.item_id = ?"
			+ ") as total FROM DUAL";

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
		int blueEvas = getItemTotal(BLUE_EVA, 0);

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

		VoidResponse response = ApiClient.stats(onlinePlayers, blueEvas, onlineChars, offlineChars, onlineGMs, getCpuLoad());

		if (response.getStatus() != 200) {
			_log.info("API: " + response.getStatus() + " for stats.");
		}
	}

	/**
	 * Calculate the total number of items from everywhere
	 * deducting items from owners above the access level
	 * @param itemId
	 * @param accessLevel
	 * @return
	 */
	private int getItemTotal(int itemId, int accessLevel) {
		int total = 0;
		try
		{
			try (Connection con = ConnectionFactory.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement(ITEM_TOTAL))
			{
				ps.setInt(1, itemId);
				ps.setInt(2, accessLevel);
				ps.setInt(3, itemId);
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

	private double getCpuLoad() {
		if (mxBean instanceof UnixOperatingSystemMXBean) {
			return ((UnixOperatingSystemMXBean) mxBean).getProcessCpuLoad();
		}
		return -1;
	}

	@Override
	public void initializate()
	{
		super.initializate();
		TaskManager.addUniqueTask(NAME, TaskTypes.TYPE_FIXED_SHEDULED, DELAY, INTERVAL, "");
	}
}
