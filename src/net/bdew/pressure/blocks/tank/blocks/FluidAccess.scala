/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.tank.blocks

import net.bdew.lib.multiblock.interact.{CIFluidInput, CIFluidOutput}
import net.bdew.lib.multiblock.tile.TileModule
import net.bdew.pressure.blocks.tank.BaseModule
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.fluids.{Fluid, FluidStack, IFluidHandler}

object BlockFluidAccess extends BaseModule("TankFluidAccess", "FluidAccess", classOf[TileFluidAccess])

class TileFluidAccess extends TileModule with IFluidHandler {
  val kind: String = "FluidAccess"

  override def getCore = getCoreAs[CIFluidInput with CIFluidOutput]

  override def fill(from: ForgeDirection, resource: FluidStack, doFill: Boolean) =
    getCore map (_.inputFluid(resource, doFill)) getOrElse 0

  override def canFill(from: ForgeDirection, fluid: Fluid) =
    getCore exists (_.canInputFluid(fluid))

  override def getTankInfo(from: ForgeDirection) =
    getCore map (_.getTankInfo) getOrElse Array.empty

  override def canDrain(from: ForgeDirection, fluid: Fluid) =
    getCore.exists(_.canOutputFluid(fluid))

  override def drain(from: ForgeDirection, resource: FluidStack, doDrain: Boolean) =
    getCore.map(_.outputFluid(resource, doDrain)).orNull

  override def drain(from: ForgeDirection, maxDrain: Int, doDrain: Boolean) =
    getCore.map(_.outputFluid(maxDrain, doDrain)).orNull
}
