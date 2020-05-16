package com.silvaniastudios.graffiti.client.gui;

import com.silvaniastudios.graffiti.Graffiti;
import com.silvaniastudios.graffiti.network.GraffitiPacketHandler;
import com.silvaniastudios.graffiti.network.WriteTextPacket;
import com.silvaniastudios.graffiti.tileentity.TileEntityGraffiti;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

public class GuiWriteText extends Screen {
	
	private TileEntityGraffiti tileEntity;
	private int x;
	private int y;
	private int col;
	
	private float scale = 1;
	private int rotation;
	
	private String typedText = "";
	
	private int xSize = 256;
	private int ySize = 150;
	
	private static final ResourceLocation TEXTURE = new ResourceLocation(Graffiti.MODID, "textures/gui/drawing.png");

	public GuiWriteText(TileEntityGraffiti te, int posX, int posY, int colour, int rot) {
		super(new TranslationTextComponent("graffiti.write"));
		x = posX;
		y = posY;
		col = colour;
		rotation = rot;
		
		tileEntity = te;
	}

	@Override
	protected void init() {
		this.minecraft.keyboardListener.enableRepeatEvents(true);
		
		this.addButton(new Button(this.width / 2 + 16, this.height / 2 - 65, 20, 20, "-", (p_214266_1_) ->  {
			if (x > 0) this.x--;
		}));
		this.addButton(new Button(this.width / 2 + 101, this.height / 2 - 65, 20, 20, "+", (p_214266_1_) ->  {
			if (x < 64) this.x++;
		}));
		
		this.addButton(new Button(this.width / 2 + 16, this.height / 2 - 41, 20, 20, "-", (p_214266_1_) ->  {
			if (y < 64) this.y++;
		}));
		this.addButton(new Button(this.width / 2 + 101, this.height / 2 - 41, 20, 20, "+", (p_214266_1_) ->  {
			if (y > 0) this.y--;
		}));
		//this.minecraft.setRenderViewEntity(cam);
	}
	
	@Override
	public void onClose() {
		this.tileEntity.markDirty();
		this.minecraft.displayGuiScreen((Screen)null);
		if (!typedText.isEmpty()) {
			GraffitiPacketHandler.INSTANCE.sendToServer(new WriteTextPacket(typedText, tileEntity.getPos(), this.x, this.y, scale, col, rotation));
		}
		//this.minecraft.setRenderViewEntity(this.minecraft.player);
	}
	
	@Override
	public void renderBackground(int rb_1_) {
		//RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(TEXTURE);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.blit(i, j, 0, 0, this.xSize, this.ySize);
		//super.renderBackground(rb_1_);
	}

	@Override
	public void render(int x, int y, float partialTick) {
		//RenderHelper.setupGuiFlatDiffuseLighting();
		this.renderBackground();
		this.drawCenteredString(this.font, "X: " + this.x, this.width / 2 + 69, this.height / 2 - 59, 4210752);
		this.drawCenteredString(this.font, "Y: " + Math.abs(this.y-64), this.width / 2 + 69, this.height / 2 - 35, 4210752);
		this.drawString(this.font, typedText, this.width / 2 - (this.xSize / 2) + 7 + (this.x*2), this.height / 2 - (this.ySize / 2) + 7 + Math.abs((this.y*2)-128), col);
		
		super.render(x, y, partialTick);
	}
	
	@Override //we dont like shadows!
	public void drawCenteredString(FontRenderer p_drawCenteredString_1_, String p_drawCenteredString_2_, int p_drawCenteredString_3_, int p_drawCenteredString_4_, int p_drawCenteredString_5_) {
		p_drawCenteredString_1_.drawString(p_drawCenteredString_2_, (float)(p_drawCenteredString_3_ - p_drawCenteredString_1_.getStringWidth(p_drawCenteredString_2_) / 2), (float)p_drawCenteredString_4_, p_drawCenteredString_5_);
	}
	
	@Override
	public boolean charTyped(char c, int p_charTyped_2_) {
		//The server checks this. I'm watching you dodgy hackers... -_-
		if (typedText.length() < 45) {
			typedText += c;
		}
		return true;
	}
	
	public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
		if (p_keyPressed_1_ == 259) {
			typedText = typedText.substring(0, typedText.length() - 1);
		}
		
		return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
	}
}
