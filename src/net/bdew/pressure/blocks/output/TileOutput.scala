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
import net.bdew.lib.data.base.TileDataSlots
import net.bdew.pressure.api.IPressureEject
import net.bdew.pressure.blocks.TileFilterable
import net.bdew.pressure.misc.FakeTank
import net.minecraft.util.EnumFacing
import net.minecraftforge.fluids.{FluidStack, IFluidHandler}

class TileOutput extends TileDataSlots with FakeTank with IPressureEject with TileFilterable {
  def getFacing = BlockOutput.getFacing(worldObj, pos)

  override def eject(resource: FluidStack, direction: EnumFacing, doEject: Boolean) = {
    if (isFluidAllowed(resource) && direction == getFacing.getOpposite) {
      val f = getFacing
      worldObj.getTileSafe[IFluidHandler](pos.offset(f)) map { dest =>
        dest.fill(f.getOpposite, resource, doEject)
      } getOrElse 0
    } else 0
  }

  override def isValidDirectionForFakeTank(dir: EnumFacing) = getFacing == dir
}

