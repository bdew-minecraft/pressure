/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/pressure/master/MMPL-1.0.txt
 */

package net.bdew.pressure.misc

import net.minecraft.block.Block
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import net.minecraftforge.common.DimensionManager
import net.minecraftforge.common.util.ForgeDirection

import scala.reflect.ClassTag

case class BlockRef(dim: Int, x: Int, y: Int, z: Int) {
  def world = Option(DimensionManager.getWorld(dim))
  def block = world map (_.getBlock(x, y, z))
  def tile = world flatMap (w => Option(w.getTileEntity(x, y, z)))
  def meta = world map (_.getBlockMetadata(x, y, z))

  def blockIs(other: Block) = (block filter (_ == other)).isDefined

  def neighbour(side: ForgeDirection) = BlockRef(dim, x + side.offsetX, y + side.offsetY, z + side.offsetZ)
  def neighbours = ForgeDirection.VALID_DIRECTIONS.map(x => x -> neighbour(x))

  override def toString = "(x=%d y=%d z=%d dim=%d)".format(x, y, z, dim)

  def getTile[T: ClassTag] = tile flatMap { tile =>
    val cls = implicitly[ClassTag[T]].runtimeClass
    if (cls.isInstance(tile))
      Some(tile.asInstanceOf[T])
    else None
  }

  def getBlock[T: ClassTag] = block flatMap { block =>
    val cls = implicitly[ClassTag[T]].runtimeClass
    if (cls.isInstance(block))
      Some(block.asInstanceOf[T])
    else None
  }

  def writeToNBT(tag: NBTTagCompound) {
    tag.setInteger("dim", dim)
    tag.setInteger("x", x)
    tag.setInteger("y", y)
    tag.setInteger("z", z)
  }
}

object BlockRef {
  def apply(w: World, x: Int, y: Int, z: Int): BlockRef = BlockRef(w.provider.dimensionId, x, y, z)
  def fromTile(te: TileEntity) = BlockRef(te.getWorldObj.provider.dimensionId, te.xCoord, te.yCoord, te.zCoord)
  def fromNBT(tag: NBTTagCompound) =
    BlockRef(tag.getInteger("dim"), tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z"))
}