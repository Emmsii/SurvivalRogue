package com.egs.survivalrogue.game.level;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.egs.survivalrogue.game.world.Chunk;
import com.egs.survivalrogue.util.FileHandler;
import com.egs.survivalrogue.util.InputHandler;
import com.egs.survivalrogue.util.Noise;

public class Level {

	private InputHandler input;
	private FileHandler file;
	private Chunk chunk;
	private Noise noise;


	private String name;
	private long seed;

	private boolean debug = false;
	private static final int renderDistance = 3 * 16;
	private int chunksGenerated = 0;
	private int chunksLoaded = 0;
	
	private boolean moving;
	
	private double loadTime = 0;
	private double saveTime = 0;
	private double genTime = 0;

	private int xPos = 0;
	private int yPos = 0;

	private List<Chunk> chunks = new ArrayList<Chunk>();

	public Level(long seed, String worldName, FileHandler file, InputHandler input){
		System.out.println("New Level");
		this.seed = seed;
		this.name = worldName;
		this.file = file;
		this.input = input;
		this.noise = new Noise();
	}
	
	public Level(String worldName, FileHandler file, InputHandler input){
		System.out.println("Load level");
		this.name = worldName;
		this.file = file;
		this.input = input;
		this.noise = new Noise();
		loadLevel(worldName);
	}
	
	public void loadLevel(String name){
		String data[] = file.getWorldData(name);
		String[] rawString = data[1].split(": ");
		seed = Long.parseLong(rawString[1]);
		String[] x = data[2].split(": ");
		String[] y = data[3].split(": ");
		xPos = Integer.parseInt(x[1]);
		yPos = Integer.parseInt(y[1]);		
		loadChunks();
		moving = false;
		System.out.println("Level loaded.");
	}

	public void init(){
		file.newWorldFolder(name); //Save the world folder.
		file.newWorldData(name, seed, xPos, yPos); //Create and save the world data.dat file.

		loadChunks(); //Creates, saves and loads spawn chunks.
		moving = false;
		System.out.println("New world: " + name + " (Seed: " + seed + ")");
	}

	public void render(Graphics g){
		renderChunks(g); //Renders the chunks loaded.
		if(debug) renderDebug(g); //Renders the debug information, on by default.
	}

	public void update(){
		input(); //Updates user input.
		updateChunks(); //Updates the chunks (loading + unloading).
	}

	private void input(){
		moving = false;
		if(input.up.isPressed()){
			yPos--;
			moving = true;
		}
		if(input.down.isPressed()){
			yPos++;
			moving = true;
		}
		if(input.left.isPressed()){
			xPos--;
			moving = true;
		}
		if(input.right.isPressed()){
			xPos++;
			moving = true;
		}

		if(input.debug.isPressed()){
			if(!debug) debug = true;
			else debug = false;
		}

		if(input.reload.isPressed()) reloadAllChunks();
	}

	public void renderTile(String icon, Color first, Color second, int x, int y, Graphics g){
//		g.setColor(color);
//		g.setFont(new Font("Arial", Font.BOLD, 12));
//
//		if(id == 0) g.drawString("X", x, y);
//		else if(id == 1) g.drawString("W", x, y);
//		else if(id == 2) g.drawString("0", x, y);
//		else if(id == 3) g.drawString("8", x, y);
		g.setColor(first);
		g.fillRect(x - 1, y - 11, 13, 13);
		
		g.setColor(second);
		g.setFont(new Font("Arial", Font.BOLD, 10));
		g.drawString(icon, x, y);
	
	}

	public void renderDebug(Graphics g){
		drawText("cl: " + chunksLoaded + "/" + chunksGenerated, 30, 10, 9, false, g);
		drawText("X (top left): " + xPos, 3, 22, 9, false, g);
		drawText("Y (top left): " + yPos, 2, 33, 9, false, g);
		
		drawText("Last Load Time: " + loadTime, 540, 100, 12, true, g);
		drawText("Last Save Time: " + saveTime, 540, 111, 12, true, g);
		drawText("Last Gen Time: " + genTime, 540, 122, 12, true, g);
		
		for(int i = 0; i < chunks.size(); i++){
			drawText("id: " + chunks.get(i).getId(), 600, 150 + (i * 12), 12, true, g);
		}
	}

	/*
	 * Chunks Start
	 * -------------------------------------------------------------
	 */

	public void updateChunks(){
		if(moving) loadChunks();
		unloadChunks();
	}

