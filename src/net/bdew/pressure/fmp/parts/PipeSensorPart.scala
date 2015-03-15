/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.fmp.parts

import codechicken.multipart.IRedstonePart
import net.bdew.lib.Misc
import net.bdew.lib.data.{DataSlotBoolean, DataSlotDirection}
import net.bdew.pressure.api.IPressureInject
import net.bdew.pressure.blocks.valves.sensor.BlockPipeSensor
import net.bdew.pressure.pressurenet.Helper
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.fluids.FluidStack

class PipeSensorPart(aFacing: ForgeDirection = BlockPipeSensor.getDefaultFacing, aIsPowered: Boolean = false) extends BaseValvePart(BlockPipeSensor, "bdew.pressure.pipesensor") with IRedstonePart {
  def this(meta: Int) = this(Misc.forgeDirection(meta & 7), (meta & 8) == 8)

  override val facing: DataSlotDirection = DataSlotDirection("facing", this)
  override val isPowered: DataSlotBoolean = DataSlotBoolean("state", this, aIsPowered)

  facing.update(aFacing)

  var flowTicks = 10L
  var coolDown = 0L

  override def update(): Unit = {
    if (world.isRemote) return
    coolDown -= 1
    flowTicks += 1
    if (coolDown <= 0) {
      val state = flowTicks < 10
      if (isPowered.value != state) {
        isPowered := state
        tile.notifyPartChange(this)
        tile.markDirty()
        sendDescUpdate()
      }
      coolDown = 10
    }
  }

  override def eject(resource: FluidStack, face: ForgeDirection, doEject: Boolean) = {
    if (face == facing.getOpposite && !tile.isSolid(facing.ordinal())) {
      if (outputConnection == null)
        outputConnection = Helper.recalculateConnectionInfo(tile.asInstanceOf[IPressureInject], facing)
      val res = outputConnection.pushFluid(resource, doEject)
      if (res > 0)
        flowTicks = 0
      res
    } else 0
  }

  override def strongPowerLevel(side: Int) = 0
  override def canConnectRedstone(side: Int) =
    (facing.ordinal() != side) && (facing.getOpposite.ordinal() != side)
  override def weakPowerLevel(side: Int) =
    if (canConnectRedstone(side) && isPowered) 15 else 0
}
