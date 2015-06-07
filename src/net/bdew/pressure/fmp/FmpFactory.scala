/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.fmp

import codechicken.lib.data.MCDataInput
import codechicken.multipart.MultiPartRegistry.IPartFactory2
import codechicken.multipart.TMultiPart
import net.bdew.pressure.blocks.valves.check.BlockCheckValve
import net.bdew.pressure.fmp.parts.{CheckValvePart, PipePart, PipeSensorPart}
import net.minecraft.nbt.NBTTagCompound

object FmpFactory extends IPartFactory2 {
  override def createPart(name: String, nbt: NBTTagCompound): TMultiPart = createSimplePart(name)
  override def createPart(name: String, packet: MCDataInput): TMultiPart = createSimplePart(name)

  def createSimplePart(name: String) = name match {
    case "bdew.pressure.pipe" => new PipePart
    case "bdew.pressure.checkvalve" => new CheckValvePart(BlockCheckValve.getDefaultFacing)
    case "bdew.pressure.pipesensor" => new PipeSensorPart(BlockCheckValve.getDefaultFacing)
    case _ => null
  }
}
