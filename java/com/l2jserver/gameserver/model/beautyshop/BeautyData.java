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
package com.l2jserver.gameserver.model.beautyshop;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Sdw
 */
public class BeautyData
{
	private final Map<Integer, BeautyItem> _hairList = new HashMap<>();
	private final Map<Integer, BeautyItem> _faceList = new HashMap<>();
	
	public final void addHair(BeautyItem hair)
	{
		_hairList.put(hair.getId(), hair);
	}
	
	public final void addFace(BeautyItem face)
	{
		_faceList.put(face.getId(), face);
	}
	
	public final Map<Integer, BeautyItem> getHairList()
	{
		return _hairList;
	}
	
	public final Map<Integer, BeautyItem> getFaceList()
	{
		return _faceList;
	}
}
