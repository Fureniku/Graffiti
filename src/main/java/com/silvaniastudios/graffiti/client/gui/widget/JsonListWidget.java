package com.silvaniastudios.graffiti.client.gui.widget;

import java.util.ArrayList;

import com.silvaniastudios.graffiti.Graffiti;
import com.silvaniastudios.graffiti.client.gui.submenu.ImportListMenu;
import com.silvaniastudios.graffiti.util.ImportedJsonObject;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.gui.ScrollPanel;

public class JsonListWidget extends ScrollPanel {
	private final Minecraft mc;
	private static final ResourceLocation TEXTURE = new ResourceLocation(Graffiti.MODID, "textures/gui/json_list.png");
	private ArrayList<ImportedJsonObject> jsons;

	private int entryHeight;
	private Screen parent;

	public JsonListWidget(Minecraft client, int width, int height, int top, int left, int entryHeight, ArrayList<ImportedJsonObject> jsons, Screen parent) {
		super(client, width, height, top, left);
		this.mc = client;
		this.entryHeight = entryHeight;
		this.jsons = jsons;
		this.parent = parent;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (isMouseOver(mouseX, mouseY)) {
			int element = getElementId((int) Math.floor(mouseX), (int) Math.floor(mouseY));
			if (element >= 0) {
				
				//TODO no not texts
				if (parent instanceof ImportListMenu) {
					ImportListMenu menu = (ImportListMenu) parent;
					
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
			return Math.abs((int) Math.ceil(Math.abs(mouseListY) / this.entryHeight) - jsons.size() + 1);
		}

		return -1;
	}
	
	public void refresh(ArrayList<ImportedJsonObject> jsons) {
		this.jsons = jsons;
	}

	@Override
	protected int getContentHeight() {
		return jsons.size() * entryHeight;
	}

	@Override
	protected void drawPanel(int entryRight, int relativeY, Tessellator tess, int mouseX, int mouseY) {
		int hoverElement = -1;
		if (isMouseOver(mouseX, mouseY)) {
			hoverElement = getElementId(mouseX, mouseY);
		}
		
		boolean showScrollBar = (this.getContentHeight() + border) - height > 0;
		
		for (int i = 0; i < jsons.size(); i++) {
			this.mc.getTextureManager().bindTexture(TEXTURE);
			int startX = entryRight - this.width;
			int startY = relativeY + (i * entryHeight) - 3;
			this.blit(startX, startY, 0, i == hoverElement ? 160 + this.entryHeight : 160, 229, this.entryHeight);
			if (showScrollBar) {
				this.blit(startX + 229, startY, 229, i == hoverElement ? 160 + this.entryHeight : 160, 4, this.entryHeight);
			} else {
				this.blit(startX + 229, startY, 223, i == hoverElement ? 160 + this.entryHeight : 160, 10, this.entryHeight);
			}
			this.mc.fontRenderer.drawString(jsons.get(i).getName(), startX + 46, startY + 5, 4210752);
			
			if (jsons.get(i).hasGrid()) {
				this.mc.fontRenderer.drawString("Grid: " + jsons.get(i).gridSize() + "x" + jsons.get(i).gridSize(), startX + 46, startY + 15, 4210752);
			} else {
				this.mc.fontRenderer.drawString("No Grid", startX + 46, startY + 15, 4210752);
			}
			this.mc.fontRenderer.drawString("Text Objects: " + jsons.get(i).getTextObjectCount(), startX + 145, startY + 15, 4210752);
			
			this.mc.fontRenderer.drawString("Artist: " + jsons.get(i).getArtist(), startX + 46, startY + 25, 4210752);
			this.mc.fontRenderer.drawString(jsons.get(i).getSource().equalsIgnoreCase("Game") ? "Made in-game" : "Made with Graffiti Editor", startX + 46, startY + 35, 4210752);
			
			drawMiniGraffiti(jsons.get(i).getGrid(), startX + 8, startY + 8);
		}
	}
	
	private void drawMiniGraffiti(int[][] grid, int startX, int startY) {
		if (grid.length > 0) {
			for (int i = 0; i < grid.length; i++) {
				for (int j = 0; j < grid[i].length; j++) {
					this.fillGradient(startX + (j), startY + (i), startX + (j)+1, startY + (i)+1, grid[j][i], grid[j][i]);
				}
			}
		}
	}
}