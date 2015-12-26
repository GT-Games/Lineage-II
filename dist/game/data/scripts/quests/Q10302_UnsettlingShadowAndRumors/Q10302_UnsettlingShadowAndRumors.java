package quests.Q10302_UnsettlingShadowAndRumors;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

public class Q10302_UnsettlingShadowAndRumors extends Quest
{
	
	// NPCS
	private static final int KANTARUBIS = 32898;
	private static final int IZAEL = 32894;
	
	private static final int CAS = 32901;
	private static final int MR_KAY = 32903;
	private static final int KITT = 32902;
	
	// Other
	private static final int MAX_LEVEL = 90;
	
	// Rewards
	private static final int EXP_REWARD = 15197798;
	private static final int SP_REWARD = 3647;

	private static final int ITEM_REWARD = 57;
	private static final int ITEM_COUNT = 47085998;
	
	private static final int ITEM_REWARD_2 = 34033;
	private static final int ITEM_COUNT_2 = 1;
	
	public Q10302_UnsettlingShadowAndRumors()
	{
		super(10302, Q10302_UnsettlingShadowAndRumors.class.getSimpleName(), "Let's Start!");
		addStartNpc(KANTARUBIS);
		addTalkId(KANTARUBIS);
		addTalkId(IZAEL);
		addTalkId(CAS);
		addTalkId(MR_KAY);
		addTalkId(KITT);
		addCondMaxLevel(MAX_LEVEL, "32898-lvl.htm");
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = null;
		switch (event)
		{
			case "32898-4.htm":
			{
				qs.setCond(1);
				qs.startQuest();
				// qs.playSound(sound); sound accept quest.
				qs.isCompleted();
				htmltext = event;
				break;
			}
			case "32898-8.htm":
			{
				qs.addExpAndSp(EXP_REWARD, SP_REWARD);
				qs.giveItems(ITEM_REWARD, ITEM_COUNT);
				qs.giveItems(ITEM_REWARD_2, ITEM_COUNT_2);
				// qs.playSound(sound); sound quest finish
				qs.exitQuest(false);
				htmltext = event;
				break;
			}
			case "32894-1.htm":
			{
				// qs.playSound(sound); play sound of middle quest
				qs.setCond(2);
				htmltext = event;
				break;
			}
			case "32901-1.htm":
			{
				// qs.playSound(sound); play sound of middle quest
				qs.setCond(3);
				htmltext = event;
				break;
			}
			case "32903-1.htm":
			{
				// qs.playSound(sound); play sound of middle quest
				qs.setCond(4);
				htmltext = event;
				break;
			}
			case "32902-1.htm":
			{
				// qs.playSound(sound); play sound of middle quest
				qs.setCond(5);
				htmltext = event;
				break;
			}
			case "32894-5.htm":
			{
				// qs.playSound(sound); play sound of middle quest
				qs.setCond(6);
				htmltext = event;
				break;
			}
			
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		
		String htmltext = null;
		
		int state = qs.getState();
		
		if ((qs == null) || !qs.isCompleted())
		{
			return "32898-lvl.htm";
		}
		
		if (state == State.COMPLETED)
		{
			return "32898-com.html";
		}
		
		if (qs.getPlayer().getLevel() < MAX_LEVEL)
		{
			return "32898-lvl.htm";
		}
		
		if (npc.getId() == KANTARUBIS)
		{
			if (qs.getCond() == 0)
			{
				return "32898.htm";
			}
			else if ((qs.getCond() >= 1) && (qs.getCond() < 6))
			{
				return "32898-5.htm";
			}
			else if (qs.getCond() == 6)
			{
				return "32898-6.htm";
			}
		}
		else if (npc.getId() == IZAEL)
		{
			if (qs.getCond() == 1)
			{
				return "32894.htm";
			}
			else if ((qs.getCond() >= 2) && (qs.getCond() < 5))
			{
				return "32894-2.htm";
			}
			else if (qs.getCond() == 5)
			{
				return "32894-3.htm";
			}
			else if (qs.getCond() == 6)
			{
				return "32894-6.htm";
			}
		}
		else if (npc.getId() == CAS)
		{
			if (qs.getCond() == 2)
			{
				return "32901.htm";
			}
			return "32901-2.htm";
		}
		else if (npc.getId() == MR_KAY)
		{
			if (qs.getCond() == 3)
			{
				return "32903.htm";
			}
			return "32903-2.htm";
		}
		else if (npc.getId() == KITT)
		{
			if (qs.getCond() == 4)
			{
				return "32902.htm";
			}
			return "32902-2.htm";
		}
		
		return htmltext;
	}
	
}