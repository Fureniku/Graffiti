package com.silvaniastudios.graffiti.util;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.silvaniastudios.graffiti.drawables.CompleteGraffitiObject;
import com.silvaniastudios.graffiti.drawables.PixelGridDrawable;
import com.silvaniastudios.graffiti.drawables.TextDrawable;
import com.silvaniastudios.graffiti.network.ClearGraffitiPacket;
import com.silvaniastudios.graffiti.network.GraffitiPacketHandler;
import com.silvaniastudios.graffiti.network.SetGraffitiRowPacket;
import com.silvaniastudios.graffiti.network.WriteTextPacket;
import com.silvaniastudios.graffiti.tileentity.TileEntityGraffiti;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FileImport {
	
	public static ImportedJsonObject importFile(File file, TileEntityGraffiti te, Direction side) {
		JsonParser parser = new JsonParser();
		
		try {
			JsonObject json = (JsonObject) parser.parse(new FileReader(file));

			int json_version = json.has("json_version") ? json.get("json_version").getAsInt() : 1;
			String mod_version = json.has("mod_version") ? json.get("mod_version").getAsString() : "Unknown";
			if (mod_version.equalsIgnoreCase("NONE")) { mod_version = "Dev Build"; }
			String source = json.has("source") ? json.get("source").getAsString() : "Unknown";
			String artist = json.has("artist") ? json.get("artist").getAsString() : "Unknown";
			JsonArray texts = json.has("texts") ? json.get("texts").getAsJsonArray() : null;
			//JsonArray drawables = json.get("drawables").getAsJsonArray();
			int grid_transparency = json.has("grid_transparency") ? json.get("grid_transparency").getAsInt() : 255;
			int grid_scale = json.has("grid_scale") ? json.get("grid_scale").getAsInt() : 0;
			JsonArray grid = json.has("grid") ? json.get("grid").getAsJsonArray() : null;
			
			int[][] gridArray = new int[grid_scale][grid_scale];
			ArrayList<TextDrawable> textList = new ArrayList<TextDrawable>();
			
			PixelGridDrawable pixelgrid = null;
			ItemStack backdropBlock = null;
			
			if (texts != null) {
				for (int i = 0; i < texts.size(); i++) {
					JsonObject textEntry = texts.get(i).getAsJsonObject();
					
					String text = textEntry.get("text").getAsString();
					int col = textEntry.get("colour").getAsInt();
					short posX = textEntry.get("posX").getAsShort();
					short posY = textEntry.get("posY").getAsShort();
					float scale = textEntry.get("scale").getAsFloat();
					short rotation = textEntry.get("rotation").getAsShort();
					String format = textEntry.get("format").getAsString();
					short alignment = textEntry.get("alignment").getAsShort();
					
					//validation
					if (posX < 0 || posX > 64 || posY < 0 || posY > 64 || rotation < 0 || rotation > 360 || format.length() > 10 || text.length() > 50 || alignment > 2 || alignment < 0) {
						return null;
					}
					
					textList.add(new TextDrawable(text, posX, posY, col, scale, rotation, format, alignment));
				}
			}
			
			if (grid_scale == 16 || grid_scale == 32 || grid_scale == 64 || grid_scale == 128) {
				if (grid_scale == grid.size()) {
					for (int i = 0; i < grid.size(); i++) {
						JsonArray gridRow = grid.get(i).getAsJsonArray();
						if (gridRow.size() != grid_scale) {
							return null;
						}
						for (int j = 0; j < gridRow.size(); j++) {
							gridArray[j][i] = gridRow.get(j).getAsInt();
						}
					}
					pixelgrid = new PixelGridDrawable(gridArray);
					pixelgrid.setTransparency(grid_transparency);
				}
			}
			
			CompleteGraffitiObject graffiti = new CompleteGraffitiObject(side, pixelgrid, textList, backdropBlock, 0, 200, "", 0.015625, true);
			
			if (json_version >= 2) {
				boolean offset = json.has("offset") ? json.get("offset").getAsBoolean() : true;
				double offset_amount = json.has("offset_amount") ? json.get("offset_amount").getAsDouble() : 0.015625;
				
				if (offset_amount < -0.25 || offset_amount > 0.25) {
					offset_amount = 0.015625;
				}

				graffiti.setAlignment(offset, offset_amount, te);
			}
			
			if (json.has("rightClickActionId")) {
				int rightClickActionId = json.get("rightClickActionId").getAsInt();
				if (rightClickActionId < 0 || rightClickActionId > 3) {
					rightClickActionId = 0;
				}
				graffiti.setRightClickAction(rightClickActionId);
			}
			
			if (json.has("url")) {
				String url = json.get("url").getAsString();
				if (url.length() < 500) {
					graffiti.setUrl(url);
				}
			}
			
			if (json.has("backgroundTransparency")) {
				int backgroundTransparency = json.get("backgroundTransparency").getAsInt();
				if (backgroundTransparency < 0 || backgroundTransparency > 255) {
					backgroundTransparency = 200;
				}
				graffiti.setBackgroundTransparency(backgroundTransparency);
			}
			
			return new ImportedJsonObject(file.getName(), graffiti, artist, mod_version, source, json_version);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	//much faster, no processing or validating just for info display.
	public static ImportedJsonObject importFileInfoBasic(File file) {
		JsonParser parser = new JsonParser();
		
		try {
			JsonObject json = (JsonObject) parser.parse(new FileReader(file));
			
			int json_version = json.get("json_version").getAsInt();
			String mod_version = json.get("mod_version").getAsString();
			String source = json.get("source").getAsString();
			String artist = json.has("artist") ? json.get("artist").getAsString() : "Unknown";
			JsonArray texts = json.get("texts").getAsJsonArray();
			int grid_scale = json.has("grid_scale") ? json.get("grid_scale").getAsInt() : 0;
			JsonArray grid = json.has("grid") ? json.get("grid").getAsJsonArray() : null;
			int[][] gridArray = new int[grid_scale][grid_scale];
			
			if (grid_scale == 16 || grid_scale == 32 || grid_scale == 64 || grid_scale == 128) {
				if (grid_scale == grid.size()) {
					for (int i = 0; i < grid.size(); i++) {
						JsonArray gridRow = grid.get(i).getAsJsonArray();
						if (gridRow.size() != grid_scale) {
							return null;
						}
						for (int j = 0; j < gridRow.size(); j++) {
							gridArray[j][i] = gridRow.get(j).getAsInt();
						}
					}
				}
			}
			
			GraffitiUtils.rescaleMultiple(gridArray, 16, true);

			return new ImportedJsonObject(file.getName(), GraffitiUtils.rescaleMultiple(gridArray, 32, true), artist, grid_scale > 0, grid_scale, texts.size(), 0, mod_version, source, json_version);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	@OnlyIn(Dist.CLIENT) //it should only ever be called from a client GUI anyway so this is just informative really
	public static void sendDataToServer(CompleteGraffitiObject graffiti, boolean clearOldData) {
		if (clearOldData) {
			GraffitiPacketHandler.INSTANCE.sendToServer(new ClearGraffitiPacket());
			
		}
		
		int iterations = graffiti.pixelGrid.getSize() / 16;
		
		for (int i = 0; i < iterations; i++) {
			//the packet will send graffiti in rows of 16 (technically supporting width of 256). 16x16 graffiti = 1 packet, 32x32 = 2 packets, 64x64 = 4 packets etc
			//the actual splitting of the grid occurs in the packet class just before its sent, and it's assigned to the block individually. dropped packets will result in a gap in the image.
			GraffitiPacketHandler.INSTANCE.sendToServer(new SetGraffitiRowPacket(graffiti.pixelGrid.getPixelGrid(), graffiti.pixelGrid.getSize(), i));
		}

		//send each text object
		for (int i = 0; i < graffiti.textList.size(); i++) {
			TextDrawable text = graffiti.textList.get(i);
			System.out.println("sending text " + text.getText());
			GraffitiPacketHandler.INSTANCE.sendToServer(new WriteTextPacket(text.getText(), text.xPos(), text.yPos(), text.scale(), text.getCol(), text.getRotation(), text.getFormat(), text.getAlignment()));
		}
		
		//send final data bits
	}

}
