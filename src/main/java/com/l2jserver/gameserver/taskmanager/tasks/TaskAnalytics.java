package com.l2jserver.gameserver.taskmanager.tasks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

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
	private static final String DELAY = "900000";
	private static final String INTERVAL = "900000";

	private static final String NAME = "analytics";
	private static final String QUERY = "INSERT INTO analytics VALUES (?,?)";

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
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(QUERY))
		{
			ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			ps.setInt(2, getOnlinePlayers());
			ps.execute();
		}
		catch (SQLException e)
		{
			_log.warning("Error saving analytics: " + e.getMessage());
		}
	}

	private int getOnlinePlayers() {
		int count = 0;
		for (L2PcInstance player : L2World.getInstance().getPlayers()) {
			if (player.isInOfflineMode()) {
				continue;
			}
			count++;
		}
		return count;
	}

	@Override
	public void initializate()
	{
		super.initializate();
		TaskManager.addUniqueTask(NAME, TaskTypes.TYPE_FIXED_SHEDULED, DELAY, INTERVAL, "");
	}
}
