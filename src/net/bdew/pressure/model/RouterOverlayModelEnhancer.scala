/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.model

import java.util

import net.bdew.lib.render.models.ModelEnhancer
import net.bdew.lib.render.primitive.{Texture, Vertex}
import net.bdew.lib.render.{Cuboid, QuadBakerDefault}
import net.bdew.pressure.blocks.router.{BlockRouter, RouterIcons}
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.{BlockRenderLayer, EnumFacing, ResourceLocation}
import net.minecraftforge.client.MinecraftForgeClient

object RouterOverlayModelEnhancer extends ModelEnhancer {
  lazy val faceQuads = EnumFacing.values().map(f => f -> Cuboid.face(Vertex(-0.01f, -0.01f, -0.01f), Vertex(1.01f, 1.01f, 1.01f), f)).toMap
  override val additionalTextureLocations = RouterIcons.overlays.values.toList

  override def processQuads(state: IBlockState, side: EnumFacing, rand: Long, textures: Map[ResourceLocation, TextureAtlasSprite], base: () => util.List[BakedQuad]): util.List[BakedQuad] = {
    if (MinecraftForgeClient.getRenderLayer == BlockRenderLayer.CUTOUT && state != null && side != null) {
      val quad = faceQuads(side)
      val list = base()
      for (mode <- BlockRouter.Properties.MODE(side).get(state)) {
        list.add(QuadBakerDefault.bakeQuad(quad.withTexture(Texture(textures(RouterIcons.overlays(mode))), tint = side.getIndex, shading = false)))
      }
      list
    } else base()
  }
}