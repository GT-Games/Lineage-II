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
package com.l2jserver.gameserver.model.zone.type;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import com.l2jserver.Config;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.zone.L2ZoneType;
import com.l2jserver.gameserver.model.zone.ZoneId;
import com.l2jserver.gameserver.network.serverpackets.ExAutoFishAvailable;

/**
 * A fishing zone
 * @author durgus, Darky999, Thonygez
 */
public class L2FishingZone extends L2ZoneType
{
    private final Map<Integer, Future<?>> _task = new HashMap<>();
    int _fishx = 0;
    int _fishy = 0;
    int _fishz = 0;

    public L2FishingZone(int id)
    {
        super(id);
    }

    @Override
    protected void onEnter(L2Character character)
    {
        if (!isInsideZone(character))
        {
           stopTask(character);
           return;
        }

        final L2PcInstance player = (L2PcInstance) character;

        if(player.isPlayable() || player.getLevel() > 85 && player.isPlayer())
        {
           character.setInsideZone(ZoneId.FISHING, true);

           _task.put(player.getObjectId(), ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new fishingAvailable(player), 500, 2000));
        }
    }

    @Override
    protected void onExit(L2Character character)
    {
        if (character.isPlayer())
        {
            character.setInsideZone(ZoneId.FISHING, false);
        }
        stopTask(character);
    }

    @Override
    public void onDieInside(L2Character character)
    {
        onExit(character);
    }

    @Override
    public void onReviveInside(L2Character character)
    {
        onEnter(character);
    }

    /*
     * getWaterZ() this added function returns the Z value for the water surface. In effect this simply returns the upper Z value of the zone. This required some modification of L2ZoneForm, and zone form extentions.
     */
    public int getWaterZ()
    {
        return getZone().getHighZ();
    }

    protected void stopTask(L2Character character)
    {
        int playerObjectId = character.getObjectId();

        Future<?> t = _task.get(playerObjectId);

        _task.remove(playerObjectId);

        if (t != null)
        {
            t.cancel(false);
        }
    }

    class fishingAvailable implements Runnable
    {
        private final L2PcInstance _player;

        fishingAvailable(L2PcInstance player)
        {
            _player = player;
        }

        @Override
        public void run()
        {
            if (Config.ALLOWFISHING && _player.isInsideZone(ZoneId.FISHING) && !_player.isInsideZone(ZoneId.WATER) && !_player.isInBoat() && !_player.isInCraftMode() && !_player.isInStoreMode() && !_player.isTransformed())
            {
                _player.sendPacket(new ExAutoFishAvailable(_player));
                stopTask(_player);
            }
        }
    }
}