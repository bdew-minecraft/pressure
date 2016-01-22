/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.tank.gui

import net.bdew.lib.data.base.ContainerDataSlots
import net.bdew.lib.gui.{BaseContainer, SlotClickable, SlotValidating}
import net.bdew.lib.multiblock.interact.ContainerOutputFaces
import net.bdew.pressure.blocks.tank.controller.TileTankController
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Slot
import net.minecraftforge.fluids.{FluidContainerRegistry, IFluidContainerItem}

class ContainerTank(val te: TileTankController, player: EntityPlayer) extends BaseContainer(te.inventory) with ContainerDataSlots with ContainerOutputFaces {
  lazy val dataSource = te

  addSlotToContainer(new SlotValidating(te.inventory, 0, 44, 19))
  addSlotToContainer(new SlotValidating(te.inventory, 1, 80, 19))

  addSlotToContainer(new Slot(te.inventory, 2, 149, 19) with SlotClickable {
    override def onClick(button: Int, mods: Int, player: EntityPlayer) = {
      val stack = player.inventory.getItemStack
      if (!te.getWorldObj.isRemote) {
        if (stack == null || stack.getItem == null) {
          te.fluidFilter := null
        } else if (FluidContainerRegistry.isFilledContainer(stack)) {
          te.fluidFilter := FluidContainerRegistry.getFluidForFilledItem(stack).getFluid.getName
        } else if (stack.getItem.isInstanceOf[IFluidContainerItem]) {
          val fluid = stack.getItem.asInstanceOf[IFluidContainerItem].getFluid(stack)
          if (fluid != null && fluid.getFluid != null)
            te.fluidFilter := fluid.getFluid.getName
        }
      }
      stack
    }
  })

  bindPlayerInventory(player.inventory, 8, 84, 142)
}
