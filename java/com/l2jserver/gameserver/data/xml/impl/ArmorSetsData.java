/*
 * Copyright (C) 2004-2015 L2J Server
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
package com.l2jserver.gameserver.data.xml.impl;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.l2jserver.gameserver.model.L2ArmorSet;
import com.l2jserver.gameserver.model.holders.ArmorsetSkillHolder;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.util.data.xml.IXmlReader;

/**
 * Loads armor set bonuses.
 * @author godson, Luno, UnAfraid
 */
public final class ArmorSetsData implements IXmlReader
{
	private final Map<Integer, L2ArmorSet> _armorSets = new HashMap<>();
	
	/**
	 * Instantiates a new armor sets data.
	 */
	protected ArmorSetsData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		_armorSets.clear();
		parseDatapackDirectory("stats/armorsets", false);
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _armorSets.size() + " Armor sets.");
	}
	
	@Override
	public void parseDocument(Document doc)
	{
		for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if ("list".equalsIgnoreCase(n.getNodeName()))
			{
				for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
				{
					if ("set".equalsIgnoreCase(d.getNodeName()))
					{
						final L2ArmorSet set = new L2ArmorSet();
						set.setIsVisual(parseBoolean(d.getAttributes(), "visual", false));
						set.setMinimumPieces(parseInteger(d.getAttributes(), "minimumPieces"));
						
						for (Node a = d.getFirstChild(); a != null; a = a.getNextSibling())
						{
							final NamedNodeMap attrs = a.getAttributes();
							switch (a.getNodeName())
							{
								case "chest":
								{
									set.addChest(parseInteger(attrs, "id"));
									break;
								}
								case "feet":
								{
									set.addFeet(parseInteger(attrs, "id"));
									break;
								}
								case "gloves":
								{
									set.addGloves(parseInteger(attrs, "id"));
									break;
								}
								case "head":
								{
									set.addHead(parseInteger(attrs, "id"));
									break;
								}
								case "legs":
								{
									set.addLegs(parseInteger(attrs, "id"));
									break;
								}
								case "shield":
								{
									set.addShield(parseInteger(attrs, "id"));
									break;
								}
								case "skill":
								{
									final int skillId = parseInteger(attrs, "id");
									final int skillLevel = parseInteger(attrs, "level");
									final int minimumPieces = parseInteger(attrs, "minimumPieces", set.getMinimumPieces());
									set.addSkill(new ArmorsetSkillHolder(skillId, skillLevel, minimumPieces));
									break;
								}
								case "shield_skill":
								{
									int skillId = parseInteger(attrs, "id");
									int skillLevel = parseInteger(attrs, "level");
									set.addShieldSkill(new SkillHolder(skillId, skillLevel));
									break;
								}
								case "enchant6skill":
								{
									final int skillId = parseInteger(attrs, "id");
									final int skillLevel = parseInteger(attrs, "level");
									final int minimumEnchant = parseInteger(attrs, "minimumEnchant", 6);
									set.addEnchantSkill(new ArmorsetSkillHolder(skillId, skillLevel, minimumEnchant));
									break;
								}
								case "enchant7skill":
								{
									final int skillId = parseInteger(attrs, "id");
									final int skillLevel = parseInteger(attrs, "level");
									final int minimumEnchant = parseInteger(attrs, "minimumEnchant", 7);
									set.addEnchantSkill(new ArmorsetSkillHolder(skillId, skillLevel, minimumEnchant));
									break;
								}
								case "enchant8skill":
								{
									final int skillId = parseInteger(attrs, "id");
									final int skillLevel = parseInteger(attrs, "level");
									final int minimumEnchant = parseInteger(attrs, "minimumEnchant", 8);
									set.addEnchantSkill(new ArmorsetSkillHolder(skillId, skillLevel, minimumEnchant));
									break;
								}
								case "con":
								{
									set.addCon(parseInteger(attrs, "val"));
									break;
								}
								case "dex":
								{
									set.addDex(parseInteger(attrs, "val"));
									break;
								}
								case "str":
								{
									set.addStr(parseInteger(attrs, "val"));
									break;
								}
								case "men":
								{
									set.addMen(parseInteger(attrs, "val"));
									break;
								}
								case "wit":
								{
									set.addWit(parseInteger(attrs, "val"));
									break;
								}
								case "int":
								{
									set.addInt(parseInteger(attrs, "val"));
									break;
								}
							}
						}
						
						for (int chestId : set.getChests())
						{
							_armorSets.put(chestId, set);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Checks if is armor set.
	 * @param chestId the chest Id to verify.
	 * @return {@code true} if the chest Id belongs to a registered armor set, {@code false} otherwise.
	 */
	public boolean isArmorSet(int chestId)
	{
		return _armorSets.containsKey(chestId);
	}
	
	/**
	 * Gets the sets the.
	 * @param chestId the chest Id identifying the armor set.
	 * @return the armor set associated to the give chest Id.
	 */
	public L2ArmorSet getSet(int chestId)
	{
		return _armorSets.get(chestId);
	}
	
	/**
	 * Gets the single instance of ArmorSetsData.
	 * @return single instance of ArmorSetsData
	 */
	public static ArmorSetsData getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final ArmorSetsData _instance = new ArmorSetsData();
	}
}
