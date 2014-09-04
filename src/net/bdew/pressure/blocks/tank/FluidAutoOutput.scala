/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.tank

import net.bdew.lib.multiblock.block.BlockOutput
import net.bdew.lib.multiblock.data.OutputConfigFluid
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.fluids.{Fluid, FluidStack, IFluidHandler}

object BlockFluidAutoOutput extends BaseModule("TankFluidAutoOutput", "FluidOutput", classOf[TileFluidAutoOutput]) with BlockOutput[TileFluidAutoOutput]

class TileFluidAutoOutput extends TileFluidOutputBase {
  override def doOutput(face: ForgeDirection, cfg: OutputConfigFluid) {
    for {
      core <- getCore
      target <- mypos.neighbour(face).getTile[IFluidHandler](worldObj)
      toSend <- Option(core.outputFluid(Int.MaxValue, false))
    } {
      val filled = target.fill(face.getOpposite, toSend, true)
      if (filled > 0) {
        core.outputFluid(filled, true)
        cfg.updateAvg(filled)
        core.outputConfig.updated()
      }
    }
  }

  override def canDrain(from: ForgeDirection, fluid: Fluid) = false

  override def drain(from: ForgeDirection, resource: FluidStack, doDrain: Boolean) = null
  override def drain(from: ForgeDirection, maxDrain: Int, doDrain: Boolean) = null
}
