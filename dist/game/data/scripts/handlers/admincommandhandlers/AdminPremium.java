/*
 * Copyright (C) 2004-2015 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.admincommandhandlers;

import java.text.SimpleDateFormat;

import com.l2jserver.Config;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.instancemanager.PremiumManager;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * @author Mobius
 */
public class AdminPremium implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_premium_menu",
		"admin_premium_add1",
		"admin_premium_add2",
		"admin_premium_add3",
		"admin_premium_info",
		"admin_premium_remove"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (command.equals("admin_premium_menu"))
		{
			AdminHtml.showAdminHtml(activeChar, "premium_menu.htm");
		}
		else if (command.startsWith("admin_premium_add1"))
		{
			try
			{
				String val = command.substring(19);
				addPremiumStatus(activeChar, 1, val);
			}
			catch (StringIndexOutOfBoundsException e)
			{
				activeChar.sendMessage("Please enter a valid account name.");
			}
		}
		else if (command.startsWith("admin_premium_add2"))
		{
			try
			{
				String val = command.substring(19);
				addPremiumStatus(activeChar, 2, val);
			}
			catch (StringIndexOutOfBoundsException e)
			{
				activeChar.sendMessage("Please enter a valid account name.");
			}
		}
		else if (command.startsWith("admin_premium_add3"))
		{
			try
			{
				String val = command.substring(19);
				addPremiumStatus(activeChar, 3, val);
			}
			catch (StringIndexOutOfBoundsException e)
			{
				activeChar.sendMessage("Please enter a valid account name.");
			}
		}
		else if (command.startsWith("admin_premium_info"))
		{
			try
			{
				String val = command.substring(19);
				viewPremiumInfo(activeChar, val);
			}
			catch (StringIndexOutOfBoundsException e)
			{
				activeChar.sendMessage("Please enter a valid account name.");
			}
		}
		else if (command.startsWith("admin_premium_remove"))
		{
			try
			{
				String val = command.substring(21);
				removePremium(activeChar, val);
			}
			catch (StringIndexOutOfBoundsException e)
			{
				activeChar.sendMessage("Please enter a valid account name.");
			}
		}
		
		final NpcHtmlMessage html = new NpcHtmlMessage(0, 0);
		html.setHtml(HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "html/admin/premium_menu.htm"));
		activeChar.sendPacket(html);
		return true;
	}
	
	private void addPremiumStatus(L2PcInstance admin, int months, String accountName)
	{
		if (!Config.PREMIUM_SYSTEM_ENABLED)
		{
			admin.sendMessage("Premium system is disabled.");
			return;
		}
		
		// TODO: Add check if account exists XD
		PremiumManager.getInstance().updatePremiumData(months, accountName);
		final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		final long endDate = PremiumManager.getInstance().getPremiumEndDate(accountName);
		admin.sendMessage("Account " + accountName + " will now have premium status until " + String.valueOf(format.format(endDate)) + ".");
	}
	
	private void viewPremiumInfo(L2PcInstance admin, String accountName)
	{
		if (PremiumManager.getInstance().getPremiumEndDate(accountName) > 0)
		{
			final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
			final long endDate = PremiumManager.getInstance().getPremiumEndDate(accountName);
			admin.sendMessage("Account " + accountName + " has premium status until " + String.valueOf(format.format(endDate)) + ".");
		}
		else
		{
			admin.sendMessage("Account " + accountName + " has no premium status.");
		}
	}
	
	private void removePremium(L2PcInstance admin, String accountName)
	{
		if (PremiumManager.getInstance().getPremiumEndDate(accountName) > 0)
		{
			PremiumManager.getInstance().removePremiumStatus(accountName);
			admin.sendMessage("Account " + accountName + " has no longer premium status.");
		}
		else
		{
			admin.sendMessage("Account " + accountName + " has no premium status.");
		}
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}