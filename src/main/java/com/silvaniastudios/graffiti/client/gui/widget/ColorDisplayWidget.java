package com.silvaniastudios.graffiti.client.gui.widget;

import net.minecraft.client.gui.widget.Widget;

public class ColorDisplayWidget extends Widget {

	int col = 16711680;

	public ColorDisplayWidget(int xIn, int yIn, int widthIn, int heightIn) {
		super(xIn, yIn, widthIn, heightIn, "");
	}
	
	public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
		if (this.visible) {
			//Selected colour preview window
			this.fillGradient(this.x + 1, this.y + 1, this.x + this.width - 1, this.y + this.height - 1, col, col);
		}
	}
	
	public void setColor(int col) {
		this.col = col;
	}

}
