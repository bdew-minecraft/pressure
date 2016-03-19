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
import javax.vecmath.Matrix4f

import com.google.common.collect.ImmutableList
import net.bdew.lib.Client
import net.bdew.lib.render.models.{BakedModelProxy, ModelEnhancer, ModelUtils}
import net.bdew.lib.render.primitive.{Texture, UV, Vertex}
import net.bdew.lib.render.{Cuboid, QuadBakerDefault}
import net.bdew.pressure.items.Canister
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType
import net.minecraft.client.renderer.block.model.{BakedQuad, IBakedModel, ItemOverrideList}
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.util.{EnumFacing, ResourceLocation}
import net.minecraft.world.World
import net.minecraftforge.client.model.TRSRTransformation
import org.apache.commons.lang3.tuple.Pair

object CanisterModelEnhancer extends ModelEnhancer {
  val overlay = new ResourceLocation("pressure:gui/canister_overlay")
  override val additionalTextureLocations = List(overlay)

  override def processQuads(state: IBlockState, side: EnumFacing, rand: Long, textures: Map[ResourceLocation, TextureAtlasSprite], base: () => util.List[BakedQuad]): util.List[BakedQuad] = base()

  override val itemOverrideList: ItemOverrideList = new ItemOverrideList(ImmutableList.of()) {
    override def handleItemState(baseModel: IBakedModel, stack: ItemStack, world: World, entity: EntityLivingBase): IBakedModel = {
      val fluid = Canister.getFluid(stack)
      if (fluid != null && fluid.getFluid != null && fluid.amount > 0) {
        new BakedModelProxy(baseModel) {
          override def handlePerspective(cameraTransformType: TransformType): Pair[_ <: IBakedModel, Matrix4f] = {
            if (cameraTransformType == TransformType.GUI) {
              val overlayTexture = Texture(Client.textureMapBlocks.getAtlasSprite("pressure:gui/canister_overlay"), UV(0, 0), UV(5, 16))
              val matrix = base.handlePerspective(cameraTransformType).getRight
              val trans = new TRSRTransformation(matrix)
              val fill = 15f * fluid.amount / Canister.capacity
              val fluidTexture = Texture(Client.textureMapBlocks.getAtlasSprite(fluid.getFluid.getStill(fluid).toString), UV(0.5f, 0.5f), UV(4.5f, 0.5f + fill))
              val addQuads = QuadBakerDefault.bakeList(List(
                Cuboid.face(Vertex(0, 0, 16), Vertex(5 / 16f, 1, 16), EnumFacing.SOUTH, overlayTexture, trans),
                Cuboid.face(Vertex(0.5f / 16f, 0.5f / 16f, 16), Vertex(4.5f / 16f, (0.5f + fill) / 16f, 16), EnumFacing.SOUTH, fluidTexture, trans)
              ))
              Pair.of(new BakedModelProxy(baseModel) {
                override def getQuads(state: IBlockState, side: EnumFacing, rand: Long): util.List[BakedQuad] = {
                  import scala.collection.JavaConversions._
                  super.getQuads(state, side, rand) ++ addQuads
                }
              }, matrix)
            } else ModelUtils.makePerspectiveAware(baseModel).handlePerspective(cameraTransformType)
          }
        }
      } else baseModel
    }
  }
}
