package com.silvaniastudios.graffiti.client.gui;

import com.silvaniastudios.graffiti.Graffiti;
import com.silvaniastudios.graffiti.drawables.PixelGridDrawable;
import com.silvaniastudios.graffiti.drawables.TextDrawable;
import com.silvaniastudios.graffiti.tileentity.TileEntityGraffiti;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

public class GuiCanvasEditorBase extends Screen {
	
	protected TileEntityGraffiti tileEntity;
	
	private int xSize = 256;
	private int ySize = 248;
	
	private static final ResourceLocation TEXTURE = new ResourceLocation(Graffiti.MODID, "textures/gui/canvas_editor.png");

	public GuiCanvasEditorBase(TileEntityGraffiti te) {
		super(new TranslationTextComponent("graffiti.write"));

		tileEntity = te;
	}

	@Override
	protected void init() {
		this.minecraft.keyboardListener.enableRepeatEvents(true);
	}
	
	@Override
	public void renderBackground(int rb_1_) {
		this.minecraft.getTextureManager().bindTexture(TEXTURE);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.blit(i, j, 0, 0, this.xSize, this.ySize);
		
		int startX = (this.width / 2) - (this.xSize / 2) + 11;
		int startY = (this.height / 2) - (this.ySize / 2) + 11;
		
		int texture = tileEntity.pixelGrid.getSize();
		if (tileEntity.pixelGrid.getSize() == 16)  texture = 0; 
		if (tileEntity.pixelGrid.getSize() == 128) texture = 96; 
		
		
		this.blit(startX, startY, 11+texture, 11, 32, 128);
		this.blit(startX+32, startY, 11+texture, 11, 32, 128);
		this.blit(startX+64, startY, 11+texture, 11, 32, 128);
		this.blit(startX+96, startY, 11+texture, 11, 32, 128);
	}
	
	@Override //we dont like shadows!
	public void drawCenteredString(FontRenderer p_drawCenteredString_1_, String p_drawCenteredString_2_, int p_drawCenteredString_3_, int p_drawCenteredString_4_, int p_drawCenteredString_5_) {
		p_drawCenteredString_1_.drawString(p_drawCenteredString_2_, (float)(p_drawCenteredString_3_ - p_drawCenteredString_1_.getStringWidth(p_drawCenteredString_2_) / 2), (float)p_drawCenteredString_4_, p_drawCenteredString_5_);
	}
	
	public void drawGraffiti() {
		int startX = (this.width / 2) - (this.xSize / 2) + 11;
		int startY = (this.height / 2) - (this.ySize / 2) + 11;
		
		PixelGridDrawable grid = tileEntity.pixelGrid;
		int size = 8;
		if (grid.getSize() == 32) {
			size = 4;
		} else if (grid.getSize() == 64) {
			size = 2;
		} else if (grid.getSize() == 128) {
			size = 1;
		}
		
		for (int i = 0; i < grid.getSize(); i++) {
			for (int j = 0; j < grid.getSize(); j++) {
				this.fillGradient(startX + (j*size), startY + (i*size), startX + (j*size)+size, startY + (i*size)+size, grid.getPixelRGB(j, i), grid.getPixelRGB(j, i));
				//this.blit(startX + (j*size), startY + (i*size), 0, 248, size, size);
			}
		}
		
		for (int i = 0; i < tileEntity.textList.size(); i++) {
			TextDrawable text = tileEntity.textList.get(i);
			this.font.drawString(text.getText(), startX + (text.xPos()*2), startY + Math.abs((text.yPos()*2)-128), text.getCol());
		}
	}
}
