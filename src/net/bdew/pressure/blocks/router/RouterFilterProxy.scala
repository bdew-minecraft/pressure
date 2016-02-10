/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.router

import net.bdew.pressure.api.properties.IFilterable
import net.minecraft.util.EnumFacing
import net.minecraftforge.fluids.Fluid

case class RouterFilterProxy(tile: TileRouter, side: EnumFacing) extends IFilterable {
  override def setFluidFilter(fluid: Fluid): Unit = tile.sideFilters.set(side, fluid)
  override def clearFluidFilter(): Unit = tile.sideFilters.clear(side)
}
