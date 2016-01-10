/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.items

import net.bdew.lib.PimpVanilla._
import net.bdew.lib.items.SimpleItem
import net.bdew.pressure.pressurenet.{Helper, ScanResult}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.{BlockPos, EnumFacing}
import net.minecraft.world.World

object ItemDebugger extends SimpleItem("Debugger") {
  setMaxStackSize(1)

  override def onItemUse(stack: ItemStack, player: EntityPlayer, world: World, pos: BlockPos, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean = {
    import net.bdew.lib.helpers.ChatHelper._
    if (!world.isRemote) {
      val br = pos
      var scanTime = System.nanoTime()
      val ScanResult(ins, outs, seen) = Helper.scanConnectedBlocks(world, br, side, false)
      scanTime = System.nanoTime() - scanTime
      player.addChatMessage("====")
      player.addChatMessage("Ins: " + (ins map (_.blockRefFace) mkString ", "))
      player.addChatMessage("Outs: " + (outs map (_.blockRefFace) mkString ", "))
      player.addChatMessage("Seen:")
      for (x <- seen)
        player.addChatMessage(" * %s: %s".format(x, world.getBlockState(x).getBlock.getUnlocalizedName))
      player.addChatMessage("PConn: " + Helper.getPipeConnections(world, br).mkString(","))
      player.addChatMessage("Scanned %d blocks, took %d μs".format(seen.size, scanTime / 1000))
    }
    true
  }
}
