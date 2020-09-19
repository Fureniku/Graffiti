package com.silvaniastudios.graffiti.client.gui.widget;

import java.awt.Color;

import com.silvaniastudios.graffiti.Graffiti;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.ResourceLocation;

public class ColorGridWidget extends Widget {

	private boolean mouseDrag = false;
	private boolean showSelectorBoxes = true;
	
	private int colorSelectedPos = 0;
	private int colorGridPosX = 127;
	private int colorGridPosY = 0;
	
	private int gapSize; //The size of space between the brightness grid and colour selector
	
	private boolean colorGridFocused = false;
	int finalColour = -1;
	
	Minecraft minecraft = Minecraft.getInstance();
	
	ColorDisplayWidget display;
	
	private static final ResourceLocation TEXTURE = new ResourceLocation(Graffiti.MODID, "textures/gui/color_picker_widget.png");
	
	//Create and pass a colour display widget and the grid will handle its rendering and selecting its colour internally
	public ColorGridWidget(int xIn, int yIn, int gapSize, ColorDisplayWidget display) {
		super(xIn, yIn, 148 + gapSize, 130, "");
		this.gapSize = gapSize;
		this.display = display;
		this.finalColour = getSlidersColor().getRGB();
	}
	
	public ColorGridWidget(int xIn, int yIn, int gapSize) {
		this(xIn, yIn, gapSize, null);
	}
	

	public void render(int mouseX, int mouseY, float partialTick) {
		if (this.visible) {
			this.minecraft.getTextureManager().bindTexture(TEXTURE);
			
			//Background
			this.blit(this.x, this.y, 0, 0, 130, 130);
			
			//RGB selector
			this.blit(this.x + 130 + this.gapSize, this.y, 130, 0, 18, 130);
			
			//Colour brightness
			for (int i = 0; i < 128; i++) {
				int targetCol = getColorBrightness(Math.abs(i-128)).getRGB();
				this.fillGradient(this.x + 1 + i, this.y + 1, this.x + 2 + i, this.y + 128, targetCol, Color.BLACK.getRGB());
			}

			if (showSelectorBoxes) {
				this.blit(this.x + colorGridPosX, this.y + colorGridPosY, 0, 130, 3, 3); //color grid selected position
				this.blit(this.x + 130 + this.gapSize, this.y + colorSelectedPos, 3, 130, 18, 3); //colour selected position
			}
			
			if (display != null) {
				display.setColor(this.finalColour);
				display.render(mouseX, mouseY, partialTick);
			}
			
			if (mouseDrag) {
				if (mouseX >= this.x && mouseX <= this.x + this.width - 18 - this.gapSize && mouseY >= this.y && mouseY <= this.y + this.height) {
					colorGridPosX = (int) Math.floor(mouseX - this.x);
					colorGridPosY = (int) Math.floor(mouseY - this.y);
				}
				
				if (mouseX >= this.x + 130 + this.gapSize && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + this.height) {
					colorSelectedPos = (int) Math.floor(mouseY - this.y);
				}
			}
			
			if (colorGridPosX > 127) colorGridPosX = 127;
			if (colorGridPosY > 127) colorGridPosY = 127;
			if (colorSelectedPos > 127) colorSelectedPos = 127;
		}
	}
	
	//rgb refers to r, g OR b. Only pass one.
	public int getScaledPosition(int start, int end, int currentRGB, int targetRGB) {
		int totalSteps = end - start;
		float increments = (float) totalSteps / 255F;
		
		if (targetRGB == 255) {
			return (int) ((float) currentRGB * increments) + start;
		} else if (targetRGB == 0) {
			return end - (int) ((float) currentRGB * increments);
		}
		return 0;
	}
	
	@Override
	public void onClick(double mouseX, double mouseY) {
		mouseDrag = true;
		showSelectorBoxes = true;
	}
	
	public void releaseMouse() {
		if (mouseDrag) {
			this.finalColour = getSlidersColor().getRGB();
		}
		mouseDrag = false;
	}
	
	public void setColour(int colIn) {
		this.finalColour = colIn;
		showSelectorBoxes = false;
	}
	
