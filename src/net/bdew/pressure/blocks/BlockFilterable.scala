/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/pressure/master/MMPL-1.0.txt
 */

package net.bdew.pressure.blocks

import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.lib.block.HasTE
import net.bdew.lib.rotate.{BaseRotateableBlock, IconType}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ChatComponentTranslation
import net.minecraft.world.{IBlockAccess, World}
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.fluids.{FluidContainerRegistry, FluidRegistry, IFluidContainerItem}

trait BlockFilterable[T <: TileFilterable] extends BaseRotateableBlock with HasTE[T] {
  override def onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, meta: Int, xoffs: Float, yoffs: Float, zoffs: Float): Boolean = {
    if (!player.isSneaking && player.getCurrentEquippedItem != null) {
      val item = player.getCurrentEquippedItem

      val newFlilter =
        if (FluidContainerRegistry.isEmptyContainer(item)) {
          null
        } else if (FluidContainerRegistry.isFilledContainer(item)) {
          FluidContainerRegistry.getFluidForFilledItem(item)
        } else if (item.getItem.isInstanceOf[IFluidContainerItem]) {
          item.getItem.asInstanceOf[IFluidContainerItem].getFluid(item)
        } else {
          return false
        }

      if (world.isRemote) return true

      if (newFlilter == null) {
        getTE(world, x, y, z).fluidFilter := null
        player.addChatMessage(new ChatComponentTranslation("pressure.label.filter.unset"))
      } else {
        getTE(world, x, y, z).fluidFilter := newFlilter.getFluid.getName
        player.addChatMessage(new ChatComponentTranslation("pressure.label.filter.set", newFlilter.getFluid.getLocalizedName(newFlilter)))
      }

      true
    } else false
  }

  @SideOnly(Side.CLIENT)
  def getFilterIcon(w: IBlockAccess, x: Int, y: Int, z: Int, side: ForgeDirection) =
    if (IconType.fromSideAndDir(side.ordinal(), getFacing(w, x, y, z)) == IconType.SIDE)
      for {
        name <- Option(getTE(w, x, y, z).fluidFilter.cval)
        fluid <- Option(FluidRegistry.getFluid(name))
        icon <- Option(fluid.getStillIcon)
      } yield icon
    else None
}
