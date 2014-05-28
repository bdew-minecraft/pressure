/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/pressure/master/MMPL-1.0.txt
 */

package net.bdew.pressure.render

import net.minecraft.util.Icon
import net.minecraftforge.common.ForgeDirection
import net.minecraft.client.renderer.Tessellator

object RenderHelper {

  case class P2d(x: Float, y: Float)

  case class P3d(x: Float, y: Float, z: Float)

  case class PIcon(u: Int, v: Int)

  case class PRender(x: Float, y: Float, z: Float, u: Int, v: Int)

  val brightnessMultiplier = Map(
    ForgeDirection.UP -> 1F,
    ForgeDirection.DOWN -> 0.5F,
    ForgeDirection.NORTH -> 0.8F,
    ForgeDirection.SOUTH -> 0.8F,
    ForgeDirection.WEST -> 0.6F,
    ForgeDirection.EAST -> 0.6F
  )

  def ZNeg(p1: P2d, p2: P2d, o: Float, uv1: PIcon, uv2: PIcon) = (List(
    PRender(p1.x, p1.y, o, uv1.u, uv1.v),
    PRender(p1.x, p2.y, o, uv1.u, uv2.v),
    PRender(p2.x, p2.y, o, uv2.u, uv2.v),
    PRender(p2.x, p1.y, o, uv2.u, uv1.v)
  ), 0.8F)

  def ZPos(p1: P2d, p2: P2d, o: Float, uv1: PIcon, uv2: PIcon) = (List(
    PRender(p1.x, p1.y, o, uv1.u, uv1.v),
    PRender(p2.x, p1.y, o, uv2.u, uv1.v),
    PRender(p2.x, p2.y, o, uv2.u, uv2.v),
    PRender(p1.x, p2.y, o, uv1.u, uv2.v)
  ), 0.8F)

  def XNeg(p1: P2d, p2: P2d, o: Float, uv1: PIcon, uv2: PIcon) = (List(
    PRender(o, p1.y, p1.x, uv1.u, uv1.v),
    PRender(o, p1.y, p2.x, uv2.u, uv1.v),
    PRender(o, p2.y, p2.x, uv2.u, uv2.v),
    PRender(o, p2.y, p1.x, uv1.u, uv2.v)
  ), 0.6F)

  def XPos(p1: P2d, p2: P2d, o: Float, uv1: PIcon, uv2: PIcon) = (List(
    PRender(o, p1.y, p1.x, uv1.u, uv1.v),
    PRender(o, p2.y, p1.x, uv1.u, uv2.v),
    PRender(o, p2.y, p2.x, uv2.u, uv2.v),
    PRender(o, p1.y, p2.x, uv2.u, uv1.v)
  ), 0.6F)

  def YPos(p1: P2d, p2: P2d, o: Float, uv1: PIcon, uv2: PIcon) = (List(
    PRender(p1.x, o, p1.y, uv1.u, uv1.v),
    PRender(p1.x, o, p2.y, uv1.u, uv2.v),
    PRender(p2.x, o, p2.y, uv2.u, uv2.v),
    PRender(p2.x, o, p1.y, uv2.u, uv1.v)
  ), 1F)

  def YNeg(p1: P2d, p2: P2d, o: Float, uv1: PIcon, uv2: PIcon) = (List(
    PRender(p1.x, o, p1.y, uv1.u, uv1.v),
    PRender(p2.x, o, p1.y, uv2.u, uv1.v),
    PRender(p2.x, o, p2.y, uv2.u, uv2.v),
    PRender(p1.x, o, p2.y, uv1.u, uv2.v)
  ), 1F)

  def draw(rect: (List[PRender], Float), offs: P3d, icon: Icon) = {
    val (points, bright) = rect
    Tessellator.instance.setColorOpaque_F(bright, bright, bright)
    points.foreach(p =>
      Tessellator.instance.addVertexWithUV(offs.x + p.x, offs.y + p.y, offs.z + p.z,
        icon.getInterpolatedU(p.u), icon.getInterpolatedV(p.v)))
  }
}
