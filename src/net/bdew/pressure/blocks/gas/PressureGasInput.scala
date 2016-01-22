/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.gas

import mekanism.api.gas.{Gas, GasStack, IGasHandler}
import net.bdew.lib.block.BlockRef
import net.bdew.lib.data.base.TileDataSlots
import net.bdew.pressure.api.{IPressureConnectableBlock, IPressureConnection, IPressureInject}
import net.bdew.pressure.blocks.{BasePoweredBlock, BlockNotifyUpdates, TileFilterable}
import net.bdew.pressure.misc.FakeGasTank
import net.bdew.pressure.pressurenet.Helper
import net.minecraft.world.IBlockAccess
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.fluids.FluidStack

object BlockPressureGasInput extends BasePoweredBlock("gasinput", classOf[TilePressureGasInput]) with BlockNotifyUpdates with IPressureConnectableBlock {
  override def canConnectTo(world: IBlockAccess, x: Int, y: Int, z: Int, side: ForgeDirection) =
    getFacing(world, x, y, z) == side
  override def isTraversable(world: IBlockAccess, x: Int, y: Int, z: Int) = false
}

class TilePressureGasInput extends TileDataSlots with FakeGasTank with IPressureInject with TileFilterable {
  def getFacing = BlockPressureGasInput.getFacing(worldObj, xCoord, yCoord, zCoord)
  lazy val me = BlockRef.fromTile(this)
  var connection: IPressureConnection = null

  override def canReceiveGas(side: ForgeDirection, kind: Gas): Boolean = side == getFacing.getOpposite && kind.getFluid != null && isFluidAllowed(kind.getFluid)

  override def receiveGas(side: ForgeDirection, stack: GasStack, doTransfer: Boolean): Int = {
    if (!worldObj.isRemote && stack != null && stack.getGas != null && stack.getGas.getFluid != null && stack.amount > 0 && canReceiveGas(side, stack.getGas)) {
      if (connection == null && Helper.canPipeConnectTo(worldObj, me.neighbour(getFacing), getFacing.getOpposite))
        connection = Helper.recalculateConnectionInfo(this, getFacing)
      if (connection != null)
        return connection.pushFluid(new FluidStack(stack.getGas.getFluid, stack.amount), doTransfer)
    }
    return 0
  }

  serverTick.listen(doPushGas)

  def doPushGas() {
    if ((me.meta(worldObj) & 8) == 0) return
    val face = getFacing
    me.neighbour(face.getOpposite).getTile[IGasHandler](worldObj).foreach { from =>
      val res = from.drawGas(face, Int.MaxValue, false)
      if (res != null && res.getGas != null && res.getGas.getFluid != null && res.amount > 0 && isFluidAllowed(res.getGas.getFluid)) {
        val filled = receiveGas(face.getOpposite, res, true)
        if (filled > 0)
          from.drawGas(face, filled, true)
      }
    }
  }

  override def invalidateConnection(direction: ForgeDirection) = connection = null

  override def getZCoord = zCoord
  override def getYCoord = yCoord
  override def getXCoord = xCoord
  override def getWorld = worldObj

  override def isValidDirectionForFakeTank(dir: ForgeDirection) = dir == getFacing.getOpposite
}
