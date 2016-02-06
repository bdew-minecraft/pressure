/*
 * Copyright (c) bdew, 2013 - 2016
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
import net.minecraft.util.{BlockPos, EnumFacing}
import net.minecraft.world.IBlockAccess
import net.minecraftforge.fluids.{Fluid, FluidStack}

object BlockFluidOutput extends BaseModule("TankFluidOutput", "FluidOutput", classOf[TileFluidOutput]) with BlockOutput[TileFluidOutput] {
  override def canConnectRedstone(world: IBlockAccess, pos: BlockPos, side: EnumFacing) = true
}

class TileFluidOutput extends TileFluidOutputBase {
  override def doOutput(face: EnumFacing, cfg: OutputConfigFluid) {}

  override def canDrain(from: EnumFacing, fluid: Fluid) =
    (for {
      core <- getCore
      cfg <- getCfg(from) if checkCanOutput(cfg)
    } yield core.canOutputFluid(fluid)).getOrElse(false)

  override def drain(from: EnumFacing, resource: FluidStack, doDrain: Boolean) =
    (for {
      core <- getCore
      cfg <- getCfg(from) if checkCanOutput(cfg)
    } yield {
      val out = core.outputFluid(resource, doDrain)
      if (doDrain && out != null) addOutput(from, out.amount)
      out
    }).orNull

  override def drain(from: EnumFacing, maxDrain: Int, doDrain: Boolean) =
    (for {
      core <- getCore
      cfg <- getCfg(from) if checkCanOutput(cfg)
    } yield {
      val out = core.outputFluid(maxDrain, doDrain)
      if (doDrain && out != null) addOutput(from, out.amount)
      out
    }).orNull

  var outThisTick = Map.empty[EnumFacing, Float]

  def addOutput(side: EnumFacing, amt: Int) =
    outThisTick += side -> (outThisTick.getOrElse(side, 0F) + amt)

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
}
