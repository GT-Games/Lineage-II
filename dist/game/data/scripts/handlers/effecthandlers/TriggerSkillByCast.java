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

package handlers.effecthandlers;

import com.l2jserver.gameserver.handler.ITargetTypeHandler;
import com.l2jserver.gameserver.handler.TargetHandler;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.model.skills.targets.L2TargetType;
import com.l2jserver.util.Rnd;

/**
 * Force Skill effect implementation.
 * @author Helius
 */
public final class TriggerSkillByCast extends AbstractEffect
{
    private final SkillHolder _skill;
    private final int _castSkillId;
    private final int _chance;
    private final L2TargetType _targetType;
    /**
     * @param attachCond
     * @param applyCond
     * @param set
     * @param params
     */
    public TriggerSkillByCast(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params)
    {
        super(attachCond, applyCond, set, params);

        _castSkillId = params.getInt("castSkillId", 0);
        _chance = params.getInt("chance", 100);
        _skill = new SkillHolder(params.getInt("skillId", 0), params.getInt("skillLevel", 0));
        _targetType = params.getEnum("targetType", L2TargetType.class, L2TargetType.ONE);
    }

    @Override
    public void onStart(BuffInfo info)
    {
        final L2PcInstance effector = info.getEffector().getActingPlayer();

        if (Rnd.get(100) > _chance || (_castSkillId == 0 && _skill.getSkillId() == 0 && _skill.getSkillLvl() == 0))
        {
            if(_castSkillId == 0 && _skill.getSkillId() == 0 && _skill.getSkillLvl() == 0)
            {
                _log.warning("\n There's something assigned as 0, castSkillId: " + _castSkillId);
                _log.warning("\n SkillId: " + _skill.getSkillId() + " SkilLLvl: " + _skill.getSkillLvl());
            } 
                return;
        }

        final ITargetTypeHandler targetHandler = TargetHandler.getInstance().getHandler(_targetType);

        if (targetHandler == null)
        {
                _log.warning("Handler for target type: " + _targetType + " does not exist.");
                
                return;
        }

             _skill.getSkill().applyEffects(effector, effector);
    }
}