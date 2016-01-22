/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.gas

import mekanism.api.gas.{GasRegistry, GasStack, IGasHandler}
import net.bdew.lib.block.BlockRef
import net.bdew.lib.data.base.TileDataSlots
import net.bdew.pressure.api.{IPressureConnectableBlock, IPressureEject}
import net.bdew.pressure.blocks.{BaseIOBlock, BlockNotifyUpdates, TileFilterable}
import net.bdew.pressure.misc.FakeGasTank
import net.minecraft.world.IBlockAccess
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.fluids.FluidStack

object BlockPressureGasOutput extends BaseIOBlock("gasoutput", classOf[TilePressureGasOutput]) with BlockNotifyUpdates with IPressureConnectableBlock {
  override def canConnectTo(world: IBlockAccess, x: Int, y: Int, z: Int, side: ForgeDirection) =
    getFacing(world, x, y, z) == side.getOpposite
  override def isTraversable(world: IBlockAccess, x: Int, y: Int, z: Int) = false
  override def canConnectRedstone(world: IBlockAccess, x: Int, y: Int, z: Int, side: Int) = true
}

class TilePressureGasOutput extends TileDataSlots with FakeGasTank with IPressureEject with TileFilterable {
  def getFacing = BlockPressureGasOutput.getFacing(worldObj, xCoord, yCoord, zCoord)

  lazy val me = BlockRef.fromTile(this)

  override def eject(resource: FluidStack, direction: ForgeDirection, doEject: Boolean) = {
    val gas = GasRegistry.getGas(resource.getFluid)
    if (gas != null && isFluidAllowed(resource) && direction == getFacing.getOpposite) {
      val f = getFacing
      me.neighbour(f).getTile[IGasHandler](worldObj) map { dest =>
        dest.receiveGas(f.getOpposite, new GasStack(gas, resource.amount), doEject)
      } getOrElse 0
    } else 0
  }

  override def getXCoord = xCoord
  override def getYCoord = yCoord
  override def getZCoord = zCoord
  override def getWorld = worldObj

  override def isValidDirectionForFakeTank(dir: ForgeDirection) = getFacing == dir
}

