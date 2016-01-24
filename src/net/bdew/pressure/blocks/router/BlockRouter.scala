/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.router

import net.bdew.lib.block.{BaseBlock, HasTE}
import net.bdew.lib.property.SimpleUnlistedProperty
import net.bdew.pressure.api.IPressureConnectableBlock
import net.bdew.pressure.blocks.BlockNotifyUpdates
import net.bdew.pressure.blocks.router.data.RouterSideMode
import net.bdew.pressure.{Pressure, PressureResourceProvider}
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.{BlockPos, EnumFacing, EnumWorldBlockLayer}
import net.minecraft.world.{IBlockAccess, World}
import net.minecraftforge.common.property.IExtendedBlockState

object BlockRouter extends BaseBlock("Router", Material.iron) with HasTE[TileRouter] with BlockNotifyUpdates with IPressureConnectableBlock {
  override val TEClass = classOf[TileRouter]
  val cfg = MachineRouter

  setHardness(2)

  object Properties {
    val MODE = EnumFacing.values().map(f => f -> new SimpleUnlistedProperty(f.getName, classOf[RouterSideMode.Value])).toMap
  }

  override def getExtendedState(state: IBlockState, world: IBlockAccess, pos: BlockPos) = {
    val te = getTE(world, pos)
    EnumFacing.values().foldRight(super.getExtendedState(state, world, pos).asInstanceOf[IExtendedBlockState]) { (face, state) =>
      state.withProperty(Properties.MODE(face), te.sideModes.get(face))
    }
  }

  override def canRenderInLayer(layer: EnumWorldBlockLayer) =
    layer == EnumWorldBlockLayer.SOLID || layer == EnumWorldBlockLayer.CUTOUT

  override def getUnlistedProperties = super.getUnlistedProperties ++ Properties.MODE.values

  override def canConnectTo(world: IBlockAccess, pos: BlockPos, side: EnumFacing) =
    getTE(world, pos).sideModes.get(side) != RouterSideMode.DISABLED

  override def isTraversable(world: IBlockAccess, pos: BlockPos) = false

  override def onBlockActivated(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean = {
    if (player.isSneaking) return false
    if (world.isRemote) return true
    player.openGui(Pressure, cfg.guiId, world, pos.getX, pos.getY, pos.getZ)
    true
  }

  override def colorMultiplier(worldIn: IBlockAccess, pos: BlockPos, index: Int) =
    PressureResourceProvider.outputColors(index).asRGB
}
