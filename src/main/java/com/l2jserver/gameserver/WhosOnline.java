package com.l2jserver.gameserver;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

import com.l2jserver.Config;
import com.l2jserver.api.ApiClient;
import com.l2jserver.api.StringResponse;
import com.l2jserver.gameserver.data.xml.impl.ClassListData;
import com.l2jserver.gameserver.enums.Race;
import com.l2jserver.gameserver.instancemanager.GlobalVariablesManager;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * Who's Online board.
 * @author Melua
 */
public final class WhosOnline {
	
	private static final String CBCOLOR_GM = "FF0000";
	private static final String CBCOLOR_OFFLINE = "808080";

	private static String _communityPage;
	private static int _onlineCount = 0;
	private static int _onlineRecord = GlobalVariablesManager.getInstance().getInt("onlineRecord", 0);
	private static long _onlineRecordDate = GlobalVariablesManager.getInstance().getLong("onlineRecordDate", 0L);
	
	private static final SimpleDateFormat _hourFormat = new SimpleDateFormat("HH'h'mm", Locale.FRANCE);

	private static final List<L2PcInstance> _onlinePlayers = new CopyOnWriteArrayList<>();

	public static void showPlayersList(L2PcInstance activeChar)
	{
		NpcHtmlMessage html = new NpcHtmlMessage();
		html.setHtml(_communityPage);
		activeChar.sendPacket(html);
	}

	public static void refreshPlayersList()
	{
		L2PcInstance[] sortedPlayers = L2World.getInstance().getPlayersSortedBy(Comparator.comparingLong(L2PcInstance::getUptime));

		_onlinePlayers.clear();
		_onlineCount = 0;

		for (L2PcInstance player : sortedPlayers)
		{
			addOnlinePlayer(player);
		}

		if (_onlineCount >= _onlineRecord)
		{
			_onlineRecord = _onlineCount;
			_onlineRecordDate = Calendar.getInstance().getTimeInMillis();
			GlobalVariablesManager.getInstance().set("onlineRecord", _onlineRecord);
			GlobalVariablesManager.getInstance().set("onlineRecordDate", _onlineRecordDate);
		}

		_communityPage = generateCommunityPage();
	}

	private static void addOnlinePlayer(L2PcInstance player)
	{
		if (!_onlinePlayers.contains(player))
		{
			_onlinePlayers.add(player);
			if (!player.isInOfflineMode())
			{
				_onlineCount++;
			}
		}
	}
	
	private static String generateCommunityPage()
	{
		NpcHtmlMessage msg = new NpcHtmlMessage();
		msg.setFile(null, "data/html/whosonline.htm");

		// Uptime
		msg.replace("%started%", _hourFormat.format(GameServer.dateTimeServerStarted.getTime()));
		
		// Rates
		msg.replace("%rate_xp%", Config.RATE_XP);
		msg.replace("%rate_sp%", Config.RATE_SP);
		msg.replace("%rate_party%", Config.RATE_PARTY_XP);
		msg.replace("%rate_drop%", Config.RATE_DEATH_DROP_CHANCE_MULTIPLIER);
		msg.replace("%rate_spoil%", Config.RATE_CORPSE_DROP_CHANCE_MULTIPLIER);
		msg.replace("%rate_adena%", Config.RATE_DROP_CHANCE_MULTIPLIER.getOrDefault(57, 1F));
		
		// Date
		StringResponse response = ApiClient.date();
		msg.replace("%roleplay_date%", response.getEntity() != null ? response.getEntity() : "");
		
		// Caption
		msg.replace("%color_human%", getColor(Race.HUMAN));
		msg.replace("%color_elf%", getColor(Race.ELF));
		msg.replace("%color_darkelf%", getColor(Race.DARK_ELF));
		msg.replace("%color_dwarf%", getColor(Race.DWARF));
		msg.replace("%color_orc%", getColor(Race.ORC));
		msg.replace("%color_kamael%", getColor(Race.KAMAEL));
		msg.replace("%color_gm%", CBCOLOR_GM);
		msg.replace("%color_offline%", CBCOLOR_OFFLINE);
		
		// Count
		msg.replace("%online_count%", _onlineCount);
		msg.replace("%plural%", _onlineCount > 1 ? "s" : "");
		
		// Players		
		StringBuilder table = new StringBuilder();
		for (L2PcInstance player : _onlinePlayers)
		{
			NpcHtmlMessage row = new NpcHtmlMessage();
			row.setFile(null, "data/html/whosonlinerow.htm");

			String color;
			if (player.isInOfflineMode())
			{
				color = CBCOLOR_OFFLINE;
			} 
			else if (player.isGM())
			{
				color = CBCOLOR_GM;
			}
			else
			{
				color = getColor(player.getRace());
			}
			
			row.replace("%player_color%", color);
			row.replace("%player_name%", player.getName());
			row.replace("%player_class%", getClass(player));
			row.replace("%player_clan%", getClan(player));
			table.append(row.getHtml());
		}
		
		msg.replace("%players%", table.toString());
		
		return msg.getHtml();
	}

	private static String getColor(Race race)
	{
		switch (race)
		{
		case DARK_ELF:
			return "669999";
		case DWARF:
			return "CC9966";
		case ELF:
			return "FFCC99";
		case HUMAN:
			return "CC6666";
		case KAMAEL:
			return "FFBFFF";
		case ORC:
			return "99CC99";
		default:
			return "FFFFFF";
		}
	}
	
	private static String getClass(L2PcInstance player)
	{
		if (player.isGM())
		{
			return "";
		}
		return ClassListData.getInstance().getClass(player.getClassId()).getClientCode();
	}
	
	private static String getClan(L2PcInstance player)
	{
		if (player.getClan() == null) // || player.isGM())
		{
			return "";
		}
		return player.getClan().getName();
	}

}
