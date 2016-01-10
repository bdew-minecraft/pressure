/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.valves.check

import net.bdew.pressure.api.{IPressureConnection, IPressureEject, IPressureInject}
import net.bdew.pressure.pressurenet.Helper
import net.minecraft.block.state.IBlockState
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.{BlockPos, EnumFacing}
import net.minecraft.world.World
import net.minecraftforge.fluids.FluidStack

class TileCheckValve extends TileEntity with IPressureEject with IPressureInject {
  var connection: IPressureConnection = null

  override def shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newSate: IBlockState) =
    oldState.getBlock != newSate.getBlock

  def getFacing = BlockCheckValve.getFacing(worldObj, pos)

  override def eject(resource: FluidStack, face: EnumFacing, doEject: Boolean): Int = {
    if (face == getFacing.getOpposite && !BlockCheckValve.isPowered(worldObj, pos)) {
      if (connection == null)
        connection = Helper.recalculateConnectionInfo(this, getFacing)
      connection.pushFluid(resource, doEject)
    } else 0
  }

  override def invalidateConnection(direction: EnumFacing) = connection = null
}
