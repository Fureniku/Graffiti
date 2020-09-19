package com.silvaniastudios.graffiti.client.gui.submenu;

import com.silvaniastudios.graffiti.Graffiti;
import com.silvaniastudios.graffiti.client.gui.GuiCanvasEditorBase;
import com.silvaniastudios.graffiti.network.DeleteGraffitiPacket;
import com.silvaniastudios.graffiti.network.GraffitiPacketHandler;
import com.silvaniastudios.graffiti.tileentity.ContainerGraffiti;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class ConfirmDeleteMenu extends GuiCanvasEditorBase {
	
	Button confirmBtn;
	Button cancelBtn;
	
	private static final ResourceLocation TEXTURE = new ResourceLocation(Graffiti.MODID, "textures/gui/widgets.png");

	public ConfirmDeleteMenu(ContainerGraffiti container, PlayerInventory inv, ITextComponent text) {
		super(container, inv, text);
		this.xSize = 218;
		this.ySize = 121;
	}
	
	@Override
	protected void init() {
		this.minecraft.keyboardListener.enableRepeatEvents(true);
		
		initButtons();
		
		this.addButton(confirmBtn);
		this.addButton(cancelBtn);
		
		
		super.init();
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		this.drawCenteredString(this.font, "Are you sure you want", xSize / 2, 7, 4210752);
		this.drawCenteredString(this.font, "to delete this graffiti?", xSize / 2, 17, 4210752);
		
		this.drawCenteredString(this.font, "§lTHIS CANNOT BE UNDONE", xSize / 2, 30, 9830400);
	}
	
	@Override
	public void render(int mouseX, int mouseY, float p_render_3_) {
		super.render(mouseX, mouseY, p_render_3_);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		this.minecraft.getTextureManager().bindTexture(TEXTURE);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.blit(i, j, 0, 93, this.xSize, this.ySize);
	}
	
	private void initButtons() {
		int startX = (this.width / 2) - (this.xSize / 2);
		int startY = (this.height / 2) - (this.ySize / 2);
		
		
		cancelBtn = new Button(startX + 7, startY + 94, 100, 20, "Cancel", (p_214266_1_) -> {
			this.minecraft.displayGuiScreen(new GuiCanvasEditorMain(this.container, this.playerInventory, this.title));
		});
		
		confirmBtn = new Button(startX + 111, startY + 94, 100, 20, "Confirm", (p_214266_1_) -> {
			GraffitiPacketHandler.INSTANCE.sendToServer(new DeleteGraffitiPacket());
			if (!tileEntity.isLocked()) {
				tileEntity.assignGraffiti(null, graffiti.getSide());
			}
			
			this.minecraft.displayGuiScreen(null);
		});
	}
}
