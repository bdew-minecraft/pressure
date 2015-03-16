/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.fmp.waila

import java.util

import mcp.mobius.waila.api._
import net.bdew.pressure.Pressure
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumChatFormatting

import scala.collection.JavaConversions._

class BaseFMPDataProvider extends IWailaFMPProvider {
  def getTailStrings(stack: ItemStack, acc: IWailaFMPAccessor, cfg: IWailaConfigHandler): Iterable[String] = None
  def getHeadStrings(stack: ItemStack, acc: IWailaFMPAccessor, cfg: IWailaConfigHandler): Iterable[String] = None
  def getBodyStrings(stack: ItemStack, acc: IWailaFMPAccessor, cfg: IWailaConfigHandler): Iterable[String] = None

  override final def getWailaHead(itemStack: ItemStack, tip: util.List[String], accessor: IWailaFMPAccessor, config: IWailaConfigHandler) = {
    try {
      tip.addAll(getHeadStrings(itemStack, accessor, config))
    } catch {
      case e: Throwable =>
        Pressure.logWarnException("Error in waila handler", e)
        tip.add("[%s%s%s]".format(EnumChatFormatting.RED, e.toString, EnumChatFormatting.RESET))
    }
    tip
  }

  override final def getWailaBody(itemStack: ItemStack, tip: util.List[String], accessor: IWailaFMPAccessor, config: IWailaConfigHandler) = {
    try {
      tip.addAll(getBodyStrings(itemStack, accessor, config))
    } catch {
      case e: Throwable =>
        Pressure.logWarnException("Error in waila handler", e)
        tip.add("[%s%s%s]".format(EnumChatFormatting.RED, e.toString, EnumChatFormatting.RESET))
    }
    tip
  }

  override final def getWailaTail(itemStack: ItemStack, tip: util.List[String], accessor: IWailaFMPAccessor, config: IWailaConfigHandler) = {
    try {
      tip.addAll(getTailStrings(itemStack, accessor, config))
    } catch {
      case e: Throwable =>
        Pressure.logWarnException("Error in waila handler", e)
        tip.add("[%s%s%s]".format(EnumChatFormatting.RED, e.toString, EnumChatFormatting.RESET))
    }
    tip
  }
}
