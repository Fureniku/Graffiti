package com.silvaniastudios.graffiti.client.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.silvaniastudios.graffiti.Graffiti;
import com.silvaniastudios.graffiti.client.gui.widget.ColorDisplayWidget;
import com.silvaniastudios.graffiti.client.gui.widget.ColorGridWidget;
import com.silvaniastudios.graffiti.network.GraffitiPacketHandler;
import com.silvaniastudios.graffiti.network.PenCustomColourPacket;
import com.silvaniastudios.graffiti.network.SetPenColourPacket;
import com.silvaniastudios.graffiti.util.EnumColours;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.widget.Slider;

public class GuiColourPicker extends Screen {
	
	private ItemStack penIS;
	
	private int xSize = 256;
	private int ySize = 240;
	
	ColorGridWidget grid;
	
	boolean draggingGrid = false;
	boolean draggingSlider = false;
	
	Slider rCol;
	Slider gCol;
	Slider bCol;
	
	Button toggleWriting;
	Button saveColour;
	
	boolean savingColour = false;
	boolean writingMode = true;
	
	EnumColours[] presetColours = new EnumColours[] {
			EnumColours.BLACK,
			EnumColours.BLOOD_RED,
			EnumColours.BROWN,
			EnumColours.DEEP_PURPLE,
			EnumColours.DARK_BLUE,
			EnumColours.DARK_AQUA,
			EnumColours.LEAF_GREEN,
			EnumColours.DARK_GRAY,
			EnumColours.DARK_RED,
			EnumColours.ORANGE,
			EnumColours.DARK_PURPLE,
			EnumColours.ROYAL_BLUE,
			EnumColours.SKY_BLUE,
			EnumColours.DARK_GREEN,
			EnumColours.GRAY,
			EnumColours.RED,
			EnumColours.GOLD,
			EnumColours.LIGHT_PURPLE,
			EnumColours.BLUE,
			EnumColours.AQUA,
			EnumColours.GREEN,
			EnumColours.WHITE,
			EnumColours.PINK,
			EnumColours.YELLOW,
			EnumColours.BABY_PINK,
			EnumColours.PASTEL_BLUE,
			EnumColours.ICE_BLUE,
			EnumColours.MINT_GREEN
	};
	
	int[] customColours = new int[21];
	
	private static final ResourceLocation TEXTURE = new ResourceLocation(Graffiti.MODID, "textures/gui/colour_picker.png");

	public GuiColourPicker(ItemStack pen) {
		super(new TranslationTextComponent("graffiti.colour_picker"));
		this.penIS = pen;
		
		
	}

	@Override
	protected void init() {
		this.minecraft.keyboardListener.enableRepeatEvents(true);
		int startX = (this.width / 2) - (this.xSize / 2);
		int startY = (this.height / 2) - (this.ySize / 2);
		
		grid = new ColorGridWidget(startX + 7, startY + 7, 4, new ColorDisplayWidget(startX + 163, startY + 7, 86, 34));
		
		rCol = initSlider(startX + 7, startY + 141, "Red: ");
		gCol = initSlider(startX + 7, startY + 165, "Green: ");
		bCol = initSlider(startX + 7, startY + 189, "Blue: ");
		
		if (penIS.hasTag()) {
			CompoundNBT penTag = penIS.getTag();
			if (penTag.contains("customColours")) {
				customColours = penTag.getIntArray("customColours");
			}
			if (penTag.contains("colour")) {
				grid.setColour(penTag.getInt("colour"));
			}
			if (penTag.contains("writing")) {
				writingMode = penTag.getBoolean("writing");
			}
		}
		
		toggleWriting = new Button(startX + 7, startY + 213, 119, 20, writingMode ? "Mode: Writing" : "Mode: Drawing", (p_214266_1_) -> {
			writingMode = !writingMode;
			toggleWriting.setMessage(writingMode ? "Mode: Writing" : "Mode: Drawing");
		});
		
		saveColour = new Button(startX + 130, startY + 213, 119, 20, "Save Colour", (p_214266_1_) -> {
			saveColour.setMessage("Choose colour slot...");
			savingColour = true;
			saveColour.active = false;
		});
		
		this.addButton(rCol);
		this.addButton(gCol);
		this.addButton(bCol);
		
		this.addButton(toggleWriting);
		this.addButton(saveColour);
		
		updateSlidersFromColour();
	}
	
