/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.router

import net.bdew.lib.PimpVanilla._
import net.bdew.lib.block.{BaseBlock, HasTE}
import net.bdew.lib.multiblock.block.ColorHandler
import net.bdew.lib.property.SimpleUnlistedProperty
import net.bdew.lib.render.ColorHandlers
import net.bdew.pressure.api.IPressureConnectableBlock
import net.bdew.pressure.blocks.BlockNotifyUpdates
import net.bdew.pressure.blocks.router.data.RouterSideMode
import net.bdew.pressure.{Pressure, PressureResourceProvider}
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{BlockRenderLayer, EnumFacing, EnumHand}
import net.minecraft.world.{IBlockAccess, World}
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

object BlockRouter extends BaseBlock("Router", Material.IRON) with HasTE[TileRouter] with BlockNotifyUpdates with IPressureConnectableBlock {
  override val TEClass = classOf[TileRouter]
  val cfg = MachineRouter

  setHardness(2)

  object Properties {
    val MODE = EnumFacing.values().map(f => f -> new SimpleUnlistedProperty(f.getName, classOf[RouterSideMode.Value])).toMap
  }

  override def getExtendedStateFromTE(state: IExtendedBlockState, world: IBlockAccess, pos: BlockPos, te: TileRouter) = {
    super.getExtendedStateFromTE(state, world, pos, te).withPropertiesEx(
      EnumFacing.values().map(face => Properties.MODE(face) -> te.sideModes.get(face))
    )
  }

  override def canRenderInLayer(layer: BlockRenderLayer) =
    layer == BlockRenderLayer.SOLID || layer == BlockRenderLayer.CUTOUT

  override def getUnlistedProperties = super.getUnlistedProperties ++ Properties.MODE.values

  override def canConnectTo(world: IBlockAccess, pos: BlockPos, side: EnumFacing) =
    getTE(world, pos).exists(_.sideModes.get(side) != RouterSideMode.DISABLED)

  override def isTraversable(world: IBlockAccess, pos: BlockPos) = false

  override def onBlockActivated(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, hand: EnumHand, heldItem: ItemStack, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean = {
    if (player.isSneaking) return false
    if (world.isRemote) return true
    player.openGui(Pressure, cfg.guiId, world, pos.getX, pos.getY, pos.getZ)
    true
  }

  @SideOnly(Side.CLIENT)
  override def registerItemModels(): Unit = {
    super.registerItemModels()
    ColorHandlers.register(this, new ColorHandler(PressureResourceProvider))
  }
}
