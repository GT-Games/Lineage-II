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
package com.l2jserver.gameserver.network.clientpackets.appearance;

import com.l2jserver.gameserver.data.xml.impl.AppearanceItemData;
import com.l2jserver.gameserver.enums.InventorySlot;
import com.l2jserver.gameserver.enums.ItemLocation;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.request.ShapeShiftingItemRequest;
import com.l2jserver.gameserver.model.itemcontainer.PcInventory;
import com.l2jserver.gameserver.model.items.L2Item;
import com.l2jserver.gameserver.model.items.appearance.AppearanceStone;
import com.l2jserver.gameserver.model.items.appearance.AppearanceTargetType;
import com.l2jserver.gameserver.model.items.appearance.AppearanceType;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.model.items.type.ArmorType;
import com.l2jserver.gameserver.model.items.type.WeaponType;
import com.l2jserver.gameserver.model.variables.ItemVariables;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.clientpackets.L2GameClientPacket;
import com.l2jserver.gameserver.network.serverpackets.ExUserInfoEquipSlot;
import com.l2jserver.gameserver.network.serverpackets.InventoryUpdate;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import com.l2jserver.gameserver.network.serverpackets.appearance.ExPutShapeShiftingTargetItemResult;
import com.l2jserver.gameserver.network.serverpackets.appearance.ExShapeShiftingResult;

/**
 * @author UnAfraid
 */
public class RequestShapeShiftingItem extends L2GameClientPacket
{
	private int _targetItemObjId;
	
