package com.silvaniastudios.graffiti.client.gui.submenu;

import com.silvaniastudios.graffiti.client.gui.GuiCanvasEditorBase;
import com.silvaniastudios.graffiti.network.GraffitiPacketHandler;
import com.silvaniastudios.graffiti.network.LockEditPacket;
import com.silvaniastudios.graffiti.tileentity.ContainerGraffiti;
import com.silvaniastudios.graffiti.util.FileExport;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class GuiCanvasEditorMain extends GuiCanvasEditorBase {

	boolean locked;
	
	Button gridBtn;
	Button textBtn;
	Button shapesBtn;
	Button positionBtn;
	Button rightClickBtn;
	
	Button lockedBtn;
	
	Button importBtn;
	Button exportBtn;

	public GuiCanvasEditorMain(ContainerGraffiti container, PlayerInventory inv, ITextComponent text) {
		super(container, inv, text);
		locked = tileEntity.isLocked();
	}
	
	private void initButtons() {
		int startX = (this.width / 2) - (this.xSize / 2);
		int startY = (this.height / 2) - (this.ySize / 2);
		
		gridBtn = new Button(startX + 144, startY + 7, 105, 20, "Pixel Grid", (p_214266_1_) ->  {
			if (tileEntity.pixelGrid == null || tileEntity.pixelGrid.getSize() == 0) {
				this.minecraft.displayGuiScreen(new CreatePixelGrid(this.container, this.playerInventory, this.title));
			} else {
				this.minecraft.displayGuiScreen(new PixelGridMenu(this.container, this.playerInventory, this.title));
			}
		});
		
		textBtn = new Button(startX + 144, startY + 31, 105, 20, "Text", (p_214266_1_) ->  {
			this.minecraft.displayGuiScreen(new TextMenu(this.container, this.playerInventory, this.title));
		});

		shapesBtn = new Button(startX + 144, startY + 55, 105, 20, "Shapes", (p_214266_1_) ->  {
			
		});
		
		positionBtn = new Button(startX + 144, startY + 79, 105, 20, "Position", (p_214266_1_) ->  {
			this.minecraft.displayGuiScreen(new EditPositionMenu(this.container, this.playerInventory, this.title));
		});
		
		rightClickBtn = new Button(startX + 144, startY + 103, 105, 20, "Right-Click Action", (p_214266_1_) ->  {
			
		});
		
		lockedBtn = new Button(startX + 144, startY + 127, 105, 20, "Locked: " + locked, (p_214266_1_) ->  {
			locked = !locked;
			this.buttons.get(1).setMessage("Locked: " + locked);
			GraffitiPacketHandler.INSTANCE.sendToServer(new LockEditPacket(tileEntity.getPos(), locked));
		});
		
		importBtn = new Button(startX + 144, startY + 197, 105, 20, "Import", (p_214266_1_) ->  {
			
		});
		
		exportBtn = new Button(startX + 144, startY + 221, 105, 20, "Export", (p_214266_1_) ->  {
			playerInventory.player.sendMessage(FileExport.createFile("export", tileEntity.pixelGrid, tileEntity.textList, tileEntity.getAlignment()));
		});
		
		shapesBtn.active = false;
	}

	@Override
	protected void init() {
		this.minecraft.keyboardListener.enableRepeatEvents(true);
		initButtons();

		this.addButton(gridBtn);
		this.addButton(textBtn);
		this.addButton(shapesBtn);
		this.addButton(positionBtn);
		this.addButton(rightClickBtn);
		
		this.addButton(lockedBtn);
		
		this.addButton(importBtn);
		this.addButton(exportBtn);

		super.init();
	}
	
	@Override
	public void onClose() {
		this.tileEntity.markDirty();
		this.minecraft.displayGuiScreen((Screen)null);
		//packet
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		
		drawGraffiti(tileEntity.pixelGrid, true, true);
	}
	
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		if (tileEntity.pixelGrid != null && tileEntity.pixelGrid.getSize() > 0) {
			font.drawString("Pixel Grid: " + tileEntity.pixelGrid.getSize() + "x" + tileEntity.pixelGrid.getSize(), 10, 141, 4210752);
		} else {
			font.drawString("No Pixel Grid", 10, 141, 4210752);
		}
		
		font.drawString("Texts: " + tileEntity.textList.size(), 10, 151, 4210752);
		font.drawString("Shapes: ", 10, 161, 4210752);
		font.drawString("Position: ", 10, 171, 4210752);
		font.drawString("Right-click: ", 10, 181, 4210752);
		
		/*
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
		}*/
	}
}
