/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.fmp.traits

import codechicken.multipart.TileMultipart
import net.bdew.pressure.api.IPressureInject
import net.bdew.pressure.fmp.FmpUtils
import net.minecraftforge.common.util.ForgeDirection

trait TInjectPart {
  def invalidateConnection(side: ForgeDirection): Unit
}

trait TileInject extends TileMultipart with IPressureInject {
  override def invalidateConnection(side: ForgeDirection) =
    FmpUtils.getTypedPart(classOf[TInjectPart], this).foreach(_.invalidateConnection(side))

  override def getXCoord = xCoord
  override def getYCoord = yCoord
  override def getZCoord = zCoord
  override def getWorld = getWorldObj
}
