/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.director

import net.bdew.lib.data.base.{TileDataSlots, UpdateKind}
import net.bdew.pressure.blocks.director.data.{DataSlotSideFilters, DataSlotSideModes, DataSlotSideRSControl}

class TileDirector extends TileDataSlots {
  val sideModes = DataSlotSideModes("modes", this).setUpdate(UpdateKind.SAVE, UpdateKind.GUI, UpdateKind.WORLD, UpdateKind.RENDER)
  val sideControl = DataSlotSideRSControl("control", this).setUpdate(UpdateKind.SAVE, UpdateKind.GUI)
  val sideFilters = DataSlotSideFilters("filters", this).setUpdate(UpdateKind.SAVE, UpdateKind.GUI)
}
