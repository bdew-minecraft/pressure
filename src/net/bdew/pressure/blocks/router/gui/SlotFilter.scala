/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.router.gui

import net.bdew.lib.Misc
import net.bdew.lib.gui.SlotClickable
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.{FluidContainerRegistry, IFluidContainerItem}

class SlotFilter(container: ContainerRouter, index: Int, x: Int, y: Int) extends Slot(container.inventory, index, x, y) with SlotClickable {
  val dir = Misc.forgeDirection(index)
  override def onClick(button: Int, mods: Int, player: EntityPlayer): ItemStack = {
    val stack = player.inventory.getItemStack
    if (!container.te.getWorldObj.isRemote) {
      if (stack == null || stack.getItem == null) {
        container.te.sideFilters.clear(dir)
      } else if (FluidContainerRegistry.isFilledContainer(stack)) {
        container.te.sideFilters.set(dir, FluidContainerRegistry.getFluidForFilledItem(stack).getFluid)
      } else if (stack.getItem.isInstanceOf[IFluidContainerItem]) {
        val fluid = stack.getItem.asInstanceOf[IFluidContainerItem].getFluid(stack)
        if (fluid != null && fluid.getFluid != null)
          container.te.sideFilters.set(dir, fluid.getFluid)
      }
    }
    stack
  }
}
