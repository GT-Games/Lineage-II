package quests.Q10385_RedThreadofFate;

import com.l2jserver.gameserver.ai.CtrlEvent;
import com.l2jserver.gameserver.enums.QuestSound;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.network.NpcStringId;
import com.l2jserver.gameserver.network.serverpackets.ExShowScreenMessage;

public class Q10385_RedThreadofFate extends Quest
{
	public static final int INSTANCE_ID = 241;
	
	private static final int Raina = 33491;
	private static final int Morelyn = 30925;
	private static final int Lanya = 33783;
	private static final int HeineWaterSource = 33784;
	private static final int Ladyofthelike = 31745;
	private static final int Nerupa = 30370;
	private static final int Innocentin = 31328;
	private static final int Enfeux = 31519;
	private static final int Vulkan = 31539;
	private static final int Urn = 31149;
	private static final int Wesley = 30166;
	private static final int DesertedDwarvenHouse = 33788;
	private static final int PaagrioTemple = 33787;
	private static final int AltarofShilen = 33785;
	private static final int ShilensMessenger = 27492; // spawned by scripts mob
	private static final int CaveofSouls = 33789;
	private static final int MotherTree = 33786;
	private static final int Darin = 33748; // instance
	private static final int Roxxy = 33749; // instance
	private static final int BiotinHighPriest = 30031; // instance
	private static final int MysteriousDarkKnight = 33751; // spawned by script npc
	
	// Items
	private static final int MysteriousLetter = 36072;
	private static final int WaterFromTheGardenOfEva = 36066;
	private static final int Clearestwater = 36067;
	private static final int Brightestlight = 36068;
	private static final int Purestsoul = 36069;
	private static final int Vulkangold = 36113;
	private static final int Vulkansilver = 36114;
	private static final int Vulkanfire = 36115;
	private static final int Fiercestflame = 36070;
	private static final int Fondestheart = 36071;
	private static final int SOEDwarvenvillage = 20376;
	private static final int DimensionalDiamond = 7562;
	
	// Skills
	
	private static final int WATER = 9579;
	
	private static final int LIGHT = 9580;
	
	private static final int SOUL = 9581;
	
	private static final int FLAME = 9582;
	
	private static final int LOVE = 9583;
	
	// Other
	private static final int SOCIAL_BOW = 7;
	
	// Teleport
	private static final Location GARDEN_OF_EVA = new Location(80744, 254664, -10389);
	
	private static final int NPCs[] =
	{
		Raina, // Works
		Morelyn, // Works
		Lanya, // Works
		HeineWaterSource,
		Ladyofthelike,
		Nerupa,
		Innocentin,
		Enfeux,
		Vulkan,
		Urn,
		Wesley,
		DesertedDwarvenHouse,
		PaagrioTemple,
		AltarofShilen,
		ShilensMessenger,
		CaveofSouls,
		MotherTree,
		Darin,
		Roxxy,
		BiotinHighPriest,
		MysteriousDarkKnight,
	};
	
	private static final int Items[] =
	{
		MysteriousLetter,
		WaterFromTheGardenOfEva,
		Clearestwater,
		Brightestlight,
		Purestsoul,
		Vulkangold,
		Vulkansilver,
		Vulkanfire,
		Fiercestflame,
		Fondestheart,
		SOEDwarvenvillage,
		DimensionalDiamond
	};
	
