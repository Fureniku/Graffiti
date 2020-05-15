package com.silvaniastudios.graffiti.client;

import java.awt.Color;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.silvaniastudios.graffiti.block.GraffitiBlock;
import com.silvaniastudios.graffiti.drawables.GraffitiTextDrawable;
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
	
	float p = 1/16F;
	public static final ResourceLocation TEXTURE = new ResourceLocation("forge:textures/white.png");

	public GraffitiRenderer(final TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
		System.out.println("renderer is registered");
	}

	@Override
	public void render(TileEntityGraffiti tileEntityIn, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
		IVertexBuilder vertexBuilderBlockQuads = buffer.getBuffer(RenderType.getEntitySolid(TEXTURE));
		
		BlockState state = tileEntityIn.getWorld().getBlockState(tileEntityIn.getPos());
		
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				int rgb = tileEntityIn.getPixelRGB(j, i);
				if (rgb != 0) {
					RenderHelper.renderSinglePixel(state.get(GraffitiBlock.FACING), matrixStack, combinedLightIn, vertexBuilderBlockQuads, 
							j*p, i*p, new Color(rgb));
				}
			}
		}
		
		if (state.get(GraffitiBlock.FACING) == Direction.NORTH) {

		}
		
		if (state.get(GraffitiBlock.FACING) == Direction.EAST) {
			
		}
		
		if (state.get(GraffitiBlock.FACING) == Direction.SOUTH) {
			for (int i = 0; i < tileEntityIn.textList.size(); i++) {
				GraffitiTextDrawable text = tileEntityIn.textList.get(i);

				matrixStack.push();
				FontRenderer fontrenderer = this.renderDispatcher.getFontRenderer();
				matrixStack.translate(1.0D, 1.0D, 1D - (1D/64D));
				matrixStack.scale(-0.010416667F, -0.010416667F, 0.010416667F);
				
				fontrenderer.renderString(text.getText(), text.xPos()*5.5F, Math.abs(16-text.yPos())*5.5F, text.getCol()*10, false, matrixStack.getLast().getMatrix(), buffer, false, 0, combinedLightIn);
				matrixStack.pop();
			}
		}
		
		if (state.get(GraffitiBlock.FACING) == Direction.WEST) {

		}
	}
}