	public void renderChunks(Graphics g){
		for(Chunk chunk : chunks){
			for(int y = 0; y < 16; y++){
				for(int x = 0; x < 16; x++){
					int xa = ((x + (chunk.getX() * 16) - xPos) * 12);
				    int ya = ((y + (chunk.getY() * 16) - yPos) * 12) + 10;
				    
				    if(x + (chunk.getX() * 16) - xPos > 43) continue;
				    if(x + (chunk.getX() * 16) - xPos < 0) continue;
				    if(y + (chunk.getY() * 16) - yPos > 33) continue;
				    if(y + (chunk.getY() * 16) - yPos < 0) continue;
				          
				    if(chunk.getTileId(x, y) == 1) renderTile("W", new Color(87, 165, 217), new Color(45, 119, 168), xa, ya, g);
				    else if(chunk.getTileId(x, y) == 2) renderTile(". .", new Color(125, 194, 52), new Color(85, 145, 20), xa + 1, ya - 1, g);
				    else if(chunk.getTileId(x, y) == 3) renderTile("M", new Color(115, 115, 115), new Color(77, 77, 77), xa, ya, g);
				    else if(chunk.getTileId(x, y) == 4) renderTile("S", new Color(237, 225, 173), new Color(207, 194, 136), xa, ya, g);
				    else renderTile("X", new Color(0, 0, 0), new Color(255, 0, 0), xa, ya, g);
				}
			}
			if(debug){
				drawText("id:" + chunk.getId(), ((chunk.getX() * 16) - xPos) * 12, ((chunk.getY() * 16) - yPos) * 12 + 24, 12, true, g);
				g.setColor(Color.WHITE);
				g.drawRect(((chunk.getX() * 16) - xPos) * 12, ((chunk.getY() * 16) - yPos) * 12, 16 * 12, 16 * 12);	
			}
		}
	}

	public void loadChunks(){
		double start = System.nanoTime();
		for(int y = (yPos - 16); y < (yPos + renderDistance); y++){
			for(int x = (xPos - 16); x < (xPos + renderDistance); x++){
				if(x % 16 == 0 && y % 16 == 0){
					int xa = x / 16;
					int ya = y / 16;
					if(!checkListFor(xa, ya)){
						if(!file.checkFileFor(xa, ya, name)) createChunk(xa + "_" + ya, x, y);
						System.out.println("X: " + x + ", Y: " + y);
						chunks.add(file.loadChunk(xa, ya, name));
						chunksLoaded++;
					}
				}
			}
		}
		loadTime = (System.nanoTime() - start) / 1000000;
	}

	public void unloadChunks(){
		for(ListIterator<Chunk> i = chunks.listIterator(); i.hasNext();){
			Chunk chunk = i.next();
			int x = chunk.getX() * 16;
			int y = chunk.getY() * 16;
			if(x > xPos + renderDistance || x < xPos - 16|| y > yPos + renderDistance || y < yPos - 16){
				i.remove();
				chunksLoaded--;
				continue;
			}
		}
	}

	public void reloadAllChunks(){
		int size = chunks.size();
		chunksLoaded = chunksLoaded - size;
		chunks.clear();
		loadChunks();
	}

	public void createChunk(String id, int x, int y){
		double start = System.nanoTime();
		
		
		chunk = new Chunk(id, x / 16, y / 16);

		int[][] noisemap = noise.startNoise(16, 16, x, y, seed, 0.008, 0.4, 8, 16);
		//int[][] noisemap = noise.startNoise(16, 16, x, y, seed, 0.5, 0.4, 8, 16);

		chunk.setHeight(noisemap);

		/*
		 * 0: Invalid
		 * 1: Water
		 * 2: Grass
		 * 3: Moutain
		 * 4: Sand
		 */
		
		for(int ya = 0; ya < 16; ya++){
			for(int xa = 0; xa < 16; xa++){
				if(noisemap[xa][ya] < 64) chunk.setTileId(xa, ya, 1);
				if(noisemap[xa][ya] >= 64 && noisemap[xa][ya] < 72) chunk.setTileId(xa, ya, 4);
				if(noisemap[xa][ya] >= 72) chunk.setTileId(xa, ya, 2);
				if(noisemap[xa][ya] >= 250) chunk.setTileId(xa, ya, 3);
				//else chunk.setTileId(xa, ya, 0);
			}
		}
		double start2 = System.nanoTime();
		file.saveChunk(chunk, name);
		saveTime = (System.nanoTime() - start2) / 1000000;
		noisemap = null;
		chunk = null;
		genTime = (System.nanoTime() - start) / 1000000;
	}

	/*
	 * Chunks End -------------------------------------------------------------
	 */
	
	public void saveLevel(){
		System.out.println("Saving level.");
		file.updateWorldData(name, seed, xPos, yPos);
		//Update level.dat file with relevant data.
	}

	private boolean checkListFor(int x, int y){
		String id = x + "_" + y;
		for(Chunk chunk : chunks) if(chunk.getId().equals(id)) return true;
		return false;
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
