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
import net.bdew.pressure.blocks.tank.BaseModule
import net.bdew.pressure.blocks.tank.controller.TileTankController
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.ISidedInventory
import net.minecraft.item.ItemStack

object BlockTankInterface extends BaseModule("TankInterface", "TankInterface", classOf[TileTankInterface])

class TileTankInterface extends TileModule with ISidedInventory {
  val kind: String = "TankInterface"

  override def getCore = getCoreAs[TileTankController]

  def getInv = getCore map (_.inventory)

  override def getSizeInventory = if (getCore.isDefined) 2 else 0
  override def getInventoryStackLimit = 64
  override def getAccessibleSlotsFromSide(side: Int) = if (getCore.isDefined) Array(0, 1) else Array.empty
  override def canExtractItem(slot: Int, item: ItemStack, side: Int) =
    slot == 1
  override def canInsertItem(slot: Int, item: ItemStack, side: Int) =
    slot == 0 && getInv.exists(_.isItemValidForSlot(slot, item))

  override def decrStackSize(slot: Int, amount: Int) = getInv.map(_.decrStackSize(slot, amount)).orNull
  override def isItemValidForSlot(slot: Int, item: ItemStack) = getInv.exists(_.isItemValidForSlot(slot, item))
  override def getStackInSlotOnClosing(slot: Int) = getInv.map(_.getStackInSlotOnClosing(slot)).orNull
  override def setInventorySlotContents(slot: Int, item: ItemStack) = getInv.map(_.setInventorySlotContents(slot, item))
  override def getStackInSlot(slot: Int) = getInv.map(_.getStackInSlot(slot)).orNull

  override def isUseableByPlayer(player: EntityPlayer) = player.getDistance(xCoord, yCoord, zCoord) <= 5D

  override def hasCustomInventoryName = false

  override def getInventoryName = ""
  override def closeInventory() {}
  override def openInventory() {}
}