	private Slider initSlider(int x, int y, String name) {
		Slider slider = new Slider(x + 10, y, 220, 20, name, "", 0, 255, 0, false, true, (p_214266_1_) -> {
			colourUpdate();
		});
		
		this.addButton(new Button(x, y, 10, 20, "-", (p_214266_1_) -> {
			slider.setValue(slider.getValueInt() - 1);
			slider.updateSlider();
			colourUpdate();
		}));
		
		this.addButton(new Button(x + 230, y, 10, 20, "+", (p_214266_1_) -> {
			slider.setValue(slider.getValueInt() + 1);
			slider.updateSlider();
			colourUpdate();
		}));
		
		return slider;
	}
	
	private void colourUpdate() {
		grid.setColour(new Color(rCol.getValueInt(), gCol.getValueInt(), bCol.getValueInt()).getRGB());
	}
	
	@Override
	public void onClose() {
		this.minecraft.displayGuiScreen((Screen)null);
		GraffitiPacketHandler.INSTANCE.sendToServer(new SetPenColourPacket(grid.getColor(), writingMode));
	}
	
	@Override
	public void renderBackground(int rb_1_) {
		this.minecraft.getTextureManager().bindTexture(TEXTURE);
		int startX = (this.width / 2) - (this.xSize / 2);
		int startY = (this.height / 2) - (this.ySize / 2);
		
		this.blit(startX, startY, 0, 0, this.xSize, this.ySize);
	}

