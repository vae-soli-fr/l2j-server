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

import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Task dedicated to set AFK status.
 * @author Melua
 */
public class AfkTask implements Runnable
{
	private final L2PcInstance _player;
	private final Location _location;

	public AfkTask(L2PcInstance player)
	{
		_player = player;
		_location = player.getLocation();
	}

	@Override
	public void run()
	{
		if (_player != null)
		{
			if (_player.getLocation().equals(_location))
			{
				_player.setAfk(true);
				_player.sendMessage("Vous êtes considéré AFK.");
				_player.sendMessage("Vous ne gagnez plus d'expérience en roleplay.");
				_player.sendMessage("Déplacez-vous ou parlez pour quitter ce mode.");
			}
			else
			{
				_location.setLocation(_player.getLocation());
			}
		}
	}
}
