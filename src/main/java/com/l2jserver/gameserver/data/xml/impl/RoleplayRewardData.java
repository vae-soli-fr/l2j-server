package com.l2jserver.gameserver.data.xml.impl;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.l2jserver.util.data.xml.IXmlReader;

/**
 * This class holds the Experience and Skill points for each level for players.
 * @author Melua
 */
public final class RoleplayRewardData implements IXmlReader
{
	private final Map<Integer, Long> _expTable = new HashMap<>();
	private final Map<Integer, Long> _spTable = new HashMap<>();

	/**
	 * Instantiates tables.
	 */
	protected RoleplayRewardData()
	{
		load();
	}

	@Override
	public void load()
	{
		_expTable.clear();
		parseDatapackFile("data/stats/roleplayReward.xml");
		LOG.info("{}: Loaded {} levels.", getClass().getSimpleName(), _expTable.size());
	}

	@Override
	public void parseDocument(Document doc)
	{
		final Node table = doc.getFirstChild();

		for (Node n = table.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if ("roleplay".equals(n.getNodeName()))
			{
				NamedNodeMap attrs = n.getAttributes();
				int level = parseInteger(attrs, "level");
				_expTable.put(level, parseLong(attrs, "exp"));
				_spTable.put(level, parseLong(attrs, "sp"));
			}
		}
	}

	/**
	 * Gets the exp for level.
	 * @param level the level
	 * @return the experience points
	 */
	public long getXpForLevel(int level)
	{
		return _expTable.get(level);
	}

	/**
	 * Gets the skill points for level.
	 * @param level the level
	 * @return the skill points
	 */
	public long getSpForLevel(int level)
	{
		return _spTable.get(level);
	}

	/**
	 * Gets the single instance of ExperienceTable.
	 * @return single instance of ExperienceTable
	 */
	public static RoleplayRewardData getInstance()
	{
		return SingletonHolder._instance;
	}

	private static class SingletonHolder
	{
		protected static final RoleplayRewardData _instance = new RoleplayRewardData();
	}
}
