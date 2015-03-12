/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.valves.check

import net.bdew.lib.block.BlockRef
import net.bdew.pressure.api.{IPressureConnection, IPressureEject, IPressureInject}
import net.bdew.pressure.pressurenet.Helper
import net.minecraft.block.Block
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.fluids.FluidStack

class TileCheckValve extends TileEntity with IPressureEject with IPressureInject {
  var connection: IPressureConnection = null
  lazy val me = BlockRef.fromTile(this)

  override def shouldRefresh(oldBlock: Block, newBlock: Block, oldMeta: Int, newMeta: Int, world: World, x: Int, y: Int, z: Int) =
    oldBlock != newBlock

  def getFacing = BlockCheckValve.getFacing(worldObj, xCoord, yCoord, zCoord)

  override def eject(resource: FluidStack, face: ForgeDirection, doEject: Boolean): Int = {
    if (face == getFacing.getOpposite && !BlockCheckValve.isPowered(worldObj, xCoord, yCoord, zCoord)) {
      if (connection == null)
        connection = Helper.recalculateConnectionInfo(this, getFacing)
      connection.pushFluid(resource, doEject)
    } else 0
  }

  override def invalidateConnection(direction: ForgeDirection) = connection = null

  override def getZCoord = zCoord
  override def getYCoord = yCoord
  override def getXCoord = xCoord
  override def getWorld = worldObj

}
