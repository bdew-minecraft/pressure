/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks

import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.pressure.api.IPressureConnectableBlock
import net.bdew.pressure.misc.{Helper, PressureCreativeTabs}
import net.bdew.pressure.render.PipeRenderer
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.world.{IBlockAccess, World}
import net.minecraftforge.common.util.ForgeDirection

object BlockPipe extends Block(Material.iron) with IPressureConnectableBlock with BlockNotifyUpdates {
  setBlockName("pressure.pipe")
  setHardness(2)

  setCreativeTab(PressureCreativeTabs.main)

  override def renderAsNormalBlock() = false
  override def isOpaqueCube = false

  @SideOnly(Side.CLIENT)
  override def getRenderType = PipeRenderer.id

  @SideOnly(Side.CLIENT)
  override def registerBlockIcons(ir: IIconRegister) = {
    blockIcon = ir.registerIcon("pressure:pipe")
  }

  override def setBlockBoundsBasedOnState(w: IBlockAccess, x: Int, y: Int, z: Int) {
    val connections = Helper.getPipeConnections(w, x, y, z)
    val minX = if (connections.contains(ForgeDirection.WEST)) 0 else 0.2F
    val maxX = if (connections.contains(ForgeDirection.EAST)) 1 else 0.8F

    val minY = if (connections.contains(ForgeDirection.DOWN)) 0 else 0.2F
    val maxY = if (connections.contains(ForgeDirection.UP)) 1 else 0.8F

    val minZ = if (connections.contains(ForgeDirection.NORTH)) 0 else 0.2F
    val maxZ = if (connections.contains(ForgeDirection.SOUTH)) 1 else 0.8F

    this.setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ)
  }

  override def getCollisionBoundingBoxFromPool(w: World, x: Int, y: Int, z: Int) = {
    setBlockBoundsBasedOnState(w, x, y, z)
    super.getCollisionBoundingBoxFromPool(w, x, y, z)
  }

  override def setBlockBoundsForItemRender() {
    this.setBlockBounds(0, 0, 0, 1, 1, 1)
  }

  override def canConnectTo(world: IBlockAccess, x: Int, y: Int, z: Int, side: ForgeDirection) = true

}