	@Override
	public void render(int x, int y, float partialTick) {
		this.renderBackground();
		
		int startX = (this.width / 2) - (this.xSize / 2);
		int startY = (this.height / 2) - (this.ySize / 2);
		
		//Draw preset colour selector
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 4; j++) {
				int slot = i + (j * 7);
				int col = presetColours[slot].getMCCol();
				this.fillGradient(startX + 165 + (i*12), startY + 46 + (j*12), startX + 175 + (i*12), startY + 56 + (j*12), col, col);
			}
		}

		//Draw custom colour selector
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 3; j++) {
				int slot = i + (j * 7);
				int col = customColours[slot];
				this.fillGradient(startX + 165 + (i*12), startY + 102 + (j*12), startX + 175 + (i*12), startY + 112 + (j*12), col, col);
			}
		}
				
		grid.render(x, y, partialTick);
		
		if (x >= startX + 164 && x <= startX + 248 && y >= startY + 45 && y <= startY + 137) {
			int slot = getColourSlot(x, y);
			
			if (slot != -1) {
				if (slot <= 27) {
					List<String> list = new ArrayList<String>();
					Color c = presetColours[slot].getCol();
					list.add(presetColours[slot].name);
					list.add("Red: " + c.getRed());
					list.add("Green: " + c.getGreen());
					list.add("Blue: " + c.getBlue());
					this.renderTooltip(list, x, y);
				} else {
					List<String> list = new ArrayList<String>();
					Color c = new Color(customColours[slot-28]);
					list.add("Red: " + c.getRed());
					list.add("Green: " + c.getGreen());
					list.add("Blue: " + c.getBlue());
					this.renderTooltip(list, x, y);
				}
			}
		}
		
		if (draggingGrid) {
			updateSlidersFromGrid();
		}
		
		if (draggingSlider) {
			grid.setColour(new Color(rCol.getValueInt(), gCol.getValueInt(), bCol.getValueInt()).getRGB());
		}
		super.render(x, y, partialTick);
		
		
	}
	
	int sliderRLast = 0;
	int sliderGLast = 0;
	int sliderBLast = 0;
	
	private void updateSlidersFromGrid() {
		Color gridCol = grid.getSlidersColor();
		rCol.setValue(gridCol.getRed());
		gCol.setValue(gridCol.getGreen());
		bCol.setValue(gridCol.getBlue());
		
		rCol.updateSlider();
		gCol.updateSlider();
		bCol.updateSlider();
	}
	
	private void updateSlidersFromColour() {
		Color gridCol = new Color(grid.getColor());
		rCol.setValue(gridCol.getRed());
		gCol.setValue(gridCol.getGreen());
		bCol.setValue(gridCol.getBlue());
		
		rCol.updateSlider();
		gCol.updateSlider();
		bCol.updateSlider();
	}
	
	@Override
	public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
		grid.releaseMouse();
		rCol.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
		gCol.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
		bCol.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
		
		if (draggingGrid) {
			draggingGrid = false;
		}
		
		if (draggingSlider) {
			draggingSlider = false;
		}
		return super.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (grid.mouseClicked(mouseX, mouseY, button)) {
			draggingGrid = true;
		}
		
		int startX = (this.width / 2) - (this.xSize / 2);
		int startY = (this.height / 2) - (this.ySize / 2);
		
		//Check if sliders are clicked, coz sliders are dumb and cant do this themselves.
		if (mouseX >= startX + 17 && mouseX <= startX + 237) {
			if ((mouseY >= startY + 141 && mouseY <= startY + 161) || (mouseY >= startY + 165 && mouseY <= startY + 185) || (mouseY >= startY + 189 && mouseY <= startY + 209)) {
				draggingSlider = true;
			}
		}
		
		//Get clicked colour slot
		if (mouseX >= startX + 164 && mouseX <= startX + 248 && mouseY >= startY + 45 && mouseY <= startY + 137) {
			int slot = getColourSlot(mouseX, mouseY);
			
			if (slot != -1) {
				if (slot <= 27) {
					grid.setColour(presetColours[slot].getMCCol());
					updateSlidersFromColour();
				} else {
					if (savingColour) {
						customColours[slot-28] = grid.getColor();
						savingColour = false;
						saveColour.active = true;
						saveColour.setMessage("Save Colour");
						
						GraffitiPacketHandler.INSTANCE.sendToServer(new PenCustomColourPacket(grid.getColor(), slot-28));
					} else {
						if (customColours[slot-28] != 0) {
							grid.setColour(customColours[slot-28]);
							updateSlidersFromColour();
						}
					}
				}
			}
		}
		
		return super.mouseClicked(mouseX, mouseY, button);
	}
	
	private int getColourSlot(double mouseX, double mouseY) {
		//relative coordinates
		int rX = (int) Math.floor(mouseX - ((this.width / 2) - (this.xSize / 2) + 164));
		int rY = (int) Math.floor(mouseY - ((this.height / 2) - (this.ySize / 2) + 45));
		
		int slotX = 0;
		int slotY = 0;
		
		int varX = rX;

		while (varX > 12) {
			slotX++;
			varX -= 12;
		}

		//customs, else presets
		if (rY > 47) {
			if (rY < 56) {
				return -1;
			}
			rY = rY - 56;
			slotY = 4;
		}
		
		int varY = rY;
		while (varY > 12) {
			slotY++;
			varY -= 12;
		}
		
		return slotX + (slotY * 7);
	}
	
	@Override
	public boolean keyPressed(int keyID, int p_keyPressed_2_, int p_keyPressed_3_) {
		grid.keyPressed(keyID, p_keyPressed_2_, p_keyPressed_3_);
		return super.keyPressed(keyID, p_keyPressed_2_, p_keyPressed_3_);
	}
	
	@Override //we dont like shadows!
	public void drawCenteredString(FontRenderer p_drawCenteredString_1_, String p_drawCenteredString_2_, int p_drawCenteredString_3_, int p_drawCenteredString_4_, int p_drawCenteredString_5_) {
		p_drawCenteredString_1_.drawString(p_drawCenteredString_2_, (float)(p_drawCenteredString_3_ - p_drawCenteredString_1_.getStringWidth(p_drawCenteredString_2_) / 2), (float)p_drawCenteredString_4_, p_drawCenteredString_5_);
	}
}
