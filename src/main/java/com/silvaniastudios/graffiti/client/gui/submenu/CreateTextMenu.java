package com.silvaniastudios.graffiti.client.gui.submenu;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import com.silvaniastudios.graffiti.Graffiti;
import com.silvaniastudios.graffiti.client.gui.GuiCanvasEditorBase;
import com.silvaniastudios.graffiti.items.BasicPenItem;
import com.silvaniastudios.graffiti.network.GraffitiPacketHandler;
import com.silvaniastudios.graffiti.network.WriteTextPacket;
import com.silvaniastudios.graffiti.tileentity.ContainerGraffiti;

import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.gui.widget.Slider;

public class CreateTextMenu extends GuiCanvasEditorBase {
	
	ArrayList<ItemStack> penList;
	
	Button penLeftBtn;
	Button penRightBtn;
	
	Button showHideText;
	Button showHidePixels;
	Button saveContinueBtn;
	Button saveCloseBtn;
	Button cancelBtn;
	
	Button increaseXBtn;
	Button decreaseXBtn;
	Button increaseYBtn;
	Button decreaseYBtn;
	
	Button boldBtn;
	Button italicBtn;
	Button underlineBtn;
	Button strikeBtn;
	
	Slider posXSlider;
	Slider posYSlider;
	
	int buttonOffset;
	int selectedPen = -1;
	
	private TextFieldWidget typedText;
	
	private boolean bold = false;
	private boolean italic = false;
	private boolean underline = false;
	private boolean strikethrough = false;
	
	private boolean showText = true;
	private boolean showPixels = true;
	
	public CreateTextMenu(ContainerGraffiti container, PlayerInventory inv, ITextComponent text) {
		super(container, inv, text);
		this.ySize = 238;
	}

	private static final ResourceLocation TEXTURE = new ResourceLocation(Graffiti.MODID, "textures/gui/create_text.png");
	
