/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.tank

import net.bdew.lib.Misc
import net.bdew.lib.block.BlockFace
import net.bdew.lib.multiblock.block.BlockOutput
import net.bdew.lib.multiblock.data.{OutputConfig, OutputConfigFluid}
import net.bdew.lib.multiblock.interact.CIFluidOutput
import net.bdew.lib.multiblock.tile.{RSControllableOutput, TileOutput}
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.fluids.{Fluid, FluidStack, FluidTankInfo, IFluidHandler}

object BlockFluidOutput extends BaseModule("FluidOutput", "FluidOutput", classOf[TileFluidOutput]) with BlockOutput[TileFluidOutput]

class TileFluidOutput extends TileOutput with RSControllableOutput with IFluidHandler {
  val kind: String = "FluidOutput"

  override def getCore = getCoreAs[CIFluidOutput]

  def getCfg(dir: ForgeDirection): Option[OutputConfigFluid] =
    for {
      core <- getCore
      onum <- core.outputFaces.get(BlockFace(mypos, dir))
      cfggen <- core.outputConfig.get(onum)
      cfg <- Misc.asInstanceOpt(cfggen, classOf[OutputConfigFluid])
    } yield cfg

  override def makeCfgObject(face: ForgeDirection) = new OutputConfigFluid

  override def canConnectoToFace(d: ForgeDirection) = mypos.neighbour(d).getTile[IFluidHandler](worldObj).isDefined

  override def doOutput(face: ForgeDirection, cfg: OutputConfig) {}

  override def fill(from: ForgeDirection, resource: FluidStack, doFill: Boolean) = 0
  override def canFill(from: ForgeDirection, fluid: Fluid) = false

  override def getTankInfo(from: ForgeDirection): Array[FluidTankInfo] =
    getCore map (_.getTankInfo) getOrElse Array.empty

  override def canDrain(from: ForgeDirection, fluid: Fluid) =
    (for {
      core <- getCore
      cfg <- getCfg(from) if checkCanOutput(cfg)
    } yield core.canOutputFluid(fluid)).getOrElse(false)

  override def drain(from: ForgeDirection, resource: FluidStack, doDrain: Boolean) =
    (for {
      core <- getCore
      cfg <- getCfg(from) if checkCanOutput(cfg)
    } yield {
      val out = core.outputFluid(resource, doDrain)
      if (doDrain && out != null) addOutput(from, out.amount)
      out
    }).orNull

  override def drain(from: ForgeDirection, maxDrain: Int, doDrain: Boolean) =
    (for {
      core <- getCore
      cfg <- getCfg(from) if checkCanOutput(cfg)
    } yield {
      val out = core.outputFluid(maxDrain, doDrain)
      if (doDrain && out != null) addOutput(from, out.amount)
      out
    }).orNull

  var outThisTick = Map.empty[ForgeDirection, Float]

  def addOutput(side: ForgeDirection, amt: Int) =
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
