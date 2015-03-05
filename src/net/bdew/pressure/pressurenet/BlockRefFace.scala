/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.pressurenet

import net.bdew.lib.block.BlockRef
import net.minecraftforge.common.util.ForgeDirection

case class BlockRefFace(block: BlockRef, face: ForgeDirection) {
  override def toString = "(x=%d y=%d z=%d %s)".format(block.x, block.y, block.z, face)
}

object BlockRefFace {
  implicit def brf2br(v: BlockRefFace): BlockRef = v.block
}
