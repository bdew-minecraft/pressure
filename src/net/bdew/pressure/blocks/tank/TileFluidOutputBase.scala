/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.tank

import net.bdew.lib.PimpVanilla._
import net.bdew.lib.capabilities.helpers.{FluidDrainMonitor, FluidHelper, FluidMultiHandler}
import net.bdew.lib.capabilities.{Capabilities, CapabilityProvider}
import net.bdew.lib.multiblock.data.OutputConfigFluid
import net.bdew.lib.multiblock.interact.CIFluidOutput
import net.bdew.lib.multiblock.tile.{RSControllableOutput, TileOutput}
import net.minecraft.util.EnumFacing
import net.minecraftforge.fluids.FluidStack

abstract class TileFluidOutputBase extends TileOutput[OutputConfigFluid] with RSControllableOutput with CapabilityProvider {
  val kind: String = "FluidOutput"

  override def getCore = getCoreAs[CIFluidOutput]
  override val outputConfigType = classOf[OutputConfigFluid]

  addCapabilityOption(Capabilities.CAP_FLUID_HANDLER) { side =>
    if (getCfg(side).exists(checkCanOutput))
      getCore map (core => new FluidDrainMonitor(FluidMultiHandler.wrap(core.getOutputTanks), stack => addOutput(side, stack)))
    else None
  }

  def addOutput(side: EnumFacing, res: FluidStack) = {
    outThisTick += side -> (outThisTick.getOrElse(side, 0F) + res.amount)
  }

  override def canConnectToFace(d: EnumFacing) =
    getCore.isDefined && FluidHelper.hasFluidHandler(world, pos.offset(d), d.getOpposite)

  var outThisTick = Map.empty[EnumFacing, Float]

  def updateOutput() {
    for {
      core <- getCore
      (side, amt) <- outThisTick
      cfg <- getCfg(side)
    } {
      cfg.updateAvg(amt)
      core.outputConfig.updated()
    }
    outThisTick = Map.empty
  }

  serverTick.listen(updateOutput)

  override def makeCfgObject(face: EnumFacing) = new OutputConfigFluid
}
