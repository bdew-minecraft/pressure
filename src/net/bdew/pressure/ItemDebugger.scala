/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure

import cpw.mods.fml.common.registry.GameRegistry
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.lib.block.BlockRef
import net.bdew.lib.items.SimpleItem
import net.bdew.pressure.misc.Helper
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.ChatComponentText
import net.minecraft.world.World

object ItemDebugger extends SimpleItem("Debugger") {
  setMaxStackSize(1)

  import scala.language.implicitConversions

  implicit def string2chatComponent(s: String): ChatComponentText = new ChatComponentText(s)

  override def onItemUse(stack: ItemStack, player: EntityPlayer, world: World, x: Int, y: Int, z: Int, side: Int, xOff: Float, yOff: Float, zOff: Float): Boolean = {
    if (!world.isRemote) {
      val br = BlockRef(x, y, z)
      val (ins, outs, seen) = Helper.scanConnectedBlocks(world, br, false)
      player.addChatMessage("====")
      player.addChatMessage("Ins: " + (ins map (x => "[%d,%d,%d]".format(x.getXCoord, x.getYCoord, x.getZCoord)) mkString " "))
      player.addChatMessage("Outs: " + (outs map (x => "[%d,%d,%d]".format(x.getXCoord, x.getYCoord, x.getZCoord)) mkString " "))
      player.addChatMessage("Seen:")
      seen.foreach(x => player.addChatMessage(" * %d,%d,%d: %s".format(x.x, x.y, x.z, x.block(world) map (_.getUnlocalizedName) getOrElse "AIR")))
      player.addChatMessage("PConn: " + Helper.getPipeConnections(world, br).mkString(","))
    }
    true
  }

  private val stick = GameRegistry.findItem("minecraft", "stick")

  @SideOnly(Side.CLIENT)
  override def getIconFromDamage(par1: Int) = stick.getIconFromDamage(0)

  @SideOnly(Side.CLIENT)
  override def registerIcons(reg: IIconRegister) {}
}
