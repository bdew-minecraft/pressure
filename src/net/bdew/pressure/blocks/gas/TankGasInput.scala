/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.gas

import mekanism.api.gas.{Gas, GasStack, IGasHandler, ITubeConnection}
import net.bdew.lib.multiblock.interact.CIFluidInput
import net.bdew.lib.multiblock.tile.TileModule
import net.bdew.pressure.blocks.tank.BaseModule
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.fluids.FluidStack

object BlockTankGasInput extends BaseModule("TankGasInput", "FluidInput", classOf[TileTankGasInput])

class TileTankGasInput extends TileModule with IGasHandler with ITubeConnection {
  val kind: String = "FluidInput"

  override def getCore = getCoreAs[CIFluidInput]

  override def canTubeConnect(side: ForgeDirection): Boolean = getCore.isDefined

  override def canDrawGas(side: ForgeDirection, kind: Gas): Boolean = false
  override def drawGas(side: ForgeDirection, amount: Int): GasStack = drawGas(side, amount, true)
  override def drawGas(side: ForgeDirection, amount: Int, doTransfer: Boolean): GasStack = null

  override def canReceiveGas(side: ForgeDirection, kind: Gas): Boolean =
    kind.getFluid != null && (getCore exists (_.canInputFluid(kind.getFluid)))
  override def receiveGas(side: ForgeDirection, stack: GasStack): Int = receiveGas(side, stack, true)
  override def receiveGas(side: ForgeDirection, stack: GasStack, doTransfer: Boolean): Int =
    if (stack != null && stack.getGas != null && stack.getGas.getFluid != null)
      getCore map (_.inputFluid(new FluidStack(stack.getGas.getFluid, stack.amount), doTransfer)) getOrElse 0
    else
      0
}
