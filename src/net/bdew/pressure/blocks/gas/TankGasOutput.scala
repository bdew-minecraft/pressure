/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.gas

import mekanism.api.gas._
import net.bdew.lib.multiblock.block.BlockOutput
import net.bdew.lib.multiblock.data.OutputConfigFluid
import net.bdew.lib.multiblock.interact.CIFluidOutput
import net.bdew.lib.multiblock.tile.{RSControllableOutput, TileOutput}
import net.bdew.pressure.blocks.tank.BaseModule
import net.minecraft.world.IBlockAccess
import net.minecraftforge.common.util.ForgeDirection

object BlockTankGasOutput extends BaseModule("TankGasOutput", "FluidOutput", classOf[TileTankGasOutput]) with BlockOutput[TileTankGasOutput] {
  override def canConnectRedstone(world: IBlockAccess, x: Int, y: Int, z: Int, side: Int) = true
}

class TileTankGasOutput extends TileOutput[OutputConfigFluid] with RSControllableOutput with ITubeConnection {
  val kind: String = "FluidOutput"

  override def getCore = getCoreAs[CIFluidOutput]
  override val outputConfigType = classOf[OutputConfigFluid]

  override def makeCfgObject(face: ForgeDirection) = new OutputConfigFluid

  override def canConnectToFace(d: ForgeDirection): Boolean =
    getCore.isDefined && myPos.neighbour(d).getTile[IGasHandler](worldObj).isDefined

  override def canTubeConnect(side: ForgeDirection): Boolean = true

  override def doOutput(face: ForgeDirection, cfg: OutputConfigFluid) {
    val filled = for {
      core <- getCore if checkCanOutput(cfg)
      target <- myPos.neighbour(face).getTile[IGasHandler](worldObj)
      toSend <- Option(core.outputFluid(Int.MaxValue, false))
      gas <- Option(GasRegistry.getGas(toSend.getFluid))
    } yield {
        val filled = target.receiveGas(face.getOpposite, new GasStack(gas, toSend.amount), true)
        if (filled > 0) {
          core.outputFluid(filled, true)
          core.outputConfig.updated()
          filled
        } else 0D
      }
    cfg.updateAvg(filled.getOrElse(0D))
  }
}
