/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/pressure/master/MMPL-1.0.txt
 */

package net.bdew.pressure.blocks

import net.bdew.lib.block.HasTE
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.{IBlockAccess, World}
import net.minecraftforge.fluids.{FluidRegistry, FluidContainerRegistry}
import net.minecraft.item.Item
import net.bdew.lib.Misc
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraftforge.common.ForgeDirection
import net.bdew.lib.rotate.{IconType, BaseRotateableBlock}

trait BlockFilterable[T <: TileFilterable] extends BaseRotateableBlock with HasTE[T] {
  override def onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, meta: Int, xoffs: Float, yoffs: Float, zoffs: Float): Boolean = {
    if (!player.isSneaking) {
      val item = Option(player.getCurrentEquippedItem)
      if (item exists (_.getItem == Item.bucketEmpty)) {
        if (!world.isRemote) {
          getTE(world, x, y, z).fluidFilter := null
          player.addChatMessage(Misc.toLocal("pressure.label.filter.unset"))
        }
        return true
      }
      val fluid = item flatMap (x => Option(FluidContainerRegistry.getFluidForFilledItem(x))) getOrElse (return false)
      if (!world.isRemote) {
        getTE(world, x, y, z).fluidFilter := fluid.getFluid.getName
        player.addChatMessage(Misc.toLocalF("pressure.label.filter.set", fluid.getFluid.getLocalizedName))
      }
      return true
    }
    return false
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