	@Override
	protected void init() {
		this.minecraft.keyboardListener.enableRepeatEvents(true);
		
		penList = getPens();
		
		initButtons();

		if (penList.size() > 12) { 
			this.addButton(penLeftBtn);
			this.addButton(penRightBtn);
		}
		
		this.addButton(increaseXBtn);
		this.addButton(decreaseXBtn);
		this.addButton(increaseYBtn);
		this.addButton(decreaseYBtn);
		
		this.addButton(boldBtn);
		this.addButton(italicBtn);
		this.addButton(underlineBtn);
		this.addButton(strikeBtn);
		
		this.addButton(posXSlider);
		this.addButton(posYSlider);
		
		this.addButton(showHideText);
		this.addButton(showHidePixels);
		this.addButton(saveContinueBtn);
		this.addButton(saveCloseBtn);
		this.addButton(cancelBtn);
		
		super.init();
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		int startX = (this.width - this.xSize) / 2;
		int startY = (this.height - this.ySize) / 2;
		if (penList.size() > 0) {
			int startPoint = 8;
			
			if (penList.size() >= 13) {
				startPoint = 21;
			}
			
			for (int i = 0; i < penList.size(); i++) {
				if (i < 12) {
					this.minecraft.getItemRenderer().renderItemAndEffectIntoGUI(penList.get(i+buttonOffset), startPoint + (i * 18), 142);
					if (mouseX > (startX + startPoint + (i * 18)) && mouseX < (startX + startPoint + 18 + (i * 18)) && mouseY > (startY + 142) && mouseY < (startY + 160)) {
						this.fillGradient(startPoint + (i * 18), 142, startPoint + 16 + (i * 18), 158, -2130706433, -2130706433);
					}
				}
			}
		}
		
		if (penList.isEmpty()) {
			this.drawCenteredString(this.font, "You need at least one pen in your", 128, 140, 4210752);
			this.drawCenteredString(this.font, "inventory to add new text", 128, 148, 4210752);
		}
		
		if (selectedPen >= 0 && !typedText.getText().isEmpty()) {
			BasicPenItem pen = (BasicPenItem) penList.get(selectedPen).getItem();
			
			GL11.glPushMatrix();
			GL11.glScaled(2F, 2F, 2F);
			this.font.drawString(buildFormatString() + typedText.getText(), 4 + (int) Math.floor(posXSlider.getValue()), 4 + (int) Math.floor(posYSlider.getValue()), pen.getColor(penList.get(selectedPen)).getRGB());
			GL11.glScaled(0.5F, 0.5F, 0.5F);
			GL11.glPopMatrix();
			
			saveContinueBtn.active = true;
			saveCloseBtn.active = true;
		} else {
			saveContinueBtn.active = false;
			saveCloseBtn.active = false;
		}
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		this.minecraft.getTextureManager().bindTexture(TEXTURE);
		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;
		this.blit(x, y, 0, 0, this.xSize, this.ySize);
		
		if (penList.size() > 0) {
			if (penList.size() < 13) {
				for (int i = 0; i < penList.size(); i++) {
					this.blit(x + 7 + (i * 18), y + 141, 0, 238, 18, 18);
					
					if (selectedPen == i) {
						this.blit(x + 7 + (i * 18), y + 141, 18, 238, 18, 18);
					}
				}
				
				
			} else {
				for (int i = 0; i < 12; i++) {
					this.blit(x + 20 + (i * 18), y + 141, 0, 238, 18, 18);
					
					if (selectedPen - buttonOffset == i) {
						this.blit(x + 20 + (i * 18), y + 141, 18, 238, 18, 18);
					}
				}
				
				
			}
		}
		
		if (selectedPen >= 0) {
			BasicPenItem pen = (BasicPenItem) penList.get(selectedPen).getItem();
			this.fillGradient(x + 8, y + 164, x + 40, y + 230, pen.getColor(penList.get(selectedPen)).getRGB(), pen.getColor(penList.get(selectedPen)).getRGB());
		}
		
		drawGraffiti(graffiti, showPixels, showText);
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		int startX = (this.width - this.xSize) / 2;
		int startY = (this.height - this.ySize) / 2;
		int slotClicked = getClickedSlotId(startX, startY, mouseX, mouseY);
		
		if (slotClicked >= 0) {
			selectedPen = slotClicked;
			BasicPenItem pen = (BasicPenItem) penList.get(selectedPen).getItem();
			this.typedText.setTextColor(pen.getColor(penList.get(selectedPen)).getRGB());
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}
	
	private int getClickedSlotId(int startX, int startY, double mouseX, double mouseY) {
		if (penList.size() > 0) {
			int startPoint = 8;
			
			if (penList.size() >= 13) {
				startPoint = 21;
			}
			
			for (int i = 0; i < penList.size(); i++) {
				if (i < 12) {
					if (mouseX > (startX + startPoint + (i * 18)) && mouseX < (startX + startPoint + 18 + (i * 18)) && mouseY > (startY + 142) && mouseY < (startY + 160)) {
						return i + buttonOffset;
					}
				}
			}
		}
		
		return -1;
	}
	
	private ArrayList<ItemStack> getPens() {
		ArrayList<ItemStack> pens = new ArrayList<ItemStack>();
		
		for (int i = 0; i < this.playerInventory.getSizeInventory(); i++) {
			ItemStack slotStack = this.playerInventory.getStackInSlot(i);
			
			if (slotStack.getItem() instanceof BasicPenItem) {
				pens.add(slotStack);
			}
		}
		return pens;
	}
	
	private void initButtons() {
		int startX = (this.width / 2) - (this.xSize / 2);
		int startY = (this.height / 2) - (this.ySize / 2);
		
		if (penList.size() > 12) {
			penLeftBtn = new Button(startX + 7, startY + 140, 13, 20, "<", (p_214266_1_) ->  {
				if (buttonOffset > 0) {
					buttonOffset--;
				}
			});
	
			penRightBtn = new Button(startX + 236, startY + 140, 13, 20, ">", (p_214266_1_) ->  {
				if (buttonOffset < penList.size() - 12) {
					buttonOffset++;
				}
			});
		}
		
		
		decreaseXBtn = new Button(startX + 141, startY + 7, 10, 20, "-", (p_214266_1_) ->  {
			posXSlider.setValue(posXSlider.getValue() > 0 ? posXSlider.getValue()-1 : 0);
			posXSlider.updateSlider();
		});
		
		posXSlider = new Slider(startX + 151, startY + 7, 88, 20, "X: ", "", 0, 64, 0, false, true, (p_214266_1_) -> {});
		
		increaseXBtn = new Button(startX + 239, startY + 7, 10, 20, "+", (p_214266_1_) ->  {
			posXSlider.setValue(posXSlider.getValue() < 64 ? posXSlider.getValue()+1 : 64);
			posXSlider.updateSlider();
		});
		
		decreaseYBtn = new Button(startX + 141, startY + 31, 10, 20, "-", (p_214266_1_) ->  {
			posYSlider.setValue(posYSlider.getValue() > 0 ? posYSlider.getValue()-1 : 0);
			posYSlider.updateSlider();
		});
		
		posYSlider = new Slider(startX + 151, startY + 31, 88, 20, "Y: ", "", 0, 64, 0, false, true, (p_214266_1_) -> {});
		
		increaseYBtn = new Button(startX + 239, startY + 31, 10, 20, "+", (p_214266_1_) ->  {
			posYSlider.setValue(posYSlider.getValue() < 64 ? posYSlider.getValue()+1 : 64);
			posYSlider.updateSlider();
		});
		
		showHideText = new Button(startX + 141, startY + 55, 108, 20, "Hide Text", (p_214266_1_) ->  {
			this.showText = !this.showText;
			showHideText.setMessage(this.showText ? "Hide Text" : "Show Text");
		});
		
		showHidePixels = new Button(startX + 141, startY + 79, 108, 20, "Hide Art", (p_214266_1_) ->  {
			this.showPixels = !this.showPixels;
			showHidePixels.setMessage(this.showPixels ? "Hide Art" : "Show Art");
		});
		
		boldBtn = new Button(startX + 45, startY + 183, 48, 20, "Bold", (p_214266_1_) ->  {
			this.bold = !this.bold;
			boldBtn.setMessage(this.bold ? "§lBold" : "Bold");
		});
		
		italicBtn = new Button(startX + 97, startY + 183, 48, 20, "Italic", (p_214266_1_) ->  {
			this.italic = !this.italic;
			italicBtn.setMessage(this.italic ? "§l§oItalic" : "Italic");
		});
		
		underlineBtn = new Button(startX + 149, startY + 183, 48, 20, "Underline", (p_214266_1_) ->  {
			this.underline = !this.underline;
			underlineBtn.setMessage(this.underline ? "§l§nUnderline" : "Underline");
		});
		
		strikeBtn = new Button(startX + 201, startY + 183, 48, 20, "Strikeout", (p_214266_1_) ->  {
			this.strikethrough = !this.strikethrough;
			strikeBtn.setMessage(this.strikethrough ? "§l§mStrikeout" : "Strikeout");
		});
		
		cancelBtn = new Button(startX + 45, startY + 211, 40, 20, "Cancel", (p_214266_1_) ->  {
			this.minecraft.displayGuiScreen(new GuiCanvasEditorMain(this.container, this.playerInventory, this.title));
		});
		
		saveContinueBtn = new Button(startX + 89, startY + 211, 78, 20, "Save & Close", (p_214266_1_) ->  {
			sendPacket();
			this.minecraft.displayGuiScreen(new GuiCanvasEditorMain(this.container, this.playerInventory, this.title));
		});
		
		saveCloseBtn = new Button(startX + 171, startY + 211, 78, 20, "Save", (p_214266_1_) ->  {
			sendPacket();
			this.minecraft.displayGuiScreen(new CreateTextMenu(this.container, this.playerInventory, this.title));
		});
		
		this.typedText = new TextFieldWidget(this.font, startX + 45, startY + 163, 204, 16, "");
		
		this.typedText.setCanLoseFocus(false);
		this.typedText.changeFocus(true);
		this.typedText.setTextColor(-1);
		this.typedText.setEnableBackgroundDrawing(true);
		this.typedText.setMaxStringLength(45);
		this.children.add(this.typedText);
		this.setFocusedDefault(this.typedText);
	}
	
	public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
		super.render(p_render_1_, p_render_2_, p_render_3_);
		this.typedText.render(p_render_1_, p_render_2_, p_render_3_);
	}
	
	@Override
	public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
	      if (p_keyPressed_1_ == 256) {
	         this.minecraft.player.closeScreen();
	      }

	      return !this.typedText.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) && !this.typedText.canWrite() ? super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) : true;
	   }

	private void sendPacket() {
		if (selectedPen >= 0) {
			BasicPenItem pen = (BasicPenItem) penList.get(selectedPen).getItem();
			GraffitiPacketHandler.INSTANCE.sendToServer(new WriteTextPacket(typedText.getText(), (short) posXSlider.getValueInt(), (short) Math.abs(posYSlider.getValueInt()-64), 1, pen.getColor(penList.get(selectedPen)).getRGB(), (short) 0, buildFormatString(), (short) 0, (short) -1, false));
		}
	}
	
	public String buildFormatString() {
		String b = this.bold ? "§l" : "";
		String i = this.italic ? "§o" : "";
		String u = this.underline ? "§n" : "";
		String s = this.strikethrough? "§m" : "";
		
		return b + i + u + s;
	}
	
	@Override
	public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
		posXSlider.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
		posYSlider.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
		return super.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
	}
}
