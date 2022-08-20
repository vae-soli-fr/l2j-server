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

import com.l2jserver.gameserver.HeadUtil;
import com.l2jserver.gameserver.data.xml.impl.NpcData;
import com.l2jserver.gameserver.data.xml.impl.PlayerTemplateData;
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.enums.MountType;
import com.l2jserver.gameserver.enums.Race;
import com.l2jserver.gameserver.enums.Sex;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.items.L2Item;

public class L2FakePcTemplate extends L2NpcTemplate
{
	private Race _race;
	private int _baseClass;
	private double _collisionHeight;
	private double _collisionRadius;
	
	private boolean _female;
	
	private byte _hairStyle;
	private byte _hairColor;
	private byte _face;
	
	private int[] _paperdoll;
	
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
		
		L2PcTemplate playerTemplate = PlayerTemplateData.getInstance().getTemplate(set.getInt("fake_classId"));
		
		_race = playerTemplate.getRace();
		_baseClass = playerTemplate.getClassId().getId();

		_female = set.getBoolean("fake_female", false);
		
		_hairStyle = HeadUtil.toByte(set.getString("fake_head_style", null));
		_hairColor = HeadUtil.toByte(set.getString("fake_head_color", null));
		_face = HeadUtil.toByte(set.getString("fake_head_face", null));
		
		_mountNpcId = set.getInt("fake_mountNpcId", 0);
		_mountType = MountType.findByNpcId(_mountNpcId);
		
		if (isMounted())
		{
			L2NpcTemplate mountTemplate = NpcData.getInstance().getTemplate(getMountNpcId());
			_collisionHeight = mountTemplate.getfCollisionHeight();
			_collisionRadius = mountTemplate.getfCollisionRadius();
		}
		else
		{
			_collisionHeight = isFemale() ? playerTemplate.getFCollisionHeightFemale() : playerTemplate.getfCollisionHeight();
			_collisionRadius = isFemale() ? playerTemplate.getFCollisionRadiusFemale() : playerTemplate.getfCollisionRadius();
		}
		
		_hero = set.getBoolean("fake_hero", false);
		_ghost = set.getBoolean("fake_ghost", false);
		
		_enchantEffect = Math.min(127, set.getInt("fake_equipment_weaponEnchant", 0));
		
		_paperdoll = new int[PAPERDOLL_TOTALSLOTS];		
		_paperdoll[PAPERDOLL_RHAND] = set.getInt("fake_equipment_rhand", 7); // apprentice rod
		_paperdoll[PAPERDOLL_GLOVES] = set.getInt("fake_equipment_gloves", 48); // short gloves		
		_paperdoll[PAPERDOLL_CHEST] = set.getInt("fake_equipment_chest", 425); // apprentice tunic
		_paperdoll[PAPERDOLL_FEET] = set.getInt("fake_equipment_feet", 1121); // apprentice shoes	
		_paperdoll[PAPERDOLL_CLOAK] = set.getInt("fake_equipment_cloak", 0);		
		_paperdoll[PAPERDOLL_HAIR] = set.getInt("fake_equipment_hair", 0);
		
		// handle double-handed weapon
		L2Item rhand = ItemTable.getInstance().getTemplate(getPaperdollItemDisplayId(PAPERDOLL_RHAND));
		if (rhand == null || rhand.getBodyPart() != L2Item.SLOT_LR_HAND) {
			_paperdoll[PAPERDOLL_LHAND] = set.getInt("fake_equipment_lhand", 0);
		}
		
		// handle full armor
		L2Item chest = ItemTable.getInstance().getTemplate(getPaperdollItemDisplayId(PAPERDOLL_CHEST));
		if (chest == null || chest.getBodyPart() != L2Item.SLOT_FULL_ARMOR) {
			_paperdoll[PAPERDOLL_LEGS] = set.getInt("fake_equipment_legs", 461); // apprentice stockings
		}
		
		// handle double-slot accessories
		L2Item hair = ItemTable.getInstance().getTemplate(getPaperdollItemDisplayId(PAPERDOLL_HAIR));
		if (hair == null || hair.getBodyPart() != L2Item.SLOT_HAIRALL) {
			_paperdoll[PAPERDOLL_HAIR2] = set.getInt("fake_equipment_hair2", 0);
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
		return _race;
	}
	
	@Override
	public Sex getSex()
	{
		return isFemale() ? Sex.FEMALE : Sex.MALE;
	}
	
	@Override
	public double getCollisionRadiusGrown()
	{
		return _collisionRadius;
	}
	
	@Override
	public double getCollisionHeightGrown()
	{
		return _collisionHeight;
	}

	public boolean isFemale()
	{
		return _female;
	}

	public int getBaseClass()
	{
		return _baseClass;
	}
	
	public int getActiveClass()
	{
		return _baseClass;
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
	
}
