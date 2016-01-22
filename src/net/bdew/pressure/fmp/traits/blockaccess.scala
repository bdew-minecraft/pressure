/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.fmp.traits

import codechicken.multipart.TMultiPart
import net.bdew.lib.world.BlockAccessProxy
import net.minecraft.block.Block

trait TBlockAccessPart extends TMultiPart {
  def getBlock: Block
  def getBlockMetadata: Int
}

class PartBlockAccess(val part: TBlockAccessPart) extends BlockAccessProxy(part.world) {
  override def getBlock(x: Int, y: Int, z: Int) =
    if (part.x == x && part.y == y && part.z == z)
      part.getBlock
    else
      super.getBlock(x, y, z)

  override def getBlockMetadata(x: Int, y: Int, z: Int) =
    if (part.x == x && part.y == y && part.z == z)
      part.getBlockMetadata
    else
      super.getBlockMetadata(x, y, z)
}
