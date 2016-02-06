/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.compat.opencomputers

import li.cil.oc.api.driver.Block
import li.cil.oc.api.network.ManagedEnvironment
import net.bdew.lib.computers.TileCommandHandler
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockPos
import net.minecraft.world.World

class BlockDriver[T <: TileEntity](kind: String, commands: TileCommandHandler[T], teClass: Class[T]) extends Block {
  override def worksWith(world: World, pos: BlockPos): Boolean = {
    val te = world.getTileEntity(pos)
    te != null && teClass.isInstance(te)
  }

  override def createEnvironment(world: World, pos: BlockPos): ManagedEnvironment = {
    val te = world.getTileEntity(pos)
    if (te != null && teClass.isInstance(te))
      new ManagedEnvironmentProvider(kind, commands, te.asInstanceOf[T])
    else
      null
  }
}