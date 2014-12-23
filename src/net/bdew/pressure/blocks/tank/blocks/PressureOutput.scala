/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.tank.blocks

import net.bdew.lib.multiblock.block.BlockOutput
import net.bdew.lib.multiblock.data.OutputConfigFluid
import net.bdew.lib.multiblock.interact.CIFluidOutput
import net.bdew.lib.multiblock.tile.{RSControllableOutput, TileOutput}
import net.bdew.pressure.api.{IPressureConnectableBlock, IPressureConnection, IPressureInject}
import net.bdew.pressure.blocks.BlockNotifyUpdates
import net.bdew.pressure.blocks.tank.{BaseModule, PressureModule}
import net.bdew.pressure.misc.Helper
import net.minecraft.world.IBlockAccess
import net.minecraftforge.common.util.ForgeDirection

object BlockPressureOutput extends BaseModule("TankPressureOutput", "FluidOutput", classOf[TilePressureOutput])
with BlockOutput[TilePressureOutput] with BlockNotifyUpdates with IPressureConnectableBlock {
  override def canConnectTo(world: IBlockAccess, x: Int, y: Int, z: Int, side: ForgeDirection) =
    getTE(world, x, y, z).getCore.isDefined
}

class TilePressureOutput extends TileOutput[OutputConfigFluid] with PressureModule with RSControllableOutput with IPressureInject {
  val kind: String = "FluidOutput"

  override val outputConfigType = classOf[OutputConfigFluid]
  override def getCore = getCoreAs[CIFluidOutput]

  override def canConnectToFace(d: ForgeDirection) =
    Helper.canPipeConnectFrom(worldObj, myPos.neighbour(d), d.getOpposite)

  override def makeCfgObject(face: ForgeDirection) = new OutputConfigFluid

  override def invalidateConnection() = connections = Map.empty

  var connections = Map.empty[ForgeDirection, IPressureConnection]

  override def doOutput(face: ForgeDirection, cfg: OutputConfigFluid) =
    if (checkCanOutput(cfg)) {
      getCore map { core =>
        if (!connections.isDefinedAt(face))
          Option(Helper.recalculateConnectionInfo(this, face)) map { cObj => connections += face -> cObj }

        connections.get(face) map { conn =>
          val fs = core.outputFluid(Int.MaxValue, false)
          val out = conn.pushFluid(fs, true)
          if (out > 0) {
            core.outputFluid(out, true)
            cfg.updateAvg(out)
            core.outputConfig.updated()
          }
        }
      }
    }
}

