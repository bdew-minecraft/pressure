/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.fmp

import codechicken.multipart.MultiPartRegistry.IPartFactory
import net.bdew.pressure.blocks.valves.check.BlockCheckValve
import net.bdew.pressure.fmp.parts.{CheckValvePart, PipePart}

object FmpFactory extends IPartFactory {
  override def createPart(name: String, client: Boolean) = (name, client) match {
    case ("bdew.pressure.pipe", _) => new PipePart
    case ("bdew.pressure.checkvalve", _) => new CheckValvePart(BlockCheckValve.getDefaultFacing)
    case _ => null
  }
}
