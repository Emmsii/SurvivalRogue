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

	private InputHandler input;
	private FileHandler file;
	private Chunk chunk;
	private Noise noise;
	
	private Random random = new Random();
	
	private String name;
	private long seed;
	
	private boolean debug = true;
	private static final int renderDistance = 48;
	private int chunksGenerated = 0;
	private int chunksLoaded = 0;
	
	private int xPos = 0;
	private int yPos = 0;
	
	private List<Chunk> chunks = new ArrayList<Chunk>();
	private int[][] toRender = new int[43][35];

	public Level(long seed, MainComponent main, FileHandler file, InputHandler input){
		System.out.println("New Level");
		this.seed = seed;
		this.file = file;
		this.input = input;
		this.noise = new Noise();

	}
	
	public void init(){
		String[] names = file.loadNames("world_names"); //Load a list of names from the res folder.
		name = names[random.nextInt(names.length)]; //Assign a random world name from the list.
		
		file.newWorldFolder(name); //Save the world folder.
		file.newWorldData(name, seed); //Create and save the world data .dat file.

		System.out.println("New world: " + name + " (Seed: " + seed + ")");
	}
	
	public void render(Graphics g){
		/* Render temp game window. */
		for(int y = 0; y < 35; y++){
			for(int x = 0; x < 43; x++){
				int xa = x * 12 + 1;
				int ya = y * 12 + 10;
				renderTile(0, Color.BLUE, xa, ya, g);
			}
		}
		
		
		renderChunks(g);
		if(debug) renderDebug(g); 

	}
	
	public void update(){
		input();
		updateChunks();
	}
	
	private void input(){
		if(input.up.isPressed()) yPos--;
		if(input.down.isPressed()) yPos++;
		if(input.left.isPressed()) xPos--;
		if(input.right.isPressed()) xPos++;
		
		if(input.debug.isPressed()){			
			if(!debug) debug = true;
			else debug = false;
		}
		
		if(input.reload.isPressed()) unloadAllChunks();
	}
		
	public void renderTile(int id, Color color, int x, int y, Graphics g){
		g.setColor(color);
		g.setFont(new Font("Arial", Font.BOLD, 12));
		
		if(id == 0) g.drawString("X", x, y);
		else if(id == 1) g.drawString("W", x, y);
		else if(id == 2) g.drawString("0", x, y);
		else if(id == 3) g.drawString("8", x, y);
	}
	
	public void renderDebug(Graphics g){
		drawText("cl: " + chunksLoaded + "/" + chunksGenerated, 30, 10, 9, false, g);
		drawText("X (top left): " + xPos, 3, 22, 9, false, g);
		drawText("Y (top left): " + yPos, 2, 33, 9, false, g);
	}
	
	/*
	 * Chunks Start -------------------------------------------------------------
	 */
	
	public void updateChunks(){
		loadChunks();
		unloadChunks();
	}
	
	public void renderChunks(Graphics g){

	}
					
	public void loadChunks(){
		for(int y = yPos; y < (yPos + renderDistance); y++){
			for(int x = xPos; x < (xPos + renderDistance); x++){
				int xa = x / 16;
				int ya = y / 16;
				if(!checkListFor(xa, ya)){
					if(!file.checkFileFor(xa, ya, name)) createChunk(xa + "_" + ya, x, y);
					chunks.add(file.loadChunk(xa, ya, name));
					chunksLoaded++;
				}
			}
		}
	}
		
	public void unloadChunks(){
		for(ListIterator<Chunk> i = chunks.listIterator(); i.hasNext();){
            Chunk chunk = i.next();
            int x = chunk.getX() * 16;
            int y = chunk.getY() * 16;
            if(x > xPos + renderDistance || x < xPos || y > yPos + renderDistance || y < yPos){
                    i.remove();
                    chunksLoaded--;
                    continue;
            }
		}
	}
	
	public void unloadAllChunks(){
		int size = chunks.size();
		chunksLoaded = chunksLoaded - size;
		chunks.clear();
	}
	
	public void createChunk(String id, int x, int y){
		chunk = new Chunk(id, x / 16, y / 16);
		
        //int[][] noisemap = noise.startNoise(16, 16, x, y, seed, 0.008, 0.4, 8, 16);
		int[][] noisemap = noise.startNoise(16, 16, x, y, seed, 0.5, 0.4, 8, 16);
		
		chunk.setHeight(noisemap);
		
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
	
	/*
	 * Chunks End -------------------------------------------------------------
	 */

	private boolean checkListFor(int x, int y){
		String id = x + "_" + y;
		for(Chunk chunk : chunks) if(chunk.getId().equals(id)) return true;
		return false;
	}
	
	//TODO: Possibly undeeded method.
	private Chunk getChunk(int x, int y){
		x = x / 16;
		y = y / 16;
		
		String id = x + "_" + y;
		for(Chunk chunk : chunks) if(chunk.getId().equals(id)) return chunk;	
		return null;
	}
	
	public void drawText(String msg, int x, int y, int size, boolean bold, Graphics g){
		if(!bold) g.setFont(new Font("Arial", Font.PLAIN, size));
		if(bold) g.setFont(new Font("Arial", Font.BOLD, size));
		
		g.setColor(Color.BLACK);
		g.drawString(msg, x + 1, y + 1);
		g.setColor(Color.WHITE);
		g.drawString(msg, x, y);
	}

}
