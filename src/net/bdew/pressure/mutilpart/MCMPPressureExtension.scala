/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.mutilpart

import mcmultipart.multipart.{IMultipart, MultipartHelper}
import net.bdew.pressure.api.IPressureExtension
import net.bdew.pressure.blocks.pipe.BlockPipe
import net.bdew.pressure.mutilpart.traits.ConnectablePart
import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.{BlockPos, EnumFacing}
import net.minecraft.world.{IBlockAccess, World}

object MCMPPressureExtension extends IPressureExtension {
  override def isConnectableBlock(world: IBlockAccess, pos: BlockPos): Boolean =
    MCMPUtils.getTypedParts(classOf[ConnectablePart], world, pos).nonEmpty

  override def isTraversableBlock(world: IBlockAccess, pos: BlockPos): Boolean =
    MCMPUtils.getTypedParts(classOf[ConnectablePart], world, pos).exists(_.isTraversable)

  override def canPipeConnectTo(world: IBlockAccess, pos: BlockPos, side: EnumFacing): Boolean =
    MCMPUtils.getTypedParts(classOf[ConnectablePart], world, pos).exists(_.canConnectTo(side))

  override def canPipeConnectFrom(world: IBlockAccess, pos: BlockPos, side: EnumFacing): Boolean =
    MCMPUtils.getTypedParts(classOf[ConnectablePart], world, pos).exists(_.canConnectTo(side))

  val blockMap = Map[Block, () => IMultipart](
    BlockPipe -> (() => new PipePart)
  )

  override def tryPlaceBlock(w: World, pos: BlockPos, block: Block, p: EntityPlayer, reallyPlace: Boolean): Boolean =
    MultipartHelper.getPartContainer(w, pos) != null && blockMap.get(block).exists(constructor =>
      if (reallyPlace)
        MultipartHelper.addPartIfPossible(w, pos, constructor.apply())
      else
        MultipartHelper.canAddPart(w, pos, constructor.apply())
    )
}
