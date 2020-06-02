package com.silvaniastudios.graffiti.client.gui;

import org.lwjgl.opengl.GL11;

import com.silvaniastudios.graffiti.Graffiti;
import com.silvaniastudios.graffiti.drawables.PixelGridDrawable;
import com.silvaniastudios.graffiti.drawables.TextDrawable;
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
	
	private boolean bold = false;
	private boolean italic = false;
	private boolean underline = false;
	private boolean strikethrough = false;
	
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
		
		this.addButton(new Button(this.width / 2 + 16, this.height / 2 - 3, 50, 20, "Bold", (p_214266_1_) ->  {
			this.bold = !this.bold;
			this.buttons.get(4).setMessage(this.bold ? "§lBold" : "Bold");
		}));
		
		this.addButton(new Button(this.width / 2 + 71, this.height / 2 - 3, 50, 20, "Italic", (p_214266_1_) ->  {
			this.italic = !this.italic;
			this.buttons.get(5).setMessage(this.italic ? "§oItalic" : "Italic");
		}));
		
		this.addButton(new Button(this.width / 2 + 16, this.height / 2 + 21, 105, 20, "Underline", (p_214266_1_) ->  {
			this.underline = !this.underline;
			this.buttons.get(6).setMessage(this.underline ? "§nUnderline Enabled" : "Underline Disabled");
		}));
		
		this.addButton(new Button(this.width / 2 + 16, this.height / 2 + 45, 105, 20, "Strikeout", (p_214266_1_) ->  {
			this.strikethrough = !this.strikethrough;
			this.buttons.get(7).setMessage(this.strikethrough ? "§mStrikeout Enabled" : "Strikeout Disabled");
		}));
	}
	
	@Override
	public void onClose() {
		this.tileEntity.markDirty();
		this.minecraft.displayGuiScreen((Screen)null);
		if (!typedText.isEmpty()) {
			GraffitiPacketHandler.INSTANCE.sendToServer(new WriteTextPacket(typedText, tileEntity.getPos(), this.x, this.y, scale, col, rotation, buildFormatString(), 0));
		}
		//this.minecraft.setRenderViewEntity(this.minecraft.player);
	}
	
	public String buildFormatString() {
		String b = this.bold ? "§l" : "";
		String i = this.italic ? "§o" : "";
		String u = this.underline ? "§n" : "";
		String s = this.strikethrough? "§m" : "";
		
		return b + i + u + s;
	}
	
	@Override
	public void renderBackground(int rb_1_) {
		this.minecraft.getTextureManager().bindTexture(TEXTURE);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.blit(i, j, 0, 0, this.xSize, this.ySize);
		
		int startX = (this.width / 2) - (this.xSize / 2) + 11;
		int startY = (this.height / 2) - (this.ySize / 2) + 11;
		
		int texture = tileEntity.pixelGrid == null ? 0 : tileEntity.pixelGrid.getSize();
		if (tileEntity.pixelGrid != null) {
			if (tileEntity.pixelGrid.getSize() == 16)  texture = 0; 
			if (tileEntity.pixelGrid.getSize() == 128) texture = 96; 
		}
		
		this.blit(startX, startY, 11+texture, 11, 32, 128);
		this.blit(startX+32, startY, 11+texture, 11, 32, 128);
		this.blit(startX+64, startY, 11+texture, 11, 32, 128);
		this.blit(startX+96, startY, 11+texture, 11, 32, 128);

		drawGraffiti();
	}

	@Override
	public void render(int x, int y, float partialTick) {
		//RenderHelper.setupGuiFlatDiffuseLighting();
		this.renderBackground();
		this.drawCenteredString(this.font, "X: " + this.x, this.width / 2 + 69, this.height / 2 - 59, 4210752);
		this.drawCenteredString(this.font, "Y: " + Math.abs(this.y-64), this.width / 2 + 69, this.height / 2 - 35, 4210752);
		
		GL11.glPushMatrix();
		GL11.glScaled(2F, 2F, 2F);
		this.font.drawString(buildFormatString() + typedText, (this.width / 2 - (this.xSize / 2) + 12 + (this.x*2))/2, (this.height / 2 - (this.ySize / 2) + 12 + Math.abs((this.y*2)-128))/2, col);
		GL11.glScaled(0.5F, 0.5F, 0.5F);
		GL11.glPopMatrix();
		
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
		if (p_keyPressed_1_ == 259 && !typedText.isEmpty()) {
			typedText = typedText.substring(0, typedText.length() - 1);
		}
		
		return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
	}
	
	public void drawGraffiti() {
		int startX = (this.width / 2) - (this.xSize / 2) + 11;
		int startY = (this.height / 2) - (this.ySize / 2) + 11;
		
		PixelGridDrawable grid = tileEntity.pixelGrid;
		if (grid != null && grid.getSize() > 0) {
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
		for (int i = 0; i < tileEntity.textList.size(); i++) {
			TextDrawable text = tileEntity.textList.get(i);
			
			GL11.glPushMatrix();
			GL11.glScaled(2F, 2F, 2F);
			this.font.drawString(text.getDrawableText(), (startX + (text.xPos()*2))/2, (startY + Math.abs((text.yPos()*2)-128))/2, text.getCol());
			GL11.glScaled(0.5F, 0.5F, 0.5F);
			GL11.glPopMatrix();
		}
	}
}
