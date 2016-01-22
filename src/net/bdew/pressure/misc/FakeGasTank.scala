/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.misc

import mekanism.api.gas.{Gas, GasStack, IGasHandler, ITubeConnection}
import net.minecraftforge.common.util.ForgeDirection

/**
 * Mixin that makes a TE look like a gas tank but not do anything. Used to make pipes from other mods connect correctly.
 */
trait FakeGasTank extends IGasHandler with ITubeConnection {
  def isValidDirectionForFakeTank(dir: ForgeDirection): Boolean

  override def canDrawGas(side: ForgeDirection, kind: Gas): Boolean = false
  override def drawGas(side: ForgeDirection, amount: Int): GasStack = drawGas(side, amount, true)
  override def drawGas(side: ForgeDirection, amount: Int, doTransfer: Boolean): GasStack = null

  override def canReceiveGas(side: ForgeDirection, `type`: Gas): Boolean = false
  override def receiveGas(side: ForgeDirection, stack: GasStack): Int = receiveGas(side, stack, true)
  override def receiveGas(side: ForgeDirection, stack: GasStack, doTransfer: Boolean): Int = 0

  override def canTubeConnect(side: ForgeDirection): Boolean = isValidDirectionForFakeTank(side)
}
