/*
 * Copyright (C) 2004-2017 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.gameserver.dao.impl.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.database.pool.impl.ConnectionFactory;
import com.l2jserver.gameserver.dao.FashionDAO;
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.itemcontainer.Inventory;

/**
 * Fashion DAO MySQL implementation.
 * @author Melua
 */
public class FashionDAOMySQLImpl implements FashionDAO
{
	private static final Logger LOG = LoggerFactory.getLogger(FashionDAOMySQLImpl.class);
	
	private static final String SELECT = "SELECT under, head, rhand, lhand, gloves, chest, legs, feet, cloak, hair, hair2, rbracelet, lbracelet, deco1, deco2, deco3, deco4, deco5, deco6, belt FROM character_fashion WHERE charId=?";
	private static final String INSERT = "INSERT INTO character_fashion (charId) values (?)";
	private static final String UPDATE = "UPDATE character_fashion SET under=?,head=?,rhand=?,lhand=?,gloves=?,chest=?,legs=?,feet=?,cloak=?,hair=?,hair2=?,rbracelet=?,lbracelet=?,deco1=?,deco2=?,deco3=?,deco4=?,deco5=?,deco6=?,belt=? WHERE charId=?";
	
	@Override
	public void update(L2PcInstance player)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(UPDATE))
		{
			ps.setInt(1, player.getInventory().getFashionItemId(Inventory.PAPERDOLL_UNDER));
			ps.setInt(2, player.getInventory().getFashionItemId(Inventory.PAPERDOLL_HEAD));
			ps.setInt(3, player.getInventory().getFashionItemId(Inventory.PAPERDOLL_RHAND));
			ps.setInt(4, player.getInventory().getFashionItemId(Inventory.PAPERDOLL_LHAND));
			ps.setInt(5, player.getInventory().getFashionItemId(Inventory.PAPERDOLL_GLOVES));
			ps.setInt(6, player.getInventory().getFashionItemId(Inventory.PAPERDOLL_CHEST));
			ps.setInt(7, player.getInventory().getFashionItemId(Inventory.PAPERDOLL_LEGS));
			ps.setInt(8, player.getInventory().getFashionItemId(Inventory.PAPERDOLL_FEET));
			ps.setInt(9, player.getInventory().getFashionItemId(Inventory.PAPERDOLL_CLOAK));
			ps.setInt(10, player.getInventory().getFashionItemId(Inventory.PAPERDOLL_HAIR));
			ps.setInt(11, player.getInventory().getFashionItemId(Inventory.PAPERDOLL_HAIR2));
			ps.setInt(12, player.getInventory().getFashionItemId(Inventory.PAPERDOLL_RBRACELET));
			ps.setInt(13, player.getInventory().getFashionItemId(Inventory.PAPERDOLL_LBRACELET));
			ps.setInt(14, player.getInventory().getFashionItemId(Inventory.PAPERDOLL_DECO1));
			ps.setInt(15, player.getInventory().getFashionItemId(Inventory.PAPERDOLL_DECO2));
			ps.setInt(16, player.getInventory().getFashionItemId(Inventory.PAPERDOLL_DECO3));
			ps.setInt(17, player.getInventory().getFashionItemId(Inventory.PAPERDOLL_DECO4));
			ps.setInt(18, player.getInventory().getFashionItemId(Inventory.PAPERDOLL_DECO5));
			ps.setInt(19, player.getInventory().getFashionItemId(Inventory.PAPERDOLL_DECO6));
			ps.setInt(20, player.getInventory().getFashionItemId(Inventory.PAPERDOLL_BELT));
			ps.setInt(21, player.getObjectId());
			ps.execute();
		}
		catch (Exception e)
		{
			LOG.error("Could not store fashion data for {} : {}", player, e);
		}
	}
	
	@Override
	public boolean insert(L2PcInstance player)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(INSERT))
		{
			ps.setInt(1, player.getObjectId());
			ps.execute();
		}
		catch (Exception e)
		{
			LOG.error("Could not add fashion for {} : {}", player, e);
			return false;
		}
		return true;
	}

	@Override
	public void load(L2PcInstance player)
	{
		int[] paperdoll = load(player.getObjectId());
		for (int slot = 0; slot < paperdoll.length; slot++) {
			player.getInventory().setFashionItem(slot, ItemTable.getInstance().getTemplate(paperdoll[slot]));
		}
	}

	@Override
	public int[] load(int objectId)
	{
		int[] paperdoll = new int[Inventory.PAPERDOLL_TOTALSLOTS];
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT))
		{
			ps.setInt(1, objectId);
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					paperdoll[Inventory.PAPERDOLL_UNDER] = rs.getInt("under");
					paperdoll[Inventory.PAPERDOLL_HEAD] = rs.getInt("head");
					paperdoll[Inventory.PAPERDOLL_RHAND] = rs.getInt("rhand");
					paperdoll[Inventory.PAPERDOLL_LHAND] = rs.getInt("lhand");
					paperdoll[Inventory.PAPERDOLL_GLOVES] = rs.getInt("gloves");
					paperdoll[Inventory.PAPERDOLL_CHEST] = rs.getInt("chest");
					paperdoll[Inventory.PAPERDOLL_LEGS] = rs.getInt("legs");
					paperdoll[Inventory.PAPERDOLL_FEET] = rs.getInt("feet");
					paperdoll[Inventory.PAPERDOLL_CLOAK] = rs.getInt("cloak");
					paperdoll[Inventory.PAPERDOLL_HAIR] = rs.getInt("hair");
					paperdoll[Inventory.PAPERDOLL_HAIR2] = rs.getInt("hair2");
					paperdoll[Inventory.PAPERDOLL_RBRACELET] = rs.getInt("rbracelet");
					paperdoll[Inventory.PAPERDOLL_LBRACELET] = rs.getInt("lbracelet");
					paperdoll[Inventory.PAPERDOLL_DECO1] = rs.getInt("deco1");
					paperdoll[Inventory.PAPERDOLL_DECO2] = rs.getInt("deco2");
					paperdoll[Inventory.PAPERDOLL_DECO3] = rs.getInt("deco3");
					paperdoll[Inventory.PAPERDOLL_DECO4] = rs.getInt("deco4");
					paperdoll[Inventory.PAPERDOLL_DECO5] = rs.getInt("deco5");
					paperdoll[Inventory.PAPERDOLL_DECO6] = rs.getInt("deco6");
					paperdoll[Inventory.PAPERDOLL_BELT] = rs.getInt("belt");
				}
			}
		}
		catch (Exception e)
		{
			LOG.error("Could not restore fashion for {} : {}", objectId, e);
		}
		return paperdoll;
	}
}
