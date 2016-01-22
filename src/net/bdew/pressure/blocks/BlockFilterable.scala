/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks

import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.lib.Misc
import net.bdew.lib.block.HasTE
import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ChatComponentTranslation
import net.minecraft.world.{IBlockAccess, World}
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.fluids.{FluidContainerRegistry, FluidRegistry, IFluidContainerItem}

trait BlockFilterable[T <: TileFilterable] extends Block with HasTE[T] {
  override def onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, meta: Int, xOffs: Float, yOffs: Float, zOffs: Float): Boolean = {
    if (!player.isSneaking && player.getCurrentEquippedItem != null) {
      val item = player.getCurrentEquippedItem

      val newFilter =
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

      if (newFilter == null) {
        getTE(world, x, y, z).fluidFilter := null
        player.addChatMessage(new ChatComponentTranslation("pressure.label.filter.unset"))
      } else {
        getTE(world, x, y, z).fluidFilter := newFilter.getFluid.getName
        player.addChatMessage(new ChatComponentTranslation("pressure.label.filter.set", newFilter.getFluid.getLocalizedName(newFilter)))
      }

      true
    } else false
  }

  def shouldShowFilterIconOnSide(w: IBlockAccess, x: Int, y: Int, z: Int, side: ForgeDirection): Boolean

  @SideOnly(Side.CLIENT)
  def getFilterIcon(w: IBlockAccess, x: Int, y: Int, z: Int, side: ForgeDirection) =
    if (shouldShowFilterIconOnSide(w, x, y, z, side))
      for {
        name <- Option(getTE(w, x, y, z).fluidFilter.value)
        fluid <- Option(FluidRegistry.getFluid(name))
      } yield (Misc.getFluidIcon(fluid), Misc.getFluidColor(fluid))
    else None
}
