/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.tank

import net.bdew.lib.multiblock.tile.{TileController, TileModule}
import net.bdew.pressure.api.IFilterable
import net.minecraftforge.fluids.Fluid

trait CIFilterable extends TileController with IFilterable {
  def getFluidFilter: Option[Fluid]
}

trait MIFilterable extends TileModule with IFilterable {
  override def getCore = getCoreAs[CIFilterable]
  override def setFluidFilter(fluid: Fluid) = getCore foreach (_.setFluidFilter(fluid))
  override def clearFluidFilter() = getCore foreach (_.clearFluidFilter())
}
