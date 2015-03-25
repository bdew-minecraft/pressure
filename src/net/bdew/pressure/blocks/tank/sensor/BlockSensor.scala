/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.tank.sensor

import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.lib.sensors.multiblock.{BlockRedstoneSensorModule, TileRedstoneSensorModule}
import net.bdew.pressure.Pressure
import net.bdew.pressure.blocks.tank.BaseModule
import net.bdew.pressure.config.Config
import net.bdew.pressure.sensor.Sensors
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World

class TileSensor extends TileRedstoneSensorModule(Sensors, BlockSensor)

object BlockSensor extends BaseModule("Sensor", "Sensor", classOf[TileSensor]) with BlockRedstoneSensorModule[TileSensor] {
  override def guiId = 4
  override type TEClass = TileSensor

  Config.guiHandler.register(this)

  override def doOpenGui(world: World, x: Int, y: Int, z: Int, player: EntityPlayer): Unit =
    player.openGui(Pressure, guiId, world, x, y, z)

  @SideOnly(Side.CLIENT)
  override def getGui(te: TEClass, player: EntityPlayer) = new GuiSensor(te, player)
  override def getContainer(te: TEClass, player: EntityPlayer) = new ContainerSensor(te, player)

  @SideOnly(Side.CLIENT) override
  def registerBlockIcons(reg: IIconRegister): Unit = {
    sideIcon = reg.registerIcon(Pressure.modId + ":tank/sensor/side_off")
    frontIcon = reg.registerIcon(Pressure.modId + ":tank/sensor/front_off")
    bottomIcon = reg.registerIcon(Pressure.modId + ":tank/sensor/back")
    frontIconOn = reg.registerIcon(Pressure.modId + ":tank/sensor/front_on")
    sideIconOn = reg.registerIcon(Pressure.modId + ":tank/sensor/side_on")
  }

}
