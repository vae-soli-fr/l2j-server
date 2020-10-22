package com.l2jserver.gameserver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.l2jserver.Config;
import com.l2jserver.gameserver.instancemanager.GlobalVariablesManager;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.base.ClassId;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * Who's Online board.
 * @author Melua
 */
public final class WhosOnline {

	private static int NAME_PER_ROW_COMMUNITYBOARD = 3;
	private static String CBCOLOR_HUMAN = "CC6666";
	private static String CBCOLOR_ELF = "FFCC99";
	private static String CBCOLOR_DARKELF = "669999";
	private static String CBCOLOR_DWARF = "CC9966";
	private static String CBCOLOR_ORC = "99CC99";
	private static String CBCOLOR_KAMAEL = "FFBFFF";
	private static String CBCOLOR_GM = "FF0000";
	private static String CBCOLOR_OFFLINE = "808080";

	private static int _onlineCount = 0;
	private static int _onlineRecord = GlobalVariablesManager.getInstance().getInt("onlineRecord", 0);
	private static long _onlineRecordDate = GlobalVariablesManager.getInstance().getLong("onlineRecordDate", 0L);

	private static List<L2PcInstance> _onlinePlayers = new CopyOnWriteArrayList<>();
	private static Map<String, String> _communityPages = new ConcurrentHashMap<>();

	public static void showPlayersList(L2PcInstance activeChar) {
		NpcHtmlMessage html = new NpcHtmlMessage();
		html.setHtml(_communityPages.get("default"));
		activeChar.sendPacket(html);
	}

	public static void refreshPlayersList() {
		List<L2PcInstance> sortedPlayers = new ArrayList<>();
		sortedPlayers.addAll(L2World.getInstance().getPlayers());
		Collections.sort(sortedPlayers, new Comparator<L2PcInstance>() {
			@Override
			public int compare(L2PcInstance p1, L2PcInstance p2) {
				return p1.getName().compareToIgnoreCase(p2.getName());
			}
		});

		_onlinePlayers.clear();
		_onlineCount = 0;

		for (L2PcInstance player : sortedPlayers) {
			addOnlinePlayer(player);
		}

		if (_onlineCount >= _onlineRecord) {
			_onlineRecord = _onlineCount;
			_onlineRecordDate = Calendar.getInstance().getTimeInMillis();
			GlobalVariablesManager.getInstance().set("onlineRecord", _onlineRecord);
			GlobalVariablesManager.getInstance().set("onlineRecordDate", _onlineRecordDate);
		}

		_communityPages.clear();
		writeCommunityPages();
	}

	private static void addOnlinePlayer(L2PcInstance player) {
		if (!_onlinePlayers.contains(player)) {
			_onlinePlayers.add(player);
			if (!player.isInOfflineMode()) {
				_onlineCount++;
			}
		}
	}

