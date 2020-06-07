package com.silvaniastudios.graffiti.client.gui.submenu;

import java.util.ArrayList;
import java.util.List;

import com.silvaniastudios.graffiti.Graffiti;
import com.silvaniastudios.graffiti.client.gui.GuiCanvasEditorBase;
import com.silvaniastudios.graffiti.network.GraffitiPacketHandler;
import com.silvaniastudios.graffiti.network.SetPositionPacket;
import com.silvaniastudios.graffiti.tileentity.ContainerGraffiti;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.gui.widget.Slider;

public class EditPositionMenu extends GuiCanvasEditorBase {
	
	Button alignNormal;
	Button alignOffset;
	
	Button offsetIncreaseBtn;
	Button offsetDecreaseBtn;
	Button resetDefaultBtn;
	Button resetZeroBtn;
	
	Button saveBtn;
	Button discardBtn;
	
	Slider offsetSldr;
	
	boolean inBlockOffset = false;
	
	private static final ResourceLocation TEXTURE = new ResourceLocation(Graffiti.MODID, "textures/gui/widgets.png");

	public EditPositionMenu(ContainerGraffiti container, PlayerInventory inv, ITextComponent text) {
		super(container, inv, text);
		this.xSize = 218;
		this.ySize = 121;
	}
	
	@Override
	protected void init() {
		this.minecraft.keyboardListener.enableRepeatEvents(true);
		
		initButtons();

		this.addButton(alignNormal);
		this.addButton(alignOffset);
		this.addButton(offsetIncreaseBtn);
		this.addButton(offsetDecreaseBtn);
		this.addButton(resetDefaultBtn);
		this.addButton(resetZeroBtn);
		
		this.addButton(saveBtn);
		this.addButton(discardBtn);
		
		this.addButton(offsetSldr);
		
		super.init();
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		this.drawCenteredString(this.font, "Set positioning options", xSize / 2, 7, 4210752);
		
		if (alignNormal.isHovered()) {
			List<String> list = new ArrayList<String>();
			list.add("Align the graffiti so an offset of zero would be the edge of a full-sized block");
			this.renderTooltip(list, mouseX, mouseY);
		}
		
		if (alignOffset.isHovered()) {
			List<String> list = new ArrayList<String>();
			list.add("Align the graffiti so an offset of zero would leave it flush with the block behind it");
			list.add("Useful when graffiti is placed on non-full cubes, such as chests.");
			this.renderTooltip(list, mouseX, mouseY);
		}
		
		if (resetZeroBtn.isHovered()) {
			List<String> list = new ArrayList<String>();
			list.add("Reset offset to zero");
			list.add("May cause Z-fighting with blocks behind the graffiti.");
			this.renderTooltip(list, mouseX, mouseY);
		}
		
		if (resetDefaultBtn.isHovered()) {
			List<String> list = new ArrayList<String>();
			list.add("Reset offset to default setting (1/64th away from block)");
			this.renderTooltip(list, mouseX, mouseY);
		}
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
		
		alignNormal = new Button(startX + 7, startY + 18, 100, 20, "Normal Alignment", (p_214266_1_) -> {
			inBlockOffset = false;
			alignNormal.active = false;
			alignOffset.active = true;
		});
		
		alignOffset = new Button(startX + 111, startY + 18, 100, 20, "In-Block Offset", (p_214266_1_) -> {
			inBlockOffset = true;
			alignNormal.active = true;
			alignOffset.active = false;
		});
		
		
		offsetSldr = new Slider(startX + 17, startY + 42, 184, 20, "Offset: ", "", -0.5, 0.5, 0, true, true, (p_214266_1_) -> {});
		offsetSldr.precision = 7;
		
		offsetDecreaseBtn = new Button(startX + 7, startY + 42, 10, 20, "-", (p_214266_1_) -> {
			if (offsetSldr.getValue() > -0.5 + (1.0/128.0)) {
				offsetSldr.setValue(offsetSldr.getValue() - (1.0/128.0));
			} else {
				offsetSldr.setValue(-0.5);
			}
			
			offsetSldr.updateSlider();
		});
		
		offsetIncreaseBtn = new Button(startX + 201, startY + 42, 10, 20, "+", (p_214266_1_) -> {
			if (offsetSldr.getValue() < 0.5 - (1.0/128.0)) {
				offsetSldr.setValue(offsetSldr.getValue() + (1.0/128.0));
			} else {
				offsetSldr.setValue(0.5);
			}
			
			offsetSldr.updateSlider();
		});
		
		resetZeroBtn = new Button(startX + 7, startY + 66, 100, 20, "Reset to Zero", (p_214266_1_) -> {
			offsetSldr.setValue(0.0);
			offsetSldr.updateSlider();
		});
		
		resetDefaultBtn = new Button(startX + 111, startY + 66, 100, 20, "Reset to Default", (p_214266_1_) -> {
			offsetSldr.setValue(0.015625);
			offsetSldr.updateSlider();
		});
		
		discardBtn = new Button(startX + 7, startY + 94, 100, 20, "Discard Changes", (p_214266_1_) -> {
			this.minecraft.displayGuiScreen(new GuiCanvasEditorMain(this.container, this.playerInventory, this.title));
		});
		
		saveBtn = new Button(startX + 111, startY + 94, 100, 20, "Save Changes", (p_214266_1_) -> {
			GraffitiPacketHandler.INSTANCE.sendToServer(new SetPositionPacket(tileEntity.getPos(), inBlockOffset, offsetSldr.getValue()));
			
			this.minecraft.displayGuiScreen(new GuiCanvasEditorMain(this.container, this.playerInventory, this.title));
		});
		
		offsetSldr.setValue(tileEntity.getAlignment());
		offsetSldr.updateSlider();
		
		alignNormal.active = tileEntity.isOffsetIntoBlock();
		alignOffset.active = !tileEntity.isOffsetIntoBlock();
	}
}
