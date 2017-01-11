/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.tank.blocks

import java.util.Locale

import net.bdew.lib.computers._
import net.bdew.lib.multiblock.data.{OutputConfigFluid, OutputConfigRSControllable, RSMode}
import net.bdew.lib.multiblock.tile.TileModule
import net.bdew.pressure.blocks.tank.BaseModule
import net.bdew.pressure.blocks.tank.controller.TileTankController
import net.bdew.pressure.misc.FluidMapHelpers
import net.minecraftforge.fluids.FluidRegistry

object BlockDataPort extends BaseModule("tank_data_port", "TankDataPort", classOf[TileDataPort])

class TileDataPort extends TileModule {
  val kind: String = "TankDataPort"

  override def getCore = getCoreAs[TileTankController]
}

object DataPortCommands extends ModuleCommandHandler[TileTankController, TileDataPort] {
  val outputNames = Map(
    "red" -> 0,
    "green" -> 1,
    "blue" -> 2,
    "yellow" -> 3,
    "cyan" -> 4,
    "purple" -> 5
  )

  command("isConnected", direct = true) { ctx =>
    ctx.tile.getCore.isDefined
  }

  command("getCapacity", direct = true) { ctx =>
    getCore(ctx).tank.getCapacity
  }

  command("hasFluid", direct = true) { ctx =>
    val tank = getCore(ctx).tank
    tank.getFluid != null && tank.getFluid.amount > 0
  }

  command("getFluid", direct = true) { ctx =>
    val fluid = getCore(ctx).tank.getFluid
    if (fluid != null && fluid.getFluid != null && fluid.amount > 0)
      Result.Map(
        "name" -> fluid.getFluid.getName,
        "amount" -> fluid.amount
      )
    else
      Result.Null
  }

  command("getOutputs", direct = true) { ctx =>
    val configs = getCore(ctx).outputConfig
    for ((oName, oNum) <- outputNames) yield {
      oName -> (configs.get(oNum) match {
        case Some(x: OutputConfigFluid) =>
          Result.Map(
            "type" -> "fluid",
            "mode" -> x.rsMode.toString,
            "average" -> x.avg
          )
        case None => Result.Map("type" -> "unconnected")
        case _ => Result.Map("type" -> "unknown")
      })
    }
  }

  command("setOutputMode") { ctx =>
    val (oName, mode) = ctx.params(PString, PString)
    val oNum = outputNames.getOrElse(oName.toLowerCase(Locale.US), err("Invalid output name"))
    val newMode = try {
      RSMode.withName(mode.toUpperCase(Locale.US))
    } catch {
      case e: NoSuchElementException => err("Invalid output mode")
    }
    getCore(ctx).outputConfig.get(oNum) match {
      case Some(x: OutputConfigRSControllable) =>
        x.rsMode = newMode
        getCore(ctx).outputConfig.updated()
        true
      case _ => err("Unable to set output state")
    }
  }

  command("getFilter", direct = true) { ctx =>
    getCore(ctx).getFluidFilter map (x => Result(x.getName)) getOrElse Result.Null
  }

  command("setFilter") { ctx =>
    ctx.params(POption(PString)) match {
      case Some(s) =>
        if (FluidRegistry.isFluidRegistered(s)) {
          getCore(ctx).filterableCapability.setFluidFilter(FluidRegistry.getFluid(s))
          true
        } else {
          err("Unknown fluid")
        }
      case None =>
        getCore(ctx).filterableCapability.clearFluidFilter()
        true
    }
  }

  command("getCountsIn", direct = true) { ctx =>
    FluidMapHelpers.fluidPairsToResult(getCore(ctx).tank.fluidIn.values, "count")
  }

  command("getCountsOut", direct = true) { ctx =>
    FluidMapHelpers.fluidPairsToResult(getCore(ctx).tank.fluidOut.values, "count")
  }
}