	private static void writeCommunityPages() {
		final StringBuilder htmlCode = new StringBuilder(2000);
		final String tdClose = "</td>";
		final String tdOpen = "<td align=left valign=top fixwidth=70>";
		final String trClose = "</tr>";
		final String trOpen = "<tr>";
		final String colSpacer = "<td FIXWIDTH=15></td>";
		final SimpleDateFormat formater = new SimpleDateFormat("EEEE dd MMMM HH:mm", Locale.FRANCE);

		htmlCode.setLength(0);
		htmlCode.append("<html><title>Communauté</title><body><br>" + "Serveur redémarré " + formater.format(GameServer.dateTimeServerStarted.getTime())
				+ "<table>" + trOpen + tdOpen
				+ "XP x" + Config.RATE_XP + tdClose + colSpacer + tdOpen + "SP x" + Config.RATE_SP + tdClose
				+ colSpacer + tdOpen + "Party x" + Config.RATE_PARTY_XP + tdClose + trClose + trOpen
				+ tdOpen + "Drop x" + Config.RATE_DEATH_DROP_CHANCE_MULTIPLIER + tdClose + colSpacer + tdOpen + "Spoil x"
				+ Config.RATE_CORPSE_DROP_CHANCE_MULTIPLIER + tdClose + colSpacer + tdOpen + "Adena x" + Config.RATE_DROP_CHANCE_MULTIPLIER.getOrDefault(57, 1F) + tdClose
				+ trClose + "</table>" + "<br>Record de " + _onlineRecord + " joueurs " + formater.format(_onlineRecordDate)
				+ "<table>" + trOpen + "<td><img src=\"sek.cbui355\" width=600 height=1><br></td>" + trClose + trOpen + tdOpen
				+ _onlineCount + " joueurs en ligne " + "(<font color=\"" + CBCOLOR_HUMAN + "\">Humain</font>, " + "<font color=\""
				+ CBCOLOR_ELF + "\">Elfe</font>, " + "<font color=\"" + CBCOLOR_DARKELF + "\">Sombre</font>, " + "<br1><font color=\"" + CBCOLOR_DWARF
				+ "\">Nain</font>, " + "<font color=\"" + CBCOLOR_ORC + "\">Orc</font>, " + "<font color=\"" + CBCOLOR_KAMAEL + "\">Kamael</font>, "
				+ "<font color=\"" + CBCOLOR_GM + "\">GM</font>, " + "<font color=\"" + CBCOLOR_OFFLINE + "\">Offline</font>)</td>" + trClose + "</table>");

		int cell = 0;
		htmlCode.append("<table border=0><tr><td><table border=0>");

		for (L2PcInstance player : _onlinePlayers) {
			cell++;

			if (cell == 1) {
				htmlCode.append(trOpen);
			}

			htmlCode.append("<td align=left valign=top fixwidth=70>");

			if (player.isGM()) {
				htmlCode.append("<font color=\"" + CBCOLOR_GM + "\">" + player.getName() + "</font>");
			} else if (player.isInOfflineMode()) {
				htmlCode.append("<font color=\"" + CBCOLOR_OFFLINE + "\">" + player.getName() + "</font>");
			} else {
				switch (player.getRace()) {
				case DARK_ELF:
					htmlCode.append("<font color=\"").append(CBCOLOR_DARKELF).append("\">").append(player.getName()).append("<br1>(").append(getClassAbbreviation(player.getClassId())).append(")").append("</font>");
					break;
				case DWARF:
					htmlCode.append("<font color=\"").append(CBCOLOR_DWARF).append("\">").append(player.getName()).append("<br1>(").append(getClassAbbreviation(player.getClassId())).append(")").append("</font>");
					break;
				case ELF:
					htmlCode.append("<font color=\"").append(CBCOLOR_ELF).append("\">").append(player.getName()).append("<br1>(").append(getClassAbbreviation(player.getClassId())).append(")").append("</font>");
					break;
				case HUMAN:
					htmlCode.append("<font color=\"").append(CBCOLOR_HUMAN).append("\">").append(player.getName()).append("<br1>(").append(getClassAbbreviation(player.getClassId())).append(")").append("</font>");
					break;
				case KAMAEL:
					htmlCode.append("<font color=\"").append(CBCOLOR_KAMAEL).append("\">").append(player.getName()).append("<br1>(").append(getClassAbbreviation(player.getClassId())).append(")").append("</font>");
					break;
				case ORC:
					htmlCode.append("<font color=\"").append(CBCOLOR_ORC).append("\">").append(player.getName()).append("<br1>(").append(getClassAbbreviation(player.getClassId())).append(")").append("</font>");
					break;
				default:
					htmlCode.append(player.getName());
					break;
				}

			}

			htmlCode.append("</a></td>");

			if (cell < NAME_PER_ROW_COMMUNITYBOARD)
				htmlCode.append(colSpacer);

			if (cell == NAME_PER_ROW_COMMUNITYBOARD) {
				cell = 0;
				htmlCode.append(trClose);
			}
		}
		if (cell > 0 && cell < NAME_PER_ROW_COMMUNITYBOARD) {
			htmlCode.append(trClose);
		}

		htmlCode.append("</table><br></td></tr>" + trOpen + "<td><img src=\"sek.cbui355\" width=600 height=1><br></td>" + trClose + "</table></body></html>");

		_communityPages.put("default", htmlCode.toString());
	}

