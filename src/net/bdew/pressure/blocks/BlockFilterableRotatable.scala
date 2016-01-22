/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks

import net.bdew.lib.rotate.{BaseRotatableBlock, IconType}
import net.minecraft.world.IBlockAccess
import net.minecraftforge.common.util.ForgeDirection

trait BlockFilterableRotatable[T <: TileFilterable] extends BlockFilterable[T] with BaseRotatableBlock {
  override def shouldShowFilterIconOnSide(w: IBlockAccess, x: Int, y: Int, z: Int, side: ForgeDirection): Boolean =
    IconType.fromSideAndDir(side.ordinal(), getFacing(w, x, y, z)) == IconType.SIDE
}
