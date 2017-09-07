/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.gas

import mekanism.api.gas.{Gas, GasRegistry, GasStack, ITubeConnection}
import net.bdew.lib.capabilities.CapabilityProvider
import net.bdew.lib.multiblock.block.BlockOutput
import net.bdew.lib.multiblock.data.OutputConfigFluid
import net.bdew.lib.multiblock.interact.CIFluidOutput
import net.bdew.lib.multiblock.tile.{RSControllableOutput, TileOutput}
import net.bdew.pressure.blocks.tank.BaseModule
import net.minecraft.block.state.IBlockState
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraftforge.fluids.FluidStack

object BlockTankGasOutput extends BaseModule("tank_gas_output", "FluidOutput", classOf[TileTankGasOutput]) with BlockOutput[TileTankGasOutput] {
  override def canConnectRedstone(state: IBlockState, world: IBlockAccess, pos: BlockPos, side: EnumFacing): Boolean = true
}

class TileTankGasOutput extends TileOutput[OutputConfigFluid] with RSControllableOutput with CapabilityProvider with GasHandlerProxy {
  val kind: String = "FluidOutput"

  override def getCore = getCoreAs[CIFluidOutput]
  override val outputConfigType = classOf[OutputConfigFluid]

  object gasHandler extends BaseGasHandler with ITubeConnection {
    override def canTubeConnect(side: EnumFacing): Boolean = true
    override def canDrawGas(side: EnumFacing, kind: Gas): Boolean =
      getCore.exists { core =>
        getCfg(side).exists(checkCanOutput) && (kind == null || core.getOutputTanks.exists(tank => tank.getTankProperties.exists(props => props.getContents.getFluid == kind.getFluid)))
      }

    override def drawGas(side: EnumFacing, amount: Int, doTransfer: Boolean): GasStack = {
      if (getCfg(side).exists(checkCanOutput)) {
        for {
          core <- getCore
          tank <- core.getOutputTanks
          drained <- Option(tank.drain(amount, false)) if drained.amount > 0
          gas <- Option(GasRegistry.getGas(drained.getFluid))
        } {
          if (doTransfer) {
            addOutput(side, drained)
            tank.drain(drained, true)
          }
          return new GasStack(gas, drained.amount)
        }
      }
      null
    }
  }

  addCapability(GasSupport.CAP_GAS_HANDLER, gasHandler)
  addCapability(GasSupport.CAP_TUBE_CONNECTION, gasHandler)

  override def doOutput(face: EnumFacing, cfg: OutputConfigFluid) {
    for {
      core <- getCore if checkCanOutput(cfg)
      target <- GasSupport.getGasHandler(world, pos.offset(face), face.getOpposite)
      tank <- core.getOutputTanks
      drained <- Option(tank.drain(Int.MaxValue, false)) if drained.amount > 0
      gas <- Option(GasRegistry.getGas(drained.getFluid))
    } {
      val filled = target.receiveGas(face.getOpposite, new GasStack(gas, drained.amount), true)
      if (filled > 0) {
        drained.amount = filled
        tank.drain(drained, true)
        addOutput(face, drained)
      }
    }
  }

  def addOutput(side: EnumFacing, res: FluidStack) = {
    outThisTick += side -> (outThisTick.getOrElse(side, 0F) + res.amount)
  }

  override def canConnectToFace(d: EnumFacing) =
    getCore.isDefined && GasSupport.getGasHandler(world, pos.offset(d), d.getOpposite).isDefined

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
