/*
 * Copyright (C) 2004-2022 L2J Server
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
package com.l2jserver.gameserver;

import static com.l2jserver.gameserver.enums.Sex.FEMALE;

import com.l2jserver.gameserver.enums.Race;
import com.l2jserver.gameserver.enums.Sex;
import com.l2jserver.util.Rnd;

/**
 * @author Melua
 *
 */
public class HeadUtil
{
	public static final byte NO_VALUE = -1;

	private static final Head M_HUMAN = new Head('E', 'D', 'C');
	private static final Head F_HUMAN = new Head('G', 'D', 'C');
	private static final Head M_ELF = new Head('E', 'D', 'C');
	private static final Head F_ELF = new Head('G', 'D', 'C');
	private static final Head M_DARKELF = new Head('E', 'D', 'C');
	private static final Head F_DARKELF = new Head('G', 'D', 'C');
	private static final Head M_ORC = new Head('E', 'D', 'C');
	private static final Head F_ORC = new Head('G', 'D', 'C');
	private static final Head M_DWARF = new Head('E', 'D', 'C');
	private static final Head F_DWARF = new Head('G', 'D', 'C');
	private static final Head M_KAMAEL = new Head('E', 'C', 'C');
	private static final Head F_KAMAEL = new Head('G', 'C', 'C');
	
	public static final byte randomHairStyle(Race race, Sex sex)
	{
		int style = 0;
		switch (race)
		{
			case HUMAN:
				style = Rnd.get(0, FEMALE.equals(sex) ? F_HUMAN._maxHairStyle : M_HUMAN._maxHairStyle);
				break;
			case ELF:
				style = Rnd.get(0, FEMALE.equals(sex) ? F_ELF._maxHairStyle : M_ELF._maxHairStyle);
				break;
			case DARK_ELF:
				style = Rnd.get(0, FEMALE.equals(sex) ? F_DARKELF._maxHairStyle : M_DARKELF._maxHairStyle);
				break;
			case ORC:
				style = Rnd.get(0, FEMALE.equals(sex) ? F_ORC._maxHairStyle : M_ORC._maxHairStyle);
				break;
			case DWARF:
				style = Rnd.get(0, FEMALE.equals(sex) ? F_DWARF._maxHairStyle : M_DWARF._maxHairStyle);
				break;
			case KAMAEL:
				style = Rnd.get(0, FEMALE.equals(sex) ? F_KAMAEL._maxHairStyle : M_KAMAEL._maxHairStyle);
		}
		return (byte) style;
	}
	
	public static final byte randomHairColor(Race race, Sex sex)
	{
		int color = 0;
		switch (race)
		{
			case HUMAN:
				color = Rnd.get(0, FEMALE.equals(sex) ? F_HUMAN._maxHairColor : M_HUMAN._maxHairColor);
				break;
			case ELF:
				color = Rnd.get(0, FEMALE.equals(sex) ? F_ELF._maxHairColor : M_ELF._maxHairColor);
				break;
			case DARK_ELF:
				color = Rnd.get(0, FEMALE.equals(sex) ? F_DARKELF._maxHairColor : M_DARKELF._maxHairColor);
				break;
			case ORC:
				color = Rnd.get(0, FEMALE.equals(sex) ? F_ORC._maxHairColor : M_ORC._maxHairColor);
				break;
			case DWARF:
				color = Rnd.get(0, FEMALE.equals(sex) ? F_DWARF._maxHairColor : M_DWARF._maxHairColor);
				break;
			case KAMAEL:
				color = Rnd.get(0, FEMALE.equals(sex) ? F_KAMAEL._maxHairColor : M_KAMAEL._maxHairColor);
		}
		return (byte) color;
	}
	
	public static final byte randomFace(Race race, Sex sex)
	{
		int face = 0;
		switch (race)
		{
			case HUMAN:
				face = Rnd.get(0, FEMALE.equals(sex) ? F_HUMAN._maxFace : M_HUMAN._maxFace);
				break;
			case ELF:
				face = Rnd.get(0, FEMALE.equals(sex) ? F_ELF._maxFace : M_ELF._maxFace);
				break;
			case DARK_ELF:
				face = Rnd.get(0, FEMALE.equals(sex) ? F_DARKELF._maxFace : M_DARKELF._maxFace);
				break;
			case ORC:
				face = Rnd.get(0, FEMALE.equals(sex) ? F_ORC._maxFace : M_ORC._maxFace);
				break;
			case DWARF:
				face = Rnd.get(0, FEMALE.equals(sex) ? F_DWARF._maxFace : M_DWARF._maxFace);
				break;
			case KAMAEL:
				face = Rnd.get(0, FEMALE.equals(sex) ? F_KAMAEL._maxFace : M_KAMAEL._maxFace);
		}
		return (byte) face;
	}
	
	public static byte toByte(String value)
	{
		if (value == null || value.length() == 0)
		{
			return NO_VALUE;
		}
		return (byte) toInt(value.charAt(0));
	}
	
	private static int toInt(char value)
	{
		return value & 30;
	}
	
	protected static class Head {
		protected Head(char maxHairStyle, char maxHairColor, char maxFace) {
			_maxHairStyle = toInt(maxHairStyle);
			_maxHairColor = toInt(maxHairColor);
			_maxFace = toInt(maxFace);
		}
		protected int _maxHairStyle;
		protected int _maxHairColor;
		protected int _maxFace;
	}
	
}