	private static String getClassAbbreviation(ClassId clazz) {
		switch(clazz) {
			case fighter:
				return "Fighter";
			case warrior:
				return "Warrior";
			case gladiator :
				return "Glad";
			case warlord :
				return "Warlord";
			case knight :
				return "Knight";
			case paladin :
				return "Paladin";
			case darkAvenger :
				return "DA";
			case rogue :
				return "Rogue";
			case treasureHunter :
				return "TH";
			case hawkeye :
				return "Hawk";

			case mage :
				return "Mage";
			case wizard :
				return "Wizard";
			case sorceror :
				return "Sorce";
			case necromancer :
				return "Necro";
			case warlock :
				return "Warlock";
			case cleric :
				return "Cleric";
			case bishop :
				return "Bishop";
			case prophet :
				return "Prophet";

			case elvenFighter :
				return "EFighter";
			case elvenKnight :
				return "EKnight";
			case templeKnight :
				return "TK";
			case swordSinger :
				return "SwS";
			case elvenScout :
				return "Scout";
			case plainsWalker :
				return "Plainwalker";
			case silverRanger :
				return "SilverRanger";

			case elvenMage :
				return "EMage";
			case elvenWizard :
				return "EWizard";
			case spellsinger :
				return "SpS";
			case elementalSummoner :
				return "ElemSum";
			case oracle :
				return "EE-";
			case elder :
				return "EE";

			case darkFighter :
				return "DFighter";
			case palusKnight :
				return "Palus";
			case shillienKnight :
				return "SK";
			case bladedancer :
				return "BD";
			case assassin :
				return "Assassin";
			case abyssWalker :
				return "AbyssW";
			case phantomRanger :
				return "PR";

			case darkMage :
				return "DMage";
			case darkWizard :
				return "DWizard";
			case spellhowler :
				return "SpH";
			case phantomSummoner :
				return "PhantomS";
			case shillienOracle :
				return "SE-";
			case shillenElder :
				return "SE";

			case orcFighter :
				return "OFighter";
			case orcRaider :
				return "Destro-";
			case destroyer :
				return "Destro";
			case orcMonk :
				return "Tyrant-";
			case tyrant :
				return "Tyrant";

			case orcMage :
				return "OMage";
			case orcShaman :
				return "Shaman";
			case overlord :
				return "OL";
			case warcryer :
				return "WC";

			case dwarvenFighter :
				return "DwFighter";
			case scavenger :
				return "BH-";
			case bountyHunter :
				return "BH";
			case artisan :
				return "WS-";
			case warsmith :
				return "WS";

			case duelist :
				return "Glad+";
			case dreadnought :
				return "Warlord+";
			case phoenixKnight :
				return "Paladin+";
			case hellKnight :
				return "DA+";
			case sagittarius :
				return "Hawk+";
			case adventurer :
				return "TH+";
			case archmage :
				return "Sorce+";
			case soultaker :
				return "Necro+";
			case arcanaLord :
				return "Warlock+";
			case cardinal :
				return "Bishop+";
			case hierophant :
				return "Prophet+";

			case evaTemplar :
				return "TK+";
			case swordMuse :
				return "SwS+";
			case windRider :
				return "Plainwalker+";
			case moonlightSentinel :
				return "SilverRanger+";
			case mysticMuse :
				return "SpS+";
			case elementalMaster :
				return "ElemSum+";
			case evaSaint :
				return "EE+";

			case shillienTemplar :
				return "SK+";
			case spectralDancer :
				return "BD+";
			case ghostHunter :
				return "AbyssW+";
			case ghostSentinel :
				return "PR+";
			case stormScreamer :
				return "Sph+";
			case spectralMaster :
				return "PhantomSum+";
			case shillienSaint :
				return "SE+";

			case titan :
				return "Destro+";
			case grandKhavatari :
				return "Tyrant+";
			case dominator :
				return "OL+";
			case doomcryer :
				return "WC+";

			case fortuneSeeker :
				return "BH+";
			case maestro :
				return "WS+";

			case maleSoldier :
				return "Soldier";
			case femaleSoldier :
				return "Soldier";
			case trooper :
				return "Trooper";
			case warder :
				return "Warder";
			case berserker :
				return "Berserker";
			case maleSoulbreaker :
				return "Soulbreaker";
			case femaleSoulbreaker :
				return "Soulbreaker";
			case arbalester :
				return "Arbalester";
			case doombringer :
				return "Doombringer";
			case maleSoulhound :
				return "Soulhound";
			case femaleSoulhound :
				return "Soulhound";
			case trickster :
				return "Trickster";
			case inspector :
				return "Inspector";
			case judicator :
				return "Judicator";

			default:
				return "Unknown";
		}
	}

}
