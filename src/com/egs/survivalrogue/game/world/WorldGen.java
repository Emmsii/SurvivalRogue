package com.egs.survivalrogue.game.world;

import com.egs.survivalrogue.gui.CreateWorldMenu;
import com.egs.survivalrogue.util.FileHandler;
import com.egs.survivalrogue.util.Noise;

public class WorldGen implements Runnable{

	private Chunk chunk;
	private Noise noise;
	private CreateWorldMenu worldMenu;
	private FileHandler file;
	
	private long seed;
	
	private int w;
	private int h;
	private int temp;
	private String name;
	
	private int percent;
	private int totalChunks;
	private int itteration;
	
	private boolean complete = false;
		
	public WorldGen(int w, int h, long seed, int temp, String name, CreateWorldMenu worldMenu, FileHandler file){
		this.w = w;
		this.h = h;
		this.seed = seed;
		this.temp = temp;
		this.name = name;
		this.worldMenu = worldMenu;
		this.file = file;
	}
	
	public void run() {
		if(!complete) generateWorld();
	}
	
	public void generateWorld(){
		file.newWorldFolder(name);
		//file.newWorldData(name, w, h, seed, temp);
		
		noise = new Noise();				
		totalChunks = (w / 16) * (h / 16);
		itteration = 1;
		
		int total = w * h;
		int current = 0;
		//System.out.println("Generating world (" + w + "x" + h + ") " + totalChunks + " chunks.");
		System.out.println("Generating World: " + name);
		System.out.println("Seed: " + seed);
		
		createChunk(0, 0);
		createChunk(-16, -16);
		createChunk(0, -16);
		createChunk(16, -16);
		createChunk(16, 0);
		createChunk(16, 16);
		createChunk(0, 16);
		createChunk(-16, 16);
		createChunk(-16, 0);
		
		
		complete = true;
		System.out.println("Generation Complete.");
		//System.out.println("Test Value: " + file.loadChunk(43, 12, name).getHeight()[3][5]);

	}
	
	public void createChunk(int x, int y, int itteration){	
		//System.out.println(x + ", " + y + ", " + itteration);
		
		chunk = new Chunk("#", 0, 0);
//		chunk.setHeight(noise.startNoise(w, h, x * itteration - 15, y * itteration - 15, seed, 0.008, 0.4, 8, 16));
		chunk.setX(x * itteration - 15);
		chunk.setY(y * itteration - 15);
		file.saveChunk(chunk, name);
		chunk = null;
	}
	
	public void createChunk(int x, int y){
		System.out.println("Creating Chunk @ " + x + ", " + y);
		chunk = new Chunk("#", 0, 0);
		chunk.setX(x);
		chunk.setY(y);
		
//		chunk.setHeight(noise.startNoise(16, 16, x, y, seed, 0.008, 0.4, 8, 16));
		for(int ya = 0; ya < 16; ya++){
			for(int xa = 0; xa < 16; xa++){
				
			}
		}
		
		file.saveChunk(chunk, name);
		chunk = null;
	}
	
	public void updateProgress(int total, int current){
		percent = (current * 100) / total;	
		worldMenu.setProgress(percent);
	}

	
}
