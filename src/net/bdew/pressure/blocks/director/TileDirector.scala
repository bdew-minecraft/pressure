/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.director

import net.bdew.lib.data.base.{TileDataSlots, UpdateKind}
import net.bdew.lib.multiblock.data.RSMode
import net.bdew.pressure.api.{IPressureConnection, IPressureEject, IPressureInject}
import net.bdew.pressure.blocks.director.data.{DataSlotSideFilters, DataSlotSideModes, DataSlotSideRSControl, DirectorSideMode}
import net.bdew.pressure.pressurenet.Helper
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.fluids.FluidStack

import scala.collection.mutable

class TileDirector extends TileDataSlots with IPressureInject with IPressureEject {
  val sideModes = DataSlotSideModes("modes", this).setUpdate(UpdateKind.SAVE, UpdateKind.GUI, UpdateKind.WORLD, UpdateKind.RENDER)
  val sideControl = DataSlotSideRSControl("control", this).setUpdate(UpdateKind.SAVE, UpdateKind.GUI)
  val sideFilters = DataSlotSideFilters("filters", this).setUpdate(UpdateKind.SAVE, UpdateKind.GUI)

  val connections = mutable.Map.empty[ForgeDirection, IPressureConnection]

  override def invalidateConnection(side: ForgeDirection): Unit = connections -= side

  def canWorkWithRsMode(rsMode: RSMode.Value) = rsMode match {
    case RSMode.ALWAYS => true
    case RSMode.NEVER => false
    case _ => getWorldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord) ^ (rsMode == RSMode.RS_OFF)
  }

  def isSideValidIO(side: ForgeDirection, fluid: FluidStack, modes: Set[DirectorSideMode.Value]) =
    fluid != null && fluid.getFluid != null && modes.contains(sideModes.get(side)) && canWorkWithRsMode(sideControl.get(side)) && (
      !sideFilters.isSet(side) || sideFilters.get(side) == fluid.getFluid
      )

  override def eject(resource: FluidStack, face: ForgeDirection, doEject: Boolean): Int =
    if (isSideValidIO(face, resource, DirectorSideMode.inputs)) {
      distributeFluid(resource, doEject)
    } else
      0

  def distributeFluid(resource: FluidStack, doEject: Boolean) = {
    val fluid = resource.copy()

    if (fluid.amount > 0)
      for (side <- sideModes.sides(DirectorSideMode.OUTPUT_HIGH) if fluid.amount > 0)
        fluid.amount -= pushFromSide(fluid.copy(), side, doEject)

    if (fluid.amount > 0)
      for (side <- sideModes.sides(DirectorSideMode.OUTPUT_MEDIUM) if fluid.amount > 0)
        fluid.amount -= pushFromSide(fluid.copy(), side, doEject)

    if (fluid.amount > 0)
      for (side <- sideModes.sides(DirectorSideMode.OUTPUT_LOW) if fluid.amount > 0)
        fluid.amount -= pushFromSide(fluid.copy(), side, doEject)

    resource.amount - fluid.amount
  }

  def pushFromSide(resource: FluidStack, side: ForgeDirection, doEject: Boolean) = {
    if (isSideValidIO(side, resource, DirectorSideMode.outputs)) {
      if (!connections.isDefinedAt(side))
        connections += side -> Helper.recalculateConnectionInfo(this, side)
      connections(side).pushFluid(resource, doEject)
    } else 0
  }

  override def getXCoord = xCoord
  override def getYCoord = yCoord
  override def getZCoord = zCoord
  override def getWorld = worldObj
}
