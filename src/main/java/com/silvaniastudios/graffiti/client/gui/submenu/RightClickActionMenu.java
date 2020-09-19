package com.silvaniastudios.graffiti.client.gui.submenu;

import java.util.ArrayList;
import java.util.List;

import com.silvaniastudios.graffiti.Graffiti;
import com.silvaniastudios.graffiti.client.gui.GuiCanvasEditorBase;
import com.silvaniastudios.graffiti.network.GraffitiPacketHandler;
import com.silvaniastudios.graffiti.network.RightClickActionPacket;
import com.silvaniastudios.graffiti.tileentity.ContainerGraffiti;

import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.gui.widget.Slider;

public class RightClickActionMenu extends GuiCanvasEditorBase {
	
	Button noActionBtn;
	Button clickThroughBtn;
	Button displayArtBtn;
	Button openUrlBtn;
	
	Button saveBtn;
	Button discardBtn;
	
	Slider backgroundTransparency;
	
	int rightClickActionId;
	
	private TextFieldWidget webUrl;
	
	private static final ResourceLocation TEXTURE = new ResourceLocation(Graffiti.MODID, "textures/gui/widgets.png");

	public RightClickActionMenu(ContainerGraffiti container, PlayerInventory inv, ITextComponent text) {
		super(container, inv, text);
		this.xSize = 218;
		this.ySize = 121;
		
		rightClickActionId = this.graffiti.getRightClickAction();
	}
	
	@Override
	protected void init() {
		this.minecraft.keyboardListener.enableRepeatEvents(true);
		
		initButtons();

		this.addButton(noActionBtn);
		this.addButton(clickThroughBtn);
		this.addButton(displayArtBtn);
		this.addButton(openUrlBtn);
		
		this.addButton(backgroundTransparency);
		
		this.addButton(saveBtn);
		this.addButton(discardBtn);
		
		super.init();
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		this.drawCenteredString(this.font, "Set right-click mode", xSize / 2, 7, 4210752);
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
		
		this.webUrl = new TextFieldWidget(this.font, startX + 7, startY + 66, 204, 16, "");
		
		this.webUrl.setCanLoseFocus(false);
		this.webUrl.changeFocus(true);
		this.webUrl.setTextColor(-1);
		this.webUrl.setEnableBackgroundDrawing(true);
		this.webUrl.setMaxStringLength(500);
		this.children.add(this.webUrl);
		this.setFocusedDefault(this.webUrl);
		
		backgroundTransparency = new Slider(startX + 7, startY + 66, 204, 20, "Background Transparency: ", "", 0, 255, 0, false, true, (p_214266_1_) -> {});
		
		clickThroughBtn = new Button(startX + 111, startY + 18, 100, 20, "Click-Through", (p_214266_1_) -> {
			clickThroughBtn.active = false;
			
			noActionBtn.active = true;
			displayArtBtn.active = true;
			openUrlBtn.active = true;
			
			rightClickActionId = 0;
			
			backgroundTransparency.active = false;
			backgroundTransparency.visible = false;
			
			webUrl.active = false;
			webUrl.visible = false;
		});
		
		noActionBtn = new Button(startX + 7, startY + 18, 100, 20, "No Action", (p_214266_1_) -> {
			noActionBtn.active = false;
			
			clickThroughBtn.active = true;
			displayArtBtn.active = true;
			openUrlBtn.active = true;
			
			rightClickActionId = 1;
			
			backgroundTransparency.active = false;
			backgroundTransparency.visible = false;
			
			webUrl.active = false;
			webUrl.visible = false;
		});
		
		displayArtBtn = new Button(startX + 7, startY + 42, 100, 20, "Display Art", (p_214266_1_) -> {
			displayArtBtn.active = false;
			
			noActionBtn.active = true;
			clickThroughBtn.active = true;
			openUrlBtn.active = true;
			
			rightClickActionId = 2;
			
			backgroundTransparency.active = true;
			backgroundTransparency.visible = true;
			
			webUrl.active = false;
			webUrl.visible = false;
		});
		
		openUrlBtn = new Button(startX + 111, startY + 42, 100, 20, "Open URL", (p_214266_1_) -> {
			openUrlBtn.active = false;
			
			noActionBtn.active = true;
			displayArtBtn.active = true;
			clickThroughBtn.active = true;
			
			rightClickActionId = 3;
			
			backgroundTransparency.active = false;
			backgroundTransparency.visible = false;
			
			webUrl.active = true;
			webUrl.visible = true;
		});
		
		backgroundTransparency.active = false;
		backgroundTransparency.visible = false;
		
		webUrl.active = false;
		webUrl.visible = false;
		
		if (rightClickActionId == 0) { clickThroughBtn.active = false; }
		if (rightClickActionId == 1) { noActionBtn.active = false; }
		if (rightClickActionId == 2) {
			displayArtBtn.active = false;
			backgroundTransparency.active = true;
			backgroundTransparency.visible = true;
		}
		
		if (rightClickActionId == 3) { 
			openUrlBtn.active = false;
			webUrl.setText(graffiti.getUrl());
			webUrl.active = true;
			webUrl.visible = true;
		}
		
		discardBtn = new Button(startX + 7, startY + 94, 100, 20, "Discard Changes", (p_214266_1_) -> {
			this.minecraft.displayGuiScreen(new GuiCanvasEditorMain(this.container, this.playerInventory, this.title));
		});
		
		saveBtn = new Button(startX + 111, startY + 94, 100, 20, "Save Changes", (p_214266_1_) -> {
			GraffitiPacketHandler.INSTANCE.sendToServer(new RightClickActionPacket(rightClickActionId, backgroundTransparency.getValueInt(), webUrl.getText()));
			
			this.minecraft.displayGuiScreen(new GuiCanvasEditorMain(this.container, this.playerInventory, this.title));
		});
	}
	
	@Override
	public void render(int mouseX, int mouseY, float p_render_3_) {
		super.render(mouseX, mouseY, p_render_3_);
		this.webUrl.render(mouseX, mouseY, p_render_3_);
		
		if (noActionBtn.isHovered()) {
			List<String> list = new ArrayList<String>();
			list.add("Right-click does nothing");
			this.renderTooltip(list, mouseX, mouseY);
		}
		
		if (clickThroughBtn.isHovered()) {
			List<String> list = new ArrayList<String>();
			list.add("Right-click will activate the block behind the graffiti");
			list.add("For example, opening a chest you've written on.");
			this.renderTooltip(list, mouseX, mouseY);
		}
		
		if (displayArtBtn.isHovered()) {
			List<String> list = new ArrayList<String>();
			list.add("Right-click shows the graffiti as a UI");
			this.renderTooltip(list, mouseX, mouseY);
		}
		
		if (openUrlBtn.isHovered()) {
			List<String> list = new ArrayList<String>();
			list.add("Right-click opens the prompt to open a custom web address");
			this.renderTooltip(list, mouseX, mouseY);
		}
	}
	
	@Override
	public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
		if (p_keyPressed_1_ == 256) {
			this.minecraft.player.closeScreen();
		}

		return !this.webUrl.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) && !this.webUrl.canWrite() ? super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) : true;
	}
	
	@Override
	public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
		backgroundTransparency.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
		return super.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
	}
}
