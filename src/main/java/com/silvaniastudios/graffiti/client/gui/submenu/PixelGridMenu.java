package com.silvaniastudios.graffiti.client.gui.submenu;

import java.util.ArrayList;
import java.util.List;

import com.silvaniastudios.graffiti.client.gui.GuiCanvasEditorBase;
import com.silvaniastudios.graffiti.drawables.PixelGridDrawable;
import com.silvaniastudios.graffiti.network.ModifyGridPacket;
import com.silvaniastudios.graffiti.network.GraffitiPacketHandler;
import com.silvaniastudios.graffiti.tileentity.ContainerGraffiti;
import com.silvaniastudios.graffiti.util.GraffitiUtils;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.gui.widget.Slider;

public class PixelGridMenu extends GuiCanvasEditorBase {
	
	int sizeId = 0;
	boolean resize = true;
	boolean deleteFlag = false;
	
	boolean showText = false;
	
	PixelGridDrawable grid;
	
	Button decreaseGridSizeBtn;
	Button increaseGridSizeBtn;
	Button resizeBtn;
	Button deleteBtn;
	Button yesBtn;
	Button noBtn;
	Button showHideTextBtn;
	Button resetTransparencyBtn;
	
	Button saveBtn;
	Button discardBtn;
	
	Slider transparencySldr;
	
	public PixelGridMenu(ContainerGraffiti container, PlayerInventory inv, ITextComponent text) {
		super(container, inv, text);
		grid = tileEntity.pixelGrid;
		sizeId = GraffitiUtils.sizeToId(grid.getSize());
	}
	
	private void initButtons() {
		int startX = (this.width / 2) - (this.xSize / 2);
		int startY = (this.height / 2) - (this.ySize / 2);
		
		decreaseGridSizeBtn = new Button(startX + 141, startY + 21, 20, 20, "-", (p_214266_1_) ->  {
			if (sizeId > 0) {
				sizeId--;
				increaseGridSizeBtn.active = true;
				validateGridScaling();
			}
			
			if (sizeId == 0) { decreaseGridSizeBtn.active = false; }
		});
		
		increaseGridSizeBtn = new Button(startX + 228, startY + 21, 20, 20, "+", (p_214266_1_) ->  {
			if (sizeId < 3) {
				sizeId++;
				decreaseGridSizeBtn.active = true;
				validateGridScaling();
			}
			
			if (sizeId == 3) { increaseGridSizeBtn.active = false; }
		});
		
		resizeBtn = new Button(startX + 141, startY + 45, 108, 20, "Resize: " + resize, (p_214266_1_) -> {
			resize = !resize;
			validateGridScaling();
			this.buttons.get(2).setMessage("Resize: " + resize);
		});
		
		deleteBtn = new Button(startX + 141, startY + 69, 108, 20, "Delete Grid", (p_214266_1_) -> {
			toggleDeleteMenu(true);
		});
		
		yesBtn = new Button(startX + 141, startY + 117, 44, 20, "Yes", (p_214266_1_) -> {
			if (deleteFlag) {
				GraffitiPacketHandler.INSTANCE.sendToServer(new ModifyGridPacket(tileEntity.getPos(), 0, (int) Math.round(transparencySldr.getValue()), resize));
				toggleDeleteMenu(false);
			}
		});
		
		noBtn = new Button(startX + 204, startY + 117, 44, 20, "No", (p_214266_1_) -> {
			if (deleteFlag) {
				toggleDeleteMenu(false);
			}
		});
		
		showHideTextBtn = new Button(startX + 7, startY + 141, 130, 20, "Show Text", (p_214266_1_) -> {
			showText = !showText;
			
			showHideTextBtn.setMessage(showText ? "Hide Text" : "Show Text");
		});
		
		transparencySldr = new Slider(startX + 7, startY + 181, 198, 20, "Transparency: ", "", 0, 255, 255, false, true, (p_214266_1_) -> {});
		
		resetTransparencyBtn = new Button(startX + 209, startY + 181, 40, 20, "Reset", (p_214266_1_) -> {
			transparencySldr.setValue(255);
			transparencySldr.updateSlider();
		});
		
		discardBtn = new Button(startX + 7, startY + 221, 110, 20, "Discard Changes", (p_214266_1_) -> {
			this.minecraft.displayGuiScreen(new GuiCanvasEditorMain(this.container, this.playerInventory, this.title));
		});
		
		saveBtn = new Button(startX + 139, startY + 221, 110, 20, "Save Changes", (p_214266_1_) -> {
			int size = 0;
			if (sizeId == 0) { size = 16; } else if (sizeId == 1) { size = 32; } else if (sizeId == 2) { size = 64; } else if (sizeId == 3) { size = 128; }
			
			GraffitiPacketHandler.INSTANCE.sendToServer(new ModifyGridPacket(tileEntity.getPos(), size, (int) Math.round(transparencySldr.getValue()), resize));
			
			this.minecraft.displayGuiScreen(new GuiCanvasEditorMain(this.container, this.playerInventory, this.title));
		});
	}
	
	protected void validateGridScaling() {
		grid = new PixelGridDrawable(GraffitiUtils.rescaleMultiple(tileEntity.pixelGrid.getPixelGrid(), GraffitiUtils.idToSize(sizeId), resize));
	}
	
	@Override
	protected void init() {
		this.minecraft.keyboardListener.enableRepeatEvents(true);
		
		initButtons();

		this.addButton(decreaseGridSizeBtn);
		this.addButton(increaseGridSizeBtn);
		this.addButton(resizeBtn);
		this.addButton(deleteBtn);
		this.addButton(yesBtn);
		this.addButton(noBtn);
		this.addButton(showHideTextBtn);
		
		this.addButton(resetTransparencyBtn);
		
		this.addButton(transparencySldr);
		
		this.addButton(saveBtn);
		this.addButton(discardBtn);
		
		if (sizeId == 0) { decreaseGridSizeBtn.active = false; }
		if (sizeId == 3) { increaseGridSizeBtn.active = false; }
		
		toggleDeleteMenu(false);
		super.init();
	}
	
	private void toggleDeleteMenu(boolean b) {
		deleteFlag = b;
		yesBtn.active = b;
		yesBtn.visible = b;
		noBtn.active = b;
		noBtn.visible = b;
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		
		drawGraffiti(tileEntity.pixelGrid, true, showText);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		this.drawCenteredString(this.font, "Pixel Grid", 197, 8, 4210752);
		
		if (sizeId == 0) { this.drawCenteredString(this.font, "16x16", 197, 27, 4210752); }
		if (sizeId == 1) { this.drawCenteredString(this.font, "32x32", 197, 27, 4210752); }
		if (sizeId == 2) { this.drawCenteredString(this.font, "64x64", 197, 27, 4210752); }
		if (sizeId == 3) { this.drawCenteredString(this.font, "128x128", 197, 27, 4210752); }
		
		if (deleteFlag) {
			this.drawCenteredString(this.font, "Are you sure?", 197, 94, 4210752);
			this.drawCenteredString(this.font, "This can't be undone!", 197, 104, 4210752);
		}

		if (resizeBtn.isHovered()) {
			List<String> list = new ArrayList<String>();
			list.add("Whether to resize the image when rescaling");
			list.add("True: the image will attempt to look the same as before it was rescaled.");
			list.add("False: the image will move to the top-left, or crop to only show the previous top-left.");
			this.renderTooltip(list, mouseX, mouseY);
		}
	}
}
