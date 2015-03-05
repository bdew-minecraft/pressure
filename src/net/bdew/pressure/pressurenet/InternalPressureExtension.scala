/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.pressurenet

import net.bdew.lib.Misc
import net.bdew.pressure.api.{IFilterable, IFilterableProvider, IPressureConnectableBlock, IPressureExtension}
import net.bdew.pressure.blocks.BlockPipe
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.world.{IBlockAccess, World}
import net.minecraftforge.common.util.ForgeDirection

object InternalPressureExtension extends IPressureExtension with IFilterableProvider {
  override def canPipeConnectTo(w: IBlockAccess, x: Int, y: Int, z: Int, side: ForgeDirection) =
    Option(w.getBlock(x, y, z)) flatMap {
      Misc.asInstanceOpt(_, classOf[IPressureConnectableBlock])
    } exists {
      _.canConnectTo(w, x, y, z, side)
    }

  override def canPipeConnectFrom(w: IBlockAccess, x: Int, y: Int, z: Int, side: ForgeDirection) = isConnectableBlock(w, x, y, z)

  override def isConnectableBlock(w: IBlockAccess, x: Int, y: Int, z: Int) =
    Option(w.getBlock(x, y, z)) exists (_.isInstanceOf[IPressureConnectableBlock])

  override def isTraversableBlock(world: IBlockAccess, x: Int, y: Int, z: Int) =
    Option(world.getBlock(x, y, z)) exists { block =>
      block.isInstanceOf[IPressureConnectableBlock] && block.asInstanceOf[IPressureConnectableBlock].isTraversable(world, x, y, z)
    }

  override def tryPlacePipe(w: World, x: Int, y: Int, z: Int, p: EntityPlayerMP) = {
    if (w.isAirBlock(x, y, z) || (Option(w.getBlock(x, y, z)) exists (_.isReplaceable(w, x, y, z)))) {
      w.setBlock(x, y, z, BlockPipe, 0, 3)
      BlockPipe.notifyPressureSystemUpdate(w, x, y, z)
      true
    } else false
  }

  override def getFilterableForWorldCoordinates(world: World, x: Int, y: Int, z: Int, side: Int) = {
    (Option(world.getTileEntity(x, y, z)) flatMap Misc.asInstanceOpt(classOf[IFilterable])).orNull
  }
}
