package com.silvaniastudios.graffiti.util;

public class GraffitiUtils {
	
	//Increase the canvas size, stretching so the original image appears identical to before the increase
	public static int[][] increaseCanvasResized(int[][] canvasIn) {
		int originalSize = canvasIn.length;
		int newSize = 0;
		
		if (originalSize == 16) { 
			newSize = 32; 
		} else if (originalSize == 32) {
			newSize = 64;
		} else if (originalSize == 64) {
			newSize = 128;
		} else {
			return canvasIn;
		}
		
		int[][] newCanvas = new int[newSize][newSize];
		for (int i = 0; i < newSize; i++) {
			for (int j = 0; j < newSize; j++) {
				newCanvas[j][i] = canvasIn[j/2][i/2];
			}
		}
		
		return newCanvas;
	}
	
	//Increase a canvas size, keeping the original image in the top-left corner
	public static int[][] increaseCanvasCropped(int[][] canvasIn) {
		int originalSize = canvasIn.length;
		int newSize = 0;
		
		if (originalSize == 16) { 
			newSize = 32; 
		} else if (originalSize == 32) {
			newSize = 64;
		} else if (originalSize == 64) {
			newSize = 128;
		} else {
			return canvasIn;
		}
		
		int[][] newCanvas = new int[newSize][newSize];
		for (int i = 0; i < newSize; i++) {
			for (int j = 0; j < newSize; j++) {
				boolean x = i < newSize/2;
				boolean y = j < newSize/2;
				
				if (x && y) {
					newCanvas[j][i] = canvasIn[j][i];
				} else {
					newCanvas[j][i] = 0;
				}
			}
		}
		
		return newCanvas;
	}
	
	//Decrease a canvas size, cropping to show what was previously the top-left quarter.
	public static int[][] decreaseCanvasCropped(int[][] canvasIn) {
		int originalSize = canvasIn.length;
		int newSize = 0;
		
		if (originalSize == 32) { 
			newSize = 16; 
		} else if (originalSize == 64) {
			newSize = 32;
		} else if (originalSize == 128) {
			newSize = 64;
		} else {
			return canvasIn;
		}
		
		int[][] newCanvas = new int[newSize][newSize];
		for (int i = 0; i < newSize; i++) {
			for (int j = 0; j < newSize; j++) {
				newCanvas[j][i] = canvasIn[j][i];
			}
		}
		
		return newCanvas;
	}
	
	//Decrease the canvas size, compressing the image and taking the top-left pixel of each four pixel area to create a new image
	public static int[][] decreaseCanvasResized(int[][] canvasIn) {
		int originalSize = canvasIn.length;
		int newSize = 0;
		
		if (originalSize == 32) { 
			newSize = 16; 
		} else if (originalSize == 64) {
			newSize = 32;
		} else if (originalSize == 128) {
			newSize = 64;
		} else {
			return canvasIn;
		}
		
		int[][] newCanvas = new int[newSize][newSize];
		for (int i = 0; i < newSize; i++) {
			for (int j = 0; j < newSize; j++) {
				newCanvas[j][i] = canvasIn[j*2][i*2];
			}
		}
		
		return newCanvas;
	}
	
	//Repeatedly scale up or down 
	public static int[][] rescaleMultiple(int[][] canvasIn, int target, boolean rescale) {
		int[][] newCanvas = canvasIn.clone();
		
		int resizeIterations = sizeToId(target) - sizeToId(canvasIn.length);
		
		if (resizeIterations > 0) {
			for (int i = 0; i < resizeIterations; i++) {
				if (rescale) {
					newCanvas = increaseCanvasResized(newCanvas);
				} else {
					newCanvas = increaseCanvasCropped(newCanvas);
				}
			}
		} else if (resizeIterations < 0) {
			resizeIterations = Math.abs(resizeIterations);
			
			for (int i = 0; i < resizeIterations; i++) {
				if (rescale) {
					newCanvas = decreaseCanvasResized(newCanvas);
				} else {
					newCanvas = decreaseCanvasCropped(newCanvas);
				}
			}
		}
		return newCanvas;
	}
	
	public static int sizeToId(int size) {
		if (size == 16) { return 0; }
		if (size == 32) { return 1; }
		if (size == 64) { return 2; }
		if (size == 128) { return 3; }
		return -1;
	}

	public static int idToSize(int id) {
		if (id == 0) { return 16; }
		if (id == 1) { return 32; }
		if (id == 2) { return 64; }
		if (id == 3) { return 128; }
		return 0;
	}
}
