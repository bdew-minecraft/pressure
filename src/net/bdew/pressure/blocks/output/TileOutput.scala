/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.output

import net.bdew.lib.PimpVanilla._
import net.bdew.lib.capabilities.helpers.{FluidHandlerNull, FluidHelper}
import net.bdew.lib.capabilities.{Capabilities, CapabilityProvider}
import net.bdew.lib.data.base.TileDataSlots
import net.bdew.pressure.api.IPressureEject
import net.bdew.pressure.blocks.TileFilterable
import net.minecraft.util.EnumFacing
import net.minecraftforge.fluids.FluidStack

class TileOutput extends TileDataSlots with CapabilityProvider with IPressureEject with TileFilterable {
  def getFacing = BlockOutput.getFacing(world, pos)

  override def eject(resource: FluidStack, direction: EnumFacing, doEject: Boolean) = {
    if (isFluidAllowed(resource) && direction == getFacing.getOpposite) {
      val f = getFacing
      FluidHelper.getFluidHandler(world, pos.offset(f), f.getOpposite) map { dest =>
        dest.fill(resource, doEject)
      } getOrElse 0
    } else 0
  }

  addCapabilityOption(Capabilities.CAP_FLUID_HANDLER) { side => if (side == getFacing) Some(FluidHandlerNull) else None }

  override def pressureNodePos = getPos
  override def pressureNodeWorld = getWorld
}

