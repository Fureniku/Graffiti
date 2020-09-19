package com.silvaniastudios.graffiti.client.gui.submenu;

import java.util.ArrayList;
import java.util.List;

import com.silvaniastudios.graffiti.client.gui.GuiCanvasEditorBase;
import com.silvaniastudios.graffiti.network.GraffitiPacketHandler;
import com.silvaniastudios.graffiti.network.LockEditPacket;
import com.silvaniastudios.graffiti.tileentity.ContainerGraffiti;
import com.silvaniastudios.graffiti.util.FileExport;
import com.silvaniastudios.graffiti.util.GraffitiUtils;

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
	Button deleteBtn;
	
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
			if (graffiti.pixelGrid == null || graffiti.pixelGrid.getSize() == 0) {
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
			this.minecraft.displayGuiScreen(new RightClickActionMenu(this.container, this.playerInventory, this.title));
		});
		
		lockedBtn = new Button(startX + 144, startY + 127, 105, 20, "Locked: " + locked, (p_214266_1_) ->  {
			locked = !locked;
			lockedBtn.setMessage("Locked: " + locked);
			GraffitiPacketHandler.INSTANCE.sendToServer(new LockEditPacket(locked));
		});
		
		importBtn = new Button(startX + 144, startY + 173, 105, 20, "Import", (p_214266_1_) ->  {
			this.minecraft.displayGuiScreen(new ImportListMenu(this.container, this.playerInventory, this.title));
		});
		exportBtn = new Button(startX + 144, startY + 197, 105, 20, "Export", (p_214266_1_) ->  {
			this.minecraft.displayGuiScreen(new ExportJsonScreen(this.container, this.playerInventory, this.title));
		});
		
		deleteBtn = new Button(startX + 144, startY + 221, 105, 20, "§4§lDelete", (p_214266_1_) ->  {
			this.minecraft.displayGuiScreen(new ConfirmDeleteMenu(this.container, this.playerInventory, this.title));
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
		this.addButton(deleteBtn);

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
		
		drawGraffiti(graffiti, true, true);
	}
	
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		if (graffiti.pixelGrid != null && graffiti.pixelGrid.getSize() > 0) {
			font.drawString("Pixel Grid: " + graffiti.pixelGrid.getSize() + "x" + graffiti.pixelGrid.getSize(), 7, 141, 4210752);
		} else {
			font.drawString("No Pixel Grid", 7, 141, 4210752);
		}
		
		font.drawString("Texts: " + graffiti.textList.size(), 7, 153, 4210752);
		font.drawString("Shapes: ", 7, 165, 4210752);
		font.drawString("Position: ", 7, 177, 4210752);
		
		font.drawString("Type: " + (graffiti.isOffsetIntoBlock() ? "In-Block Offset" : "Normal Alignment"), 11, 187, 4210752);
		font.drawString("Offset: " + Math.round(graffiti.getAlignment()*1000000)/1000000, 11, 197, 4210752);
		font.drawString("Right-click: ", 7, 209, 4210752);
		font.drawString("Action: " + GraffitiUtils.rightClickActionString(graffiti.getRightClickAction()), 11, 219, 4210752);
		if (graffiti.getRightClickAction() == 2) {
			font.drawString("BG Transparency: " + graffiti.getBackgroundTransparency(), 11, 229, 4210752);
		}
		if (graffiti.getRightClickAction() == 3) {
			String url = graffiti.getUrl();
			
			//Trim unneeded stuff from the start of the URL for display.
			//We only have limited space!
			if (url.contains("http://")) {
				url = url.substring(7);
			} else if (url.contains("https://")) {
				url = url.substring(8);
			}
			
			if (url.contains("www.")) {
				url = url.substring(4);
			}
			
			if (url.length() > 20) {
				font.drawString("URL: " + url.substring(0, 18) + "...", 11, 229, 4210752);
			} else {
				font.drawString("URL: " + url, 11, 229, 4210752);
			}
		}
	}
	
	@Override
	public void render(int mouseX, int mouseY, float p_render_3_) {
		super.render(mouseX, mouseY, p_render_3_);
		
		int startX = (this.width / 2) - (this.xSize / 2);
		int startY = (this.height / 2) - (this.ySize / 2);
		
		if (graffiti.getRightClickAction() == 3 && !graffiti.getUrl().isEmpty()) {
			if (mouseX >= startX + 11 && mouseX <= startX + 140 && mouseY >= startY + 229 && mouseY <= startY + 237) {
				this.renderTooltip(graffiti.getUrl(), mouseX, mouseY);
			}
		}
		
		if (gridBtn.isHovered()) {
			List<String> list = new ArrayList<String>();
			list.add("Add/Edit/Remove pixel grid");
			list.add("Blocks without a pixel grid will have less performance impact");
			list.add("Grids can be 16x - 128x");
			this.renderTooltip(list, mouseX, mouseY);
		}
		
		if (textBtn.isHovered()) {
			this.renderTooltip("Add/Edit/Remove text", mouseX, mouseY);
		}
		
		if (shapesBtn.isHovered()) {
			List<String> list = new ArrayList<String>();
			list.add("Add/Edit/Remove shapes");
			list.add("(Coming in 1.4!)");
			this.renderTooltip(list, mouseX, mouseY);
		}

		if (positionBtn.isHovered()) {
			List<String> list = new ArrayList<String>();
			list.add("Realign the position of the Graffiti");
			list.add("0: Graffiti is rendered inside its own blockspace, slightly indented.");
			list.add("1: Graffiti is offset into the blockspace it's mounted to, making it flush with surrounding blocks");
			list.add("2: Graffiti will attempt to align further into a block (Not yet implemeted!)");
			this.renderTooltip(list, mouseX, mouseY);
		}
		
		if (rightClickBtn.isHovered()) {
			List<String> list = new ArrayList<String>();
			list.add("Edit Right-click action");
			list.add("This changes what happens when a player right-clicks your art");
			this.renderTooltip(list, mouseX, mouseY);
		}
		
		if (lockedBtn.isHovered()) {
			List<String> list = new ArrayList<String>();
			list.add("Lock editing");
			list.add("Disables all editing for the block");
			list.add("Only the owner or OP can unlock it again");
			this.renderTooltip(list, mouseX, mouseY);
		}
		
		if (importBtn.isHovered()) {
			List<String> list = new ArrayList<String>();
			list.add("Import art from JSON");
			list.add("Art can be saved as an external file and imported into other worlds, or shared online.");
			this.renderTooltip(list, mouseX, mouseY);
		}
		
		if (exportBtn.isHovered()) {
			List<String> list = new ArrayList<String>();
			list.add("Export art to JSON");
			list.add("Art can be saved as an external file and imported into other worlds, or shared online.");
			this.renderTooltip(list, mouseX, mouseY);
		}
	}
}
