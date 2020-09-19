package com.silvaniastudios.graffiti.client.gui.submenu;

import java.io.File;
import java.util.ArrayList;

import com.silvaniastudios.graffiti.Graffiti;
import com.silvaniastudios.graffiti.client.gui.GuiCanvasEditorBase;
import com.silvaniastudios.graffiti.client.gui.widget.JsonListWidget;
import com.silvaniastudios.graffiti.drawables.PixelGridDrawable;
import com.silvaniastudios.graffiti.tileentity.ContainerGraffiti;
import com.silvaniastudios.graffiti.util.FileImport;
import com.silvaniastudios.graffiti.util.ImportedJsonObject;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;

public class ImportListMenu extends GuiCanvasEditorBase {
	
	private static final ResourceLocation LIST_TEXTURE = new ResourceLocation(Graffiti.MODID, "textures/gui/json_list.png");
	boolean showArt = false;
	
	PixelGridDrawable grid;
	
	Button backBtn;
	Button refreshBtn;
	Button openFolderBtn;
	
	JsonListWidget listWidget;
	File[] jsonList;
	
	public ImportListMenu(ContainerGraffiti container, PlayerInventory inv, ITextComponent text) {
		super(container, inv, text);
		grid = graffiti.pixelGrid;
		jsonList = new File("./graffiti/").listFiles();
	}
	
	private void initButtons() {
		int startX = (this.width / 2) - (this.xSize / 2);
		int startY = (this.height / 2) - (this.ySize / 2);
		
		ArrayList<ImportedJsonObject> jsons = populateJsonList();
		
		backBtn = new Button(startX + 7, startY + 221, 78, 20, "Back", (p_214266_1_) -> {
			this.minecraft.displayGuiScreen(new GuiCanvasEditorMain(this.container, this.playerInventory, this.title));
		});
		
		refreshBtn = new Button(startX + 89, startY + 221, 78, 20, "Refresh", (p_214266_1_) -> {
			listWidget.refresh(populateJsonList());
		});
		
		openFolderBtn = new Button(startX + 171, startY + 221, 78, 20, "Open Folder", (p_214266_1_) ->  {
			Util.getOSType().openFile(new File("./graffiti/"));
		});
		
		listWidget = new JsonListWidget(this.minecraft, 240, 209, startY + 7, startX + 8, 48, jsons, this);
	}
	
	private ArrayList<ImportedJsonObject> populateJsonList() {
		jsonList = new File("./graffiti/").listFiles();
		
		ArrayList<ImportedJsonObject> jsons = new ArrayList<ImportedJsonObject>();
		
		for (int i = 0; i < jsonList.length; i++) {
			ImportedJsonObject ijo = FileImport.importFileInfoBasic(jsonList[i]);
			if (ijo != null) {
				jsons.add(ijo);
			}
		}
		return jsons;
	}
	
	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
		listWidget.mouseScrolled(mouseX, mouseY, scrollAmount);
		return super.mouseScrolled(mouseX, mouseY, scrollAmount);
	}
	
	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		listWidget.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		listWidget.mouseClicked(mouseX, mouseY, button);
		return super.mouseClicked(mouseX, mouseY, button);
	}
	
	@Override
	protected void init() {
		this.minecraft.keyboardListener.enableRepeatEvents(true);
		
		initButtons();

		this.addButton(backBtn);
		this.addButton(refreshBtn);
		this.addButton(openFolderBtn);
		
		super.init();
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
	}
	
	@Override
	public void render(int mouseX, int mouseY, float partialTick) {
		super.render(mouseX, mouseY, partialTick);
		
		int startX = (this.width / 2) - (this.xSize / 2);
		int startY = (this.height / 2) - (this.ySize / 2);
		
		this.minecraft.getTextureManager().bindTexture(LIST_TEXTURE);
		
		this.blit(startX + 7, startY + 7, 0, 0, 242, 100);
		this.blit(startX + 7, startY + 107, 0, 50, 242, 110);
		listWidget.render(mouseX, mouseY, partialTick);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		
	}
	
	public void openEditText(int id) {
		ImportedJsonObject json = FileImport.importFile(jsonList[id], this.tileEntity, this.container.graffiti.getSide());
		this.minecraft.displayGuiScreen(new ImportJsonScreen(json, this.container, this.playerInventory, this.title));
	}
}
