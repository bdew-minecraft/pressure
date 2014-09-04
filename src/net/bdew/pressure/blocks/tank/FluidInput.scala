/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.tank

import net.bdew.lib.multiblock.interact.CIFluidInput
import net.bdew.lib.multiblock.tile.TileModule
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.fluids.{Fluid, FluidStack, IFluidHandler}

object BlockFluidInput extends BaseModule("TankFluidInput", "FluidInput", classOf[TileFluidInput])

class TileFluidInput extends TileModule with IFluidHandler {
  val kind: String = "FluidInput"

  override def getCore = getCoreAs[CIFluidInput]

  override def fill(from: ForgeDirection, resource: FluidStack, doFill: Boolean) =
    getCore map (_.inputFluid(resource, doFill)) getOrElse 0

  override def canFill(from: ForgeDirection, fluid: Fluid) =
    getCore exists (_.canInputFluid(fluid))

  override def getTankInfo(from: ForgeDirection) =
    getCore map (_.getTankInfo) getOrElse Array.empty

  override def canDrain(from: ForgeDirection, fluid: Fluid) = false
  override def drain(from: ForgeDirection, resource: FluidStack, doDrain: Boolean) = null
  override def drain(from: ForgeDirection, maxDrain: Int, doDrain: Boolean) = null
}