	@Override
	protected void readImpl()
	{
		_targetItemObjId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		final ShapeShiftingItemRequest request = player.getRequest(ShapeShiftingItemRequest.class);
		
		if (player.isInStoreMode() || player.isInCraftMode() || player.isProcessingRequest() || player.isProcessingTransaction() || (request == null))
		{
			player.sendPacket(ExShapeShiftingResult.FAILED);
			player.sendPacket(SystemMessageId.YOU_CANNOT_USE_THIS_SYSTEM_DURING_TRADING_PRIVATE_STORE_AND_WORKSHOP_SETUP);
			return;
		}
		
		final PcInventory inventory = player.getInventory();
		final L2ItemInstance targetItem = inventory.getItemByObjectId(_targetItemObjId);
		L2ItemInstance stone = request.getAppearanceStone();
		
		if ((targetItem == null) || (stone == null))
		{
			player.sendPacket(ExShapeShiftingResult.FAILED);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		if (stone.getOwnerId() != player.getObjectId())
		{
			player.sendPacket(ExShapeShiftingResult.FAILED);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		if (!targetItem.isAppearanceable())
		{
			player.sendPacket(ExShapeShiftingResult.FAILED);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		if ((targetItem.getItemLocation() != ItemLocation.INVENTORY) && (targetItem.getItemLocation() != ItemLocation.PAPERDOLL))
		{
			player.sendPacket(ExShapeShiftingResult.FAILED);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		if ((stone = inventory.getItemByObjectId(stone.getObjectId())) == null)
		{
			player.sendPacket(ExShapeShiftingResult.FAILED);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		final AppearanceStone appearanceStone = AppearanceItemData.getInstance().getStone(stone.getId());
		if (appearanceStone == null)
		{
			player.sendPacket(ExShapeShiftingResult.FAILED);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		if (((appearanceStone.getType() != AppearanceType.RESTORE) && (targetItem.getVisualId() > 0)) || ((appearanceStone.getType() == AppearanceType.RESTORE) && (targetItem.getVisualId() == 0)))
		{
			player.sendPacket(ExShapeShiftingResult.FAILED);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		// TODO: Handle hair accessory!
		// if (!targetItem.isEtcItem() && (targetItem.getItem().getCrystalType() == CrystalType.NONE))
		{
			// player.sendPacket(ExShapeShiftingResult.FAILED);
			// player.removeRequest(ShapeShiftingItemRequest.class);
			// return;
		}
		
		if (!appearanceStone.getCrystalTypes().isEmpty() && !appearanceStone.getCrystalTypes().contains(targetItem.getItem().getCrystalType()))
		{
			player.sendPacket(ExShapeShiftingResult.FAILED);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		if (appearanceStone.getTargetTypes().isEmpty())
		{
			player.sendPacket(ExShapeShiftingResult.FAILED);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		if (!appearanceStone.getTargetTypes().contains(AppearanceTargetType.ALL))
		{
			if (targetItem.isWeapon() && !appearanceStone.getTargetTypes().contains(AppearanceTargetType.WEAPON))
			{
				player.sendPacket(ExShapeShiftingResult.FAILED);
				player.removeRequest(ShapeShiftingItemRequest.class);
				return;
				
			}
			else if (targetItem.isArmor() && !appearanceStone.getTargetTypes().contains(AppearanceTargetType.ARMOR) && !appearanceStone.getTargetTypes().contains(AppearanceTargetType.ACCESSORY))
			{
				player.sendPacket(ExShapeShiftingResult.FAILED);
				player.removeRequest(ShapeShiftingItemRequest.class);
				return;
			}
			else if (targetItem.isArmor() && !appearanceStone.getBodyParts().isEmpty() && !appearanceStone.getBodyParts().contains(targetItem.getItem().getBodyPart()))
			{
				player.sendPacket(ExPutShapeShiftingTargetItemResult.FAILED);
				player.removeRequest(ShapeShiftingItemRequest.class);
				return;
			}
		}
		
		if (appearanceStone.getWeaponType() != WeaponType.NONE)
		{
			if (!targetItem.isWeapon() || (targetItem.getItemType() != appearanceStone.getWeaponType()))
			{
				player.sendPacket(ExShapeShiftingResult.FAILED);
				player.removeRequest(ShapeShiftingItemRequest.class);
				return;
			}
			
			switch (appearanceStone.getHandType())
			{
				case ONE_HANDED:
				{
					if ((targetItem.getItem().getBodyPart() & L2Item.SLOT_R_HAND) != L2Item.SLOT_R_HAND)
					{
						player.sendPacket(ExShapeShiftingResult.FAILED);
						player.removeRequest(ShapeShiftingItemRequest.class);
						return;
					}
					break;
				}
				case TWO_HANDED:
				{
					if ((targetItem.getItem().getBodyPart() & L2Item.SLOT_LR_HAND) != L2Item.SLOT_LR_HAND)
					{
						player.sendPacket(ExShapeShiftingResult.FAILED);
						player.removeRequest(ShapeShiftingItemRequest.class);
						return;
					}
					break;
				}
			}
			
			switch (appearanceStone.getMagicType())
			{
				case MAGICAL:
				{
					if (!targetItem.getItem().isMagicWeapon())
					{
						player.sendPacket(ExShapeShiftingResult.FAILED);
						player.removeRequest(ShapeShiftingItemRequest.class);
						return;
					}
					break;
				}
				case PHYISICAL:
				{
					if (targetItem.getItem().isMagicWeapon())
					{
						player.sendPacket(ExShapeShiftingResult.FAILED);
						player.removeRequest(ShapeShiftingItemRequest.class);
						return;
					}
				}
			}
		}
		
		if (appearanceStone.getArmorType() != ArmorType.NONE)
		{
			switch (appearanceStone.getArmorType())
			{
				case SHIELD:
				{
					if (!targetItem.isArmor() || (targetItem.getItemType() != ArmorType.SHIELD))
					{
						player.sendPacket(ExShapeShiftingResult.FAILED);
						player.removeRequest(ShapeShiftingItemRequest.class);
						return;
					}
					break;
				}
				case SIGIL:
				{
					if (!targetItem.isArmor() || (targetItem.getItemType() != ArmorType.SIGIL))
					{
						player.sendPacket(ExShapeShiftingResult.FAILED);
						player.removeRequest(ShapeShiftingItemRequest.class);
						return;
					}
				}
			}
		}
		
		final L2ItemInstance extracItem = request.getAppearanceExtractItem();
		
		int extracItemId = 0;
		if ((appearanceStone.getType() != AppearanceType.RESTORE) && (appearanceStone.getType() != AppearanceType.FIXED))
		{
			if (extracItem == null)
			{
				player.sendPacket(ExShapeShiftingResult.FAILED);
				player.removeRequest(ShapeShiftingItemRequest.class);
				return;
			}
			
			if (!extracItem.isAppearanceable())
			{
				player.sendPacket(ExShapeShiftingResult.FAILED);
				player.removeRequest(ShapeShiftingItemRequest.class);
				return;
			}
			
			if ((extracItem.getItemLocation() != ItemLocation.INVENTORY) && (extracItem.getItemLocation() != ItemLocation.PAPERDOLL))
			{
				player.sendPacket(ExShapeShiftingResult.FAILED);
				player.removeRequest(ShapeShiftingItemRequest.class);
				return;
			}
			
			if (!extracItem.isEtcItem() && (targetItem.getItem().getCrystalType().ordinal() <= extracItem.getItem().getCrystalType().ordinal()))
			{
				player.sendPacket(ExShapeShiftingResult.FAILED);
				player.removeRequest(ShapeShiftingItemRequest.class);
				return;
			}
			
			if (extracItem.getVisualId() > 0)
			{
				player.sendPacket(ExShapeShiftingResult.FAILED);
				player.removeRequest(ShapeShiftingItemRequest.class);
				return;
			}
			
			if (extracItem.getOwnerId() != player.getObjectId())
			{
				player.sendPacket(ExShapeShiftingResult.FAILED);
				player.removeRequest(ShapeShiftingItemRequest.class);
				return;
			}
			extracItemId = extracItem.getId();
		}
		
		if (targetItem.getOwnerId() != player.getObjectId())
		{
			player.sendPacket(ExShapeShiftingResult.FAILED);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		long cost = appearanceStone.getCost();
		if (cost > player.getAdena())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_MODIFY_AS_YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			player.sendPacket(ExShapeShiftingResult.FAILED);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		if (stone.getCount() < 1L)
		{
			player.sendPacket(ExShapeShiftingResult.FAILED);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		if (appearanceStone.getType() == AppearanceType.NORMAL)
		{
			if (inventory.destroyItem(getClass().getSimpleName(), extracItem, 1, player, this) == null)
			{
				player.sendPacket(ExShapeShiftingResult.FAILED);
				player.removeRequest(ShapeShiftingItemRequest.class);
				return;
			}
		}
		
		inventory.destroyItem(getClass().getSimpleName(), stone, 1, player, this);
		player.reduceAdena(getClass().getSimpleName(), cost, extracItem, true);
		
		switch (appearanceStone.getType())
		{
			case RESTORE:
			{
				targetItem.setVisualId(0);
				targetItem.getVariables().set(ItemVariables.VISUAL_APPEARANCE_STONE_ID, 0);
				break;
			}
			case NORMAL:
			{
				targetItem.setVisualId(extracItem.getId());
				break;
			}
			case BLESSED:
			{
				targetItem.setVisualId(extracItem.getId());
				break;
			}
			case FIXED:
			{
				targetItem.setVisualId(appearanceStone.getVisualId());
				targetItem.getVariables().set(ItemVariables.VISUAL_APPEARANCE_STONE_ID, appearanceStone.getId());
				break;
			}
		}
		
		if ((appearanceStone.getType() != AppearanceType.RESTORE) && (appearanceStone.getLifeTime() > 0))
		{
			targetItem.getVariables().set(ItemVariables.VISUAL_APPEARANCE_LIFE_TIME, System.currentTimeMillis() + appearanceStone.getLifeTime());
			targetItem.scheduleVisualLifeTime();
		}
		
		targetItem.getVariables().storeMe();
		if (appearanceStone.getCost() > 0)
		{
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_SPENT_S1_ON_A_SUCCESSFUL_APPEARANCE_MODIFICATION).addLong(cost));
		}
		else
		{
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_S_APPEARANCE_MODIFICATION_HAS_FINISHED).addItemName(targetItem.getDisplayId()));
		}
		
		final InventoryUpdate iu = new InventoryUpdate();
		iu.addModifiedItem(targetItem);
		
		if (inventory.getItemByObjectId(stone.getObjectId()) == null)
		{
			iu.addRemovedItem(stone);
		}
		else
		{
			iu.addModifiedItem(stone);
		}
		
		if (appearanceStone.getType() == AppearanceType.NORMAL)
		{
			if (inventory.getItemByObjectId(extracItem.getObjectId()) == null)
			{
				iu.addRemovedItem(extracItem);
			}
			else
			{
				iu.addModifiedItem(extracItem);
			}
		}
		
		player.sendPacket(iu);
		
		player.removeRequest(ShapeShiftingItemRequest.class);
		player.sendPacket(new ExShapeShiftingResult(ExShapeShiftingResult.RESULT_SUCCESS, targetItem.getId(), extracItemId));
		if (targetItem.isEquipped())
		{
			player.broadcastUserInfo();
			final ExUserInfoEquipSlot slots = new ExUserInfoEquipSlot(player, false);
			for (InventorySlot slot : InventorySlot.values())
			{
				if (slot.getSlot() == targetItem.getLocationSlot())
				{
					slots.addComponentType(slot);
				}
			}
			player.sendPacket(slots);
		}
	}
	
	@Override
	public String getType()
	{
		return getClass().getSimpleName();
	}
}
