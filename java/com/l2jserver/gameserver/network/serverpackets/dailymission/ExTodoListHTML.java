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
package com.l2jserver.gameserver.network.serverpackets.dailymission;

import com.l2jserver.gameserver.network.serverpackets.L2GameServerPacket;

/**
 * @author Mobius
 */
public class ExTodoListHTML extends L2GameServerPacket
{
	public ExTodoListHTML()
	{
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(0xFE);
		writeH(0x18C);
		writeC(0x01); // TodoListTab. 1=event List 2=zone List
		writeS(""); // HTML name
		writeS(""); // PreformattedHtml
	}
}
