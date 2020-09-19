package com.silvaniastudios.graffiti.util;

import com.silvaniastudios.graffiti.drawables.CompleteGraffitiObject;

public class ImportedJsonObject {
	
	private String name;
	private String artist;
	private CompleteGraffitiObject graffiti;
	private String source_version;
	private String source;
	private int json_version;
	
	private boolean hasGrid;
	private int textObjects;
	private int drawables;
	private int gridSize;
	private int[][] grid;
	
	public ImportedJsonObject(String name, int[][] grid, String artist, boolean hasGrid, int gridSize, int textObjects, int drawables, String source_ver, String source, int json_ver) {
		this.name = name;
		this.artist = artist;
		this.hasGrid = hasGrid;
		this.grid = grid;
		this.gridSize = gridSize;
		this.textObjects = textObjects;
		this.drawables = drawables;
		this.source_version = source_ver;
		this.source = source;
		this.json_version = json_ver;
	}
	
	public ImportedJsonObject(String name, CompleteGraffitiObject graffiti, String artist, String source_ver, String source, int json_ver) {
		this.name = name;
		this.artist = artist;
		this.graffiti = graffiti;
		this.hasGrid = graffiti.pixelGrid != null;
		this.grid = graffiti.pixelGrid.getPixelGrid();
		this.textObjects = graffiti.textList.size();
		//this.drawables = graffiti.drawables.size();
		this.source_version = source_ver;
		this.source = source;
		this.json_version = json_ver;
		this.gridSize = graffiti.pixelGrid != null ? graffiti.pixelGrid.getSize() : 0;
	}
	
	public int[][] getGrid() {
		return grid;
	}
	
	public String getName() {
		return name;
	}
	
	public String getArtist() {
		return artist;
	}
	
	public CompleteGraffitiObject getGraffiti() {
		return graffiti;
	}
	
	public String getSourceVersion() {
		return source_version;
	}
	
	public String getSource() {
		return source;
	}
	
	public int getJsonVersion() {
		return json_version;
	}
	
	public boolean hasGrid() {
		return hasGrid;
	}

	public int getTextObjectCount() {
		return this.textObjects;
	}
	
	public int getDrawablesCount() {
		return this.drawables;
	}

	public int gridSize() {
		return gridSize;
	}
}
