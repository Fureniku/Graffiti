package com.silvaniastudios.graffiti.client.gui.submenu;

import com.silvaniastudios.graffiti.Graffiti;
import com.silvaniastudios.graffiti.client.gui.GuiCanvasEditorBase;
import com.silvaniastudios.graffiti.client.gui.widget.ListWidget;
import com.silvaniastudios.graffiti.drawables.PixelGridDrawable;
import com.silvaniastudios.graffiti.tileentity.ContainerGraffiti;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class TextMenu extends GuiCanvasEditorBase {
	
	private static final ResourceLocation LIST_TEXTURE = new ResourceLocation(Graffiti.MODID, "textures/gui/text_menu_list.png");
	
	boolean showArt = false;
	
	PixelGridDrawable grid;
	
	Button addTextBtn;
	Button showHideArtBtn;
	Button cancel;
	
	ListWidget listWidget;
	
	public TextMenu(ContainerGraffiti container, PlayerInventory inv, ITextComponent text) {
		super(container, inv, text);
		grid = graffiti.pixelGrid;
	}
	
	private void initButtons() {
		int startX = (this.width / 2) - (this.xSize / 2);
		int startY = (this.height / 2) - (this.ySize / 2);
		
		addTextBtn = new Button(startX + 141, startY + 7, 108, 20, "Add Text", (p_214266_1_) -> {
			this.minecraft.displayGuiScreen(new CreateTextMenu(this.container, this.playerInventory, this.title));
		});
		
		showHideArtBtn = new Button(startX + 141, startY + 93, 108, 20, "Show Art", (p_214266_1_) -> {
			showArt = !showArt;
			
			showHideArtBtn.setMessage(showArt ? "Hide Art" : "Show Art");
		});
		
		cancel = new Button(startX + 141, startY + 117, 108, 20, "Cancel", (p_214266_1_) ->  {
			this.minecraft.displayGuiScreen(new GuiCanvasEditorMain(this.container, this.playerInventory, this.title));
		});
		
		listWidget = new ListWidget(this.minecraft, 240, 98, startY + 142, startX + 8, 18, graffiti.textList, this);
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

		this.addButton(addTextBtn);
		this.addButton(showHideArtBtn);
		this.addButton(cancel);
		
		super.init();
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		
		drawGraffiti(graffiti, showArt, true);
	}
	
	@Override
	public void render(int mouseX, int mouseY, float partialTick) {
		super.render(mouseX, mouseY, partialTick);
		
		int startX = (this.width / 2) - (this.xSize / 2);
		int startY = (this.height / 2) - (this.ySize / 2);
		
		this.minecraft.getTextureManager().bindTexture(LIST_TEXTURE);
		
		this.blit(startX + 7, startY + 141, 0, 0, 242, 100);
		listWidget.render(mouseX, mouseY, partialTick);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		
	}
	
	public void openEditText(int id) {
		this.minecraft.displayGuiScreen(new EditTextMenu(this.container, this.playerInventory, this.title, (short) id));
	}
}
