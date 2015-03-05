/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.pressurenet

import net.bdew.lib.block.BlockRef
import net.bdew.pressure.api.{IPressureEject, IPressureInject, IPressureTile}
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.fluids.FluidStack

trait PressureTileFace {
  val tile: IPressureTile
  val face: ForgeDirection
  def blockRef = BlockRef(tile.getXCoord, tile.getYCoord, tile.getZCoord)
  def blockRefFace = BlockRefFace(blockRef, face)
}

case class PressureOutputFace(tile: IPressureEject, face: ForgeDirection) extends PressureTileFace {
  def eject(resource: FluidStack, doEject: Boolean) = tile.eject(resource, face, doEject)
}

case class PressureInputFace(tile: IPressureInject, face: ForgeDirection) extends PressureTileFace {
  def invalidateConnection() = tile.invalidateConnection(face)
}
