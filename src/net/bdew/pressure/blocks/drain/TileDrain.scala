/*
 * Copyright (c) bdew, 2013 - 2014
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

class TileDrain extends TileDataSlots with FakeTank with IPressureEject with TileFilterable {
  def getFacing = BlockDrain.getFacing(worldObj, xCoord, yCoord, zCoord)

  lazy val me = BlockRef.fromTile(this)

  val bufferTank = new DataSlotTank("buffer", this, 1000)

  def doDrain(resource: FluidStack) {
    val target = me.neighbour(getFacing)
    if (worldObj.isAirBlock(target.x, target.y, target.z)) {
      if (bufferTank.getFluid != null && bufferTank.getFluid.getFluid != resource.getFluid)
        bufferTank.setFluid(null)
      bufferTank.fill(resource, true)
      if (bufferTank.getFluidAmount >= 1000 && resource.getFluid.canBePlacedInWorld) {
        worldObj.setBlock(target.x, target.y, target.z, resource.getFluid.getBlock)
        worldObj.notifyBlockOfNeighborChange(target.x, target.y, target.z, BlockDrain)
      }
    }
  }

  override def eject(resource: FluidStack, doEject: Boolean) = {
    if (isFluidAllowed(resource)) {
      if (doEject)
        doDrain(resource)
      resource.amount
    } else 0
  }

  override def canFill(from: ForgeDirection, fluid: Fluid) = from == getFacing.getOpposite && isFluidAllowed(fluid)

  override def fill(from: ForgeDirection, resource: FluidStack, doFill: Boolean): Int = {
    if (resource != null && resource.getFluid != null && resource.amount > 0 && canFill(from, resource.getFluid)) {
      if (!worldObj.isRemote && doFill)
        doDrain(resource)
      resource.amount
    } else 0
  }

  override def getXCoord = xCoord
  override def getYCoord = yCoord
  override def getZCoord = zCoord
  override def getWorld = worldObj

  override def isValidDirectionForFakeTank(dir: ForgeDirection) = getFacing.getOpposite == dir
}

