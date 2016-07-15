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
import net.bdew.lib.render.models.ModelEnhancer
import net.bdew.lib.render.primitive.{Texture, UV, Vertex}
import net.bdew.lib.render.{Cuboid, QuadBakerDefault}
import net.bdew.pressure.items.Canister
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.item.ItemStack
import net.minecraft.util.{EnumFacing, ResourceLocation}

object CanisterModelEnhancer extends ModelEnhancer {
  val overlay = new ResourceLocation("pressure:gui/canister_overlay")
  override val additionalTextureLocations = List(overlay)

  override def processItemQuads(stack: ItemStack, side: EnumFacing, rand: Long, mode: TransformType, textures: Map[ResourceLocation, TextureAtlasSprite], base: () => List[BakedQuad]): List[BakedQuad] = {
    var list = super.processItemQuads(stack, side, rand, mode, textures, base)
    val fluid = Canister.getContainedFluid(stack)
    if (mode == TransformType.GUI && side == null && fluid != null && fluid.getFluid != null && fluid.amount > 0) {
      val overlayTexture = Texture(Client.textureMapBlocks.getAtlasSprite("pressure:gui/canister_overlay"), UV(0, 0), UV(5, 16))
      val fill = 15f * fluid.amount / Canister.capacity
      val fluidTexture = Texture(Client.textureMapBlocks.getAtlasSprite(fluid.getFluid.getStill(fluid).toString), UV(0.5f, 0.5f), UV(4.5f, 0.5f + fill))
      list :+= QuadBakerDefault.bakeQuad(Cuboid.face(Vertex(0, 0, 1), Vertex(5 / 16f, 1, 1), EnumFacing.SOUTH, overlayTexture))
      list :+= QuadBakerDefault.bakeQuad(Cuboid.face(Vertex(0.5f / 16f, 0.5f / 16f, 1), Vertex(4.5f / 16f, (0.5f + fill) / 16f, 1), EnumFacing.SOUTH, fluidTexture))
    }
    list
  }
}
