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
import com.l2jserver.gameserver.network.serverpackets.ExNotifyFlyMoveStart;

/**
 * L2JumpZone zones
 * @author ALF (r2max), Thonygez
 */
public class L2JumpZone extends L2ZoneType
{
    private final Map<Integer, Future<?>> _task = new HashMap<>();
    private final int _startTask;
    private final int _reuseTask;
    private int _trackId;

    public L2JumpZone(int id)
    {
        super(id);

        _startTask = 10;
        _reuseTask = 500;
        _trackId = -1;
    }

    @Override
    public void setParameter(String name, String value)
    {
        if (name.equals("trackId"))
        {
                _trackId = Integer.parseInt(value);
        }
        else
        {
                super.setParameter(name, value);
        }
    }

    public int getTrackId()
    {
        return _trackId;
    }

    @Override
    protected void onEnter(L2Character character)
    {
        if (!isInsideZone(character))
        {
            stopTask(character);
            return;
        }

        if (character.isPlayer() && character instanceof L2PcInstance)
        {
            character.setInsideZone(ZoneId.JUMP, true);

            L2PcInstance player = (L2PcInstance) character;

            if (!player.isAwaken() && !Config.FREE_JUMPS_FOR_ALL)
            {
                return;
            }
            _task.put(player.getObjectId(), ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new JumpReq(player), _startTask, _reuseTask));            
        }
    }

    @Override
    protected void onExit(L2Character character)
    {
        if (character.isPlayer())
        {
            character.setInsideZone(ZoneId.JUMP, false);
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

    class JumpReq implements Runnable
    {
        private final L2PcInstance _player;

        JumpReq(L2PcInstance player)
        {
            _player = player;
        }

        @Override
        public void run()
        {
            _player.sendPacket(new ExNotifyFlyMoveStart());
            stopTask(_player);
        }
    }
}
