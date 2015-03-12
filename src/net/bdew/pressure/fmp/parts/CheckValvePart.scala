/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.fmp.parts

import java.util

import codechicken.lib.data.{MCDataInput, MCDataOutput}
import codechicken.lib.vec.Vector3
import codechicken.multipart.{TCuboidPart, TMultiPart, TNormalOcclusion}
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.lib.Misc
import net.bdew.pressure.api.{IPressureConnection, IPressureInject}
import net.bdew.pressure.blocks.checkvalve.BlockCheckValve
import net.bdew.pressure.fmp.FmpUtils
import net.bdew.pressure.fmp.traits._
import net.bdew.pressure.pressurenet.Helper
import net.minecraft.client.renderer.RenderBlocks
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.MovingObjectPosition
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.fluids.FluidStack

class CheckValvePart(var facing: ForgeDirection = BlockCheckValve.getDefaultFacing, var isPowered: Boolean = false)
  extends TCuboidPart with TNormalOcclusion with TBlockAccessPart with TConnectablePart with TInjectPart with TEjectPart {

  def this(meta: Int) = this(Misc.forgeDirection(meta & 7), (meta & 8) == 8)

  override def getType = "bdew.pressure.checkvalve"

  override def getBounds = FmpUtils.cub6(BlockCheckValve.boundsFromFacing(facing))

  override def canConnectTo(side: ForgeDirection) =
    (side == facing || side == facing.getOpposite) && !tile.isSolid(side.ordinal())

  override def isTraversable = false

  var outputConnection: IPressureConnection = null

  override def invalidateConnection(side: ForgeDirection) = outputConnection = null
  override def eject(resource: FluidStack, face: ForgeDirection, doEject: Boolean) = {
    if (!isPowered && face == facing.getOpposite && !tile.isSolid(facing.ordinal())) {
      if (outputConnection == null)
        outputConnection = Helper.recalculateConnectionInfo(tile.asInstanceOf[IPressureInject], facing)
      outputConnection.pushFluid(resource, doEject)
    } else 0
  }

  override def save(tag: NBTTagCompound) = {
    tag.setInteger("facing", facing.ordinal())
    tag.setBoolean("state", isPowered)
  }

  override def load(tag: NBTTagCompound) = {
    facing = Misc.forgeDirection(tag.getInteger("facing"))
    isPowered = tag.getBoolean("state")
  }

  override def writeDesc(packet: MCDataOutput) = {
    packet.writeInt(facing.ordinal())
    packet.writeBoolean(isPowered)
  }

  override def readDesc(packet: MCDataInput) = {
    facing = Misc.forgeDirection(packet.readInt())
    isPowered = packet.readBoolean()
  }

  override def getOcclusionBoxes = util.Arrays.asList(getBounds)

  override def onWorldJoin() = if (!world.isRemote) Helper.notifyBlockChanged(world, x, y, z)
  override def onWorldSeparate() = if (!world.isRemote) Helper.notifyBlockChanged(world, x, y, z)
  override def onPartChanged(part: TMultiPart) = Helper.notifyBlockChanged(world, x, y, z)

  override def getDrops = util.Arrays.asList(new ItemStack(BlockCheckValve))
  override def pickItem(hit: MovingObjectPosition) = new ItemStack(BlockCheckValve)

  override def getBlock = BlockCheckValve
  override def getBlockMetadata = facing.ordinal() | (if (isPowered) 8 else 0)

  @SideOnly(Side.CLIENT)
  override def renderStatic(pos: Vector3, pass: Int) = {
    if (pass == 0) {
      new RenderBlocks(new PartBlockAccess(this)).renderBlockByRenderType(getBlock, x, y, z)
      true
    } else false
  }

  override def onNeighborChanged(): Unit = {
    val powered = world.isBlockIndirectlyGettingPowered(x, y, z)
    if (powered != isPowered) {
      isPowered = powered
      tile.notifyPartChange(this)
      tile.markDirty()
      sendDescUpdate()
    }
  }
}
