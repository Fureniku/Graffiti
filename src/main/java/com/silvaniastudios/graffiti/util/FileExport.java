package com.silvaniastudios.graffiti.util;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.silvaniastudios.graffiti.Graffiti;
import com.silvaniastudios.graffiti.drawables.PixelGridDrawable;
import com.silvaniastudios.graffiti.drawables.TextDrawable;

import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

public class FileExport {
	
	public static final int JSON_VERSION = 2;
	
	public static StringTextComponent createFile(String fileName, PixelGridDrawable pixelGrid, ArrayList<TextDrawable> textList, double alignment) {
		File dir = new File("./graffiti/");
		
		if (!dir.exists()) {
			dir.mkdir();
		}
		
		int[][] grid = pixelGrid.getPixelGrid();
		
		JsonArray gridArray = new JsonArray();
		JsonArray textObjectArray = new JsonArray();
		JsonArray drawableArray = new JsonArray();
		
		
		for (int i = 0; i < grid.length; i++) {
			JsonArray subArray = new JsonArray();
			for (int j = 0; j < grid.length; j++) {
				subArray.add(grid[j][i]);
			}
			gridArray.add(subArray);
		}
		
		for (int i = 0; i < textList.size(); i++) {
			TextDrawable text = textList.get(i);
			JsonObject textObject = new JsonObject();
			
			textObject.add("text", new JsonPrimitive(text.getText()));
			textObject.add("colour", new JsonPrimitive(text.getCol()));
			textObject.add("posX", new JsonPrimitive(text.xPos()));
			textObject.add("posY", new JsonPrimitive(text.yPos()));
			textObject.add("scale", new JsonPrimitive(text.scale()));
			textObject.add("rotation", new JsonPrimitive(text.getRotation()));
			textObject.add("format", new JsonPrimitive(text.getFormat()));
			textObject.add("alignment", new JsonPrimitive(text.getAlignment()));

			textObjectArray.add(textObject);
		}
		
		LocalDateTime dateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm-ss");
		
		try {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			
		    Writer writer = new FileWriter("./graffiti/" + dateTime.format(formatter) + "-" + fileName + ".json");

		    JsonObject json = new JsonObject();
		    json.add("json_version", new JsonPrimitive(JSON_VERSION));
		    json.add("mod_version", new JsonPrimitive(getModVersion()));
		    json.add("source", new JsonPrimitive("Game"));
		    json.add("alignment", new JsonPrimitive(alignment));
		    json.add("texts", textObjectArray);
		    json.add("drawables", drawableArray);
		    json.add("grid_transparency", new JsonPrimitive(pixelGrid.getTransparency()));
		    json.add("grid_scale", new JsonPrimitive(pixelGrid.getSize()));
		    json.add("grid", gridArray);
		    
		    gson.toJson(json, writer);
		    
		    writer.close();
		    
		} catch (Exception ex) {
		    ex.printStackTrace();
		    return new StringTextComponent("File export failed! See log for details.");
		}
		
		return new StringTextComponent("File \"" + dateTime.format(formatter) + "-" + fileName + ".json\" successfully exported");
	}

	
	public static String getModVersion() {
		List<ModInfo> mods = Collections.unmodifiableList(ModList.get().getMods());
		
		for (ModInfo mod : mods) {
			if (mod.getModId().equalsIgnoreCase(Graffiti.MODID)) {
				return mod.getVersion().toString();
			}
		}
		return "ERROR: Version not found! This is a bug!";
	}
}
