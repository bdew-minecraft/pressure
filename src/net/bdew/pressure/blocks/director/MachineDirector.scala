/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.director

import cpw.mods.fml.common.FMLCommonHandler
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.lib.Misc
import net.bdew.lib.gui.GuiProvider
import net.bdew.lib.machine.Machine
import net.bdew.lib.multiblock.data.RSMode
import net.bdew.pressure.blocks.director.gui.{ContainerDirector, GuiDirector, MsgSetDirectorSideControl}
import net.bdew.pressure.network.NetworkHandler
import net.minecraft.entity.player.EntityPlayer

object MachineDirector extends Machine("Director", BlockDirector) with GuiProvider {
  def guiId: Int = 3
  type TEClass = TileDirector

  if (FMLCommonHandler.instance().getSide.isClient)
    DirectorIcons.init()

  NetworkHandler.regServerHandler({
    case (MsgSetDirectorSideControl(side, mode), player) =>
      Misc.asInstanceOpt(player.openContainer, classOf[ContainerDirector]) map { container =>
        container.te.sideControl.set(side, mode)
      }
  })

  @SideOnly(Side.CLIENT)
  def getGui(te: TileDirector, player: EntityPlayer) = new GuiDirector(te, player)
  def getContainer(te: TileDirector, player: EntityPlayer) = new ContainerDirector(te, player)

  val rsModeOrder = Map(
    RSMode.ALWAYS -> RSMode.RS_ON,
    RSMode.RS_ON -> RSMode.RS_OFF,
    RSMode.RS_OFF -> RSMode.NEVER,
    RSMode.NEVER -> RSMode.ALWAYS
  )

}
