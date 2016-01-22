/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.drain

import net.bdew.lib.block.BlockRef
import net.bdew.lib.data.DataSlotTank
import net.bdew.lib.data.base.TileDataSlots
import net.bdew.pressure.api.IPressureEject
import net.bdew.pressure.blocks.TileFilterable
import net.bdew.pressure.misc.FakeTank
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.fluids.{Fluid, FluidStack}

class TileSluice extends TileDataSlots with FakeTank with IPressureEject with TileFilterable {
  def getFacing = BlockSluice.getFacing(worldObj, xCoord, yCoord, zCoord)

  lazy val me = BlockRef.fromTile(this)

  val BucketVolume = net.minecraftforge.fluids.FluidContainerRegistry.BUCKET_VOLUME
  val bufferTank = new DataSlotTank("buffer", this, BucketVolume)
  
  override def eject(resource: FluidStack, direction: ForgeDirection, doEject: Boolean) = fill(direction, resource, doEject)

  override def canFill(from: ForgeDirection, fluid: Fluid) = {
    val target = me.neighbour(getFacing)
    from == getFacing.getOpposite && fluid != null && fluid.canBePlacedInWorld && isFluidAllowed(fluid) && worldObj.isAirBlock(target.x, target.y, target.z)
  }

  override def fill(from: ForgeDirection, resource: FluidStack, doFill: Boolean): Int = {
    if (resource != null && resource.getFluid != null && resource.amount > 0 && canFill(from, resource.getFluid)) {
      if (bufferTank.getFluid != null && bufferTank.getFluid.getFluid != resource.getFluid)
        bufferTank.setFluid(null)
      var amountFilled = bufferTank.fill(resource, doFill)
      if (doFill && !worldObj.isRemote && bufferTank.getFluidAmount >= BucketVolume) {
        val target = me.neighbour(getFacing)
        worldObj.setBlock(target.x, target.y, target.z, bufferTank.getFluid.getFluid.getBlock)
        worldObj.notifyBlockOfNeighborChange(target.x, target.y, target.z, BlockSluice)
        bufferTank.setFluid(null)
      }
      amountFilled
    } else 0
  }

  override def getXCoord = xCoord
  override def getYCoord = yCoord
  override def getZCoord = zCoord
  override def getWorld = worldObj

  override def isValidDirectionForFakeTank(dir: ForgeDirection) = getFacing.getOpposite == dir
}

