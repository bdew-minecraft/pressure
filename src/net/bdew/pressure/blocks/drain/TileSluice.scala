/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.drain

import net.bdew.lib.data.DataSlotTank
import net.bdew.lib.data.base.TileDataSlotsTicking
import net.bdew.pressure.api.IPressureEject
import net.bdew.pressure.blocks.TileFilterable
import net.bdew.pressure.misc.FakeTank
import net.minecraft.util.EnumFacing
import net.minecraftforge.fluids.{Fluid, FluidStack}

class TileSluice extends TileDataSlotsTicking with FakeTank with IPressureEject with TileFilterable {
  def getFacing = BlockSluice.getFacing(worldObj, pos)

  val BucketVolume = net.minecraftforge.fluids.FluidContainerRegistry.BUCKET_VOLUME
  val bufferTank = new DataSlotTank("buffer", this, BucketVolume)

  override def eject(resource: FluidStack, direction: EnumFacing, doEject: Boolean) = fill(direction, resource, doEject)

  override def canFill(from: EnumFacing, fluid: Fluid) = {
    val target = pos.offset(getFacing)
    from == getFacing.getOpposite && fluid != null && fluid.canBePlacedInWorld && isFluidAllowed(fluid) && worldObj.isAirBlock(target)
  }

  override def fill(from: EnumFacing, resource: FluidStack, doFill: Boolean): Int = {
    if (resource != null && resource.getFluid != null && resource.amount > 0 && canFill(from, resource.getFluid)) {
      if (bufferTank.getFluid != null && bufferTank.getFluid.getFluid != resource.getFluid)
        bufferTank.setFluid(null)
      var amountFilled = bufferTank.fill(resource, doFill)
      if (doFill && !worldObj.isRemote && bufferTank.getFluidAmount >= BucketVolume) {
        val target = pos.offset(getFacing)
        worldObj.setBlockState(pos, bufferTank.getFluid.getFluid.getBlock.getDefaultState, 3)
        worldObj.notifyBlockOfStateChange(pos, BlockSluice)
        bufferTank.setFluid(null)
      }
      amountFilled
    } else 0
  }

  override def isValidDirectionForFakeTank(dir: EnumFacing) = getFacing.getOpposite == dir
}

