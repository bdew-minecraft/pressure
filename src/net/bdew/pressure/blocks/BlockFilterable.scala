/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks

import net.bdew.lib.PimpVanilla._
import net.bdew.lib.block.{BaseBlock, HasTE}
import net.bdew.pressure.model.FluidFilterProperty
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.util.{EnumFacing, EnumHand}
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

  override def onBlockActivated(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, hand: EnumHand, heldItem: ItemStack, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean = {
    if (!player.isSneaking && heldItem != null) {
      val newFilter =
        if (FluidContainerRegistry.isEmptyContainer(heldItem)) {
          null
        } else if (FluidContainerRegistry.isFilledContainer(heldItem)) {
          FluidContainerRegistry.getFluidForFilledItem(heldItem)
        } else if (heldItem.getItem.isInstanceOf[IFluidContainerItem]) {
          heldItem.getItem.asInstanceOf[IFluidContainerItem].getFluid(heldItem)
        } else {
          return false
        }

      if (world.isRemote) return true

      if (newFilter == null) {
        getTE(world, pos).FilterableImpl.clearFluidFilter()
        player.addChatMessage(new TextComponentTranslation("pressure.label.filter.unset"))
      } else {
        getTE(world, pos).FilterableImpl.setFluidFilter(newFilter.getFluid)
        player.addChatMessage(new TextComponentTranslation("pressure.label.filter.set", newFilter.getFluid.getLocalizedName(newFilter)))
      }

      true
    } else false
  }
}
