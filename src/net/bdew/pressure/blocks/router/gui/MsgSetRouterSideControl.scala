/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.router.gui

import net.bdew.lib.multiblock.data.RSMode
import net.bdew.pressure.network.NetworkHandler
import net.minecraft.util.EnumFacing

case class MsgSetRouterSideControl(side: EnumFacing, mode: RSMode.Value) extends NetworkHandler.Message

