/*
 * Copyright (C) 2004-2016 L2J Server
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
package com.l2jserver.gameserver.model.actor.tasks.player;

import com.l2jserver.Config;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import com.l2jserver.gameserver.network.serverpackets.CreatureSay;

/**
 * Task dedicated to set AFK status.
 * @author Melua
 */
public class AfkTask implements Runnable
{
	private final L2PcInstance _player;
	private long _actionId;

	public AfkTask(L2PcInstance player)
	{
		_player = player;
		_actionId = player.getActionId();
	}

	@Override
	public void run()
	{
		if (_player != null)
		{
			if (!(_player.getActionId() > _actionId))
			{
				if (!_player.isAfk())
				{
					_player.enterAfk();
					_player.sendPacket(announce("Vous êtes inactif depuis " + Config.AFK_DELAY + " secondes."));
					_player.sendPacket(announce("Vous êtes maintenant considéré AFK."));
					_player.sendMessage("Vous ne gagnez plus d'expérience en roleplay.");
					_player.sendMessage("Déplacez-vous ou parlez pour quitter ce mode.");
				}
			}
			else
			{
				_actionId = _player.getActionId();
			}
		}
	}

	private static CreatureSay announce(String text)
	{
		return new CreatureSay(0, Say2.CRITICAL_ANNOUNCE, "", text);
	}
}
