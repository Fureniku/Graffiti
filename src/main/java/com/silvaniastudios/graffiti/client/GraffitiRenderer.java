package com.silvaniastudios.graffiti.client;

import java.awt.Color;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.silvaniastudios.graffiti.block.GraffitiBlock;
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

public class GraffitiRenderer extends TileEntityRenderer<TileEntityGraffiti> {
	
	
	public static final ResourceLocation TEXTURE = new ResourceLocation("forge:textures/white.png");

	public GraffitiRenderer(final TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
		System.out.println("renderer is registered");
	}

	@Override
	public void render(TileEntityGraffiti tileEntityIn, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
		IVertexBuilder vertexBuilderBlockQuads = buffer.getBuffer(RenderType.getEntitySolid(TEXTURE));
		FontRenderer fontrenderer = this.renderDispatcher.getFontRenderer();
		BlockState state = tileEntityIn.getWorld().getBlockState(tileEntityIn.getPos());
		
		if (tileEntityIn.pixelGrid != null) {
			float p = 1.0F/tileEntityIn.pixelGrid.getSize();
			for (int i = 0; i < tileEntityIn.pixelGrid.getSize(); i++) {
				for (int j = 0; j < tileEntityIn.pixelGrid.getSize(); j++) {
					int rgb = tileEntityIn.pixelGrid.getPixelRGB(j, i);
					if (rgb != 0) {
						RenderHelper.renderSinglePixel(state.get(GraffitiBlock.FACING), matrixStack, combinedLightIn, vertexBuilderBlockQuads, 
								j*p, i*p, new Color(rgb), tileEntityIn.pixelGrid.getSize());
					}
				}
			}
		}
		
		if (state.get(GraffitiBlock.FACING) == Direction.NORTH) {
			for (int i = 0; i < tileEntityIn.textList.size(); i++) {
				TextDrawable text = tileEntityIn.textList.get(i);
				
				RenderHelper.renderTextNorthSouth(text, matrixStack, fontrenderer, buffer, combinedLightIn, true);
				//RenderHelper.renderText(text, matrixStack, fontrenderer, buffer, combinedLightIn, 180, 0.0D, 1.0D, 1D/64D);
			}
		}
		
		if (state.get(GraffitiBlock.FACING) == Direction.EAST) {
			for (int i = 0; i < tileEntityIn.textList.size(); i++) {
				TextDrawable text = tileEntityIn.textList.get(i);
				
				RenderHelper.renderTextEastWest(text, matrixStack, fontrenderer, buffer, combinedLightIn, true);
				//RenderHelper.renderText(text, matrixStack, fontrenderer, buffer, combinedLightIn, 270, 1.0D, 1.0D, 1D/64D);
			}
		}
		
		if (state.get(GraffitiBlock.FACING) == Direction.SOUTH) {
			for (int i = 0; i < tileEntityIn.textList.size(); i++) {
				TextDrawable text = tileEntityIn.textList.get(i);
				
				RenderHelper.renderTextNorthSouth(text, matrixStack, fontrenderer, buffer, combinedLightIn, false);
				//RenderHelper.renderText(text, matrixStack, fontrenderer, buffer, combinedLightIn, 0, 1.0D, 1.0D, 1 - (1D/64D));
			}
		}
		
		if (state.get(GraffitiBlock.FACING) == Direction.WEST) {
			for (int i = 0; i < tileEntityIn.textList.size(); i++) {
				TextDrawable text = tileEntityIn.textList.get(i);
				
				RenderHelper.renderTextEastWest(text, matrixStack, fontrenderer, buffer, combinedLightIn, false);
				//RenderHelper.renderText(text, matrixStack, fontrenderer, buffer, combinedLightIn, 90, 0.0D, 1.0D, 1 - (1D/64D));
			}
		}
		
		if (state.get(GraffitiBlock.FACING) == Direction.UP) {
			for (int i = 0; i < tileEntityIn.textList.size(); i++) {
				TextDrawable text = tileEntityIn.textList.get(i);
				
				RenderHelper.renderTextUpDown(text, matrixStack, fontrenderer, buffer, combinedLightIn, true);
			}
		}
		
		if (state.get(GraffitiBlock.FACING) == Direction.DOWN) {
			for (int i = 0; i < tileEntityIn.textList.size(); i++) {
				TextDrawable text = tileEntityIn.textList.get(i);
				
				RenderHelper.renderTextUpDown(text, matrixStack, fontrenderer, buffer, combinedLightIn, false);
			}
		}
	}
}
