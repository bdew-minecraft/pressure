/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/pressure/master/MMPL-1.0.txt
 */

package net.bdew.pressure.fmp

import java.util

import codechicken.lib.vec.BlockCoord
import codechicken.multipart.MultiPartRegistry.{IPartConverter, IPartFactory}
import codechicken.multipart.{MultiPartRegistry, TileMultipart}
import net.bdew.lib.Misc
import net.bdew.pressure.api.IPressureExtension
import net.bdew.pressure.blocks.BlockPipe
import net.bdew.pressure.misc.{BlockRef, Helper}
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.world.{IBlockAccess, World}
import net.minecraftforge.common.util.ForgeDirection

object FmpConverter extends IPartConverter {
  override def blockTypes = util.Arrays.asList(BlockPipe)
  override def convert(world: World, pos: BlockCoord) = {
    BlockRef(pos.x, pos.y, pos.z).block(world) match {
      case Some(BlockPipe) => new PipePart
      case _ => null
    }
  }
}

object FmpFactory extends IPartFactory {
  override def createPart(name: String, client: Boolean) = (name, client) match {
    case ("bdew.pressure.pipe", _) => new PipePart
    case _ => null
  }
}

object FmpPressureExtension extends IPressureExtension {
  override def canPipeConnectFrom(world: IBlockAccess, x: Int, y: Int, z: Int, side: ForgeDirection) =
    Option(world.getTileEntity(x, y, z)) flatMap
      (Misc.asInstanceOpt(_, classOf[TileMultipart])) exists { tmp =>
      tmp.partList.exists(_.isInstanceOf[PipePart]) && !tmp.isSolid(side.ordinal())
    }

  override def canPipeConnectTo(world: IBlockAccess, x: Int, y: Int, z: Int, side: ForgeDirection) =
    Option(world.getTileEntity(x, y, z)) flatMap
      (Misc.asInstanceOpt(_, classOf[TileMultipart])) exists { tmp =>
      tmp.partList.exists(_.isInstanceOf[PipePart]) && !tmp.isSolid(side.ordinal())
    }

  override def isConnectableBlock(world: IBlockAccess, x: Int, y: Int, z: Int) =
    Option(world.getTileEntity(x, y, z)) flatMap
      (Misc.asInstanceOpt(_, classOf[TileMultipart])) exists
      (_.partList.exists(_.isInstanceOf[PipePart]))

  override def tryPlacePipe(w: World, x: Int, y: Int, z: Int, p: EntityPlayerMP) = {
    val part = new PipePart
    val pos = new BlockCoord(x, y, z)
    if (TileMultipart.getTile(w, pos) != null && TileMultipart.canPlacePart(w, pos, part)) {
      TileMultipart.addPart(w, pos, part)
      true
    } else false
  }
}

class FmpHandler {
  MultiPartRegistry.registerParts(FmpFactory, Array(
    "bdew.pressure.pipe"
  ))
  MultiPartRegistry.registerConverter(FmpConverter)
  Helper.registerExtension(FmpPressureExtension)
}
