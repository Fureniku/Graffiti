package com.silvaniastudios.graffiti.client;

import java.awt.Color;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.silvaniastudios.graffiti.drawables.CompleteGraffitiObject;
import com.silvaniastudios.graffiti.drawables.TextDrawable;
import com.silvaniastudios.graffiti.tileentity.TileEntityGraffiti;

import net.minecraft.block.BlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.shapes.VoxelShape;

public class GraffitiRenderer extends TileEntityRenderer<TileEntityGraffiti> {
	
	
	public static final ResourceLocation TEXTURE = new ResourceLocation("forge:textures/white.png");

	public GraffitiRenderer(final TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@Override
	public void render(TileEntityGraffiti te, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
		for (Direction dir : Direction.values()) {
			if (te.getGraffitiForFace(dir) != null) {
				drawGraffitiSided(te, te.getGraffitiForFace(dir), dir, matrixStack, buffer, combinedLightIn, combinedOverlayIn);
			}
		}
	}
	
	public void drawGraffitiSided(TileEntityGraffiti te, CompleteGraffitiObject graffiti, Direction dir, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
		IVertexBuilder vertexBuilderBlockQuads = buffer.getBuffer(RenderType.getEntitySolid(TEXTURE));
		FontRenderer fontrenderer = this.renderDispatcher.getFontRenderer();
		
		float offset = (float) graffiti.getAlignment();
		
		if (graffiti.isOffsetIntoBlock()) {
			BlockState stateBehind = te.getWorld().getBlockState(te.getPos().offset(dir));
			VoxelShape voxelShape = stateBehind.getCollisionShape(te.getWorld(), te.getPos().offset(dir));
			
			if (!voxelShape.isEmpty()) {
				AxisAlignedBB shapeBehind = voxelShape.getBoundingBox();
	
				if (dir == Direction.NORTH) { offset = (float) ((shapeBehind.maxZ - 1) + graffiti.getAlignment()); }
				if (dir == Direction.EAST)  { offset = (float) ((1 - shapeBehind.minX) - 1 + graffiti.getAlignment()); }
				if (dir == Direction.SOUTH) { offset = (float) ((1 - shapeBehind.minZ) - 1 + graffiti.getAlignment()); }
				if (dir == Direction.WEST)  { offset = (float) ((shapeBehind.maxX - 1) + graffiti.getAlignment()); }
				
				if (dir == Direction.UP)   { offset = (float) (graffiti.getAlignment() - shapeBehind.minY); }
				if (dir == Direction.DOWN) { offset = (float) (graffiti.getAlignment() - (1-shapeBehind.maxY)); }
			}
		}
		
		if (graffiti.pixelGrid != null) {
			float p = 1.0F/graffiti.pixelGrid.getSize();
			for (int i = 0; i < graffiti.pixelGrid.getSize(); i++) {
				for (int j = 0; j < graffiti.pixelGrid.getSize(); j++) {
					int rgb = graffiti.pixelGrid.getPixelRGB(j, i);
					if (rgb != 0) {
						Color col = new Color(rgb);
						RenderHelper.renderSinglePixel(dir, matrixStack, combinedLightIn, vertexBuilderBlockQuads, 
								j*p, i*p, new Color(col.getRed(), col.getGreen(), col.getBlue(), graffiti.pixelGrid.getTransparency()), offset, graffiti.pixelGrid.getSize());
					}
				}
			}
		}
		
		if (dir == Direction.NORTH) {
			for (int i = 0; i < graffiti.textList.size(); i++) {
				TextDrawable text = graffiti.textList.get(i);
				RenderHelper.renderTextNorthSouth(text, matrixStack, fontrenderer, buffer, combinedLightIn, offset, true);
			}
		}
		
		if (dir == Direction.EAST) {
			for (int i = 0; i < graffiti.textList.size(); i++) {
				TextDrawable text = graffiti.textList.get(i);
				RenderHelper.renderTextEastWest(text, matrixStack, fontrenderer, buffer, combinedLightIn, offset, true);
			}
		}
		
		if (dir == Direction.SOUTH) {
			for (int i = 0; i < graffiti.textList.size(); i++) {
				TextDrawable text = graffiti.textList.get(i);
				RenderHelper.renderTextNorthSouth(text, matrixStack, fontrenderer, buffer, combinedLightIn, offset, false);
			}
		}
		
		if (dir == Direction.WEST) {
			for (int i = 0; i < graffiti.textList.size(); i++) {
				TextDrawable text = graffiti.textList.get(i);
				RenderHelper.renderTextEastWest(text, matrixStack, fontrenderer, buffer, combinedLightIn, offset, false);
			}
		}
		
		if (dir == Direction.UP) {
			for (int i = 0; i < graffiti.textList.size(); i++) {
				TextDrawable text = graffiti.textList.get(i);
				RenderHelper.renderTextUpDown(text, matrixStack, fontrenderer, buffer, combinedLightIn, offset, true);
			}
		}
		
		if (dir == Direction.DOWN) {
			for (int i = 0; i < graffiti.textList.size(); i++) {
				TextDrawable text = graffiti.textList.get(i);
				RenderHelper.renderTextUpDown(text, matrixStack, fontrenderer, buffer, combinedLightIn, offset, false);
			}
		}
	}
}
