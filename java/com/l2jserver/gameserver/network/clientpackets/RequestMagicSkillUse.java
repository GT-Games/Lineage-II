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
package com.l2jserver.gameserver.network.clientpackets;

import com.l2jserver.Config;
import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.data.xml.impl.SkillTreesData;
import com.l2jserver.gameserver.datatables.SkillData;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.effects.L2EffectType;
import com.l2jserver.gameserver.model.skills.AbnormalType;
import com.l2jserver.gameserver.model.skills.CommonSkill;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.skills.targets.L2TargetType;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.ActionFailed;

public final class RequestMagicSkillUse extends L2GameClientPacket
{
	private static final String _C__39_REQUESTMAGICSKILLUSE = "[C] 39 RequestMagicSkillUse";
	
	private int _magicId;
	private boolean _ctrlPressed;
	private boolean _shiftPressed;
	
	@Override
	protected void readImpl()
	{
            _magicId = readD(); // Identifier of the used skill
            _ctrlPressed = readD() != 0; // True if it's a ForceAttack : Ctrl pressed
            _shiftPressed = readC() != 0; // True if Shift pressed
	}
	
	@Override
	protected void runImpl()
	{
            // Get the current L2PcInstance of the player
            final L2PcInstance activeChar = getActiveChar();
            
            if (activeChar == null)
            {
                return;
            }

            if (activeChar.isDead())
            {
                activeChar.sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }

            if (activeChar.isFakeDeath())
            {
                activeChar.sendPacket(SystemMessageId.YOU_CANNOT_MOVE_WHILE_SITTING);
                activeChar.sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }

            Skill SkillInUse = null;

            int MagicSkillUseId = getMagicSkillUseId();

            if(isHeavyHitSkill(MagicSkillUseId))
            {
                SkillInUse = SkillData.getInstance().getSkill(MagicSkillUseId, 1);
            }
            else 
            {
                SkillInUse = SkillData.getInstance().getSkill(MagicSkillUseId, activeChar.getSkillLevel(MagicSkillUseId));
            }     

            if (SkillInUse == null)
            {
                // Player doesn't know this skill, maybe it's the display Id.
                SkillInUse = activeChar.getCustomSkill(MagicSkillUseId);

                if (SkillInUse == null)
                {
                    if ((MagicSkillUseId == CommonSkill.HAIR_ACCESSORY_SET.getId()) || SkillTreesData.getInstance().isSubClassChangeSkill(MagicSkillUseId, 1))
                    {
                        SkillInUse = SkillData.getInstance().getSkill(MagicSkillUseId, 1);
                    }
                    else
                    {
                        SkillInUse = activeChar.getTransformSkill(_magicId);

                        if (SkillInUse == null)
                        {
                            activeChar.sendPacket(ActionFailed.STATIC_PACKET);
                            _log.warning("Skill Id " + MagicSkillUseId + " not found in player : " + activeChar);
                            return;
                        }
                    }
                }
            }
		
            // Avoid Use of Skills in AirShip.
            if (activeChar.isPlayable() && activeChar.isInAirShip())
            {
                activeChar.sendPacket(SystemMessageId.THIS_ACTION_IS_PROHIBITED_WHILE_MOUNTED_OR_ON_AN_AIRSHIP);
                activeChar.sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }

            if ((activeChar.isTransformed() || activeChar.isInStance()) && !activeChar.hasTransformSkill(MagicSkillUseId))
            {
                activeChar.sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }

            // If Alternate rule Karma punishment is set to true, forbid skill Return to player with Karma
            if (!Config.ALT_GAME_KARMA_PLAYER_CAN_TELEPORT && (activeChar.getReputation() < 0) && SkillInUse.hasEffectType(L2EffectType.TELEPORT))
            {
                return;
            }

            // players mounted on pets cannot use any toggle skills
            if (SkillInUse.isToggle() && activeChar.isMounted())
            {
                return;
            }

            // Stop if use self-buff (except if on AirShip or Boat).
            if ((SkillInUse.isContinuous() && !SkillInUse.isDebuff() && (SkillInUse.getTargetType() == L2TargetType.SELF)) && (!activeChar.isInAirShip() || !activeChar.isInBoat()))
            {
                activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, activeChar.getLocation());
            }

            // Avoid of leave all elemental stances disabled.
            if(SkillInUse.getAbnormalType() == AbnormalType.FEOH_STANCE && activeChar.isAffectedBySkill(SkillInUse.getId()))
            {
                return;
            }
           
            SkillInUse = SkillInUse.getElementalSkill(activeChar);
            
            activeChar.useMagic(SkillInUse, _ctrlPressed, _shiftPressed);
	}
	
        public int getMagicSkillUseId()
        {
            return _magicId;
        }
        
        private static boolean isHeavyHitSkill(final int magicId) 
        {
            switch (magicId)
            {
                // Heavy Hit - Inflicts a powerful blow on target
                case 10249: 
                case 10250:
                case 10499:
                case 10500:
                case 10749:
                case 10750:
                case 10999:
                case 11000:
                case 11249:
                case 11250:
                case 11499:
                case 11500:
                case 11749:
                case 11750:
                case 11999:
                case 12000:
                case 15606: 
                {
                    return true;
                }
                default:
                {
                    return false;
                }
            }
       }
            
	@Override
	public String getType()
	{
		return _C__39_REQUESTMAGICSKILLUSE;
	}
}
