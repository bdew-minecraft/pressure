/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.tank.blocks

import net.bdew.lib.multiblock.tile.TileModule
import net.bdew.lib.tile.inventory.InventoryProxy
import net.bdew.pressure.blocks.tank.BaseModule
import net.bdew.pressure.blocks.tank.controller.TileTankController
import net.minecraft.inventory.ISidedInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing

object BlockTankInterface extends BaseModule("TankInterface", "TankInterface", classOf[TileTankInterface])

class TileTankInterface extends TileModule with ISidedInventory with InventoryProxy {
  val kind: String = "TankInterface"

  override def getCore = getCoreAs[TileTankController]
  override def targetInventory = getCore map (_.inventory)

  override def getSlotsForFace(side: EnumFacing) = Array(0, 1)

  override def canExtractItem(slot: Int, stack: ItemStack, direction: EnumFacing) =
    slot == 1

  override def canInsertItem(slot: Int, stack: ItemStack, direction: EnumFacing) =
    slot == 0 && targetInventory.exists(_.isItemValidForSlot(slot, stack))
}
