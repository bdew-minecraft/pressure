/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.model

import net.bdew.lib.Client
import net.bdew.lib.model.{BakedModelAdditionalFaceQuads, BakedQuadHelper, ModelEnhancer}
import net.bdew.lib.property.SimpleUnlistedProperty
import net.bdew.lib.rotate.Properties
import net.minecraft.block.state.IBlockState
import net.minecraft.client.resources.model.{IBakedModel, ModelRotation}
import net.minecraft.util.EnumFacing._
import net.minecraftforge.fluids.Fluid

object FilteredModelEnhancer extends ModelEnhancer {
  lazy val helper = new BakedQuadHelper(ModelRotation.X0_Y0)

  val FILTER = new SimpleUnlistedProperty("filter", classOf[Fluid]) {
    override def valueToString(value: Fluid): String = value.getName
  }

  override def handleState(base: IBakedModel, state: IBlockState) =
    FILTER.get(state) map { fluid =>
      val icon = Client.textureMapBlocks.getAtlasSprite(fluid.getStill.toString)
      val rot = state.getValue(Properties.FACING)
      val ts = 5.5f
      val te = 16f - ts
      val sides = Map(
        DOWN -> List(helper.quad((ts, -0.1f, ts), (te, -0.1f, te), DOWN, icon, (ts, ts), (te, te), shaded = false)),
        UP -> List(helper.quad((ts, 16.1f, ts), (te, 16.1f, te), UP, icon, (ts, ts), (te, te), shaded = false)),
        NORTH -> List(helper.quad((ts, ts, -0.1f), (te, te, -0.1f), NORTH, icon, (ts, ts), (te, te), shaded = false)),
        SOUTH -> List(helper.quad((ts, ts, 16.1f), (te, te, 16.1f), SOUTH, icon, (ts, ts), (te, te), shaded = false)),
        WEST -> List(helper.quad((-0.1f, ts, ts), (-0.1f, te, te), WEST, icon, (ts, ts), (te, te), shaded = false)),
        EAST -> List(helper.quad((16.1f, ts, ts), (16.1f, te, te), EAST, icon, (ts, ts), (te, te), shaded = false))
      ) filter (x => x._1 != rot && x._1 != rot.getOpposite)
      new BakedModelAdditionalFaceQuads(base, sides)
    } getOrElse base
}