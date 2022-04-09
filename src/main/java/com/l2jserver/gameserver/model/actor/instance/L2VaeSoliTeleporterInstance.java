package com.l2jserver.gameserver.model.actor.instance;

import com.l2jserver.gameserver.enums.InstanceType;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.templates.L2NpcTemplate;
import com.l2jserver.gameserver.network.serverpackets.ActionFailed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.StringTokenizer;

public class L2VaeSoliTeleporterInstance extends L2Npc
{
	private static final Logger LOG = LoggerFactory.getLogger(L2VaeSoliTeleporterInstance.class);

	public L2VaeSoliTeleporterInstance(L2NpcTemplate template)
	{
		super(template);
		setInstanceType(InstanceType.L2VaeSoliTeleporterInstance);
	}
	
	@Override
	public void onBypassFeedback(L2PcInstance player, String command)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
		
		StringTokenizer st = new StringTokenizer(command, " ");
		String actualCommand = st.nextToken(); // Get actual command

		int x = player.getLocation().getX();
		int y = player.getLocation().getY();
		int z = player.getLocation().getZ();
		int instance = player.getInstanceId();

		if(st.countTokens() == 1) {
			instance = Integer.parseInt(st.nextToken());
		}else if(st.countTokens() >= 3) {
			x = Integer.parseInt(st.nextToken());
			y = Integer.parseInt(st.nextToken());
			z = Integer.parseInt(st.nextToken());
			if (st.countTokens() == 1) {
				instance = Integer.parseInt(st.nextToken());
			}
		}
		doTeleport(player, x, y, z, instance);
	}
	
	private void doTeleport(L2PcInstance player, int x, int y, int z, int instance )
	{
		if (player.isAlikeDead())
			{
				return;
			}
		player.teleToLocation(x, y, z, player.getHeading(), instance);
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	@Override
	public String getHtmlPath(int npcId, int val)
	{
		String pom = "";
		if (val == 0)
		{
			pom = "" + npcId;
		}
		else
		{
			pom = npcId + "-" + val;
		}
		
		return "data/html/teleporter/vaeSoli/" + pom + ".htm";
	}
}