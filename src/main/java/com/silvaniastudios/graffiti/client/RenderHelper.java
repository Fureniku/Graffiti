/*
 * This class is mostly code used from Fureniku's Roads (my own mod). Both complex and boring things that I didn't want to rewrite :)
 * Some parts changed/updated for 1.15
 */
package com.silvaniastudios.graffiti.client;

import java.awt.Color;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Matrix3f;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

public class RenderHelper {

	private static ResourceLocation rloc = new ResourceLocation("forge:white");
	private static TextureAtlasSprite tex = Minecraft.getInstance().getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE).apply(rloc);
	private static final float p = 1F/16F;
	
	public static void renderSinglePixel(Direction dir, MatrixStack matrixStack, int light, IVertexBuilder buffer, float x, float y, Color col) {
		if (dir == Direction.NORTH) {
			float depth = 1F/64F;
			renderPlainQuad(dir, matrixStack, light, buffer, col,
					1-Math.abs(-1+x),       1-Math.abs(-(y + p)), depth, //bl
					1-Math.abs(-1+(x + p)), 1-Math.abs(-(y + p)), depth, //br
					1-Math.abs(-1+(x + p)), 1-Math.abs(-y),       depth, //tr
					1-Math.abs(-1+x),       1-Math.abs(-y),       depth); //tl
		}
		if (dir == Direction.EAST) {
			float depth = 1F - (1F/64F);
			renderPlainQuad(dir, matrixStack, light, buffer, col,
					depth, 1-Math.abs(-(y + p)), 1-Math.abs(-1+x),       //bl
					depth, 1-Math.abs(-(y + p)), 1-Math.abs(-1+(x + p)), //br
					depth, 1-Math.abs(-y),       1-Math.abs(-1+(x + p)), //tr
					depth, 1-Math.abs(-y),       1-Math.abs(-1+x));	     //tl
		}
		if (dir == Direction.SOUTH) {
			float depth = 1F - (1F/64F);
			renderPlainQuad(dir, matrixStack, light, buffer, col,
					1-Math.abs(-x),       1-Math.abs(-(y + p)), depth, //bl
					1-Math.abs(-(x + p)), 1-Math.abs(-(y + p)), depth, //br
					1-Math.abs(-(x + p)), 1-Math.abs(-y),       depth, //tr
					1-Math.abs(-x),       1-Math.abs(-y),       depth); //tl
		}
		if (dir == Direction.WEST) {
			float depth = 1F/64F;
			renderPlainQuad(dir, matrixStack, light, buffer, col,
					depth, 1-Math.abs(-(y + p)), 1-Math.abs(-x), 	   //bl
					depth, 1-Math.abs(-(y + p)), 1-Math.abs(-(x + p)), //br
					depth, 1-Math.abs(-y),       1-Math.abs(-(x + p)), //tr
					depth, 1-Math.abs(-y),       1-Math.abs(-x));      //tl
		}
	}

	public static void renderPlainQuad(Direction dir, MatrixStack matrixStack, int light, IVertexBuilder buffer, Color col,
			float x1, float y1, float z1,
			float x2, float y2, float z2,
			float x3, float y3, float z3,
			float x4, float y4, float z4) {

		final float minU = tex.getMinU();
		final float maxU = tex.getMaxU();
		final float minV = tex.getMinV();
		final float maxV = tex.getMaxV();
		
		Matrix4f matrixPos = matrixStack.getLast().getMatrix();
		Matrix3f matrixNormal = matrixStack.getLast().getNormal();
		Vector3f normalVector = dir.toVector3f();
		
		buffer.pos(matrixPos, x1, y1, z1)
		.color(col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha())
		.tex(minU, maxV)
		.overlay(OverlayTexture.NO_OVERLAY)
		.lightmap(light)
		.normal(matrixNormal, normalVector.getX(), normalVector.getY(), normalVector.getZ())
		.endVertex(); //SE
		
		buffer.pos(matrixPos, x2, y2, z2)
		.color(col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha())
		.tex(minU, minV)
		.overlay(OverlayTexture.NO_OVERLAY)
		.lightmap(light)
		.normal(matrixNormal, normalVector.getX(), normalVector.getY(), normalVector.getZ())
		.endVertex();
		
		buffer.pos(matrixPos, x3, y3, z3)
		.color(col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha())
		.tex(maxU, minV)
		.overlay(OverlayTexture.NO_OVERLAY)
		.lightmap(light)
		.normal(matrixNormal, normalVector.getX(), normalVector.getY(), normalVector.getZ())
		.endVertex();
		
		buffer.pos(matrixPos, x4, y4, z4)
		.color(col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha())
		.tex(maxU, maxV)
		.overlay(OverlayTexture.NO_OVERLAY)
		.lightmap(light)
		.normal(matrixNormal, normalVector.getX(), normalVector.getY(), normalVector.getZ())
		.endVertex();
	}	
}
