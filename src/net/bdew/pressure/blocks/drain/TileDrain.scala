/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.drain

import net.bdew.lib.capabilities.{Capabilities, CapabilityProvider}
import net.bdew.lib.data.DataSlotTank
import net.bdew.lib.data.base.TileDataSlotsTicking
import net.bdew.pressure.api.IPressureEject
import net.bdew.pressure.blocks.TileFilterable
import net.bdew.pressure.config.Modules
import net.bdew.pressure.misc.FakeFluidHandler
import net.minecraft.entity.item.EntityXPOrb
import net.minecraft.util.EnumFacing
import net.minecraftforge.fluids.FluidStack

class TileDrain extends TileDataSlotsTicking with CapabilityProvider with IPressureEject with TileFilterable {
  def getFacing = BlockDrain.getFacing(world, pos)

  val bufferTank = new DataSlotTank("buffer", this, Int.MaxValue)

  val handler = new FakeFluidHandler {
    override def canFill: Boolean = true
    override def canFillFluidType(fluidStack: FluidStack): Boolean = isFluidAllowed(fluidStack)
    override def fill(resource: FluidStack, doFill: Boolean): Int = {
      if (resource != null && resource.getFluid != null && resource.amount > 0 && canFillFluidType(resource)) {
        if (!world.isRemote && doFill)
          doDrain(resource)
        resource.amount
      } else 0
    }
  }

  addCapabilityOption(Capabilities.CAP_FLUID_HANDLER) { side =>
    if (side == getFacing.getOpposite)
      Some(handler)
    else
      None
  }

  def doDrain(resource: FluidStack) {
    val target = pos.offset(getFacing)
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
        val ent = new EntityXPOrb(this.world, target.getX + 0.5D, target.getY + 0.5D, target.getZ + 0.5D, dropNow)
        val v = getFacing.getDirectionVec
        ent.motionX = v.getX * (Math.random() * 0.5 - 0.25) + (Math.random() * 0.2 - 0.1)
        ent.motionY = v.getY * (Math.random() * 0.5 - 0.25) + (Math.random() * 0.2 - 0.1)
        ent.motionZ = v.getZ * (Math.random() * 0.5 - 0.25) + (Math.random() * 0.2 - 0.1)
        this.world.spawnEntity(ent)
      }
    } else {
      if (world.isAirBlock(target) && bufferTank.getFluidAmount >= 1000 && resource.getFluid.canBePlacedInWorld) {
        bufferTank.setFluid(null)
        world.setBlockState(target, resource.getFluid.getBlock.getDefaultState, 3)
        world.neighborChanged(target, BlockDrain, pos)
      }
    }
  }

  override def eject(resource: FluidStack, direction: EnumFacing, doEject: Boolean) = {
    if (isFluidAllowed(resource) && direction == getFacing.getOpposite) {
      if (doEject)
        doDrain(resource)
      resource.amount
    } else 0
  }

  override def pressureNodePos = getPos
  override def pressureNodeWorld = getWorld
}

