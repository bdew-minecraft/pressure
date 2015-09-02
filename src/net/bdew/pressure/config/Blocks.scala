/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.config

import net.bdew.lib.Misc
import net.bdew.lib.config.BlockManager
import net.bdew.pressure.blocks._
import net.bdew.pressure.blocks.drain.{BlockDrain, BlockSluice}
import net.bdew.pressure.blocks.gas.{BlockPressureGasInput, BlockPressureGasOutput, BlockTankGasInput, BlockTankGasOutput}
import net.bdew.pressure.blocks.input.BlockInput
import net.bdew.pressure.blocks.output.BlockOutput
import net.bdew.pressure.blocks.pump.BlockPump
import net.bdew.pressure.blocks.source.{BlockCreativeSource, BlockWaterSource}
import net.bdew.pressure.blocks.tank.blocks._
import net.bdew.pressure.blocks.tank.sensor.BlockSensor
import net.bdew.pressure.blocks.valves.check.BlockCheckValve
import net.bdew.pressure.blocks.valves.sensor.BlockPipeSensor
import net.bdew.pressure.misc.PressureCreativeTabs

object Blocks extends BlockManager(PressureCreativeTabs.main) {
  regBlock(BlockPipe)
  regBlock(BlockCheckValve)
  regBlock(BlockPipeSensor)

  regBlock(BlockPump, "Pump")
  regBlock(BlockOutput, "Output")
  regBlock(BlockInput, "Input")
  regBlock(BlockWaterSource, "Water")
  regBlock(BlockCreativeSource, "Creative")
  regBlock(BlockDrain, "Drain")
  regBlock(BlockSluice, "Sluice")

  regBlock(BlockTankBlock)
  regBlock(BlockTankIndicator)

  regBlock(BlockFluidOutput)
  regBlock(BlockFluidAutoOutput)
  regBlock(BlockFluidInput)

  regBlock(BlockPressureOutput)
  regBlock(BlockPressureInput)

  regBlock(BlockTankInterface)
  regBlock(BlockTankFilter)

  regBlock(BlockSensor)

  if (Misc.haveModVersion("ComputerCraft")) {
    regBlock(BlockDataPort)
  }

  if (Misc.haveModVersion("MekanismAPI|gas@[8.0.0,)")) {
    regBlock(BlockPressureGasInput, "GasInput")
    regBlock(BlockPressureGasOutput, "GasOutput")
    regBlock(BlockTankGasOutput)
    regBlock(BlockTankGasInput)
  }
}