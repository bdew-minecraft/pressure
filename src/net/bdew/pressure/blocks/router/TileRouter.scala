/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.router

import net.bdew.lib.capabilities.helpers.FluidHelper
import net.bdew.lib.capabilities.legacy.OldFluidHandlerEmulator
import net.bdew.lib.capabilities.{Capabilities, CapabilityProvider}
import net.bdew.lib.data.base.{TileDataSlotsTicking, UpdateKind}
import net.bdew.lib.multiblock.data.RSMode
import net.bdew.pressure.api.{IPressureConnection, IPressureEject, IPressureInject, PressureAPI}
import net.bdew.pressure.blocks.router.data.{DataSlotSideFilters, DataSlotSideModes, DataSlotSideRSControl, RouterSideMode}
import net.bdew.pressure.pressurenet.Helper
import net.minecraft.util.EnumFacing
import net.minecraftforge.fluids.{Fluid, FluidStack}

import scala.collection.mutable

class TileRouter extends TileDataSlotsTicking with IPressureInject with IPressureEject with OldFluidHandlerEmulator with CapabilityProvider {
  val sideModes = DataSlotSideModes("modes", this).setUpdate(UpdateKind.SAVE, UpdateKind.GUI, UpdateKind.WORLD, UpdateKind.RENDER)
  val sideControl = DataSlotSideRSControl("control", this).setUpdate(UpdateKind.SAVE, UpdateKind.GUI)
  val sideFilters = DataSlotSideFilters("filters", this).setUpdate(UpdateKind.SAVE, UpdateKind.GUI)

  val connections = mutable.Map.empty[EnumFacing, IPressureConnection]

  addCapability(PressureAPI.FILTERABLE) {
    case face => RouterFilterProxy(this, face)
  }

  addCapability(Capabilities.CAP_FLUID_HANDLER) {
    case face if sideModes.get(face) != RouterSideMode.DISABLED => new RouterFluidProxy(this, face)
  }

  override def invalidateConnection(side: EnumFacing): Unit = connections -= side

  def canWorkWithRsMode(rsMode: RSMode.Value) = rsMode match {
    case RSMode.ALWAYS => true
    case RSMode.NEVER => false
    case _ => (getWorld.isBlockIndirectlyGettingPowered(pos) > 0) ^ (rsMode == RSMode.RS_OFF)
  }

  def isSideValidIO(side: EnumFacing, fluid: Fluid, modes: Set[RouterSideMode.Value]): Boolean =
    fluid != null && fluid != null && modes.contains(sideModes.get(side)) && canWorkWithRsMode(sideControl.get(side)) && (
      !sideFilters.isSet(side) || sideFilters.get(side) == fluid
      )

  def isSideValidIO(side: EnumFacing, stack: FluidStack, modes: Set[RouterSideMode.Value]): Boolean =
    stack != null && isSideValidIO(side, stack.getFluid, modes)

  override def eject(resource: FluidStack, face: EnumFacing, doEject: Boolean): Int =
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

  def pushFromSide(resource: FluidStack, side: EnumFacing, doEject: Boolean) = {
    if (isSideValidIO(side, resource, RouterSideMode.outputs)) {
      FluidHelper.getFluidHandler(worldObj, pos.offset(side), side.getOpposite) map { handler =>
        handler.fill(resource, doEject)
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
      handler <- FluidHelper.getFluidHandler(worldObj, pos.offset(face), face.getOpposite)
    } {
      val fluid = if (sideFilters.isSet(face)) {
        handler.drain(new FluidStack(sideFilters.get(face), Int.MaxValue), false)
      } else {
        handler.drain(Int.MaxValue, false)
      }
      if (fluid != null && fluid.getFluid != null && fluid.amount > 0 && isSideValidIO(face, fluid, RouterSideMode.inputs)) {
        val ejected = new FluidStack(fluid.getFluid, distributeFluid(fluid, true))
        handler.drain(ejected, true)
      }
    }
  })

  override def pressureNodePos = getPos
  override def pressureNodeWorld = getWorld
}
