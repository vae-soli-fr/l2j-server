package com.l2jserver.gameserver.model.actor.templates;

import static com.l2jserver.gameserver.model.itemcontainer.Inventory.PAPERDOLL_CHEST;
import static com.l2jserver.gameserver.model.itemcontainer.Inventory.PAPERDOLL_CLOAK;
import static com.l2jserver.gameserver.model.itemcontainer.Inventory.PAPERDOLL_FEET;
import static com.l2jserver.gameserver.model.itemcontainer.Inventory.PAPERDOLL_GLOVES;
import static com.l2jserver.gameserver.model.itemcontainer.Inventory.PAPERDOLL_HAIR;
import static com.l2jserver.gameserver.model.itemcontainer.Inventory.PAPERDOLL_HAIR2;
import static com.l2jserver.gameserver.model.itemcontainer.Inventory.PAPERDOLL_LEGS;
import static com.l2jserver.gameserver.model.itemcontainer.Inventory.PAPERDOLL_LHAND;
import static com.l2jserver.gameserver.model.itemcontainer.Inventory.PAPERDOLL_RHAND;
import static com.l2jserver.gameserver.model.itemcontainer.Inventory.PAPERDOLL_TOTALSLOTS;

import com.l2jserver.gameserver.data.xml.impl.NpcData;
import com.l2jserver.gameserver.data.xml.impl.PlayerTemplateData;
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.enums.MountType;
import com.l2jserver.gameserver.enums.Race;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.items.L2Item;

public class L2FakePcTemplate extends L2NpcTemplate
{
	private L2PcTemplate _playerTemplate;
	
	private boolean _female;
	
	private byte _hairStyle;
	private byte _hairColor;
	private byte _face;
	
	private int[] _paperdoll;
	private int[] _paperdollAug;
	
	private boolean _ghost;
	private int _mountNpcId;
	private MountType _mountType;
	private boolean _hero;
	private int _enchantEffect;
	
	public L2FakePcTemplate(StatsSet set)
	{
		super(set);
	}
	
	@Override
	public void set(StatsSet set)
	{
		super.set(set);
		_playerTemplate = PlayerTemplateData.getInstance().getTemplate(set.getInt("classId", 0));
		
		_female = set.getBoolean("female", false);
		
		_hairStyle = (byte) set.getInt("hairStyle", -1);
		_hairColor = (byte) set.getInt("hairColor", -1);
		_face = (byte) set.getInt("face", -1);
		
		_mountNpcId = set.getInt("mountNpcId", 0);
		_mountType = MountType.findByNpcId(_mountNpcId);
		
		_hero = set.getBoolean("hero", false);
		_ghost = set.getBoolean("ghost", false);
		
		_enchantEffect = Math.min(127, getWeaponEnchant());
		
		_paperdoll = new int[PAPERDOLL_TOTALSLOTS];
		_paperdollAug = new int[PAPERDOLL_TOTALSLOTS];
		
		_paperdoll[PAPERDOLL_RHAND] = set.getInt("pd_rhand", 7); // apprentice rod
		_paperdollAug[PAPERDOLL_RHAND] = set.getInt("pd_rhand_aug", 0);
		
		// handle double-handed weapon
		L2Item rhand = ItemTable.getInstance().getTemplate(getPaperdollItemDisplayId(PAPERDOLL_RHAND));
		if (rhand == null || rhand.getBodyPart() != L2Item.SLOT_LR_HAND) {
			_paperdoll[PAPERDOLL_LHAND] = set.getInt("pd_lhand", 0);
			_paperdollAug[PAPERDOLL_LHAND] = set.getInt("pd_lhand_aug", 0);
		}
		
		_paperdoll[PAPERDOLL_GLOVES] = set.getInt("pd_gloves", 48); // short gloves
		_paperdollAug[PAPERDOLL_GLOVES] = set.getInt("pd_gloves_aug", 0);
		
		_paperdoll[PAPERDOLL_CHEST] = set.getInt("pd_chest", 425); // apprentice tunic
		_paperdollAug[PAPERDOLL_CHEST] = set.getInt("pd_chest_aug", 0);
		
		// handle full armor
		L2Item chest = ItemTable.getInstance().getTemplate(getPaperdollItemDisplayId(PAPERDOLL_CHEST));
		if (chest == null || chest.getBodyPart() != L2Item.SLOT_FULL_ARMOR) {
			_paperdoll[PAPERDOLL_LEGS] = set.getInt("pd_legs", 461); // apprentice stockings
			_paperdollAug[PAPERDOLL_LEGS] = set.getInt("pd_legs_aug", 0);
		}

		_paperdoll[PAPERDOLL_FEET] = set.getInt("pd_feet", 1121); // apprentice shoes
		_paperdollAug[PAPERDOLL_FEET] = set.getInt("pd_feet_aug", 0);
		
		_paperdoll[PAPERDOLL_CLOAK] = set.getInt("pd_cloak", 0);
		_paperdollAug[PAPERDOLL_CLOAK] = set.getInt("pd_cloak_aug", 0);
		
		_paperdoll[PAPERDOLL_HAIR] = set.getInt("pd_hair", 0);
		_paperdollAug[PAPERDOLL_HAIR] = set.getInt("pd_hair_aug", 0);
		
		// handle double-slot accessories
		L2Item hair = ItemTable.getInstance().getTemplate(getPaperdollItemDisplayId(PAPERDOLL_HAIR));
		if (hair == null || hair.getBodyPart() != L2Item.SLOT_HAIRALL) {
			_paperdoll[PAPERDOLL_HAIR2] = set.getInt("pd_hair2", 0);
			_paperdollAug[PAPERDOLL_HAIR2] = set.getInt("pd_hair2_aug", 0);
		}
	}
	
	@Override
	public boolean isFakePc()
	{
		return true;
	}
	
	@Override
	public Race getRace()
	{
		return _playerTemplate.getRace();
	}
	
	@Override
	public double getCollisionRadiusGrown()
	{
		if (isMounted())
		{
			return NpcData.getInstance().getTemplate(getMountNpcId()).getfCollisionRadius();
		}
		return isFemale() ? _playerTemplate.getFCollisionRadiusFemale() : _playerTemplate.getfCollisionRadius();
	}
	
	@Override
	public double getCollisionHeightGrown()
	{
		if (isMounted())
		{
			return NpcData.getInstance().getTemplate(getMountNpcId()).getfCollisionHeight();
		}
		return isFemale() ? _playerTemplate.getFCollisionHeightFemale() : _playerTemplate.getfCollisionHeight();
	}

	public boolean isFemale()
	{
		return _female;
	}

	public int getBaseClass()
	{
		return _playerTemplate.getClassId().getId();
	}
	
	public int getActiveClass()
	{
		return getBaseClass();
	}
	
	public byte getHairStyle()
	{
		return _hairStyle;
	}
	
	public byte getHairColor()
	{
		return _hairColor;
	}
	
	public byte getFace()
	{
		return _face;
	}
	
	public boolean isMounted()
	{
		return _mountNpcId > 0;
	}
	
	public MountType getMountType()
	{
		return _mountType;
	}
	
	public int getMountNpcId()
	{
		return _mountNpcId;
	}
	
	public boolean isHero()
	{
		return _hero;
	}
	
	public int getEnchantEffect()
	{
		return _enchantEffect;
	}
	
	public boolean isGhost()
	{
		return _ghost;
	}
	
	public int getPaperdollItemDisplayId(int slot)
	{
		return _paperdoll[slot];
	}
	
	public int getPaperdollAugmentationId(int slot)
	{
		return _paperdollAug[slot];
	}

}
