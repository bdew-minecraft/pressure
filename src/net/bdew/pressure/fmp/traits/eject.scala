/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.fmp.traits

import codechicken.multipart.TileMultipart
import net.bdew.pressure.api.IPressureEject
import net.bdew.pressure.fmp.FmpHandler
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.fluids.FluidStack

trait TEjectPart {
  def eject(resource: FluidStack, face: ForgeDirection, doEject: Boolean): Int
}

trait TileEject extends TileMultipart with IPressureEject {
  override def eject(resource: FluidStack, face: ForgeDirection, doEject: Boolean) =
    FmpHandler.getTypedPart(classOf[TEjectPart], this).map(_.eject(resource, face, doEject)).getOrElse(0)

  override def getXCoord = xCoord
  override def getYCoord = yCoord
  override def getZCoord = zCoord
  override def getWorld = getWorldObj
}
