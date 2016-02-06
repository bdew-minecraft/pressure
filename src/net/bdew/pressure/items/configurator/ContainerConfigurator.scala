/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.items.configurator

import net.bdew.lib.gui.NoInvContainer
import net.minecraft.entity.player.EntityPlayer

class ContainerConfigurator(player: EntityPlayer) extends NoInvContainer {
  bindPlayerInventory(player.inventory, 8, 88, 146)
  override def canInteractWith(player: EntityPlayer) = true
}
