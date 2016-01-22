/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.fmp.parts

import java.util

import codechicken.lib.vec.Vector3
import codechicken.multipart.{TCuboidPart, TMultiPart, TNormalOcclusion}
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.lib.data.{DataSlotBoolean, DataSlotDirection}
import net.bdew.pressure.api.IPressureConnection
import net.bdew.pressure.blocks.valves.BlockValve
import net.bdew.pressure.fmp.FmpUtils
import net.bdew.pressure.fmp.traits._
import net.bdew.pressure.pressurenet.Helper
import net.minecraft.client.renderer.RenderBlocks
import net.minecraft.item.ItemStack
import net.minecraft.util.MovingObjectPosition
import net.minecraftforge.common.util.ForgeDirection

abstract class BaseValvePart(val block: BlockValve, id: String) extends TCuboidPart with TDataSlots with TNormalOcclusion with TBlockAccessPart with TConnectablePart with TInjectPart with TEjectPart {
  val facing: DataSlotDirection
  val isPowered: DataSlotBoolean

  override def getType = id

  // ==== PRESSURE NET ====

  override def canConnectTo(side: ForgeDirection) =
    (side == facing.value || side == facing.getOpposite) && !tile.isSolid(side.ordinal())

  override def isTraversable = false

  var outputConnection: IPressureConnection = null

  override def invalidateConnection(side: ForgeDirection) = outputConnection = null

  // ==== BOUNDS ====

  override def getOcclusionBoxes = util.Arrays.asList(getBounds)
  override def getBounds = FmpUtils.cub6(block.boundsFromFacing(facing))

  // ==== EVENTS ====

  override def onWorldJoin() = if (!world.isRemote) Helper.notifyBlockChanged(world, x, y, z)
  override def onWorldSeparate() = if (!world.isRemote) Helper.notifyBlockChanged(world, x, y, z)
  override def onPartChanged(part: TMultiPart) = Helper.notifyBlockChanged(world, x, y, z)

  // ==== MISC ====

  override def getDrops = util.Arrays.asList(new ItemStack(block))
  override def pickItem(hit: MovingObjectPosition) = new ItemStack(block)

  // ==== RENDERING ====

  override def getBlock = block
  override def getBlockMetadata = facing.ordinal() | (if (isPowered) 8 else 0)

  @SideOnly(Side.CLIENT)
  override def renderStatic(pos: Vector3, pass: Int) = {
    if (pass == 0) {
      new RenderBlocks(new PartBlockAccess(this)).renderBlockByRenderType(block, x, y, z)
      true
    } else false
  }
}
