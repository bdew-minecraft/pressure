/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.tank.gui

import net.bdew.lib.data.base.ContainerDataSlots
import net.bdew.lib.gui.{BaseContainer, SlotValidating}
import net.bdew.lib.multiblock.interact.ContainerOutputFaces
import net.bdew.pressure.blocks.tank.controller.TileTankController
import net.bdew.pressure.misc.SlotFilter
import net.minecraft.entity.player.EntityPlayer

class ContainerTank(val te: TileTankController, player: EntityPlayer) extends BaseContainer(te.inventory) with ContainerDataSlots with ContainerOutputFaces {
  lazy val dataSource = te

  addSlotToContainer(new SlotValidating(te.inventory, 0, 44, 19))
  addSlotToContainer(new SlotValidating(te.inventory, 1, 80, 19))

  addSlotToContainer(new SlotFilter(te.inventory, te.filterableCapability, 2, 149, 19))

  bindPlayerInventory(player.inventory, 8, 84, 142)
}
