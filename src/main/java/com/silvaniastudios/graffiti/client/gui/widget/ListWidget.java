package com.silvaniastudios.graffiti.client.gui.widget;

import java.util.ArrayList;

import com.silvaniastudios.graffiti.Graffiti;
import com.silvaniastudios.graffiti.client.gui.submenu.TextMenu;
import com.silvaniastudios.graffiti.drawables.TextDrawable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.gui.ScrollPanel;

public class ListWidget extends ScrollPanel {
	private final Minecraft mc;
	private static final ResourceLocation TEXTURE = new ResourceLocation(Graffiti.MODID, "textures/gui/text_menu_list.png");
	private ArrayList<TextDrawable> texts;

	private int entryHeight;
	private Screen parent;

	public ListWidget(Minecraft client, int width, int height, int top, int left, int entryHeight, ArrayList<TextDrawable> texts, Screen parent) {
		super(client, width, height, top, left);
		this.mc = client;
		this.entryHeight = entryHeight;
		this.texts = texts;
		this.parent = parent;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (isMouseOver(mouseX, mouseY)) {
			int element = getElementId((int) Math.floor(mouseX), (int) Math.floor(mouseY));
			if (element >= 0) {
				if (parent instanceof TextMenu) {
					TextMenu menu = (TextMenu) parent;
					
					menu.openEditText(element);
				}
			}
		}
		
		return super.mouseClicked(mouseX, mouseY, button);
	}
	
	private int getElementId(int mouseX, int mouseY) {
		if (mouseX > this.left + this.width - 8) {
			return -1;
		}
		int mouseListY = ((int)mouseY) - this.top - this.getContentHeight() + (int)this.scrollDistance + 1;
		if (mouseListY < 0) {
			return Math.abs((int) Math.ceil(Math.abs(mouseListY) / this.entryHeight) - texts.size() + 1);
		}

		return -1;
	}

	@Override
	protected int getContentHeight() {
		return texts.size() * entryHeight;
	}

	@Override
	protected void drawPanel(int entryRight, int relativeY, Tessellator tess, int mouseX, int mouseY) {
		int hoverElement = -1;
		if (isMouseOver(mouseX, mouseY)) {
			hoverElement = getElementId(mouseX, mouseY);
		}
		
		boolean showScrollBar = (this.getContentHeight() + border) - height > 0;
		
		for (int i = 0; i < texts.size(); i++) {
			this.mc.getTextureManager().bindTexture(TEXTURE);
			int startX = entryRight - this.width;
			int startY = relativeY + (i * entryHeight) - 3;
			this.blit(startX, startY, 0, i == hoverElement ? 100 + this.entryHeight : 100, 229, this.entryHeight);
			if (showScrollBar) {
				this.blit(startX + 229, startY, 229, i == hoverElement ? 100 + this.entryHeight : 100, 4, this.entryHeight);
			} else {
				this.blit(startX + 229, startY, 223, i == hoverElement ? 100 + this.entryHeight : 100, 10, this.entryHeight);
			}
			this.mc.fontRenderer.drawString(texts.get(i).getDrawableText(), startX + 5, startY + 5, texts.get(i).getCol());
		}
	}
}