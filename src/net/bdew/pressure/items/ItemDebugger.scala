/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.items

import cpw.mods.fml.common.registry.GameRegistry
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.lib.Misc
import net.bdew.lib.block.BlockRef
import net.bdew.lib.items.SimpleItem
import net.bdew.pressure.pressurenet.{Helper, ScanResult}
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.World

object ItemDebugger extends SimpleItem("Debugger") {
  setMaxStackSize(1)

  import scala.language.implicitConversions

  override def onItemUse(stack: ItemStack, player: EntityPlayer, world: World, x: Int, y: Int, z: Int, side: Int, xOff: Float, yOff: Float, zOff: Float): Boolean = {
    import net.bdew.lib.helpers.ChatHelper._
    if (!world.isRemote) {
      val br = BlockRef(x, y, z)
      var scanTime = System.nanoTime()
      val ScanResult(ins, outs, seen) = Helper.scanConnectedBlocks(world, br, Misc.forgeDirection(side), false)
      scanTime = System.nanoTime() - scanTime
      player.addChatMessage("====")
      player.addChatMessage("Ins: " + (ins map (_.blockRefFace) mkString ", "))
      player.addChatMessage("Outs: " + (outs map (_.blockRefFace) mkString ", "))
      player.addChatMessage("Seen:")
      for (x <- seen)
        player.addChatMessage(" * %s: %s".format(x, x.block(world) map (_.getUnlocalizedName) getOrElse "AIR"))
      player.addChatMessage("PConn: " + Helper.getPipeConnections(world, br).mkString(","))
      player.addChatMessage("Scanned %d blocks, took %d μs".format(seen.size, scanTime / 1000))
    }
    true
  }

  private val stick = GameRegistry.findItem("minecraft", "stick")

  @SideOnly(Side.CLIENT)
  override def getIconFromDamage(par1: Int) = stick.getIconFromDamage(0)

  @SideOnly(Side.CLIENT)
  override def registerIcons(reg: IIconRegister) {}
}
