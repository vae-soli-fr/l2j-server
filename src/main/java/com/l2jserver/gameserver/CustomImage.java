package com.l2jserver.gameserver;

import java.io.File;
import java.nio.file.Files;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.l2jserver.Config;
import com.l2jserver.gameserver.idfactory.IdFactory;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jserver.gameserver.network.serverpackets.PledgeCrest;

public class CustomImage {

	private static final Logger _log = Logger.getLogger(CustomImage.class.getName());

	private static final Pattern PATTERN = Pattern.compile("\\[img\\](\\w+_(\\d+)_(\\d+)\\.dds)\\[\\/img\\]", Pattern.CASE_INSENSITIVE);
	private static final String CREST_FORMAT = "<img src=\"Crest.crest_%d_%d\" width=%d height=%d>";

	/**
	 *	Send the DDS as byte array to L2PcInstance through PledgeCrest
	 *  based on the content of NpcHtmlMessage
	 * @param activeChar
	 * @param html
	 */
	public static void sendPackets(L2PcInstance activeChar, NpcHtmlMessage html) {

		if (html.getHtml() == null) {
			return;
		}

		Matcher matcher = PATTERN.matcher(html.getHtml());
		while (matcher.find()) {
			try {
				String imageTag = matcher.group(0);
				String filename = matcher.group(1);
				Integer width = Integer.valueOf(matcher.group(2));
				Integer height = Integer.valueOf(matcher.group(3));

				if (!(height > 0 && (height & (height - 1)) == 0) && (width > 0 && (width & (width - 1)) == 0)) {
					_log.warning(filename + " has to have dimensions of power of 2 (2,4,8,16,32,64,...)");
					return;
				}

				File image = new File(Config.DATAPACK_ROOT + "/data/images/" + filename);
				byte[] data = Files.readAllBytes(image.toPath());

				int crestId = IdFactory.getInstance().getNextId();

				String replacement = String.format(CREST_FORMAT, Config.REQUEST_ID, crestId, width, height);
				PledgeCrest crestImage = new PledgeCrest(crestId, data);

				html.replace(Pattern.quote(imageTag), replacement);
				activeChar.sendPacket(crestImage);

			} catch (Exception e) {
				_log.warning(e.getMessage());
			}
		}
	}

}
