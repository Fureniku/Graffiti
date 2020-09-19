package com.silvaniastudios.graffiti.client.gui.submenu;

import com.silvaniastudios.graffiti.Graffiti;
import com.silvaniastudios.graffiti.client.gui.GuiCanvasEditorBase;
import com.silvaniastudios.graffiti.network.GraffitiPacketHandler;
import com.silvaniastudios.graffiti.network.ModifyGridPacket;
import com.silvaniastudios.graffiti.tileentity.ContainerGraffiti;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class CreatePixelGrid extends GuiCanvasEditorBase {
	
	Button size16;
	Button size32;
	Button size64;
	Button size128;
	Button cancel;
	
	private static final ResourceLocation TEXTURE = new ResourceLocation(Graffiti.MODID, "textures/gui/widgets.png");

	public CreatePixelGrid(ContainerGraffiti container, PlayerInventory inv, ITextComponent text) {
		super(container, inv, text);
		this.xSize = 118;
		this.ySize = 93;
	}
	
	@Override
	protected void init() {
		this.minecraft.keyboardListener.enableRepeatEvents(true);
		
		initButtons();

		this.addButton(size16);
		this.addButton(size32);
		this.addButton(size64);
		this.addButton(size128);
		this.addButton(cancel);
		
		super.init();
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		this.drawCenteredString(this.font, "Select new grid size", xSize / 2, 8, 4210752);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		this.minecraft.getTextureManager().bindTexture(TEXTURE);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.blit(i, j, 0, 0, this.xSize, this.ySize);
	}
	
	private void initButtons() {
		int startX = (this.width / 2) - (this.xSize / 2);
		int startY = (this.height / 2) - (this.ySize / 2);
		
		size16 = new Button(startX + 7, startY + 18, 50, 20, "16x16", (p_214266_1_) ->  {
			sendPacket(16);
		});
		
		size32 = new Button(startX + 61, startY + 18, 50, 20, "32x32", (p_214266_1_) ->  {
			sendPacket(32);
		});
		
		size64 = new Button(startX + 7, startY + 42, 50, 20, "64x64", (p_214266_1_) ->  {
			sendPacket(64);
		});
		
		size128 = new Button(startX + 61, startY + 42, 50, 20, "128x128", (p_214266_1_) ->  {
			sendPacket(128);
		});
		
		cancel = new Button(startX + 7, startY + 66, 104, 20, "Cancel", (p_214266_1_) ->  {
			this.minecraft.displayGuiScreen(new GuiCanvasEditorMain(this.container, this.playerInventory, this.title));
		});
	}

	private void sendPacket(int size) {
		GraffitiPacketHandler.INSTANCE.sendToServer(new ModifyGridPacket(size, (int) Math.round(255), false));
		this.minecraft.displayGuiScreen(new GuiCanvasEditorMain(this.container, this.playerInventory, this.title));
	}
}
