/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.gas

import mekanism.api.gas.{GasRegistry, GasStack}
import net.bdew.lib.data.base.TileDataSlots
import net.bdew.pressure.api.{IPressureConnectableBlock, IPressureEject}
import net.bdew.pressure.blocks.output.BlockOutput
import net.bdew.pressure.blocks.{BaseIOBlock, BlockNotifyUpdates, TileFilterable}
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraftforge.fluids.FluidStack

object BlockPressureGasOutput extends BaseIOBlock("gas_output", classOf[TilePressureGasOutput]) with BlockNotifyUpdates with IPressureConnectableBlock {
  override def canConnectTo(world: IBlockAccess, pos: BlockPos, side: EnumFacing) =
    getFacing(world, pos) == side.getOpposite
  override def isTraversable(world: IBlockAccess, pos: BlockPos) = false
}

class TilePressureGasOutput extends TileDataSlots with IPressureEject with TileFilterable with GasHandlerProxy {
  def getFacing = BlockOutput.getFacing(world, pos)

  val gasHandler = new BaseGasHandler
  addCapability(GasSupport.CAP_GAS_HANDLER) { case side if side == getFacing => gasHandler }
  addCapability(GasSupport.CAP_TUBE_CONNECTION) { case side if side == getFacing => gasHandler }

  override def eject(resource: FluidStack, direction: EnumFacing, doEject: Boolean) = {
    if (isFluidAllowed(resource) && direction == getFacing.getOpposite) {
      val gas = GasRegistry.getGas(resource.getFluid)
      if (gas != null) {
        val f = getFacing
        GasSupport.getGasHandler(world, pos.offset(f), f.getOpposite) map { dest =>
          dest.receiveGas(f.getOpposite, new GasStack(gas, resource.amount), doEject)
        } getOrElse 0
      } else 0
    } else 0
  }

  override def pressureNodePos = getPos
  override def pressureNodeWorld = getWorld
}

