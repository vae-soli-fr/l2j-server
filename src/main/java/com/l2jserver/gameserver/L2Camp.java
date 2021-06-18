package com.l2jserver.gameserver;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.gameserver.datatables.SpawnTable;
import com.l2jserver.gameserver.model.L2Spawn;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.util.Util;

public class L2Camp
{
	private static enum Status
	{
		TIMBER,
		FIRE,
		TENT,
		FOOD,
		EXTINCT,
		EATEN,
		PACKED,
		EMPTY
	}
	
	private static final Logger LOG = LoggerFactory.getLogger(L2Camp.class);
	
	private static final int TIMBER_NPC_ID = 60004;
	private static final int FIRE_NPC_ID = 60005;
	private static final int FOOD_NPC_ID = 60006;
	private static final int TENT_NPC_ID = 60007;
	
	
	private Status _status = Status.EMPTY;
	private List<L2Npc> _npcs = new ArrayList<>();
	
	public void evolve(L2PcInstance activeChar)
	{
		if (!_npcs.isEmpty() && !Util.checkIfInRange(500, activeChar, getNpc(Status.TIMBER), true)) {
			activeChar.sendMessage("Vous êtes trop loin du foyer.");
			return;
		}

		try 
		{
			switch (_status)
			{
				case EMPTY:
				{
					addSpawn(TIMBER_NPC_ID, activeChar.getLocation());
					activeChar.sendMessage("Vous disposez du bois sec au sol.");
					setStatus(Status.TIMBER);
					break;
				}

				case TIMBER:
				{
					addSpawn(FIRE_NPC_ID, getNpc(Status.TIMBER).getLocation());
					activeChar.sendMessage("Vous allumez un feu.");
					setStatus(Status.FIRE);
					break;
				}

				case FIRE:
				{
					int heading = Util.calculateHeadingFrom(activeChar.getLocation(), getNpc(Status.FIRE).getLocation());
					addSpawn(TENT_NPC_ID, activeChar.getLocation(), heading);
					activeChar.sendMessage("Vous montez une tente pour vous abriter.");
					setStatus(Status.TENT);
					break;
				}

				case TENT:
				{
					int heading = getNpc(Status.TENT).getHeading();
					addSpawn(FOOD_NPC_ID, getNpc(Status.TIMBER).getLocation(), heading);
					activeChar.sendMessage("Vous faites cuire le repas.");				
					setStatus(Status.FOOD);
					break;
				}
					
				case FOOD:
				{
					removeNpc(Status.FIRE);
					activeChar.sendMessage("Vous étouffez le feu.");
					setStatus(Status.EXTINCT);
					break;
				}

				case EXTINCT:
				{
					removeNpc(Status.FOOD);
					activeChar.sendMessage("Vous rangez les ustensiles de cuisine.");		
					setStatus(Status.EATEN);
					break;
				}
					
				case EATEN:
				{
					removeNpc(Status.TIMBER);
					activeChar.sendMessage("Vous dispersez les morceaux de bois calcinés.");
					setStatus(Status.PACKED);
					break;
				}

				case PACKED:
				{
					clean();
					activeChar.sendMessage("Vous démontez la tente.");
					setStatus(Status.EMPTY);
					break;
				}
			}
		}
		catch (Exception ex)
		{
			LOG.error("Error while evolving player camp", ex);
		}
	}
	
	private void addSpawn(int npcId, Location location) throws Exception
	{
		addSpawn(npcId, location.getX(), location.getY(), location.getZ(), location.getHeading(), location.getInstanceId());
	}
	
	private void addSpawn(int npcId, Location location, int heading) throws Exception
	{
		addSpawn(npcId, location.getX(), location.getY(), location.getZ(), heading, location.getInstanceId());
	}
	
	private void addSpawn(int npcId, int x, int y, int z, int heading, int instanceId) throws Exception
	{
		L2Spawn spawn = new L2Spawn(npcId);

		spawn.getLocation().setX(x);
		spawn.getLocation().setY(y);
		spawn.getLocation().setZ(z);
		
		spawn.setHeading(heading);
		spawn.setInstanceId(instanceId);
		
		spawn.setAmount(1);
		spawn.setRespawnDelay(60);	

		SpawnTable.getInstance().addNewSpawn(spawn, false);
		
		spawn.init();
		spawn.stopRespawn();
		
		_npcs.add(spawn.getLastSpawn());
	}

	private void removeNpc(Status status)
	{
		removeNpc(status.ordinal());
	}

	private void removeNpc(int index)
	{
		_npcs.get(index).deleteMe();
		SpawnTable.getInstance().deleteSpawn(_npcs.get(index).getSpawn(), false);
	}

	private void setStatus(Status status)
	{
		_status = status;
	}
	
	private L2Npc getNpc(Status status)
	{
		return _npcs.get(status.ordinal());
	}
	
	public void clean()
	{
		for (int i = 0; i < _npcs.size(); i++) {
			removeNpc(i);
		}
		_npcs.clear();
	}
}