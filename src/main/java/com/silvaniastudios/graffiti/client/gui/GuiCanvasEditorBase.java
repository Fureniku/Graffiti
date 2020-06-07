package com.silvaniastudios.graffiti.client.gui;

import org.lwjgl.opengl.GL11;

import com.silvaniastudios.graffiti.Graffiti;
import com.silvaniastudios.graffiti.drawables.PixelGridDrawable;
import com.silvaniastudios.graffiti.drawables.TextDrawable;
import com.silvaniastudios.graffiti.tileentity.ContainerGraffiti;
import com.silvaniastudios.graffiti.tileentity.TileEntityGraffiti;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class GuiCanvasEditorBase extends ContainerScreen<ContainerGraffiti> {
	
	protected TileEntityGraffiti tileEntity;
	
	private static final ResourceLocation TEXTURE = new ResourceLocation(Graffiti.MODID, "textures/gui/canvas_editor.png");

	public GuiCanvasEditorBase(ContainerGraffiti container, PlayerInventory inv, ITextComponent text) {
		super(container, inv, text);

		tileEntity = container.te;
		
		this.xSize = 256;
		this.ySize = 248;
	}
	
	@Override
	public void render(int x, int y, float partialTick) {
		this.renderBackground();
		super.render(x, y, partialTick);
		this.renderHoveredToolTip(x, y);
	}

	@Override
	protected void init() {
		this.minecraft.keyboardListener.enableRepeatEvents(true);
		super.init();
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		this.minecraft.getTextureManager().bindTexture(TEXTURE);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.blit(i, j, 0, 0, this.xSize, this.ySize);
		
		int startX = (this.width / 2) - (this.xSize / 2) + 8;
		int startY = (this.height / 2) - (this.ySize / 2) + 8;
		
		int texture = tileEntity.pixelGrid == null ? 0 : tileEntity.pixelGrid.getSize();
		if (tileEntity.pixelGrid != null) {
			if (tileEntity.pixelGrid.getSize() == 16)  texture = 0; 
			if (tileEntity.pixelGrid.getSize() == 128) texture = 96; 
		}
		
		this.blit(startX, startY, 8+texture, 8, 32, 128);
		this.blit(startX+32, startY, 8+texture, 8, 32, 128);
		this.blit(startX+64, startY, 8+texture, 8, 32, 128);
		this.blit(startX+96, startY, 8+texture, 8, 32, 128);
	}
	
	@Override //we dont like shadows!
	public void drawCenteredString(FontRenderer p_drawCenteredString_1_, String p_drawCenteredString_2_, int p_drawCenteredString_3_, int p_drawCenteredString_4_, int p_drawCenteredString_5_) {
		p_drawCenteredString_1_.drawString(p_drawCenteredString_2_, (float)(p_drawCenteredString_3_ - p_drawCenteredString_1_.getStringWidth(p_drawCenteredString_2_) / 2), (float)p_drawCenteredString_4_, p_drawCenteredString_5_);
	}
	
	public void drawGraffiti(PixelGridDrawable grid, boolean drawGrid, boolean drawText) {
		int startX = ((this.width - this.xSize) / 2) + 8;
		int startY = ((this.height - this.ySize) / 2) + 8;
		
		if (drawGrid && grid != null && grid.getSize() > 0) {
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
				}
			}
		}
		
		if (drawText) {
			for (int i = 0; i < tileEntity.textList.size(); i++) {
				TextDrawable text = tileEntity.textList.get(i);
				GL11.glPushMatrix();
				GL11.glScaled(2F, 2F, 2F);
				this.font.drawString(text.getDrawableText(), (startX + (text.xPos()*2))/2, (startY + Math.abs((text.yPos()*2)-128))/2, text.getCol());
				GL11.glScaled(0.5F, 0.5F, 0.5F);
				GL11.glPopMatrix();
			}
		}
		this.minecraft.getTextureManager().bindTexture(TEXTURE);
		this.blit(startX + 128, startY - 1, 136, 7, 116, 130); //mask over any text which extrudes out of the box
	}
}
