/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.director.gui

import net.bdew.lib.multiblock.data.RSMode
import net.bdew.pressure.network.NetworkHandler
import net.minecraftforge.common.util.ForgeDirection

case class MsgSetDirectorSideControl(side: ForgeDirection, mode: RSMode.Value) extends NetworkHandler.Message

