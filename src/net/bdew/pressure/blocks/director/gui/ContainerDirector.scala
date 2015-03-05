/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.director.gui

import net.bdew.lib.data.base.ContainerDataSlots
import net.bdew.lib.gui.NoInvContainer
import net.bdew.lib.tile.inventory.SimpleInventory
import net.bdew.pressure.blocks.director.TileDirector
import net.minecraft.entity.player.EntityPlayer

class ContainerDirector(val te: TileDirector, player: EntityPlayer) extends NoInvContainer with ContainerDataSlots {
  lazy val dataSource = te

  val inventory = new SimpleInventory(6)

  for (i <- 0 until 6)
    addSlotToContainer(new SlotMode(this, i, 27 + 21 * i, 19))

  for (i <- 0 until 6)
    addSlotToContainer(new SlotFilter(this, i, 27 + 21 * i, 39))

  bindPlayerInventory(player.inventory, 8, 84, 142)

  override def canInteractWith(player: EntityPlayer): Boolean = true
}
