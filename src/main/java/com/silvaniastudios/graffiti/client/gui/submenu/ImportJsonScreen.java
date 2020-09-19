package com.silvaniastudios.graffiti.client.gui.submenu;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.silvaniastudios.graffiti.client.gui.GuiCanvasEditorBase;
import com.silvaniastudios.graffiti.drawables.CompleteGraffitiObject;
import com.silvaniastudios.graffiti.drawables.PixelGridDrawable;
import com.silvaniastudios.graffiti.drawables.TextDrawable;
import com.silvaniastudios.graffiti.network.ClearGraffitiPacket;
import com.silvaniastudios.graffiti.network.GraffitiPacketHandler;
import com.silvaniastudios.graffiti.tileentity.ContainerGraffiti;
import com.silvaniastudios.graffiti.util.FileImport;
import com.silvaniastudios.graffiti.util.ImportedJsonObject;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class ImportJsonScreen extends GuiCanvasEditorBase {
	
	ImportedJsonObject json;
	Button backBtn;
	Button addDataBtn;
	Button confirmBtn;
	Button clearDataBtn;

	public ImportJsonScreen(ImportedJsonObject json, ContainerGraffiti container, PlayerInventory inv, ITextComponent text) {
		super(container, inv, text);
		this.json = json;
	}
	

	private void initButtons() {
		int startX = (this.width / 2) - (this.xSize / 2);
		int startY = (this.height / 2) - (this.ySize / 2);
		
		backBtn = new Button(startX + 141, startY + 7, 108, 20, "Back", (p_214266_1_) -> {
			this.minecraft.displayGuiScreen(new ImportListMenu(this.container, this.playerInventory, this.title));
		});
		
		addDataBtn = new Button(startX + 141, startY + 69, 108, 20, "Add To Old Data", (p_214266_1_) -> {
			addDataBtn.active = false;
			clearDataBtn.active = true;
			confirmBtn.active = true;
		});
		
		clearDataBtn = new Button(startX + 141, startY + 93, 108, 20, "Clear Old Data", (p_214266_1_) -> {
			addDataBtn.active = true;
			clearDataBtn.active = false;
			confirmBtn.active = true;
		});
		
		confirmBtn = new Button(startX + 141, startY + 117, 108, 20, "Confirm Import", (p_214266_1_) -> {
			FileImport.sendDataToServer(json.getGraffiti(), !clearDataBtn.active);
			
			this.minecraft.displayGuiScreen(new ImportListMenu(this.container, this.playerInventory, this.title));
		});
		
		confirmBtn.active = false;
		
		if (!container.graffiti.hasPixelGrid() && container.graffiti.textList.isEmpty()) {
			addDataBtn.active = false;
			clearDataBtn.active = false;
			confirmBtn.active = true;
		}
	}
	
	@Override
	protected void init() {
		this.minecraft.keyboardListener.enableRepeatEvents(true);
		
		if (json.getGraffiti() != null) {
			graffiti = json.getGraffiti();
		}
		
		initButtons();
		this.addButton(backBtn);
		this.addButton(confirmBtn);
		this.addButton(addDataBtn);
		this.addButton(clearDataBtn);
		
		super.init();
	}
	
	@Override
	public void render(int mouseX, int mouseY, float p_render_3_) {
		super.render(mouseX, mouseY, p_render_3_);
		
		if (addDataBtn.isHovered()) {
			List<String> list = new ArrayList<String>();
			list.add("Add new data over existing graffiti data");
			this.renderTooltip(list, mouseX, mouseY);
		}
		
		if (clearDataBtn.isHovered()) {
			List<String> list = new ArrayList<String>();
			list.add("Delete all current graffiti data.");
			list.add("§lTHIS CANNOT BE UNDONE.");
			this.renderTooltip(list, mouseX, mouseY);
		}
		
		if (confirmBtn.isHovered() && !confirmBtn.active) {
			List<String> list = new ArrayList<String>();
			list.add("Please select what to do with existing data before continuing");
			this.renderTooltip(list, mouseX, mouseY);
		}
		
		if (confirmBtn.isHovered() && confirmBtn.active && !clearDataBtn.active) {
			List<String> list = new ArrayList<String>();
			list.add("ALL EXISTING GRAFFITI DATA WILL BE OVERWRITTEN");
			list.add("THIS IS YOUR LAST WARNING! Click to continue");
			this.renderTooltip(list, mouseX, mouseY);
		}
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		
		this.blit(i, j+189, 0, 249, this.xSize, 7);	
		
		if (json.getGraffiti() != null) {
			drawGraffiti(json.getGraffiti());
		}
	}
	
	public void drawGraffiti(CompleteGraffitiObject graffiti) {
		int startX = ((this.width - this.xSize) / 2) + 8;
		int startY = ((this.height - this.ySize) / 2) + 8;
		
		int texture = graffiti.pixelGrid == null ? 0 : graffiti.pixelGrid.getSize();
		if (graffiti.pixelGrid != null) {
			if (graffiti.pixelGrid.getSize() == 16)  texture = 0; 
			if (graffiti.pixelGrid.getSize() == 128) texture = 96; 
		}
		
		this.blit(startX, startY, 8+texture, 8, 32, 128);
		this.blit(startX+32, startY, 8+texture, 8, 32, 128);
		this.blit(startX+64, startY, 8+texture, 8, 32, 128);
		this.blit(startX+96, startY, 8+texture, 8, 32, 128);
		
		PixelGridDrawable grid = graffiti.pixelGrid;
		
		if (grid.getSize() > 0) {
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

		for (int i = 0; i < graffiti.textList.size(); i++) {
			
			TextDrawable text = graffiti.textList.get(i);
			GL11.glPushMatrix();
			GL11.glScaled(2F, 2F, 2F);
			this.font.drawString(text.getDrawableText(), (startX + (text.xPos()*2))/2, (startY + Math.abs((text.yPos()*2)-128))/2, text.getCol());
			GL11.glScaled(0.5F, 0.5F, 0.5F);
			GL11.glPopMatrix();
		}
		//this.minecraft.getTextureManager().bindTexture(TEXTURE);
		//this.blit(startX + 128, startY - 1, 136, 7, 116, 130); //mask over any text which extrudes out of the box
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		this.font.drawString("Imported Art Data", 7, 140, 4210752);
		this.font.drawString("Name: " + json.getName(), 7, 152, 4210752);
		this.font.drawString("Artist: " + json.getArtist(), 7, 164, 4210752);
		this.font.drawString("Source: " + json.getSource() + (json.getSource().equalsIgnoreCase("Game") ? " (Version " + json.getSourceVersion() + ")" : ""), 7, 176, 4210752);
		this.font.drawString("Has Grid?: " + json.hasGrid() + (json.hasGrid() ? " (" + graffiti.pixelGrid.getSize() + "x" + graffiti.pixelGrid.getSize() + ")" : ""), 7, 188, 4210752);
		this.font.drawString("Texts: " + json.getTextObjectCount(), 7, 200, 4210752);
	}
}
