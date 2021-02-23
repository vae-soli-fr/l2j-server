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
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT))
		{
			ps.setInt(1, player.getObjectId());
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					player.getInventory().setFashionItem(Inventory.PAPERDOLL_UNDER, ItemTable.getInstance().getTemplate(rs.getInt("under")));
					player.getInventory().setFashionItem(Inventory.PAPERDOLL_HEAD, ItemTable.getInstance().getTemplate(rs.getInt("head")));
					player.getInventory().setFashionItem(Inventory.PAPERDOLL_RHAND, ItemTable.getInstance().getTemplate(rs.getInt("rhand")));
					player.getInventory().setFashionItem(Inventory.PAPERDOLL_LHAND, ItemTable.getInstance().getTemplate(rs.getInt("lhand")));
					player.getInventory().setFashionItem(Inventory.PAPERDOLL_GLOVES, ItemTable.getInstance().getTemplate(rs.getInt("gloves")));
					player.getInventory().setFashionItem(Inventory.PAPERDOLL_CHEST, ItemTable.getInstance().getTemplate(rs.getInt("chest")));
					player.getInventory().setFashionItem(Inventory.PAPERDOLL_LEGS, ItemTable.getInstance().getTemplate(rs.getInt("legs")));
					player.getInventory().setFashionItem(Inventory.PAPERDOLL_FEET, ItemTable.getInstance().getTemplate(rs.getInt("feet")));
					player.getInventory().setFashionItem(Inventory.PAPERDOLL_CLOAK, ItemTable.getInstance().getTemplate(rs.getInt("cloak")));
					player.getInventory().setFashionItem(Inventory.PAPERDOLL_HAIR, ItemTable.getInstance().getTemplate(rs.getInt("hair")));
					player.getInventory().setFashionItem(Inventory.PAPERDOLL_HAIR2, ItemTable.getInstance().getTemplate(rs.getInt("hair2")));
					player.getInventory().setFashionItem(Inventory.PAPERDOLL_RBRACELET, ItemTable.getInstance().getTemplate(rs.getInt("rbracelet")));
					player.getInventory().setFashionItem(Inventory.PAPERDOLL_LBRACELET, ItemTable.getInstance().getTemplate(rs.getInt("lbracelet")));
					player.getInventory().setFashionItem(Inventory.PAPERDOLL_DECO1, ItemTable.getInstance().getTemplate(rs.getInt("deco1")));
					player.getInventory().setFashionItem(Inventory.PAPERDOLL_DECO2, ItemTable.getInstance().getTemplate(rs.getInt("deco2")));
					player.getInventory().setFashionItem(Inventory.PAPERDOLL_DECO3, ItemTable.getInstance().getTemplate(rs.getInt("deco3")));
					player.getInventory().setFashionItem(Inventory.PAPERDOLL_DECO4, ItemTable.getInstance().getTemplate(rs.getInt("deco4")));
					player.getInventory().setFashionItem(Inventory.PAPERDOLL_DECO5, ItemTable.getInstance().getTemplate(rs.getInt("deco5")));
					player.getInventory().setFashionItem(Inventory.PAPERDOLL_DECO6, ItemTable.getInstance().getTemplate(rs.getInt("deco6")));
					player.getInventory().setFashionItem(Inventory.PAPERDOLL_BELT, ItemTable.getInstance().getTemplate(rs.getInt("belt")));
				}
			}
		}
		catch (Exception e)
		{
			LOG.error("Could not restore fashion for {} : {}", player, e);
		}
	}
}
