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
import net.bdew.lib.property.SimpleUnlistedProperty
import net.bdew.lib.render.models.{BakedModelAdditionalFaceQuads, ModelEnhancer}
import net.bdew.lib.render.primitive.{Texture, UV, Vertex}
import net.bdew.lib.render.{Cuboid, QuadBaker}
import net.bdew.lib.rotate.Properties
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing._
import net.minecraft.util.{EnumFacing, ResourceLocation}
import net.minecraftforge.client.model.IPerspectiveAwareModel
import net.minecraftforge.fluids.Fluid

object FluidFilterProperty extends SimpleUnlistedProperty("filter", classOf[Fluid]) {
  override def valueToString(value: Fluid): String = value.getName
}

class BaseFluidFilterModelEnhancer(size: Float) extends ModelEnhancer {
  val quads = {
    val start = (8f - size / 2f) / 16f
    val end = (8f + size / 2f) / 16f
    val offset1 = -0.01f
    val offset2 = 1.01f

    Map(
      DOWN -> Cuboid.face(Vertex(start, offset1, start), Vertex(end, offset1, end), DOWN),
      UP -> Cuboid.face(Vertex(start, offset2, start), Vertex(end, offset2, end), UP),
      NORTH -> Cuboid.face(Vertex(start, start, offset1), Vertex(end, end, offset1), NORTH),
      SOUTH -> Cuboid.face(Vertex(start, start, offset2), Vertex(end, end, offset2), SOUTH),
      WEST -> Cuboid.face(Vertex(offset1, start, start), Vertex(offset1, end, end), WEST),
      EAST -> Cuboid.face(Vertex(offset2, start, start), Vertex(offset2, end, end), EAST)
    )
  }

  def sidesWithIcon(state: IBlockState): Set[EnumFacing] = EnumFacing.values().toSet

  override def handleItemState(base: IPerspectiveAwareModel, stack: ItemStack, textures: Map[ResourceLocation, TextureAtlasSprite]) = base

  override def handleBlockState(base: IPerspectiveAwareModel, state: IBlockState, additionalSprites: Map[ResourceLocation, TextureAtlasSprite]) = {
    val quadBaker = new QuadBaker(base.getFormat)
    FluidFilterProperty.get(state) map { fluid =>
      val sides = sidesWithIcon(state)

      val icon = Texture(Client.textureMapBlocks.getAtlasSprite(fluid.getStill.toString),
        UV(8f - size / 2f, 8f - size / 2f), UV(8f + size / 2f, 8f + size / 2f))

      val baked =
        for ((side, quad) <- quads if sides.contains(side)) yield {
          side -> List(quadBaker.bakeQuad(quad.withTexture(icon, shading = false)))
        }

      new BakedModelAdditionalFaceQuads(base, baked.toMap)
    } getOrElse base
  }
}

object FluidFilterModelEnhancer extends BaseFluidFilterModelEnhancer(5)

object FluidFilterRotatedModelEnhancer extends BaseFluidFilterModelEnhancer(5) {
  override def sidesWithIcon(state: IBlockState) = {
    val rot = state.getValue(Properties.FACING)
    EnumFacing.values().toSet -- Set(rot, rot.getOpposite)
  }
}