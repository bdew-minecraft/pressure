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
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.resources.model.{IBakedModel, ModelRotation}
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumFacing._
import net.minecraftforge.fluids.Fluid

object FluidFilterProperty extends SimpleUnlistedProperty("filter", classOf[Fluid]) {
  override def valueToString(value: Fluid): String = value.getName
}

class BaseFluidFilterModelEnhancer(size: Float) extends ModelEnhancer {
  lazy val helper = new BakedQuadHelper(ModelRotation.X0_Y0)

  def filterSides(state: IBlockState, quads: Map[EnumFacing, List[BakedQuad]]): Map[EnumFacing, List[BakedQuad]] = quads

  override def handleState(base: IBakedModel, state: IBlockState) =
    FluidFilterProperty.get(state) map { fluid =>
      val icon = Client.textureMapBlocks.getAtlasSprite(fluid.getStill.toString)
      val ts = 8f - size / 2
      val te = 8f + size / 2
      val o1 = -0.1f
      val o2 = 16.1f
      val sides = Map(
        DOWN -> List(helper.quad((ts, o1, ts), (te, o1, te), DOWN, icon, (ts, ts), (te, te), shaded = false)),
        UP -> List(helper.quad((ts, o2, ts), (te, o2, te), UP, icon, (ts, ts), (te, te), shaded = false)),
        NORTH -> List(helper.quad((ts, ts, o1), (te, te, o1), NORTH, icon, (ts, ts), (te, te), shaded = false)),
        SOUTH -> List(helper.quad((ts, ts, o2), (te, te, o2), SOUTH, icon, (ts, ts), (te, te), shaded = false)),
        WEST -> List(helper.quad((o1, ts, ts), (o1, te, te), WEST, icon, (ts, ts), (te, te), shaded = false)),
        EAST -> List(helper.quad((o2, ts, ts), (o2, te, te), EAST, icon, (ts, ts), (te, te), shaded = false))
      )
      new BakedModelAdditionalFaceQuads(base, filterSides(state, sides))
    } getOrElse base
}

object FluidFilterModelEnhancer extends BaseFluidFilterModelEnhancer(5)

object FluidFilterRotatedModelEnhancer extends BaseFluidFilterModelEnhancer(5) {
  override def filterSides(state: IBlockState, quads: Map[EnumFacing, List[BakedQuad]]) = {
    val rot = state.getValue(Properties.FACING)
    quads.filterKeys(x => x != rot && x != rot.getOpposite)
  }
}