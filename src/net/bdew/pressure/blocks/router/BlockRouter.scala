/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.router

import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.lib.block.{HasTE, SimpleBlock}
import net.bdew.pressure.Pressure
import net.bdew.pressure.api.IPressureConnectableBlock
import net.bdew.pressure.blocks.BlockNotifyUpdates
import net.bdew.pressure.blocks.router.data.RouterSideMode
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.{IBlockAccess, World}
import net.minecraftforge.common.util.ForgeDirection

object BlockRouter extends SimpleBlock("Router", Material.iron) with HasTE[TileRouter] with BlockNotifyUpdates with IPressureConnectableBlock {
  override val TEClass = classOf[TileRouter]
  val cfg = MachineRouter

  setHardness(2)

  override def getRenderType = RouterRenderer.id

  override def canConnectTo(world: IBlockAccess, x: Int, y: Int, z: Int, side: ForgeDirection) =
    getTE(world, x, y, z).sideModes.get(side) != RouterSideMode.DISABLED

  override def isTraversable(world: IBlockAccess, x: Int, y: Int, z: Int) = false

  override def onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, meta: Int, xOffs: Float, yOffs: Float, zOffs: Float): Boolean = {
    if (player.isSneaking) return false
    if (world.isRemote) return true
    player.openGui(Pressure, cfg.guiId, world, x, y, z)
    true
  }

  @SideOnly(Side.CLIENT)
  override def registerBlockIcons(reg: IIconRegister) {
    blockIcon = reg.registerIcon(modId + ":" + name.toLowerCase + "/main")
  }
}
