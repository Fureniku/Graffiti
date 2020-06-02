package com.silvaniastudios.graffiti.client.gui;

import java.util.ArrayList;
import java.util.List;

import com.silvaniastudios.graffiti.file.FileExport;
import com.silvaniastudios.graffiti.network.AddGridPacket;
import com.silvaniastudios.graffiti.network.GraffitiPacketHandler;
import com.silvaniastudios.graffiti.network.LockEditPacket;
import com.silvaniastudios.graffiti.network.RemoveGridPacket;
import com.silvaniastudios.graffiti.network.SetPositionPacket;
import com.silvaniastudios.graffiti.tileentity.ContainerGraffiti;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class GuiCanvasEditorMain extends GuiCanvasEditorBase {

	int position;
	boolean locked;

	public GuiCanvasEditorMain(ContainerGraffiti container, PlayerInventory inv, ITextComponent text) {
		super(container, inv, text);
		position = tileEntity.getAlignment();
		locked = tileEntity.isLocked();
		
		this.xSize = 256;
		this.ySize = 256;
	}

	@Override
	protected void init() {
		this.minecraft.keyboardListener.enableRepeatEvents(true);
		this.addButton(new Button(this.width / 2 + 16, this.height / 2 - 118, 100, 20, "Position: " + position, (p_214266_1_) ->  {
			if (position < 2) {
				position++;
			} else {
				position = 0;
			}
			this.buttons.get(0).setMessage("Position: " + position);
			GraffitiPacketHandler.INSTANCE.sendToServer(new SetPositionPacket(tileEntity.getPos(), position));
		}));
		
		this.addButton(new Button(this.width / 2 + 16, this.height / 2 - 94, 100, 20, "Locked: " + locked, (p_214266_1_) ->  {
			locked = !locked;
			this.buttons.get(1).setMessage("Locked: " + locked);
			GraffitiPacketHandler.INSTANCE.sendToServer(new LockEditPacket(tileEntity.getPos(), locked));
		}));
		
		//2
		this.addButton(new Button(this.width / 2 + 16, this.height / 2 - 46, 100, 20, "Add Grid", (p_214266_1_) ->  {
			showScaleButtons();
		}));
		
		//3
		this.addButton(new Button(this.width / 2 + 16, this.height / 2 - 46, 100, 20, "Remove Grid", (p_214266_1_) ->  {
			if (!tileEntity.isLocked()) {
				GraffitiPacketHandler.INSTANCE.sendToServer(new RemoveGridPacket(tileEntity.getPos()));
			}
			addRemoveButtons(true);
		}));
		
		//4
		this.addButton(new Button(this.width / 2 + 16, this.height / 2 - 22, 20, 20, "16", (p_214266_1_) ->  {
			addGrid(16);
		}));
		
		//5
		this.addButton(new Button(this.width / 2 + 43, this.height / 2 - 22, 20, 20, "32", (p_214266_1_) ->  {
			addGrid(32);
		}));
		
		//6
		this.addButton(new Button(this.width / 2 + 69, this.height / 2 - 22, 20, 20, "64", (p_214266_1_) ->  {
			addGrid(64);
		}));
		
		//7
		this.addButton(new Button(this.width / 2 + 96, this.height / 2 - 22, 20, 20, "128", (p_214266_1_) ->  {
			addGrid(128);
		}));
		
		//8
		this.addButton(new Button(this.width / 2 + 16, this.height / 2 + 2, 100, 20, "Right-click Action: 0", (p_214266_1_) ->  {
			
		}));
		
		//9
		this.addButton(new Button(this.width / 2 - 116, this.height / 2 + 101, 100, 20, "Import", (p_214266_1_) ->  {
			
		}));
		
		//10
		this.addButton(new Button(this.width / 2 + 16, this.height / 2 + 101, 100, 20, "Export", (p_214266_1_) ->  {
			playerInventory.player.sendMessage(FileExport.createFile("export", tileEntity.pixelGrid, tileEntity.textList, tileEntity.getAlignment()));
		}));
		
		this.buttons.get(8).active = false;
		this.buttons.get(9).active = false;
		hideButtons();
		super.init();
	}
	
	private void addRemoveButtons(boolean showAdd) {
		this.buttons.get(2).active = showAdd;
		this.buttons.get(2).visible = showAdd;
		this.buttons.get(3).active = !showAdd;
		this.buttons.get(3).visible = !showAdd;
	}
	
	private void showScaleButtons() {
		this.buttons.get(4).active = true;
		this.buttons.get(4).visible = true;
		this.buttons.get(5).active = true;
		this.buttons.get(5).visible = true;
		this.buttons.get(6).active = true;
		this.buttons.get(6).visible = true;
		this.buttons.get(7).active = true;
		this.buttons.get(7).visible = true;
	}
	
	private void hideButtons() {
		addRemoveButtons(tileEntity.pixelGrid == null || tileEntity.pixelGrid.getSize() == 0);
		this.buttons.get(4).active = false;
		this.buttons.get(4).visible = false;
		this.buttons.get(5).active = false;
		this.buttons.get(5).visible = false;
		this.buttons.get(6).active = false;
		this.buttons.get(6).visible = false;
		this.buttons.get(7).active = false;
		this.buttons.get(7).visible = false;
	}
	
	private void addGrid(int size) {
		GraffitiPacketHandler.INSTANCE.sendToServer(new AddGridPacket(tileEntity.getPos(), size));
		hideButtons();
	}
	
	@Override
	public void onClose() {
		this.tileEntity.markDirty();
		this.minecraft.displayGuiScreen((Screen)null);
		//packet
	}
	
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		drawGraffiti();
		
		if (tileEntity.pixelGrid != null && tileEntity.pixelGrid.getSize() > 0) {
			this.drawCenteredString(this.font, "Pixel Grid: " + tileEntity.pixelGrid.getSize(), 194, 64, 4210752);
		} else {
			this.drawCenteredString(this.font, "No Pixel Grid", 194, 64, 4210752);
		}
		
		if (this.buttons.get(0).isHovered()) {
			List<String> list = new ArrayList<String>();
			list.add("Realign the position of the Graffiti");
			list.add("0: Graffiti is rendered inside its own blockspace, slightly indented.");
			list.add("1: Graffiti is offset into the blockspace it's mounted to, making it flush with surrounding blocks");
			list.add("2: Graffiti will attempt to align further into a block (Not yet implemeted!)");
			this.renderTooltip(list, mouseX, mouseY);
		}
		
		if (this.buttons.get(1).isHovered()) {
			List<String> list = new ArrayList<String>();
			list.add("Lock editing");
			list.add("Disables all editing for the block");
			list.add("Only the owner or OP can unlock it again");
			this.renderTooltip(list, mouseX, mouseY);
		}
		
		if (this.buttons.get(2).isHovered() && this.buttons.get(2).visible) {
			List<String> list = new ArrayList<String>();
			list.add("Add pixel grid");
			list.add("Blocks without a pixel grid will have less performance impact");
			list.add("Grids can be 16x - 128x");
			this.renderTooltip(list, mouseX, mouseY);
		}
		
		if (this.buttons.get(3).isHovered() && this.buttons.get(3).visible) {
			List<String> list = new ArrayList<String>();
			list.add("Remove pixel grid");
			list.add("Blocks without a pixel grid will have less performance impact");
			list.add("Grids can be 16x - 128x");
			this.renderTooltip(list, mouseX, mouseY);
		}
		
		if (this.buttons.get(8).isHovered()) {
			List<String> list = new ArrayList<String>();
			list.add("Right-click action");
			list.add("Not yet implemented!");
			list.add("0: Click-through (interact with block behind)");
			list.add("1: Examine full-screen");
			list.add("2: Open web link");
			list.add("Open to suggestions for more actions!");
			this.renderTooltip(list, mouseX, mouseY);
		}
		
		if (this.buttons.get(9).isHovered()) {
			List<String> list = new ArrayList<String>();
			list.add("Import art");
			list.add("Not yet implemented!");
			this.renderTooltip(list, mouseX, mouseY);
		}
		
		if (this.buttons.get(9).isHovered()) {
			List<String> list = new ArrayList<String>();
			list.add("Export art");
			list.add("Saved as a .json file in your minecraft/graffiti directory");
			this.renderTooltip(list, mouseX, mouseY);
		}
	}
}