	public Q10385_RedThreadofFate()
	{
		super(10385, Q10385_RedThreadofFate.class.getSimpleName(), "Red Thread of Fate");
		
		addStartNpc(Raina);
		addTalkId(NPCs);
		registerQuestItems(Items);
		addSocialActionSeeId(Lanya);
		addKillId(ShilensMessenger);
		addCondMinLevel(85, "");
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		
		String htmltext = event;
		
		int cond = qs.getCond();
		
		if (event.equalsIgnoreCase("TELEPORT"))
		{
			teleportPlayer(player, GARDEN_OF_EVA, 0);
		}
		else if ((cond == 0) && event.equalsIgnoreCase("Raina-03.htm"))
		{
			qs.setState(State.STARTED);
			qs.setCond(1);
			qs.giveItems(MysteriousLetter, 1L);
			qs.playSound(QuestSound.ITEMSOUND_QUEST_ACCEPT);
		}
		else if ((cond == 1) && event.equalsIgnoreCase("Morelyn-02.htm"))
		{
			qs.setCond(2);
			qs.playSound(QuestSound.ITEMSOUND_QUEST_MIDDLE);
		}
		else if ((cond == 2) && event.equalsIgnoreCase("Lanya-02.htm"))
		{
			qs.setCond(3);
			qs.playSound(QuestSound.ITEMSOUND_QUEST_MIDDLE);
		}
		
		return htmltext;
		
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		
		String htmltext = null;
		
		int cond = qs.getCond();
		
		switch (npc.getId())
		{
			case Raina:
			{
				if (cond == 0)
				{
					htmltext = "Raina-01.htm";
				}
				break;
			}
			case Morelyn:
			{
				if (cond == 1)
				{
					htmltext = "Morelyn-01.htm";
				}
				break;
			}
			case Lanya:
			{
				if (cond == 4)
				{
					htmltext = "Lanya-03.htm";
					qs.setCond(5);
					qs.playSound(QuestSound.ITEMSOUND_QUEST_MIDDLE);
				}
				else if (cond == 2)
				{
					htmltext = "Lanya-01.htm";
				}
				else if (cond == 3)
				{
					htmltext = "Lanya-02-again.htm";
				}
				break;
			}
			
		}
		
		return htmltext;
	}
	
	@Override
	public String onSocialActionSee(L2Npc npc, L2PcInstance caster, int actionId)
	{
		final QuestState qs = getQuestState(caster, false);
		
		if ((qs.getCond() == 3) && (actionId == SOCIAL_BOW) && (npc.getId() == Lanya))
		{
			qs.setCond(4);
			qs.playSound(QuestSound.ITEMSOUND_QUEST_MIDDLE);
		}
		
		return super.onSocialActionSee(npc, caster, actionId);
	}
	
	@Override
	public String onSkillSee(L2Npc npc, L2PcInstance caster, Skill skill, L2Object[] targets, boolean isSummon)
	{
		if ((caster == null) || !caster.isPlayer() || (caster.getTarget() == null) || !caster.getTarget().isNpc())
		{
			return null;
		}
		
		final QuestState qs = getQuestState(caster, false);
		
		if (qs == null)
		{
			return null;
		}
		
		int SkillId = skill.getId();
		int npcId = npc.getId();
		int cond = qs.getCond();
		
		switch (SkillId)
		{
			case WATER:
			{
				if ((npcId == MotherTree) && (cond == 18))
				{
					takeItems(caster, Clearestwater, 1L);
					// enterInstance(st.getPlayer());
					qs.setCond(19);
				}
				break;
			}
			case LIGHT:
			{
				if ((npcId == AltarofShilen) && (cond == 16))
				{
					takeItems(caster, Brightestlight, 1L);
					caster.sendPacket(new ExShowScreenMessage(NpcStringId.YOU_MUST_DEFEAT_SHILEN_S_MESSENGER, 4500, ExShowScreenMessage.TOP_CENTER, new String[0]));
					L2Npc mob = qs.addSpawn(ShilensMessenger, 28760, 11032, -4252);
					mob.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, caster, 100000);
				}
				break;
			}
			case SOUL:
			{
				if ((npcId == CaveofSouls) && (cond == 17))
				{
					takeItems(caster, Purestsoul, 1L);
					qs.setCond(18);
				}
				break;
			}
			case FLAME:
			{
				if ((npcId == PaagrioTemple) && (cond == 15))
				{
					takeItems(caster, Fiercestflame, 1L);
					qs.setCond(16);
				}
				break;
			}
			case LOVE:
			{
				if ((npcId == DesertedDwarvenHouse) && (cond == 14))
				{
					takeItems(caster, Fondestheart, 1L);
					qs.setCond(15);
				}
				break;
			}
		}
		return null;
	}
}
