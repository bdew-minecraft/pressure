/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.mutilpart

import mcmultipart.multipart.{IMultipart, IMultipartContainer, MultipartHelper}
import net.bdew.lib.Misc
import net.minecraft.util.BlockPos
import net.minecraft.world.IBlockAccess

import scala.collection.JavaConversions._

object MCMPUtils {
  def getTypedParts[T <: IMultipart](cls: Class[T], te: IMultipartContainer): Iterable[T] =
    Misc.filterType(te.getParts, cls)

  def getTypedParts[T <: IMultipart](cls: Class[T], w: IBlockAccess, p: BlockPos): Iterable[T] =
    Option(MultipartHelper.getPartContainer(w, p)) map (te => Misc.filterType(te.getParts, cls)) getOrElse List.empty
}
