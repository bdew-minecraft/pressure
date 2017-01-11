/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure

import java.util.Locale

import net.bdew.pressure.blocks.drain.{BlockDrain, BlockSluice}
import net.bdew.pressure.blocks.input.BlockInput
import net.bdew.pressure.blocks.output.BlockOutput
import net.bdew.pressure.blocks.pump.BlockPump
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
    // Blocks with changed name
    "CheckValve" -> BlockCheckValve,
    "PipeSensor" -> BlockPipeSensor,
    "WaterSource" -> BlockWaterSource,
    "CreativeSource" -> BlockCreativeSource,
    "TankBlock" -> BlockTankBlock,
    "TankDataPort" -> BlockDataPort,
    "TankIndicator" -> BlockTankIndicator,
    "TankFluidOutput" -> BlockFluidOutput,
    "TankFluidAutoOutput" -> BlockFluidAutoOutput,
    "TankFluidInput" -> BlockFluidInput,
    "TankFluidAccess" -> BlockFluidAccess,
    "TankPressureOutput" -> BlockPressureOutput,
    "TankPressureInput" -> BlockPressureInput,
    "TankInterface" -> BlockTankInterface,
    "TankFilter" -> BlockTankFilter,
    "TankController" -> BlockTankController,
    "Sensor" -> BlockSensor,
    "Router" -> BlockRouter,

    // Items with changed name
    "Debugger" -> ItemDebugger,
    "Interface" -> Items.interface,
    "TankWall" -> Items.tankWall,
    "FluidInterface" -> Items.fluidInterface,
    "HandPump" -> HandPump,
    "Canister" -> Canister,
    "Configurator" -> ItemConfigurator,

    // Blocks with unchanged name, but need to have TE remapped
    "drain" -> BlockDrain,
    "sluice" -> BlockSluice,
    "input" -> BlockInput,
    "output" -> BlockOutput,
    "pump" -> BlockPump
  )

  val lowerMap: Map[String, IForgeRegistryEntry[_]] = map.map(x => "pressure:" + x._1.toLowerCase(Locale.US) -> x._2).toMap

  def checkRemap(mapping: MissingMapping): Unit = {
    lowerMap.get(mapping.name) match {
      case Some(x: Block) if mapping.`type` == Type.BLOCK => mapping.remap(x)
      case Some(x: Block) if mapping.`type` == Type.ITEM => mapping.remap(Item.getItemFromBlock(x))
      case Some(x: Item) if mapping.`type` == Type.ITEM => mapping.remap(x)
      case _ => //nothing
    }
  }
}
