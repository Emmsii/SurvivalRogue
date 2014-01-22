package com.egs.survivalrogue.game.level;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import com.egs.survivalrogue.MainComponent;
import com.egs.survivalrogue.game.world.Chunk;
import com.egs.survivalrogue.util.FileHandler;
import com.egs.survivalrogue.util.InputHandler;
import com.egs.survivalrogue.util.Noise;

public class Level {

	private MainComponent main;
	private InputHandler input;
	private FileHandler file;
	private Chunk chunk;
	private Noise noise;
	
	private Random random = new Random();
	
	private String name;
	private long seed;
	
	private boolean debug = true;
	private static final int rcd = 3;
	private int chunksGenerated = 0;
	private int chunksLoaded = 0;
	
	private int xOffset = 0;
	private int yOffset = 0;
	
	private int xCo = 0;
	private int yCo = 0;
	
	private List<Chunk> chunks = new ArrayList<Chunk>();
	private int[][] toRender = new int[41][33];

	public Level(long seed, MainComponent main, FileHandler file, InputHandler input){
		System.out.println("New Level");
		this.seed = seed;
		this.main = main;
		this.file = file;
		this.input = input;
		this.noise = new Noise();

	}
	
	public void init(){
		String[] names = file.loadNames("world_names"); //Load a list of names from the res folder.
		name = names[random.nextInt(names.length)]; //Assign a random world name from the list.
		
		file.newWorldFolder(name); //Save the world folder.
		file.newWorldData(name, seed); //Create and save the world data .dat file.
		
		createSpawnChunks(); 
		loadSpawnChunks();
		
		System.out.println("New world: " + name + " (Seed: " + seed + ")");
	}
	
	public void render(Graphics g){
		renderChunks(g);
		
		if(debug) renderDebug(g); 
		
		
		drawText("Chunks Loaded: " + chunks.size(), 500, 100, 12, true, g);
		
		drawText("XO: " + xOffset, 500, 200, 12, true, g);
		drawText("YO: " + yOffset, 500, 212, 12, true, g);
		drawText("ChunkXO: " + xCo, 500, 224, 12, true, g);
		drawText("ChUnkYO: " + yCo, 500, 236, 12, true, g);
		
		for(int i = 0; i < chunks.size(); i++){
			drawText(chunks.get(i).getId(), 500, 300 + (12 * i), 12, true, g);
		}
	}
	
	public void update(){
		input();
		updateChunks();
	}
	
	private void input(){
		if(input.up.isPressed()) yOffset++;
		if(input.down.isPressed()) yOffset--;
		if(input.left.isPressed()) xOffset++;
		if(input.right.isPressed()) xOffset--;
		
		if(input.debug.isPressed()){
			if(!debug) debug = true;
			else debug = false;
		}
	}
	
	public void createSpawnChunks(){
		createChunk(0, 0);
		createChunk(1, 0);
		createChunk(1, 1);
		createChunk(0, 1);
	}
	
	private void loadSpawnChunks(){
		double start = System.currentTimeMillis();
		chunks.add(file.loadChunk(0, 0, name));
		chunks.add(file.loadChunk(1, 0, name));
		chunks.add(file.loadChunk(1, 1, name));
		chunks.add(file.loadChunk(0, 1, name));
		
		double end = System.currentTimeMillis();
		System.out.println("Spawn chunks loaded in: " + (end - start) + "ms");
	}
	
	public void createChunk(int x, int y){
		System.out.println("Creating Chunk @ " + x + ", " + y);
		chunk = new Chunk(x + "_" + y);
		chunksGenerated++;
		x = x * 16;
		y = y * 16;
		
		chunk.setX(x);
		chunk.setY(y);
		
		//int[][] noisemap = noise.startNoise(16, 16, x, y, seed, 0.008, 0.4, 8, 16);
		int[][] noisemap = noise.startNoise(16, 16, x, y, seed, 0.5, 0.4, 8, 16);
		
		for(int ya = 0; ya < 16; ya++){
			for(int xa = 0; xa < 16; xa++){			
				if(noisemap[xa][ya] < 64) chunk.setTileId(xa, ya, 1);
				else if(noisemap[xa][ya] >= 64) chunk.setTileId(xa, ya, 2);
				else if(noisemap[xa][ya] >= 128) chunk.setTileId(xa, ya, 3);
				else chunk.setTileId(xa, ya, 0);
			}
		}
		
		
		file.saveChunk(chunk, name);
		noisemap = null;
		chunk = null;
	}
	
	
	public void updateChunks(){
		yCo = yOffset / 16;
		xCo = xOffset / 16;
		loadChunks();
		unloadChunks();
	}
	
