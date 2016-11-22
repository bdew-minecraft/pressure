/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure

import net.bdew.pressure.blocks.router.BlockRouter
import net.bdew.pressure.blocks.source.{BlockCreativeSource, BlockWaterSource}
import net.bdew.pressure.blocks.tank.blocks.{BlockTankFilter, _}
import net.bdew.pressure.blocks.tank.controller.BlockTankController
import net.bdew.pressure.blocks.tank.sensor.BlockSensor
import net.bdew.pressure.blocks.valves.check.BlockCheckValve
import net.bdew.pressure.blocks.valves.sensor.BlockPipeSensor
import net.bdew.pressure.config.Items
import net.bdew.pressure.items.configurator.ItemConfigurator
import net.bdew.pressure.items.{Canister, HandPump, ItemDebugger}
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraftforge.fml.common.event.FMLMissingMappingsEvent.MissingMapping
import net.minecraftforge.fml.common.registry.GameRegistry.Type
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry

object OldNames {
  val map: Map[String, IForgeRegistryEntry[_]] = Map(
    "CheckValve" -> BlockCheckValve,
    "PipeSensor" -> BlockPipeSensor,
    "WaterSource" -> BlockWaterSource,
    "CreativeSource" -> BlockCreativeSource,
    "TankBlock" -> BlockTankBlock,
    "TankIndicator" -> BlockTankIndicator,
    "TankFluidOutput" -> BlockFluidOutput,
    "TankFluidAutoOutput" -> BlockFluidAutoOutput,
    "TankFluidInput" -> BlockFluidInput,
    "TankFluidAccess" -> BlockFluidAccess,
    "TankPressureOutput" -> BlockPressureOutput,
    "TankPressureInput" -> BlockPressureInput,
    "TankInterface" -> BlockTankInterface,
    "TankFilter" -> BlockTankFilter,
    "Sensor" -> BlockSensor,

    "Router" -> BlockRouter,
    "TankController" -> BlockTankController,

    "Debugger" -> ItemDebugger,
    "Interface" -> Items.interface,
    "TankWall" -> Items.tankWall,
    "FluidInterface" -> Items.fluidInterface,
    "HandPump" -> HandPump,
    "Canister" -> Canister,
    "Configurator" -> ItemConfigurator
  )

  def checkRemap(mapping: MissingMapping): Unit = {
    map.get(mapping.name) match {
      case Some(x: Block) if mapping.`type` == Type.BLOCK => mapping.remap(x)
      case Some(x: Block) if mapping.`type` == Type.ITEM => mapping.remap(Item.getItemFromBlock(x))
      case Some(x: Item) if mapping.`type` == Type.ITEM => mapping.remap(x)
      case _ => //nothing
    }
  }
}
