/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.router

import net.bdew.lib.Misc
import net.bdew.lib.data.base.{TileDataSlots, UpdateKind}
import net.bdew.lib.multiblock.data.RSMode
import net.bdew.pressure.api.{IPressureConnection, IPressureEject, IPressureInject}
import net.bdew.pressure.blocks.router.data.{DataSlotSideFilters, DataSlotSideModes, DataSlotSideRSControl, RouterSideMode}
import net.bdew.pressure.misc.FakeTank
import net.bdew.pressure.pressurenet.Helper
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.fluids.{Fluid, FluidStack, IFluidHandler}

import scala.collection.mutable

class TileRouter extends TileDataSlots with IPressureInject with IPressureEject with FakeTank {
  val sideModes = DataSlotSideModes("modes", this).setUpdate(UpdateKind.SAVE, UpdateKind.GUI, UpdateKind.WORLD, UpdateKind.RENDER)
  val sideControl = DataSlotSideRSControl("control", this).setUpdate(UpdateKind.SAVE, UpdateKind.GUI)
  val sideFilters = DataSlotSideFilters("filters", this).setUpdate(UpdateKind.SAVE, UpdateKind.GUI)

  val connections = mutable.Map.empty[ForgeDirection, IPressureConnection]

  override def invalidateConnection(side: ForgeDirection): Unit = connections -= side

  override def isValidDirectionForFakeTank(dir: ForgeDirection) = sideModes.get(dir) != RouterSideMode.DISABLED

  def canWorkWithRsMode(rsMode: RSMode.Value) = rsMode match {
    case RSMode.ALWAYS => true
    case RSMode.NEVER => false
    case _ => getWorldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord) ^ (rsMode == RSMode.RS_OFF)
  }

  def isSideValidIO(side: ForgeDirection, fluid: Fluid, modes: Set[RouterSideMode.Value]): Boolean =
    fluid != null && fluid != null && modes.contains(sideModes.get(side)) && canWorkWithRsMode(sideControl.get(side)) && (
      !sideFilters.isSet(side) || sideFilters.get(side) == fluid
      )

  def isSideValidIO(side: ForgeDirection, stack: FluidStack, modes: Set[RouterSideMode.Value]): Boolean =
    stack != null && isSideValidIO(side, stack.getFluid, modes)

  override def eject(resource: FluidStack, face: ForgeDirection, doEject: Boolean): Int =
    if (isSideValidIO(face, resource, RouterSideMode.inputs)) {
      distributeFluid(resource, doEject)
    } else
      0

  def distributeFluid(resource: FluidStack, doEject: Boolean) = {
    val fluid = resource.copy()

    if (fluid.amount > 0)
      for (side <- sideModes.sides(RouterSideMode.OUTPUT_HIGH) if fluid.amount > 0)
        fluid.amount -= pushFromSide(fluid.copy(), side, doEject)

    if (fluid.amount > 0)
      for (side <- sideModes.sides(RouterSideMode.OUTPUT_MEDIUM) if fluid.amount > 0)
        fluid.amount -= pushFromSide(fluid.copy(), side, doEject)

    if (fluid.amount > 0)
      for (side <- sideModes.sides(RouterSideMode.OUTPUT_LOW) if fluid.amount > 0)
        fluid.amount -= pushFromSide(fluid.copy(), side, doEject)

    resource.amount - fluid.amount
  }

  def pushFromSide(resource: FluidStack, side: ForgeDirection, doEject: Boolean) = {
    if (isSideValidIO(side, resource, RouterSideMode.outputs)) {
      Misc.getNeighbourTile(this, side, classOf[IFluidHandler]) map { handler =>
        handler.fill(side.getOpposite, resource, doEject)
      } getOrElse {
        if (!connections.isDefinedAt(side))
          connections += side -> Helper.recalculateConnectionInfo(this, side)
        connections(side).pushFluid(resource, doEject)
      }
    } else 0
  }

  serverTick.listen(() => {
    for {
      face <- sideModes.sides(RouterSideMode.INPUT_ACTIVE) if canWorkWithRsMode(sideControl.get(face))
      handler <- Misc.getNeighbourTile(this, face, classOf[IFluidHandler])
    } {
      val fluid = if (sideFilters.isSet(face)) {
        handler.drain(face.getOpposite, new FluidStack(sideFilters.get(face), Int.MaxValue), false)
      } else {
        handler.drain(face.getOpposite, Int.MaxValue, false)
      }
      if (fluid != null && fluid.getFluid != null && fluid.amount > 0 && isSideValidIO(face, fluid, RouterSideMode.inputs)) {
        val ejected = new FluidStack(fluid.getFluid, distributeFluid(fluid, true))
        handler.drain(face.getOpposite, ejected, true)
      }
    }
  })

  override def canFill(from: ForgeDirection, fluid: Fluid): Boolean = isSideValidIO(from, fluid, RouterSideMode.inputs)

  override def fill(from: ForgeDirection, resource: FluidStack, doFill: Boolean): Int =
    if (isSideValidIO(from, resource, RouterSideMode.inputs))
      distributeFluid(resource, doFill)
    else
      0

  override def getXCoord = xCoord
  override def getYCoord = yCoord
  override def getZCoord = zCoord
  override def getWorld = worldObj
}
