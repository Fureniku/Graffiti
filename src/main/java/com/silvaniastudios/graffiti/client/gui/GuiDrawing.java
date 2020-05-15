package com.silvaniastudios.graffiti.client.gui;

import com.silvaniastudios.graffiti.Graffiti;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.OptionSlider;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class GuiDrawing extends Screen {
	
	Button btn;
	OptionSlider slider_red;
	OptionSlider slider_green;
	OptionSlider slider_blue;
	
	TextFieldWidget text_red;
	TextFieldWidget text_green;
	TextFieldWidget text_blue;
	
	private final int guiWidth = 256;
	private final int guiHeight = 150;
	
	private static ResourceLocation texture = new ResourceLocation(Graffiti.MODID + ":textures/gui/drawing.png");

	protected GuiDrawing(ITextComponent titleIn) {
		super(titleIn);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void init() {
		
	}

}
