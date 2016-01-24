/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.model

import net.bdew.lib.render.models.{ModelEnhancer, SmartBakedModelBuilder}
import net.bdew.lib.render.primitive.{Texture, Vertex}
import net.bdew.lib.render.{Cuboid, QuadBaker}
import net.bdew.pressure.blocks.router.{BlockRouter, RouterIcons}
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.{EnumFacing, EnumWorldBlockLayer, ResourceLocation}
import net.minecraftforge.client.MinecraftForgeClient
import net.minecraftforge.client.model.IFlexibleBakedModel

object RouterOverlayModelEnhancer extends ModelEnhancer {
  lazy val faceQuads = EnumFacing.values().map(f => f -> Cuboid.face(Vertex(-0.01f, -0.01f, -0.01f), Vertex(1.01f, 1.01f, 1.01f), f)).toMap
  override val additionalTextureLocations = RouterIcons.overlays.values.toList
  override def handleState(base: IFlexibleBakedModel, state: IBlockState, additionalSprites: Map[ResourceLocation, TextureAtlasSprite]) = {
    if (MinecraftForgeClient.getRenderLayer == EnumWorldBlockLayer.CUTOUT) {
      val builder = new SmartBakedModelBuilder
      val baker = new QuadBaker(base.getFormat)
      builder.format = base.getFormat
      builder.texture = base.getParticleTexture
      for ((face, quad) <- faceQuads; mode <- BlockRouter.Properties.MODE(face).get(state))
        builder.addQuad(face, baker.bakeQuad(
          quad.withTexture(Texture(additionalSprites(RouterIcons.overlays(mode))), face.getIndex)
        ))
      builder.build()
    } else base
  }
}