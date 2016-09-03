/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.network

import net.bdew.lib.Misc
import net.bdew.lib.network.NetChannel
import net.bdew.pressure.Pressure
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.math.BlockPos
import net.minecraft.world.{World, WorldServer}

object NetworkHandler extends NetChannel(Pressure.channel) {
  //todo: Move to bdlib if this works out
  def sendToWatchingPlayers(message: Message, world: World, pos: BlockPos) {
    import scala.collection.JavaConversions._
    val playerChunkMap = world.asInstanceOf[WorldServer].getPlayerChunkMap
    val chunkX = pos.getX >> 4
    val chunkZ = pos.getZ >> 4
    Misc.filterType(world.playerEntities, classOf[EntityPlayerMP])
      .filter(player => playerChunkMap.isPlayerWatchingChunk(player, chunkX, chunkZ))
      .foreach(player => sendTo(message, player))
  }
}