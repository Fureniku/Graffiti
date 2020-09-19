package com.silvaniastudios.graffiti.client.gui;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import com.silvaniastudios.graffiti.Graffiti;
import com.silvaniastudios.graffiti.drawables.CompleteGraffitiObject;
import com.silvaniastudios.graffiti.drawables.PixelGridDrawable;
import com.silvaniastudios.graffiti.drawables.TextDrawable;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

public class GuiDisplayArt extends Screen {
	
	private CompleteGraffitiObject graffiti;
	
	private int xSize = 256;
	private int ySize = 256;
	
	private static final ResourceLocation TEXTURE = new ResourceLocation(Graffiti.MODID, "textures/gui/drawing.png");

	public GuiDisplayArt(CompleteGraffitiObject graffiti) {
		super(new TranslationTextComponent("graffiti.display"));
		
		this.graffiti = graffiti;
	}

	@Override
	protected void init() {
		this.minecraft.keyboardListener.enableRepeatEvents(true);
	}
	
	@Override
	public void renderBackground(int rb_1_) {
		this.minecraft.getTextureManager().bindTexture(TEXTURE);
		
		int startX = (this.width / 2) - (this.xSize / 2);
		int startY = (this.height / 2) - (this.ySize / 2);
		
		this.blit(startX-10, startY-10, 0, 0, 10, 10); //top left
		this.blit(startX, startY-10, 10, 0, 128, 10);
		this.blit(startX+128, startY-10, 10, 0, 128, 10);
		this.blit(startX+this.xSize, startY-10, 246, 0, 10, 10); //top right
		
		//left
		this.blit(startX-10, startY, 0, 10, 10, 128);
		this.blit(startX-10, startY + 128, 0, 10, 10, 128);
		
		//right
		this.blit(startX+this.xSize, startY, 246, 10, 10, 128);
		this.blit(startX+this.xSize, startY + 128, 246, 10, 10, 128);
		
		this.blit(startX-10, startY+this.ySize, 0, 140, 10, 10); //bottom left
		this.blit(startX, startY+this.ySize, 10, 140, 128, 10);
		this.blit(startX+128, startY+this.ySize, 10, 140, 128, 10);
		this.blit(startX+this.xSize, startY+this.ySize, 246, 140, 10, 10); //bottom right
		
		int bg = new Color(64, 64, 64, graffiti.getBackgroundTransparency()).getRGB();
		
		this.fillGradient(startX, startY, startX + 256, startY + 256, bg, bg);

		drawGraffiti();
	}
	
	@Override
	public void render(int x, int y, float partialTick) {
		this.renderBackground();
		super.render(x, y, partialTick);
	}
	
	public void drawGraffiti() {
		int startX = (this.width / 2) - (this.xSize / 2);
		int startY = (this.height / 2) - (this.ySize / 2);
		
		PixelGridDrawable grid = graffiti.pixelGrid;
		if (grid != null && grid.getSize() > 0) {
			int size = 16;
			if (grid.getSize() == 32) {
				size = 8;
			} else if (grid.getSize() == 64) {
				size = 4;
			} else if (grid.getSize() == 128) {
				size = 2;
			}
			
			for (int i = 0; i < grid.getSize(); i++) {
				for (int j = 0; j < grid.getSize(); j++) {
					this.fillGradient(startX + (j*size), startY + (i*size), startX + (j*size)+size, startY + (i*size)+size, grid.getPixelRGB(j, i), grid.getPixelRGB(j, i));
				}
			}
		}
		for (int i = 0; i < graffiti.textList.size(); i++) {
			TextDrawable text = graffiti.textList.get(i);
			
			float scale = 4F;
			
			GL11.glPushMatrix();
			GL11.glScaled(scale, scale, scale);
			this.font.drawString(text.getDrawableText(), (startX + (text.xPos()*scale))/scale, (startY + Math.abs((text.yPos()*scale)-256))/scale, text.getCol());
			GL11.glScaled(1/scale, 1/scale, 1/scale);
			GL11.glPopMatrix();
		}
	}
}
