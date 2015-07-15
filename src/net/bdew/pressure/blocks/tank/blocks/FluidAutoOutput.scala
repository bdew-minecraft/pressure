/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.tank.blocks

import net.bdew.lib.multiblock.block.BlockOutput
import net.bdew.lib.multiblock.data.OutputConfigFluid
import net.bdew.pressure.blocks.tank.{BaseModule, TileFluidOutputBase}
import net.minecraft.world.IBlockAccess
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.fluids.{Fluid, FluidStack, IFluidHandler}

object BlockFluidAutoOutput extends BaseModule("TankFluidAutoOutput", "FluidOutput", classOf[TileFluidAutoOutput]) with BlockOutput[TileFluidAutoOutput] {
  override def canConnectRedstone(world: IBlockAccess, x: Int, y: Int, z: Int, side: Int) = true
}

class TileFluidAutoOutput extends TileFluidOutputBase {
  override def doOutput(face: ForgeDirection, cfg: OutputConfigFluid) {
    val filled = for {
      core <- getCore if checkCanOutput(cfg)
      target <- myPos.neighbour(face).getTile[IFluidHandler](worldObj)
      toSend <- Option(core.outputFluid(Int.MaxValue, false))
    } yield {
      val filled = target.fill(face.getOpposite, toSend, true)
      if (filled > 0) {
        core.outputFluid(filled, true)
        core.outputConfig.updated()
        filled
      } else 0D
    }
    cfg.updateAvg(filled.getOrElse(0D))
  }

  override def canDrain(from: ForgeDirection, fluid: Fluid) = false

  override def drain(from: ForgeDirection, resource: FluidStack, doDrain: Boolean) = null
  override def drain(from: ForgeDirection, maxDrain: Int, doDrain: Boolean) = null
}
