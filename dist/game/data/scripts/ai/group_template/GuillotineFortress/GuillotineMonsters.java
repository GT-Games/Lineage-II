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

package ai.group_template.GuillotineFortress;

import ai.npc.AbstractNpcAI;

import com.l2jserver.gameserver.datatables.SkillData;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.NpcStringId;
import com.l2jserver.gameserver.network.serverpackets.ExShowScreenMessage;
import com.l2jserver.util.Rnd;

/**
 * GuillotineMonsters
 * @author Thonygez
 */

public class GuillotineMonsters extends AbstractNpcAI
{
	private boolean _isChaosShieldActive = true;
	
	// Other
	private static final int CHAOS_SHIELD = 15208;
	private static final int ARMOR_DESTRUCTION = 10258;
	private static final int PROOF_OF_SURVIVAL = 34898;
	
	// Normal Mobs
	private static final int NAGDU_THE_DEFORMED_MERMAN = 23201;
	private static final int HAKAL_THE_BUTCHERED = 23202;
	private static final int ROSENIA_DIVINE_SPIRIT = 23208;
	
	// Special Mob
	private static final int SCALDISECT_THE_FURIOUS = 23212;
	
	private static final int[] NPCS =
	{
		NAGDU_THE_DEFORMED_MERMAN,
		HAKAL_THE_BUTCHERED,
		ROSENIA_DIVINE_SPIRIT,
	};
	
	public GuillotineMonsters()
	{
		super(GuillotineMonsters.class.getSimpleName(), "ai/group_template/GuillotineFortress");
		
		registerMobs(NPCS);
	}
	
	public void spawnMob(int npcId, final Location loc, L2PcInstance target)
	{
		L2Npc npc = new L2Npc(npcId);
		
		L2MonsterInstance mob = new L2MonsterInstance(npc.getTemplate());
		
		mob.setHeading(loc.getHeading() < 0 ? Rnd.get(65535) : loc.getHeading());
		mob.spawnMe(loc.getX(), loc.getY(), loc.getZ());
		mob.setCurrentHpMp(npc.getMaxHp(), npc.getMaxMp());
		mob.setTarget(target);
		mob.doAttack(target);
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		npc.doCast(SkillData.getInstance().getSkill(CHAOS_SHIELD, 9));
		
		return super.onSpawn(npc);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		if ((killer != null) && killer.isPlayer())
		{
			if ((killer.getInventory().getItemByItemId(PROOF_OF_SURVIVAL) != null) && Rnd.chance(5)) // TODO Apply Random Chance
			{
				spawnMob(SCALDISECT_THE_FURIOUS, killer.getLocation(), killer);
				
				killer.getInventory().destroyItemByItemId("Proof of Survival", PROOF_OF_SURVIVAL, 1, killer, null);
			}
			else if (Rnd.chance(1))
			{
				killer.getInventory().addItem("Proof of Survival", PROOF_OF_SURVIVAL, 1, killer, null);
			}
		}
		
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance player, int damage, boolean isSummon)
	{
		if (player == null)
		{
			return super.onAttack(npc, player, damage, isSummon);
		}
		
		double npcHpPercent = npc.getCurrentHpPercentage(npc);
		
		if (((npcHpPercent < 85) && _isChaosShieldActive) || ((npcHpPercent >= 85) && npc.isAffectedBySkill(ARMOR_DESTRUCTION)))
		{
			_isChaosShieldActive = false;
			
			npc.getEffectList().stopSkillEffects(true, SkillData.getInstance().getSkill(CHAOS_SHIELD, 9));
			
			if (player.getParty() == null)
			{
				player.sendPacket(new ExShowScreenMessage(NpcStringId.CHAOS_SHIELD_BREAKTHROUGH, ExShowScreenMessage.BOTTOM_CENTER, 10000));
			}
			else
			{
				for (L2PcInstance partyMembers : player.getParty().getMembers())
				{
					partyMembers.sendPacket(new ExShowScreenMessage(NpcStringId.CHAOS_SHIELD_BREAKTHROUGH, ExShowScreenMessage.BOTTOM_CENTER, 10000));
				}
			}
		}
		else if ((npcHpPercent > 85) && !npc.isAffectedBySkill(ARMOR_DESTRUCTION) && !_isChaosShieldActive)
		{
			_isChaosShieldActive = true;
			
			npc.doCast(SkillData.getInstance().getSkill(CHAOS_SHIELD, 9));
		}
		
		return super.onAttack(npc, player, damage, isSummon);
	}
	
	public static void main(String[] args)
	{
		new GuillotineMonsters();
	}
}