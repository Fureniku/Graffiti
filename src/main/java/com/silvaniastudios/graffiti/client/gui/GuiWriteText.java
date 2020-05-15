package com.silvaniastudios.graffiti.client.gui;

import com.silvaniastudios.graffiti.network.GraffitiPacketHandler;
import com.silvaniastudios.graffiti.network.WriteTextPacket;
import com.silvaniastudios.graffiti.tileentity.TileEntityGraffiti;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;

public class GuiWriteText extends Screen {
	
	private TileEntityGraffiti tileEntity;
	private int x;
	private int y;
	private int col;
	private String typedText = "";

	public GuiWriteText(TileEntityGraffiti te, int posX, int posY, int colour) {
		super(new TranslationTextComponent("graffiti.write"));
		x = posX;
		y = posY;
		col = colour;
		tileEntity = te;
	}

	@Override
	protected void init() {
		this.minecraft.keyboardListener.enableRepeatEvents(true);
		this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 120, 200, 20, I18n.format("gui.done"), (p_214266_1_) -> {
			this.onClose();
		}));
		//this.minecraft.setRenderViewEntity(cam);
	}
	
	@Override
	public void onClose() {
		this.tileEntity.markDirty();
		this.minecraft.displayGuiScreen((Screen)null);
		GraffitiPacketHandler.INSTANCE.sendToServer(new WriteTextPacket(typedText, tileEntity.getPos(), this.x, this.y, col));
		//this.minecraft.setRenderViewEntity(this.minecraft.player);
	}

	@Override
	public void render(int x, int y, float partialTick) {
		RenderHelper.setupGuiFlatDiffuseLighting();
		this.renderBackground();
		this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 40, 16777215);
		this.drawCenteredString(this.font, "Enter text", this.width / 2, this.height / 2 - 100, 16777215);
		this.drawCenteredString(this.font, typedText, this.width / 2 - ((this.x / 2)*4), this.height / 2 - ((this.y / 2)*4), col);
		
		super.render(x, y, partialTick);
	}
	
	@Override
	public boolean charTyped(char c, int p_charTyped_2_) {
		typedText += c;
		return true;
	}
}
