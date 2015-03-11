/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.fmp

import java.util

import codechicken.lib.vec.BlockCoord
import codechicken.multipart.MultiPartRegistry.{IPartConverter, IPartFactory}
import codechicken.multipart.{MultiPartRegistry, MultipartGenerator, TileMultipart}
import net.bdew.lib.Misc
import net.bdew.lib.block.BlockRef
import net.bdew.pressure.api.IPressureExtension
import net.bdew.pressure.blocks.BlockPipe
import net.bdew.pressure.blocks.checkvalve.BlockCheckValve
import net.bdew.pressure.fmp.parts.{CheckValvePart, PipePart}
import net.bdew.pressure.fmp.traits.TConnectablePart
import net.bdew.pressure.pressurenet.Helper
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.world.{IBlockAccess, World}
import net.minecraftforge.common.util.ForgeDirection

object FmpConverter extends IPartConverter {
  override def blockTypes = util.Arrays.asList(BlockPipe, BlockCheckValve)
  override def convert(world: World, pos: BlockCoord) = {
    BlockRef(pos.x, pos.y, pos.z).block(world) match {
      case Some(BlockPipe) => new PipePart
      case Some(BlockCheckValve) => new CheckValvePart(world.getBlockMetadata(pos.x, pos.y, pos.z))
      case _ => null
    }
  }
}

object FmpFactory extends IPartFactory {
  override def createPart(name: String, client: Boolean) = (name, client) match {
    case ("bdew.pressure.pipe", _) => new PipePart
    case ("bdew.pressure.checkvalve", _) => new CheckValvePart(BlockCheckValve.getDefaultFacing)
    case _ => null
  }
}

object FmpPressureExtension extends IPressureExtension {
  def findTypedParts[T](world: IBlockAccess, x: Int, y: Int, z: Int, cls: Class[T]): Seq[T] = {
    val te = world.getTileEntity(x, y, z)
    if (te != null && te.isInstanceOf[TileMultipart]) {
      te.asInstanceOf[TileMultipart].partList.flatMap(Misc.asInstanceOpt(_, cls))
    } else Seq.empty
  }

  override def isConnectableBlock(world: IBlockAccess, x: Int, y: Int, z: Int) =
    findTypedParts(world, x, y, z, classOf[TConnectablePart]).nonEmpty

  override def isTraversableBlock(world: IBlockAccess, x: Int, y: Int, z: Int) =
    findTypedParts(world, x, y, z, classOf[TConnectablePart]).exists(_.isTraversable)

  override def canPipeConnectFrom(world: IBlockAccess, x: Int, y: Int, z: Int, side: ForgeDirection) =
    findTypedParts(world, x, y, z, classOf[TConnectablePart]).exists(_.canConnectTo(side))

  override def canPipeConnectTo(world: IBlockAccess, x: Int, y: Int, z: Int, side: ForgeDirection) =
    findTypedParts(world, x, y, z, classOf[TConnectablePart]).exists(_.canConnectTo(side))

  override def tryPlacePipe(w: World, x: Int, y: Int, z: Int, p: EntityPlayerMP) = {
    val part = new PipePart
    val pos = new BlockCoord(x, y, z)
    if (TileMultipart.getTile(w, pos) != null && TileMultipart.canPlacePart(w, pos, part)) {
      TileMultipart.addPart(w, pos, part)
      true
    } else false
  }
}

object FmpHandler {
  def getTypedPart[T](cls: Class[T], te: TileMultipart): Option[T] =
    te.partList.flatMap(Misc.asInstanceOpt(_, cls)).headOption

  def init() {
    MultiPartRegistry.registerParts(FmpFactory, Array(
      "bdew.pressure.pipe",
      "bdew.pressure.checkvalve"
    ))
    MultiPartRegistry.registerConverter(FmpConverter)
    Helper.registerExtension(FmpPressureExtension)

    // here be dragons (and ASM)
    MultipartGenerator.registerTrait("net.bdew.pressure.fmp.traits.TInjectPart", "net.bdew.pressure.fmp.traits.TileInject")
    MultipartGenerator.registerTrait("net.bdew.pressure.fmp.traits.TEjectPart", "net.bdew.pressure.fmp.traits.TileEject")
  }
}
