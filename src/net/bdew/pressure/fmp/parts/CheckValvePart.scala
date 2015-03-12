/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.fmp.parts

import net.bdew.lib.Misc
import net.bdew.pressure.api.IPressureInject
import net.bdew.pressure.blocks.valves.check.BlockCheckValve
import net.bdew.pressure.pressurenet.Helper
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.fluids.FluidStack

class CheckValvePart(var facing: ForgeDirection = BlockCheckValve.getDefaultFacing, var isPowered: Boolean = false) extends BaseValvePart(BlockCheckValve, "bdew.pressure.checkvalve") {
  def this(meta: Int) = this(Misc.forgeDirection(meta & 7), (meta & 8) == 8)

  override def eject(resource: FluidStack, face: ForgeDirection, doEject: Boolean) = {
    if (!isPowered && face == facing.getOpposite && !tile.isSolid(facing.ordinal())) {
      if (outputConnection == null)
        outputConnection = Helper.recalculateConnectionInfo(tile.asInstanceOf[IPressureInject], facing)
      outputConnection.pushFluid(resource, doEject)
    } else 0
  }

  override def onNeighborChanged(): Unit = {
    val powered = world.isBlockIndirectlyGettingPowered(x, y, z)
    if (powered != isPowered) {
      isPowered = powered
      tile.notifyPartChange(this)
      tile.markDirty()
      sendDescUpdate()
    }
  }
}
