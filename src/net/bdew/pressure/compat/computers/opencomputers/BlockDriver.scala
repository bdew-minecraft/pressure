/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.compat.computers.opencomputers

import li.cil.oc.api.driver.Block
import li.cil.oc.api.network.ManagedEnvironment
import net.bdew.pressure.compat.computers._
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

class BlockDriver[T <: TileEntity](kind: String, commands: TileCommandHandler[T], teClass: Class[T]) extends Block {
  override def worksWith(world: World, x: Int, y: Int, z: Int): Boolean = {
    val te = world.getTileEntity(x, y, z)
    te != null && teClass.isInstance(te)
  }

  override def createEnvironment(world: World, x: Int, y: Int, z: Int): ManagedEnvironment = {
    val te = world.getTileEntity(x, y, z)
    if (te != null && teClass.isInstance(te))
      new ManagedEnvironmentProvider(kind, commands, te.asInstanceOf[T])
    else
      null
  }
}