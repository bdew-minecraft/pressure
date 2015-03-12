/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.config

import net.bdew.lib.config.BlockManager
import net.bdew.pressure.blocks._
import net.bdew.pressure.blocks.drain.BlockDrain
import net.bdew.pressure.blocks.input.BlockInput
import net.bdew.pressure.blocks.output.BlockOutput
import net.bdew.pressure.blocks.pump.BlockPump
import net.bdew.pressure.blocks.source.BlockWaterSource
import net.bdew.pressure.blocks.tank.blocks._
import net.bdew.pressure.blocks.valves.check.BlockCheckValve
import net.bdew.pressure.blocks.valves.sensor.BlockPipeSensor
import net.bdew.pressure.misc.PressureCreativeTabs

object Blocks extends BlockManager(PressureCreativeTabs.main) {
  regBlock(BlockPipe, classOf[CustomItemBlock])
  regBlock(BlockCheckValve, classOf[CustomItemBlock])
  regBlock(BlockPipeSensor, classOf[CustomItemBlock])

  regBlock(BlockPump, "Pump")
  regBlock(BlockOutput, "Output")
  regBlock(BlockInput, "Input")
  regBlock(BlockWaterSource, "Water")
  regBlock(BlockDrain, "Drain")

  regBlock(BlockTankBlock)
  regBlock(BlockTankIndicator)

  regBlock(BlockFluidOutput)
  regBlock(BlockFluidAutoOutput)
  regBlock(BlockFluidInput)

  regBlock(BlockPressureOutput)
  regBlock(BlockPressureInput)

  regBlock(BlockTankInterface)
  regBlock(BlockTankFilter)
}