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
import net.bdew.lib.multiblock.data.{OutputConfigPower, OutputConfigFluid}
import net.bdew.lib.multiblock.interact.CIFluidOutput
import net.bdew.lib.multiblock.tile.{RSControllableOutput, TileOutput}
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.fluids.{Fluid, FluidStack, IFluidHandler}

abstract class TileFluidOutputBase extends TileOutput[OutputConfigFluid] with RSControllableOutput with IFluidHandler {
  val kind: String = "FluidOutput"

  override def getCore = getCoreAs[CIFluidOutput]
  override val outputConfigType = classOf[OutputConfigFluid]

  override def canConnectoToFace(d: ForgeDirection) =
    getCore exists { core =>
      mypos.neighbour(d).getTile[IFluidHandler](worldObj).isDefined
    }

  override def fill(from: ForgeDirection, resource: FluidStack, doFill: Boolean) = 0
  override def canFill(from: ForgeDirection, fluid: Fluid) = false

  override def getTankInfo(from: ForgeDirection) =
    getCore map (_.getTankInfo) getOrElse Array.empty


  override def makeCfgObject(face: ForgeDirection) = new OutputConfigFluid
}
