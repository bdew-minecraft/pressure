/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.valves.sensor

import net.bdew.lib.DecFormat
import net.bdew.lib.block.{HasItemBlock, HasTE}
import net.bdew.pressure.blocks.CustomItemBlock
import net.bdew.pressure.blocks.valves.BlockValve
import net.bdew.pressure.misc.{DataSlotFluidAverages, FluidNameHelper}
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util._
import net.minecraft.world.{IBlockAccess, World}

object BlockPipeSensor extends BlockValve("PipeSensor") with HasTE[TilePipeSensor] with HasItemBlock {
  override val TEClass = classOf[TilePipeSensor]
  override val ItemBlockClass = classOf[CustomItemBlock]

  setHardness(2)

  override def canProvidePower = true

  override def getWeakPower(world: IBlockAccess, pos: BlockPos, state: IBlockState, side: EnumFacing) = {
    val facing = getFacing(world, pos)
    if (facing != side && facing.getOpposite != side && isPowered(world, pos))
      15
    else
      0
  }

  def sendAveragesToPlayer(ds: DataSlotFluidAverages, player: EntityPlayer): Unit = {
    import net.bdew.lib.helpers.ChatHelper._
    val flow = ds.getAverages.toList.filter(_._2 > 0.000001).sortBy(-_._2)
    if (flow.nonEmpty) {
      player.addChatComponentMessage(L("pressure.message.flow.head", ds.values.size.toString))
      for ((fluid, amount) <- flow) {
        player.addChatComponentMessage(
          L(" * %s - %s mB/t",
            L(FluidNameHelper.sanitizeUnlocalizedName(fluid)).setColor(Color.YELLOW),
            DecFormat.short(amount).setColor(Color.YELLOW)
          )
        )
      }
    }
    else {
      player.addChatComponentMessage(L("pressure.message.flow.empty").setColor(Color.RED))
    }
  }

  override def onBlockActivated(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean = {
    if (player.isSneaking) return false
    if (world.isRemote) return true
    sendAveragesToPlayer(getTE(world, pos).averages, player)
    true
  }
}
