package com.silvaniastudios.graffiti.client.gui;

import java.util.ArrayList;
import java.util.List;

import com.silvaniastudios.graffiti.network.AddGridPacket;
import com.silvaniastudios.graffiti.network.GraffitiPacketHandler;
import com.silvaniastudios.graffiti.network.LockEditPacket;
import com.silvaniastudios.graffiti.network.RemoveGridPacket;
import com.silvaniastudios.graffiti.network.SetPositionPacket;
import com.silvaniastudios.graffiti.tileentity.TileEntityGraffiti;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;

public class GuiCanvasEditorMain extends GuiCanvasEditorBase {

	int position;
	boolean locked;

	public GuiCanvasEditorMain(TileEntityGraffiti te) {
		super(te);
		position = te.getAlignment();
		locked = te.isLocked();
	}

	@Override
	protected void init() {
		this.minecraft.keyboardListener.enableRepeatEvents(true);
		this.addButton(new Button(this.width / 2 + 16, this.height / 2 - 114, 100, 20, "Position: " + position, (p_214266_1_) ->  {
			if (position < 2) {
				position++;
			} else {
				position = 0;
			}
			this.buttons.get(0).setMessage("Position: " + position);
			GraffitiPacketHandler.INSTANCE.sendToServer(new SetPositionPacket(tileEntity.getPos(), position));
		}));
		
		this.addButton(new Button(this.width / 2 + 16, this.height / 2 - 84, 100, 20, "Locked: " + locked, (p_214266_1_) ->  {
			locked = !locked;
			this.buttons.get(1).setMessage("Locked: " + locked);
			GraffitiPacketHandler.INSTANCE.sendToServer(new LockEditPacket(tileEntity.getPos(), locked));
		}));
		
		//2
		this.addButton(new Button(this.width / 2 + 16, this.height / 2 - 24, 100, 20, "Add Grid", (p_214266_1_) ->  {
			showScaleButtons();
		}));
		
		//3
		this.addButton(new Button(this.width / 2 + 16, this.height / 2 - 24, 100, 20, "Remove Grid", (p_214266_1_) ->  {
			if (!tileEntity.isLocked()) {
				GraffitiPacketHandler.INSTANCE.sendToServer(new RemoveGridPacket(tileEntity.getPos()));
			}
			addRemoveButtons(true);
		}));
		
		//4
		this.addButton(new Button(this.width / 2 + 16, this.height / 2 + 6, 20, 20, "16x", (p_214266_1_) ->  {
			addGrid(16);
		}));
		
		//5
		this.addButton(new Button(this.width / 2 + 43, this.height / 2 + 6, 20, 20, "32x", (p_214266_1_) ->  {
			addGrid(32);
		}));
		
		//6
		this.addButton(new Button(this.width / 2 + 69, this.height / 2 + 6, 20, 20, "64x", (p_214266_1_) ->  {
			addGrid(64);
		}));
		
		//7
		this.addButton(new Button(this.width / 2 + 96, this.height / 2 + 6, 20, 20, "128x", (p_214266_1_) ->  {
			addGrid(128);
		}));
		
		hideButtons();
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
		addRemoveButtons(tileEntity.pixelGrid == null);
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

	@Override
	public void render(int x, int y, float partialTick) {
		//RenderHelper.setupGuiFlatDiffuseLighting();
		this.renderBackground();
		
		drawGraffiti();
		
		if (tileEntity.pixelGrid != null && tileEntity.pixelGrid.getSize() > 0) {
			this.drawCenteredString(this.font, "Pixel Grid: " + tileEntity.pixelGrid.getSize(), this.width / 2 + 66, this.height / 2 - 48, 4210752);
		} else {
			this.drawCenteredString(this.font, "No Pixel Grid", this.width / 2 + 66, this.height / 2 - 48, 4210752);
		}
		//this.drawString(this.font, typedText, this.width / 2 - (this.xSize / 2) + 7 + (this.x*2), this.height / 2 - (this.ySize / 2) + 7 + Math.abs((this.y*2)-128), col);
		
		super.render(x, y, partialTick);
		
		//if (x > this.width / 2 + 16 && y > this.height / 2 - 114 && x < this.width / 2 + 116 && y <  this.height / 2 - 94) {
		if (this.buttons.get(0).isHovered()) {
			List<String> list = new ArrayList<String>();
			list.add("Realign the position of the Graffiti");
			list.add("Not yet implemeted!");
			list.add("0: Graffiti is rendered inside its own blockspace, slightly indented.");
			list.add("1: Graffiti is offset into the blockspace it's mounted to, making it flush with surrounding blocks");
			list.add("2: Graffiti will attempt to align further into a block (e.g. to place on a slab");
			this.renderTooltip(list, x, y);
		}
		
		if (this.buttons.get(1).isHovered()) {
			List<String> list = new ArrayList<String>();
			list.add("Lock editing");
			list.add("Disables all editing for the block");
			list.add("Only the owner or OP can unlock it again");
			this.renderTooltip(list, x, y);
		}
		
		if (this.buttons.get(2).isHovered() || this.buttons.get(3).isHovered()) {
			List<String> list = new ArrayList<String>();
			list.add("Add/Remove pixel grid");
			list.add("Blocks without a pixel grid will have less performance impact");
			list.add("Grids can be 16x - 128x");
			this.renderTooltip(list, x, y);
		}
	}
}
