/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.director.data

import net.bdew.lib.data.base.TileDataSlots

case class DataSlotSideModes(name: String, parent: TileDataSlots) extends DataSlotDirectionMap(DirectorSideMode, DirectorSideMode.DISABLED) {
  def sides(mode: DirectorSideMode.Value) =
    for ((side, sideMode) <- map if sideMode == mode) yield side
  def sides(modes: Set[DirectorSideMode.Value]) =
    for ((side, sideMode) <- map if modes.contains(sideMode)) yield side
}