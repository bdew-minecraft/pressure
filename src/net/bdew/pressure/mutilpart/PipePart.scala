/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.mutilpart

import java.util

import com.google.common.collect.ImmutableList
import mcmultipart.multipart.{IMultipart, ISlottedPart, Multipart, PartSlot}
import mcmultipart.raytrace.PartMOP
import net.bdew.pressure.blocks.pipe.BlockPipe
import net.bdew.pressure.mutilpart.traits.ConnectablePart
import net.bdew.pressure.pressurenet.Helper
import net.minecraft.block.state.{BlockStateContainer, IBlockState}
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.{EnumFacing, ResourceLocation}

class PipePart extends Multipart with ISlottedPart with ConnectablePart {
  override def createBlockState(): BlockStateContainer = {
    new BlockStateContainer(BlockPipe, BlockPipe.getProperties: _*)
  }

  override def getActualState(state: IBlockState): IBlockState =
    BlockPipe.getActualState(BlockPipe.getDefaultState, getWorld, getPos)

  override def getModelPath = new ResourceLocation("pressure", "pipe")

  override def getRenderBoundingBox = {
    if (getWorld == null || getPos == null) {
      new AxisAlignedBB(0.2F, 0.2F, 0.2F, 0.8F, 0.8F, 0.8F)
    } else {
      val connections = Helper.getPipeConnections(getWorld, getPos)
      val minX = if (connections.contains(EnumFacing.WEST)) 0 else 0.2F
      val maxX = if (connections.contains(EnumFacing.EAST)) 1 else 0.8F

      val minY = if (connections.contains(EnumFacing.DOWN)) 0 else 0.2F
      val maxY = if (connections.contains(EnumFacing.UP)) 1 else 0.8F

      val minZ = if (connections.contains(EnumFacing.NORTH)) 0 else 0.2F
      val maxZ = if (connections.contains(EnumFacing.SOUTH)) 1 else 0.8F

      new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ)
    }
  }

  override def addCollisionBoxes(mask: AxisAlignedBB, list: util.List[AxisAlignedBB], collidingEntity: Entity): Unit = {
    val bb = getRenderBoundingBox
    if (bb.intersectsWith(mask))
      list.add(bb)
  }

  override def addSelectionBoxes(list: util.List[AxisAlignedBB]): Unit =
    list.add(getRenderBoundingBox)

  override def getDrops: util.List[ItemStack] = ImmutableList.of(new ItemStack(BlockPipe))

  override def getPickBlock(player: EntityPlayer, hit: PartMOP): ItemStack = new ItemStack(BlockPipe)

  override def getHardness(hit: PartMOP): Float = 0.5f

  override def onPartChanged(part: IMultipart): Unit = Helper.notifyBlockChanged(getWorld, getPos)

  override def onConverted(tile: TileEntity): Unit = Helper.notifyBlockChanged(getWorld, getPos)

  override def getSlotMask: util.EnumSet[PartSlot] = util.EnumSet.of(PartSlot.CENTER)

  override def isTraversable: Boolean = true

  override def canConnectTo(side: EnumFacing): Boolean = !getWorld.isSideSolid(getPos, side)
}
