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
import net.bdew.pressure.config.Modules
import net.bdew.pressure.misc.FakeTank
import net.minecraft.entity.item.EntityXPOrb
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.fluids.{Fluid, FluidStack}

class TileDrain extends TileDataSlots with FakeTank with IPressureEject with TileFilterable {
  def getFacing = BlockDrain.getFacing(worldObj, xCoord, yCoord, zCoord)

  lazy val me = BlockRef.fromTile(this)

  val bufferTank = new DataSlotTank("buffer", this, Int.MaxValue)

  def doDrain(resource: FluidStack) {
    val target = me.neighbour(getFacing)
    if (bufferTank.getFluid != null && bufferTank.getFluid.getFluid != resource.getFluid)
      bufferTank.setFluid(null)
    bufferTank.fill(resource, true)
    if (Modules.Drain.makeXPOrbs && Modules.Drain.ratioMap.contains(bufferTank.getFluid.getFluid.getName)) {
      val ratio = Modules.Drain.ratioMap(bufferTank.getFluid.getFluid.getName)
      var xpToDrop = (bufferTank.getFluidAmount.toDouble / ratio).floor.toInt
      bufferTank.drain(xpToDrop * ratio, true)
      while (xpToDrop > 0) {
        val dropNow = EntityXPOrb.getXPSplit(xpToDrop)
        xpToDrop -= dropNow
        val ent = new EntityXPOrb(this.worldObj, target.x + 0.5D, target.y + 0.5D, target.z + 0.5D, dropNow)
        ent.motionX = getFacing.offsetX * (Math.random() * 0.5 - 0.25) + (Math.random() * 0.2 - 0.1)
        ent.motionY = getFacing.offsetY * (Math.random() * 0.5 - 0.25) + (Math.random() * 0.2 - 0.1)
        ent.motionZ = getFacing.offsetZ * (Math.random() * 0.5 - 0.25) + (Math.random() * 0.2 - 0.1)
        this.worldObj.spawnEntityInWorld(ent)
      }
    } else {
      if (worldObj.isAirBlock(target.x, target.y, target.z) && bufferTank.getFluidAmount >= 1000 && resource.getFluid.canBePlacedInWorld) {
        bufferTank.setFluid(null)
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

