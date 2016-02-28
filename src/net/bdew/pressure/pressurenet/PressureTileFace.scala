/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.pressurenet

import net.bdew.lib.block.BlockFace
import net.bdew.pressure.api.{IPressureEject, IPressureInject, IPressureNode}
import net.minecraft.util.EnumFacing
import net.minecraftforge.fluids.FluidStack

trait PressureTileFace {
  val tile: IPressureNode
  val face: EnumFacing
  def blockRef = tile.pressureNodePos
  def blockRefFace = BlockFace(tile.pressureNodePos, face)
}

case class PressureOutputFace(tile: IPressureEject, face: EnumFacing) extends PressureTileFace {
  def eject(resource: FluidStack, doEject: Boolean) = tile.eject(resource, face, doEject)
}

case class PressureInputFace(tile: IPressureInject, face: EnumFacing) extends PressureTileFace {
  def invalidateConnection() = tile.invalidateConnection(face)
}
