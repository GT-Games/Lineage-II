package ai.group_template.Harnak;

import ai.npc.AbstractNpcAI;

import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.NpcStringId;
import com.l2jserver.gameserver.network.serverpackets.ExShowScreenMessage;
import com.l2jserver.util.Rnd;

public class firstBasement extends AbstractNpcAI
{
	
	// First Basement Group
	private static final int WEISS_KHAN = 22935;
	private static final int WEISS_ELE = 22936;
	private static final int BAMONTI = 22937;
	private static final int KRAKIA_BATHUS = 22931;
	private static final int RAKZAN = 22934;
	private static final int KRAKIA_LOTUS = 22933;
	
	// First Basement Demonic Group
	private static final int DEMONIC_WEISS_KHAN = 22943;
	private static final int DEMONIC_WEISS_ELE = 22944;
	private static final int DEMONIC_BAMONTI = 22945;
	private static final int DEMONIC_KRAKIA_BATHUS = 22939;
	private static final int DEMONIC_RAKZAN = 22942;
	private static final int DEMONIC_KRAKIA_LOTUS = 22941;
	
	private int countMonstersKilled; // count monsters killed
	
	private static boolean isDemonicSystemActive = false;
	
	private static int FIRST_BASEMENT[] =
	{
		WEISS_KHAN,
		WEISS_ELE,
		BAMONTI,
		KRAKIA_BATHUS,
		RAKZAN,
		KRAKIA_LOTUS,
	};
	
	private static int DEMONIC_FIRST_BASEMENT[] =
	{
		DEMONIC_WEISS_KHAN,
		DEMONIC_WEISS_ELE,
		DEMONIC_BAMONTI,
		DEMONIC_KRAKIA_BATHUS,
		DEMONIC_RAKZAN,
		DEMONIC_KRAKIA_LOTUS,
	};
	
	public firstBasement()
	{
		super(firstBasement.class.getSimpleName(), "ai/group_template/firstBasement");
		
		registerMobs(FIRST_BASEMENT);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance attacker, boolean isSummon)
	{
		countMonstersKilled++;
		
		Location loc = attacker.getLocation();
		
		if (!isDemonicSystemActive)
		{
			for (int tmp : FIRST_BASEMENT)
			{
				if ((npc.getId() == tmp) && npc.isDead() && Rnd.chance(5))
				{
					L2Npc newNPC = new L2Npc(tmp);
					L2MonsterInstance mob = new L2MonsterInstance(newNPC.getTemplate());
					
					System.out.println("Respawning NPC: " + npc.getId());
					mob.setHeading(loc.getHeading() < 0 ? Rnd.get(65535) : loc.getHeading());
					mob.spawnMe(loc.getX(), loc.getY(), loc.getZ());
					mob.setCurrentHpMp(npc.getMaxHp(), npc.getMaxMp());
				}
			}
		}
		else
		{
			for (int tmp : DEMONIC_FIRST_BASEMENT)
			{
				if ((npc.getId() == tmp) && npc.isDead() && Rnd.chance(5))
				{
					L2Npc newNPC = new L2Npc(tmp);
					L2MonsterInstance mob = new L2MonsterInstance(newNPC.getTemplate());
					
					System.out.println("Respawning NPC: " + npc.getId());
					mob.setHeading(loc.getHeading() < 0 ? Rnd.get(65535) : loc.getHeading());
					mob.spawnMe(loc.getX(), loc.getY(), loc.getZ());
					mob.setCurrentHpMp(npc.getMaxHp(), npc.getMaxMp());
				}
			}
		}
		return super.onKill(npc, attacker, isSummon);
	}
	
	public void startDemonicSystemTransform(L2Npc npc, int demonicId)
	{
		L2Npc newNPC = new L2Npc(demonicId);
		
		L2MonsterInstance mob = new L2MonsterInstance(newNPC.getTemplate());
		
		Location loc = npc.getLocation();
		
		npc.decayMe();
		mob.setHeading(loc.getHeading() < 0 ? Rnd.get(65535) : loc.getHeading());
		mob.spawnMe(loc.getX(), loc.getY(), loc.getZ());
		mob.setCurrentHpMp(mob.getMaxHp(), mob.getMaxMp());
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		if (attacker == null)
		{
			return super.onAttack(npc, attacker, damage, isPet);
		}
		
		if ((countMonstersKilled > 10) && !isDemonicSystemActive)
		{
			countMonstersKilled = 0;
			
			isDemonicSystemActive = true;
			
			if (attacker.getParty() == null)
			{
				attacker.sendPacket(new ExShowScreenMessage(NpcStringId.DEMONIC_SYSTEM_WILL_ACTIVATE, ExShowScreenMessage.TOP_CENTER, 10000));
			}
			else
			{
				for (L2PcInstance partyMembers : attacker.getParty().getMembers())
				{
					partyMembers.sendPacket(new ExShowScreenMessage(NpcStringId.DEMONIC_SYSTEM_WILL_ACTIVATE, ExShowScreenMessage.TOP_CENTER, 10000));
				}
			}
			
			switch (npc.getId())
			{
				case WEISS_KHAN:
					startDemonicSystemTransform(npc, DEMONIC_WEISS_KHAN);
					break;
				case WEISS_ELE:
					startDemonicSystemTransform(npc, DEMONIC_WEISS_ELE);
					break;
				case BAMONTI:
					startDemonicSystemTransform(npc, DEMONIC_BAMONTI);
					break;
				case KRAKIA_BATHUS:
					startDemonicSystemTransform(npc, DEMONIC_KRAKIA_BATHUS);
					break;
				case RAKZAN:
					startDemonicSystemTransform(npc, DEMONIC_RAKZAN);
					break;
				case KRAKIA_LOTUS:
					startDemonicSystemTransform(npc, DEMONIC_KRAKIA_LOTUS);
					break;
			}
			
			System.out.println("Transform Ok");
		}
		else
		{
			isDemonicSystemActive = false;
		}
		
		return super.onAttack(npc, attacker, damage, isPet);
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		npc.setIsNoRndWalk(false);
		
		return super.onSpawn(npc);
	}
	
	public static void main(String[] args)
	{
		new firstBasement();
	}
	
}