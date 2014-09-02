/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.tank

import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.lib.gui.GuiProvider
import net.bdew.lib.machine.Machine
import net.bdew.lib.multiblock.MachineCore
import net.bdew.pressure.blocks.tank.controller.{BlockTankController, TileTankController}
import net.minecraft.entity.player.EntityPlayer

object MachineTank extends Machine("TankController", BlockTankController) with MachineCore with GuiProvider {
  def guiId: Int = 1
  type TEClass = TileTankController

  @SideOnly(Side.CLIENT)
  def getGui(te: TileTankController, player: EntityPlayer) = new GuiTank(te, player)
  def getContainer(te: TileTankController, player: EntityPlayer) = new ContainerTank(te, player)
}
