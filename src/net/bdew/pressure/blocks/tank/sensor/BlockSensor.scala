/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.tank.sensor

import net.bdew.lib.sensors.multiblock.{BlockRedstoneSensorModule, TileRedstoneSensorModule}
import net.bdew.pressure.Pressure
import net.bdew.pressure.blocks.tank.BaseModule
import net.bdew.pressure.config.Config
import net.bdew.pressure.sensor.Sensors
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

class TileSensor extends TileRedstoneSensorModule(Sensors, BlockSensor)

object BlockSensor extends BaseModule("tank_sensor", "Sensor", classOf[TileSensor]) with BlockRedstoneSensorModule[TileSensor] {
  override def guiId = 4
  override type TEClass = TileSensor

  Config.guiHandler.register(this)

  override def doOpenGui(world: World, pos: BlockPos, player: EntityPlayer): Unit =
    player.openGui(Pressure, guiId, world, pos.getX, pos.getY, pos.getZ)

  @SideOnly(Side.CLIENT)
  override def getGui(te: TEClass, player: EntityPlayer) = new GuiSensor(te, player)
  override def getContainer(te: TEClass, player: EntityPlayer) = new ContainerSensor(te, player)
}
