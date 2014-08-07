/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/pressure/master/MMPL-1.0.txt
 */

package net.bdew.pressure.misc

import net.bdew.pressure.Pressure
import net.bdew.pressure.api.{IPressureConnection, IPressureEject, IPressureInject}
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.fluids.FluidStack

case class PressureConnection(origin: IPressureInject, side: ForgeDirection, tiles: Set[IPressureEject]) extends IPressureConnection {
  override def pushFluid(fluid: FluidStack, doPush: Boolean): Int = {
    if (fluid == null || fluid.getFluid == null || fluid.amount == 0 || tiles.isEmpty) return 0

    // Here we check that this connection hasn't been already visited
    // and if a loop is detected we blow up the block.

    if (Helper.recursionGuard.value.contains(this)) {
      Pressure.logInfo("Detected loop, blowing up %d,%d,%d (dim %d)",
        origin.getXCoord, origin.getYCoord, origin.getZCoord, origin.getWorld.provider.dimensionId)
      origin.getWorld.createExplosion(null, origin.getXCoord, origin.getYCoord, origin.getZCoord, 1, true)
      return 0
    }

    Helper.recursionGuard.withValue(Helper.recursionGuard.value + this) {
      // Now the inner part will see this block as part of the path
      // The set is reset back to the old value automatically once execution leaves this block

      if (fluid.amount < 10) {
        // Don't try balancing small amounts
        var toPush = fluid.amount
        tiles.foreach { target =>
          toPush -= target.eject(new FluidStack(fluid.getFluid, toPush), doPush)
          if (toPush <= 0) return fluid.amount
        }
        toPush - fluid.amount
      } else {
        val maxFill = tiles.map(target => target -> target.eject(fluid.copy(), false)).toMap
        val totalFill = maxFill.values.sum
        if (!doPush) return totalFill
        val mul = if (totalFill > fluid.amount) fluid.amount.toFloat / totalFill else 1
        (maxFill map { case (te, amount) =>
          val toFill = (amount * mul).round
          if (toFill > 0)
            te.eject(new FluidStack(fluid.getFluid, toFill), doPush)
          else
            0
        }).sum
      }
    }
  }

}
