/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.fmp

import codechicken.lib.vec.Cuboid6
import codechicken.multipart.TileMultipart
import net.bdew.lib.Misc
import net.minecraft.world.IBlockAccess

object FmpUtils {
  def getTypedPart[T](cls: Class[T], te: TileMultipart): Option[T] =
    te.partList.flatMap(Misc.asInstanceOpt(_, cls)).headOption

  def findTypedParts[T](world: IBlockAccess, x: Int, y: Int, z: Int, cls: Class[T]): Seq[T] = {
    val te = world.getTileEntity(x, y, z)
    if (te != null && te.isInstanceOf[TileMultipart]) {
      te.asInstanceOf[TileMultipart].partList.flatMap(Misc.asInstanceOpt(_, cls))
    } else Seq.empty
  }

  def cub6(p: (Float, Float, Float, Float, Float, Float)) = new Cuboid6(p._1, p._2, p._3, p._4, p._5, p._6)

}
