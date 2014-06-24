/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/pressure/master/MMPL-1.0.txt
 */

package net.bdew.pressure

import net.bdew.lib.items.SimpleItem
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraft.entity.player.EntityPlayer
import net.bdew.pressure.misc.{Helper, BlockRef}
import net.minecraft.client.renderer.texture.IIconRegister
import cpw.mods.fml.relauncher.{SideOnly, Side}
import net.minecraft.util.ChatComponentText
import cpw.mods.fml.common.registry.GameRegistry

object ItemDebugger extends SimpleItem("Debugger") {
  setMaxStackSize(1)

  implicit def string2chatcomp(s: String) = new ChatComponentText(s)

  override def onItemUse(stack: ItemStack, player: EntityPlayer, world: World, x: Int, y: Int, z: Int, side: Int, xOff: Float, yOff: Float, zOff: Float): Boolean = {
    if (!world.isRemote) {
      val br = BlockRef(world, x, y, z)
      val (ins, outs, seen) = Helper.scanConnectedBlocks(br, false)
      player.addChatMessage("====")
      player.addChatMessage("Ins: " + (ins map (x => "[%d,%d,%d]".format(x.getXCoord, x.getYCoord, x.getZCoord)) mkString " "))
      player.addChatMessage("Outs: " + (outs map (x => "[%d,%d,%d]".format(x.getXCoord, x.getYCoord, x.getZCoord)) mkString " "))
      player.addChatMessage("Seen:")
      seen.foreach(x => player.addChatMessage(" * %d,%d,%d: %s".format(x.x, x.y, x.z, x.block map (_.getUnlocalizedName) getOrElse "AIR")))
      player.addChatMessage("PConn: " + Helper.getPipeConnections(br).mkString(","))
    }
    true
  }

  private val stick = GameRegistry.findItem("minecraft", "stick")

  @SideOnly(Side.CLIENT)
  override def getIconFromDamage(par1: Int) = stick.getIconFromDamage(0)

  @SideOnly(Side.CLIENT)
  override def registerIcons(reg: IIconRegister) {}
}
