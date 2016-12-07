/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.misc

import net.bdew.lib.capabilities.helpers.FluidHelper
import net.bdew.lib.gui.SlotClickable
import net.bdew.pressure.api.properties.IFilterable
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.{ClickType, IInventory, Slot}
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing

class SlotFilter(container: IInventory, filterable: IFilterable, index: Int, x: Int, y: Int) extends Slot(container, index, x, y) with SlotClickable {
  val dir = EnumFacing.getFront(index)

  override def onClick(clickType: ClickType, button: Int, player: EntityPlayer): ItemStack = {
    val stack = player.inventory.getItemStack
    if (!player.world.isRemote) {
      if (stack.isEmpty) {
        filterable.clearFluidFilter()
      } else if (FluidHelper.hasFluidHandler(stack)) {
        for {
          handler <- FluidHelper.getFluidHandler(stack)
          tank <- handler.getTankProperties
          stack <- Option(tank.getContents)
          fluid <- Option(stack.getFluid)
        } {
          filterable.setFluidFilter(fluid)
        }
      }
    }
    stack
  }
}
