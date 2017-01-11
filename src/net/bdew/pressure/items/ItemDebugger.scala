/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.items

import net.bdew.lib.PimpVanilla._
import net.bdew.lib.items.BaseItem
import net.bdew.pressure.pressurenet.{Helper, ScanResult}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{EnumActionResult, EnumFacing, EnumHand}
import net.minecraft.world.World

object ItemDebugger extends BaseItem("debugger") {
  setMaxStackSize(1)

  override def onItemUse(player: EntityPlayer, world: World, pos: BlockPos, hand: EnumHand, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult = {
    import net.bdew.lib.helpers.ChatHelper._
    if (!world.isRemote) {
      val br = pos
      var scanTime = System.nanoTime()
      val ScanResult(ins, outs, seen) = Helper.scanConnectedBlocks(world, br, side, false)
      scanTime = System.nanoTime() - scanTime
      player.sendMessage("====")
      player.sendMessage("Ins: " + (ins map (_.blockRefFace) mkString ", "))
      player.sendMessage("Outs: " + (outs map (_.blockRefFace) mkString ", "))
      player.sendMessage("Seen:")
      for (x <- seen)
        player.sendMessage(" * %s: %s".format(x, world.getBlockState(x).getBlock.getUnlocalizedName))
      player.sendMessage("PConn: " + Helper.getPipeConnections(world, br).mkString(","))
      player.sendMessage("Scanned %d blocks, took %d μs".format(seen.size, scanTime / 1000))
    }
    EnumActionResult.SUCCESS
  }
}
