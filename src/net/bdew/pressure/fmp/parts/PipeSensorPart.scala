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
import net.bdew.lib.data.base.UpdateKind
import net.bdew.lib.data.{DataSlotBoolean, DataSlotDirection}
import net.bdew.pressure.api.IPressureInject
import net.bdew.pressure.blocks.valves.sensor.BlockPipeSensor
import net.bdew.pressure.misc.DataSlotFluidAverages
import net.bdew.pressure.pressurenet.Helper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.MovingObjectPosition
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.fluids.{Fluid, FluidStack}

class PipeSensorPart(aFacing: ForgeDirection = BlockPipeSensor.getDefaultFacing, aIsPowered: Boolean = false) extends BaseValvePart(BlockPipeSensor, "bdew.pressure.pipesensor") with IRedstonePart {
  def this(meta: Int) = this(Misc.forgeDirection(meta & 7), (meta & 8) == 8)

  override val facing = DataSlotDirection("facing", this).setUpdate(UpdateKind.WORLD, UpdateKind.SAVE)
  override val isPowered = DataSlotBoolean("state", this, aIsPowered).setUpdate(UpdateKind.WORLD, UpdateKind.SAVE)

  facing.update(aFacing)

  val averages = DataSlotFluidAverages("flow", this, 50).setUpdate(UpdateKind.SAVE)
  var flowThisTick = Map.empty[Fluid, Double]

  onServerTick(() => {
    averages.update(flowThisTick)
    flowThisTick = Map.empty
    coolDown -= 1
    flowTicks += 1
    if (coolDown <= 0) {
      val state = flowTicks < 10
      if (isPowered.value != state) {
        isPowered := state
        tile.notifyPartChange(this)
      }
      coolDown = 10
    }
  })

  var flowTicks = 10L
  var coolDown = 0L

  override def activate(player: EntityPlayer, hit: MovingObjectPosition, item: ItemStack): Boolean =
    if (!player.isSneaking) {
      if (!world.isRemote) {
        BlockPipeSensor.sendAveragesToPlayer(averages, player)
        true
      } else true
    } else false

  override def eject(resource: FluidStack, face: ForgeDirection, doEject: Boolean) = {
    if (face == facing.getOpposite && !tile.isSolid(facing.ordinal())) {
      if (outputConnection == null)
        outputConnection = Helper.recalculateConnectionInfo(tile.asInstanceOf[IPressureInject], facing)
      val res = outputConnection.pushFluid(resource, doEject)
      if (res > 0) {
        flowTicks = 0
        if (doEject)
          flowThisTick += resource.getFluid -> (flowThisTick.getOrElse(resource.getFluid, 0D) + res)
      }
      res
    } else 0
  }

  override def strongPowerLevel(side: Int) = 0
  override def canConnectRedstone(side: Int) =
    (facing.ordinal() != side) && (facing.getOpposite.ordinal() != side)
  override def weakPowerLevel(side: Int) =
    if (canConnectRedstone(side) && isPowered) 15 else 0
}