	public Color getColorBrightness(int pos) {
		int selected = colorSelectedPos;
		int r = 0;
		int g = 0;
		int b = 0;
		
		if (selected < 21) {
			r = 255;
			
			g = interpolatedValue(selected, pos);
			g = constrain(g);
			
			b = constrain(pos*2);
		} else if (selected < 43) {
			r = interpolatedValue(Math.abs(selected - 42), pos);
			r = constrain(r);
			
			g = 255;
			
			b = constrain(pos*2);
		} else if (selected < 64) {
			r = constrain(pos*2);
			
			g = 255;
			
			b = interpolatedValue(selected - 43, pos);
			b = constrain(b);
		} else if (selected < 85) {
			r = constrain(pos*2);
			
			g = interpolatedValue(Math.abs(selected - 85), pos);
			g = constrain(g);
			
			b = 255;
		} else if (selected < 106) {
			r = interpolatedValue(selected - 85, pos);
			r = constrain(r);
			
			g = constrain(pos*2);
			
			b = 255;
		} else {
			r = 255;
			
			g = constrain(pos*2);
			
			b = interpolatedValue(Math.abs(selected - 127), pos);
			b = constrain(b);
		}
		
		//Round up values to account for weirdness on our small scale
		if (r > 250) r = 255;
		if (g > 250) g = 255;
		if (b > 250) b = 255;
		return new Color(r,g,b);
	}
	
	private int interpolatedValue(int sel, int pos) {
		int var = sel * 12;
		int remain = 255 - var;
		float add = (remain / 127F) * pos;
		return (int) (var + add);
	}

	public Color getSlidersColor() {
		Color brightness = getColorBrightness(Math.abs(colorGridPosX-128));
		int r = constrain(calculateDarkness(brightness.getRed()));
		int g = constrain(calculateDarkness(brightness.getGreen()));
		int b = constrain(calculateDarkness(brightness.getBlue()));
		
		return new Color(r,g,b);
	}
	
	public int getColor() {
		return this.finalColour;
	}
	
	//return my soul
	public int calculateDarkness(int in) {
		if (in > 0) {
			in = (int) ((in/127.0) * Math.abs(colorGridPosY-127)); 
		}
		return in;
	}
	
	//Ensure values are between 0-255, and if they're close, round up/down to account for the small scale we have for the colour picker
	private int constrain(int in) {
		if (in < 5) in = 0;
		return in > 255 ? 255 : in;
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (mouseX >= this.x && mouseX <= this.x + this.width - 18 - this.gapSize && mouseY >= this.y && mouseY <= this.y + this.height) {
			colorGridPosX = (int) Math.floor(mouseX - this.x);
			colorGridPosY = (int) Math.floor(mouseY - this.y);
			colorGridFocused = true;
			this.finalColour = getSlidersColor().getRGB();
		}
		
		if (mouseX >= this.x + 130 + this.gapSize && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + this.height) {
			colorSelectedPos = (int) Math.floor(mouseY - this.y);
			colorGridFocused = false;
			this.finalColour = getSlidersColor().getRGB();
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}
	
	@Override
	public boolean keyPressed(int keyID, int p_keyPressed_2_, int p_keyPressed_3_) {
		//265: up, 264: down, 263: left, 262: right
		if (colorGridFocused) {
			if (keyID == 265 && colorGridPosY > 0) {
				colorGridPosY--;
			}
			if (keyID == 264 && colorGridPosY < 127) {
				colorGridPosY++;
			}
			if (keyID == 263 && colorGridPosX > 0) {
				colorGridPosX--;
			}
			if (keyID == 262 && colorGridPosX < 127) {
				colorGridPosX++;
			}
			if (keyID >= 262 && keyID <= 265) {
				this.finalColour = getSlidersColor().getRGB();
			}
		} else {
			if (keyID == 265 && colorSelectedPos > 0) {
				colorSelectedPos--;
			}
			if (keyID == 264 && colorSelectedPos < 127) {
				colorSelectedPos++;
			}
			if (keyID == 264 || keyID == 265) {
				this.finalColour = getSlidersColor().getRGB();
			}
		}
		return super.keyPressed(keyID, p_keyPressed_2_, p_keyPressed_3_);
	}
}
