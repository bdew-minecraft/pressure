/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.fmp.parts

import codechicken.multipart.{IRedstonePart, RedstoneInteractions, TMultiPart}
import net.bdew.lib.Misc
import net.bdew.lib.data.base.UpdateKind
import net.bdew.lib.data.{DataSlotBoolean, DataSlotDirection}
import net.bdew.pressure.api.IPressureInject
import net.bdew.pressure.blocks.valves.check.BlockCheckValve
import net.bdew.pressure.pressurenet.Helper
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.fluids.FluidStack

class CheckValvePart(aFacing: ForgeDirection = BlockCheckValve.getDefaultFacing, aIsPowered: Boolean = false) extends BaseValvePart(BlockCheckValve, "bdew.pressure.checkvalve") with IRedstonePart {
  def this(meta: Int) = this(Misc.forgeDirection(meta & 7), (meta & 8) == 8)

  override val facing = DataSlotDirection("facing", this).setUpdate(UpdateKind.WORLD, UpdateKind.SAVE)
  override val isPowered = DataSlotBoolean("state", this, aIsPowered).setUpdate(UpdateKind.WORLD, UpdateKind.SAVE)

  facing.update(aFacing)

  override def eject(resource: FluidStack, face: ForgeDirection, doEject: Boolean) = {
    if (!isPowered && face == facing.getOpposite && !tile.isSolid(facing.ordinal())) {
      if (outputConnection == null)
        outputConnection = Helper.recalculateConnectionInfo(tile.asInstanceOf[IPressureInject], facing)
      outputConnection.pushFluid(resource, doEject)
    } else 0
  }

  override def strongPowerLevel(side: Int) = 0
  override def weakPowerLevel(side: Int) = 0
  override def canConnectRedstone(side: Int) = side != facing.ordinal() && side != facing.getOpposite.ordinal()

  override def onPartChanged(part: TMultiPart) = {
    super.onPartChanged(part)
    onNeighborChanged()
  }

  override def onNeighborChanged(): Unit = {
    val sides = ForgeDirection.VALID_DIRECTIONS.toSet -- Set(facing.value, facing.value.getOpposite)
    val powered = sides.exists(s => RedstoneInteractions.getPowerTo(this, s.ordinal()) > 0)
    if (powered != isPowered.value) {
      isPowered := powered
      tile.notifyPartChange(this)
    }
  }
}
