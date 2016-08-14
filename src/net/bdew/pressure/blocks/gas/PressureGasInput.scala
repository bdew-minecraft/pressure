/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.gas

import mekanism.api.gas.{Gas, GasStack}
import net.bdew.lib.data.base.TileDataSlotsTicking
import net.bdew.pressure.api.{IPressureConnectableBlock, IPressureConnection, IPressureInject}
import net.bdew.pressure.blocks.input.BlockInput
import net.bdew.pressure.blocks.{BasePoweredBlock, BlockNotifyUpdates, TileFilterable}
import net.bdew.pressure.pressurenet.Helper
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraftforge.fluids.FluidStack

object BlockPressureGasInput extends BasePoweredBlock("gasinput", classOf[TilePressureGasInput]) with BlockNotifyUpdates with IPressureConnectableBlock {
  override def canConnectTo(world: IBlockAccess, pos: BlockPos, side: EnumFacing) =
    getFacing(world, pos) == side
  override def isTraversable(world: IBlockAccess, pos: BlockPos) = false
}

class TilePressureGasInput extends TileDataSlotsTicking with IPressureInject with TileFilterable with GasHandlerProxy {
  def getFacing = BlockInput.getFacing(worldObj, pos)
  var connection: IPressureConnection = _

  object gasHandler extends BaseGasHandler {
    override def canReceiveGas(side: EnumFacing, kind: Gas): Boolean =
      side == getFacing.getOpposite && (kind == null || (kind.getFluid != null && isFluidAllowed(kind.getFluid)))

    override def receiveGas(side: EnumFacing, stack: GasStack, doTransfer: Boolean): Int = {
      if (!worldObj.isRemote) {
        pushGas(stack, doTransfer)
      } else 0
    }
  }

  addCapability(GasSupport.CAP_GAS_HANDLER) { case side if side == getFacing.getOpposite => gasHandler }
  addCapability(GasSupport.CAP_TUBE_CONNECTION) { case side if side == getFacing.getOpposite => gasHandler }

  def pushGas(gas: GasStack, doPush: Boolean): Int = {
    if (gas != null && gas.getGas != null && gas.getGas.getFluid != null && gas.amount > 0 && isFluidAllowed(gas.getGas.getFluid)) {
      if (connection == null && Helper.canPipeConnectTo(worldObj, pos.offset(getFacing), getFacing.getOpposite))
        connection = Helper.recalculateConnectionInfo(this, getFacing)
      if (connection != null)
        connection.pushFluid(new FluidStack(gas.getGas.getFluid, gas.amount), doPush)
      else
        0
    } else 0
  }

  serverTick.listen(() => {
    if (BlockInput.getSignal(worldObj, pos)) {
      GasSupport.getGasHandler(worldObj, pos.offset(getFacing.getOpposite), getFacing) map { from =>
        val res = from.drawGas(getFacing.getOpposite, Int.MaxValue, false)
        if (res != null && res.getGas != null && res.getGas.getFluid != null && res.amount > 0 && isFluidAllowed(res.getGas.getFluid)) {
          val filled = pushGas(res, true)
          if (filled > 0)
            from.drawGas(getFacing.getOpposite, filled, true)
        }
      }
    }
  })

  override def invalidateConnection(direction: EnumFacing) = connection = null
  override def pressureNodePos = getPos
  override def pressureNodeWorld = getWorld
}
