/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.model

import net.bdew.lib.render.models.ModelEnhancer
import net.minecraft.client.resources.IResourceManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.{ICustomModelLoader, ModelLoaderRegistry}

object ExtendedModelLoader extends ICustomModelLoader {
  override def accepts(modelLocation: ResourceLocation) =
    modelLocation.getResourceDomain.equals("pressure") && modelLocation.getResourcePath.endsWith(".extended")

  def wrap(model: String, enhancer: ModelEnhancer) =
    enhancer.wrap(ModelLoaderRegistry.getModel(new ResourceLocation(model)))

  override def loadModel(modelLocation: ResourceLocation) =
    modelLocation.getResourcePath match {
      case "models/block/filtered_rotated.extended" => wrap("pressure:block/rotated", FluidFilterRotatedModelEnhancer)
      case "models/block/filtered_cube_all.extended" => wrap("minecraft:block/cube_all", FluidFilterModelEnhancer)
      case "models/block/router.extended" => wrap("minecraft:block/cube_all", RouterOverlayModelEnhancer)

      case _ => null
    }

  override def onResourceManagerReload(resourceManager: IResourceManager) = {}

  def install(): Unit = {
    ModelLoaderRegistry.registerLoader(this)
  }
}
