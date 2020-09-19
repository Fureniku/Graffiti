package com.silvaniastudios.graffiti.client.gui.submenu;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.silvaniastudios.graffiti.client.gui.GuiCanvasEditorBase;
import com.silvaniastudios.graffiti.drawables.PixelGridDrawable;
import com.silvaniastudios.graffiti.tileentity.ContainerGraffiti;
import com.silvaniastudios.graffiti.util.FileExport;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class ExportJsonScreen extends GuiCanvasEditorBase {
	
	PixelGridDrawable grid;
	
	Button exportBtn;
	Button cancelBtn;
	
	private TextFieldWidget name;
	ArrayList<String> fileNames = new ArrayList<String>();
	
	boolean fileNameTaken = false;
	
	//check box offset
	//cb right click action
	//cb right click url
	
	public ExportJsonScreen(ContainerGraffiti container, PlayerInventory inv, ITextComponent text) {
		super(container, inv, text);
		grid = graffiti.pixelGrid;
		
		this.ySize = 196;
		
		File[] files = new File("./graffiti/").listFiles();
		
		for (int i = 0; i < files.length; i++) {
			fileNames.add(files[i].getName());
		}
	}
	
	private void initButtons() {
		int startX = (this.width / 2) - (this.xSize / 2);
		int startY = (this.height / 2) - (this.ySize / 2);
		
		this.name = new TextFieldWidget(this.font, startX + 7, startY + 141, 242, 16, "");
		
		this.name.setCanLoseFocus(false);
		this.name.changeFocus(true);
		this.name.setTextColor(-1);
		this.name.setEnableBackgroundDrawing(true);
		this.name.setMaxStringLength(50);
		this.children.add(this.name);
		this.setFocusedDefault(this.name);
		
		LocalDateTime dateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");

		this.name.setText(playerInventory.player.getDisplayName().getString() + "_" + dateTime.format(formatter));

		cancelBtn = new Button(startX + 7, startY + 161, 110, 20, "Cancel", (p_214266_1_) -> {
			this.minecraft.displayGuiScreen(new GuiCanvasEditorMain(this.container, this.playerInventory, this.title));
		});
		
		exportBtn = new Button(startX + 139, startY + 161, 110, 20, "Export", (p_214266_1_) -> {
			playerInventory.player.sendMessage(FileExport.createFile(name.getText(), graffiti, playerInventory.player.getName().getString()));
			this.minecraft.displayGuiScreen(new GuiCanvasEditorMain(this.container, this.playerInventory, this.title));
		});
		
		exportBtn.active = false;
	}
	
	@Override
	protected void init() {
		this.minecraft.keyboardListener.enableRepeatEvents(true);
		
		initButtons();
		this.addButton(cancelBtn);
		this.addButton(exportBtn);
		super.init();
	}
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		
		this.blit(i, j+189, 0, 249, this.xSize, 7);	
		
		drawGraffiti(graffiti, true, true);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		this.drawCenteredString(this.font, "Pixel Grid", 195, 8, 4210752);

		if (exportBtn.isHovered() && fileNameTaken) {
			List<String> list = new ArrayList<String>();
			list.add("File already exists with this name.");
			list.add("Please choose a new name, or delete the old file and reload GUI.");
			this.renderTooltip(list, mouseX, mouseY);
		}
	}
	
	@Override
	public void render(int mouseX, int mouseY, float p_render_3_) {
		super.render(mouseX, mouseY, p_render_3_);
		
		if (fileNames.contains(name.getText() + ".json")) {
			fileNameTaken = true;
		} else {
			fileNameTaken = false;
		}
		
		if (!name.getText().isEmpty() && !fileNameTaken) {
			exportBtn.active = true;
		} else {
			exportBtn.active = false;
		}
		
		this.name.render(mouseX, mouseY, p_render_3_);
	}
	
	@Override
	public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
		if (p_keyPressed_1_ == 256) {
			this.minecraft.player.closeScreen();
		}

		return !this.name.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) && !this.name.canWrite() ? super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) : true;
	}
}