	public void renderChunks(Graphics g){
		for(Chunk chunk : chunks){
			for(int y = 0; y < 16; y++){
				for(int x = 0; x < 16; x++){
					int xPos = ((x + chunk.getX() + xOffset) * 12) + 2;
					int yPos = ((y + chunk.getY() + yOffset) * 12) + 12;
					
					if(chunk.getTileId(x, y) == 1) renderTile(1, Color.BLUE, xPos, yPos, g);
					else if(chunk.getTileId(x, y) == 2) renderTile(2, Color.GREEN, xPos, yPos, g);
					else if(chunk.getTileId(x, y) == 3) renderTile(3, Color.RED, xPos, yPos, g);
					else renderTile(0, Color.CYAN, xPos, yPos, g);
				}
			}
		}

		//Find the correct tiles to render.
		//Put tiles into toRender array.
		//Render tiles in toRender array.

//		for(int y = 0; y < 33; y++){
//			for(int x = 0; x < 41; x++){
//				int xPos = x * 12 + 2;
//				int yPos = y * 12 + 21;
//
//				if(toRender[x][y] == 1) renderTile(1, Color.BLUE, xPos, yPos, g);
//				else if(toRender[x][y] == 2) renderTile(2, Color.GREEN, xPos + 4, yPos - 2, g);
//				else if(toRender[x][y] == 3) renderTile(3, Color.RED, xPos, yPos, g);
//				//else renderTile(0, Color.ORANGE, xPos, yPos, g);
//							
//			}
//		}
	}
	
	private Chunk getChunk(int x, int y){
		x = x / 16;
		y = y / 16;
		
		String id = x + "_" + y;
		
		for(Chunk chunk : chunks){
			if(chunk.getId().equals(id)){
				return chunk;
			}
		}
		
		return null;
	}
	
	private void renderDebug(Graphics g){
		drawText("cl: " + chunksLoaded + "/" + chunksGenerated, 30, 10, 9, false, g);
	}
	
	public void renderTile(int id, Color color, int x, int y, Graphics g){
		g.setColor(color);
		g.setFont(new Font("Arial", Font.BOLD, 12));
		
		if(id == 0) g.drawString("X", x, y);
		else if(id == 1) g.drawString("W", x, y);
		else if(id == 2) g.drawString(".", x, y);
		else if(id == 3) g.drawString("8", x, y);

	}
	
	public void drawText(String msg, int x, int y, int size, boolean bold, Graphics g){
		if(!bold) g.setFont(new Font("Arial", Font.PLAIN, size));
		if(bold) g.setFont(new Font("Arial", Font.BOLD, size));
		
		g.setColor(Color.BLACK);
		g.drawString(msg, x + 1, y + 1);
		g.setColor(Color.WHITE);
		g.drawString(msg, x, y);
	}
	
	// RCD = render chunk distance
	
	public void loadChunks(){
		// If chunks in chunkList are < RCD then load them in.
		for(int y = yCo; y < (yCo + rcd); y++){
			for(int x = xCo; x < (xCo + rcd); x++){
				if(!checkListFor(x, y)){
					if(!file.checkFileFor(x, y, name)) createChunk(x, y);
					chunks.add(file.loadChunk(x, y, name));
					chunksLoaded++;
				}
			}
		}
		
//		for(int y = 0; y < rcd; y++){
//			for(int x = 0; x < rcd; x++){
//
//				x = x + xCo;
//				y = y + yCo;
//				if(!checkListFor(x, y)){
//					if(!file.checkFileFor(x, y, name)) createChunk(x, y);
//					chunks.add(file.loadChunk(x, y, name));
//					System.out.println("LOADING CHUNK.");
//					chunksLoaded++;
//				}			
//			}
//		}
//		System.out.println("Loading complete");
		
		// If chunk does not exist, create chunk.
		// Else load chunk from file to list.
	}
	
	private boolean checkListFor(int x, int y){
		String id = x + "_" + y;
		for(Chunk chunk : chunks) if(chunk.getId().equals(id)) return true;
		return false;
	}
	
	public void unloadChunks(){
		// If chunks in chunkList are > RCD then remove them from array.
		for(ListIterator<Chunk> i = chunks.listIterator(); i.hasNext();){
			Chunk chunk = i.next();
			int cx = chunk.getX() / 16;
			int cy = chunk.getY() / 16;
			if(cx > xCo + rcd || cx < xCo - rcd){
				i.remove();
				chunksLoaded--;
				continue;
			}
			if(cy > yCo + rcd || cy < yCo - rcd){
				i.remove();
				chunksLoaded--;
				continue;
			}
		}
	}
}
