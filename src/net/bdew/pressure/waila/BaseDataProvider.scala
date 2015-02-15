/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.waila

import java.util

import mcp.mobius.waila.api.{IWailaConfigHandler, IWailaDataAccessor, IWailaDataProvider}
import net.bdew.pressure.Pressure
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumChatFormatting
import net.minecraft.world.World

import scala.collection.JavaConversions._

class BaseDataProvider[T](cls: Class[T]) extends IWailaDataProvider {
  def getTailStrings(target: T, stack: ItemStack, acc: IWailaDataAccessor, cfg: IWailaConfigHandler): Iterable[String] = None
  def getHeadStrings(target: T, stack: ItemStack, acc: IWailaDataAccessor, cfg: IWailaConfigHandler): Iterable[String] = None
  def getBodyStrings(target: T, stack: ItemStack, acc: IWailaDataAccessor, cfg: IWailaConfigHandler): Iterable[String] = None

  override def getNBTData(player: EntityPlayerMP, te: TileEntity, tag: NBTTagCompound, world: World, x: Int, y: Int, z: Int): NBTTagCompound = null

  final override def getWailaTail(itemStack: ItemStack, tip: util.List[String], accessor: IWailaDataAccessor, config: IWailaConfigHandler) = {
    try {
      if (cls.isInstance(accessor.getTileEntity))
        tip.addAll(getTailStrings(accessor.getTileEntity.asInstanceOf[T], itemStack, accessor, config))
      else if (cls.isInstance(accessor.getBlock))
        tip.addAll(getTailStrings(accessor.getBlock.asInstanceOf[T], itemStack, accessor, config))
    } catch {
      case e: Throwable =>
        Pressure.logWarnException("Error in waila handler", e)
        tip.add("[%s%s%s]".format(EnumChatFormatting.RED, e.toString, EnumChatFormatting.RESET))
    }
    tip
  }

  final override def getWailaHead(itemStack: ItemStack, tip: util.List[String], accessor: IWailaDataAccessor, config: IWailaConfigHandler) = {
    try {
      if (cls.isInstance(accessor.getTileEntity))
        tip.addAll(getHeadStrings(accessor.getTileEntity.asInstanceOf[T], itemStack, accessor, config))
      else if (cls.isInstance(accessor.getBlock))
        tip.addAll(getHeadStrings(accessor.getBlock.asInstanceOf[T], itemStack, accessor, config))
    } catch {
      case e: Throwable =>
        Pressure.logWarnException("Error in waila handler", e)
        tip.add("[%s%s%s]".format(EnumChatFormatting.RED, e.toString, EnumChatFormatting.RESET))
    }
    tip
  }

  final override def getWailaBody(itemStack: ItemStack, tip: util.List[String], accessor: IWailaDataAccessor, config: IWailaConfigHandler) = {
    try {
      if (cls.isInstance(accessor.getTileEntity))
        tip.addAll(getBodyStrings(accessor.getTileEntity.asInstanceOf[T], itemStack, accessor, config))
      else if (cls.isInstance(accessor.getBlock))
        tip.addAll(getBodyStrings(accessor.getBlock.asInstanceOf[T], itemStack, accessor, config))
    } catch {
      case e: Throwable =>
        Pressure.logWarnException("Error in waila handler", e)
        tip.add("[%s%s%s]".format(EnumChatFormatting.RED, e.toString, EnumChatFormatting.RESET))
    }
    tip
  }

  override def getWailaStack(accessor: IWailaDataAccessor, config: IWailaConfigHandler): ItemStack = null
}
