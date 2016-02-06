/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks

import net.bdew.lib.block.{BaseBlock, HasTE}
import net.bdew.pressure.model.FluidFilterProperty
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.{BlockPos, ChatComponentTranslation, EnumFacing}
import net.minecraft.world.{IBlockAccess, World}
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.fluids.{FluidContainerRegistry, IFluidContainerItem}

trait BlockFilterable extends BaseBlock {
  self: HasTE[_ <: TileFilterable] =>

  override def getUnlistedProperties = super.getUnlistedProperties :+ FluidFilterProperty

  override def getExtendedState(state: IBlockState, world: IBlockAccess, pos: BlockPos): IBlockState = {
    val st = super.getExtendedState(state, world, pos).asInstanceOf[IExtendedBlockState]
    getTE(world, pos).flatMap(_.getFluidFilter).map(fluid => st.withProperty(FluidFilterProperty, fluid)).getOrElse(st)
  }

  override def onBlockActivated(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean = {
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
        getTE(world, pos).clearFluidFilter()
        player.addChatMessage(new ChatComponentTranslation("pressure.label.filter.unset"))
      } else {
        getTE(world, pos).setFluidFilter(newFilter.getFluid)
        player.addChatMessage(new ChatComponentTranslation("pressure.label.filter.set", newFilter.getFluid.getLocalizedName(newFilter)))
      }

      true
    } else false
  }
}
