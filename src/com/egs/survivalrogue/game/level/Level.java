package com.egs.survivalrogue.game.level;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import com.egs.survivalrogue.game.level.objects.Obj;
import com.egs.survivalrogue.game.world.Chunk;
import com.egs.survivalrogue.util.FileHandler;
import com.egs.survivalrogue.util.InputHandler;
import com.egs.survivalrogue.util.Noise;

public class Level {

	private InputHandler input;
	private FileHandler file;
	private Chunk chunk;
	private Noise noise;
	private Random randObj;

	private String name;
	private long objSeed;
	private long seed;

	private boolean debug = false;
	private boolean tempView = false;
	private boolean rainView = false;
	private static final int renderDistance = 3 * 16;
	private int chunksGenerated = 0;
	private int chunksLoaded = 0;
	private int obj = 0;
	
	private boolean moving;
	
	private double loadTime = 0;
	private double saveTime = 0;
	private double genTime = 0;

	private int xPos = 0;
	private int yPos = 0;
	
	double min = 0;
	double max = 0;

	private List<Chunk> chunks = new ArrayList<Chunk>();

	//TODO: Move all world/chunk generation code into a WorldGen class.
	
	public Level(long seed, String worldName, FileHandler file, InputHandler input){
		System.out.println("New Level");
		this.seed = seed;
		this.name = worldName;
		this.file = file;
		this.input = input;
		this.noise = new Noise();
		randObj = new Random(seed);
	}
	
	public Level(String worldName, FileHandler file, InputHandler input){
		System.out.println("Load level");
		this.name = worldName;
		this.file = file;
		this.input = input;
		this.noise = new Noise();
		loadLevel(worldName);
		randObj = new Random(seed);
	}

	public void init(){
		file.newWorldFolder(name); //Save the world folder.
		file.newWorldData(name, seed, xPos, yPos, obj); //Create and save the world data.dat file.

		loadChunks(); //Creates, saves and loads spawn chunks.
		moving = false;
		objSeed = seed + obj;
		System.out.println("New world: " + name + " (Seed: " + seed + ")");
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
	
	public void saveLevel(){
		file.updateWorldData(name, seed, xPos, yPos, obj);
	}

	public void render(Graphics g){
		renderChunks(g); //Renders the chunks loaded.
		renderObjects(g);
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
		
		xPos++;
		moving = true;

		if(input.debug.isPressed()){
			if(!debug) debug = true;
			else debug = false;
		}
		
		if(input.tempView.isPressed()){
			if(!tempView) tempView = true;
			else tempView = false;
		}
		
		if(input.rainView.isPressed()){
			if(!rainView) rainView = true;
			else rainView = false;
		}

		if(input.reload.isPressed()) reloadAllChunks();
	}

	public void renderTile(String icon, Color first, Color second, int x, int y, Graphics g){
		g.setColor(first);
		g.fillRect(x - 1, y - 11, 13, 13);
		
		g.setColor(second);
		g.setFont(new Font("Arial", Font.BOLD, 10));
		g.drawString(icon, x, y);
	
	}
	
	public void renderTile(Color color, int x, int y, Graphics g){
		g.setColor(color);
		g.fillRect(x - 1, y - 11, 13, 13);
	}

	public void renderDebug(Graphics g){
		drawText("cl: " + chunksLoaded + "/" + chunksGenerated, 30, 10, 9, false, g);
		drawText("X (top left): " + xPos, 3, 22, 9, false, g);
		drawText("Y (top left): " + yPos, 2, 33, 9, false, g);
		
		drawText("Last Load Time: " + loadTime, 540, 100, 12, true, g);
		drawText("Last Save Time: " + saveTime, 540, 111, 12, true, g);
		drawText("Last Gen Time: " + genTime, 540, 122, 12, true, g);
		
		drawText("MinRain: " + min, 350, 200, 12, true, g);
		drawText("MaxRan: " + max, 350, 212, 12, true, g);
		
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
	
	/*
	 * 0: Invalid
	 * 1: Water
	 * 2: Deep Water
	 * 3: Beach
	 * 4: Grass
	 * 5: Rock
	 * 6: Snow
	 */
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
				    
				    if(tempView){
				    	if(chunk.getTemp(x, y) >= -1.0 && chunk.getTemp(x, y) < -0.8) renderTile(new Color(255, 255, 255), xa, ya, g);
				    	else if(chunk.getTemp(x, y) >= -0.8 && chunk.getTemp(x, y) < -0.6) renderTile(new Color(189, 220, 240), xa, ya, g);
				    	else if(chunk.getTemp(x, y) >= -0.6 && chunk.getTemp(x, y) < -0.4) renderTile(new Color(41, 155, 217), xa, ya, g);
				    	else if(chunk.getTemp(x, y) >= -0.4 && chunk.getTemp(x, y) < -0.2) renderTile(new Color(74, 217, 198), xa, ya, g);
				    	else if(chunk.getTemp(x, y) >= -0.2 && chunk.getTemp(x, y) < 0.0) renderTile(new Color(75, 204, 153), xa, ya, g);
				    	else if(chunk.getTemp(x, y) >= 0.0 && chunk.getTemp(x, y) < 0.2) renderTile(new Color(104, 196, 71), xa, ya, g);
				    	else if(chunk.getTemp(x, y) >= 0.2 && chunk.getTemp(x, y) < 0.4) renderTile(new Color(130, 179, 52), xa, ya, g);
				    	else if(chunk.getTemp(x, y) >= 0.4 && chunk.getTemp(x, y) < 0.6) renderTile(new Color(214, 209, 45), xa, ya, g);
				    	else if(chunk.getTemp(x, y) >= 0.6 && chunk.getTemp(x, y) < 0.8) renderTile(new Color(214, 104, 45), xa, ya, g);
				    	else if(chunk.getTemp(x, y) >= 0.8) renderTile(new Color(245, 12, 12), xa, ya, g);
				    	
				    }else if(rainView){
				    	if(chunk.getRainfall(x, y) >= 0.0 && chunk.getRainfall(x, y) < 0.2) renderTile(new Color(0, 0, 0), xa, ya, g);
				    	else if(chunk.getRainfall(x, y) >= 0.2 && chunk.getRainfall(x, y) < 0.4) renderTile(new Color(0, 0, 30), xa, ya, g);
				    	else if(chunk.getRainfall(x, y) >= 0.4 && chunk.getRainfall(x, y) < 0.6) renderTile(new Color(0, 0, 60), xa, ya, g);
				    	else if(chunk.getRainfall(x, y) >= 0.6 && chunk.getRainfall(x, y) < 0.8) renderTile(new Color(0, 0, 90), xa, ya, g);
				    	else if(chunk.getRainfall(x, y) >= 0.7 && chunk.getRainfall(x, y) < 1.0) renderTile(new Color(0, 0, 120), xa, ya, g);
				    	else if(chunk.getRainfall(x, y) >= 1.0 && chunk.getRainfall(x, y) < 1.2) renderTile(new Color(0, 0, 150), xa, ya, g);
				    	else if(chunk.getRainfall(x, y) >= 1.2 && chunk.getRainfall(x, y) < 1.4) renderTile(new Color(0, 0, 180), xa, ya, g);
				    	else if(chunk.getRainfall(x, y) >= 1.4) renderTile(new Color(0, 0, 210), xa, ya, g);
				    }else{  
					    if(chunk.getTileId(x, y) == 1) renderTile("W", new Color(87, 165, 217), new Color(45, 119, 168), xa, ya, g);
					    else if(chunk.getTileId(x, y) == 2) renderTile("W", new Color(77, 145, 197), new Color(45, 119, 168), xa, ya, g);
					    else if(chunk.getTileId(x, y) == 3) renderTile("S", new Color(237, 225, 173), new Color(207, 194, 136), xa, ya, g);
					    else if(chunk.getTileId(x, y) == 4) renderTile(". .", new Color(125, 194, 52), new Color(85, 145, 20), xa + 1, ya - 1, g);
					    else if(chunk.getTileId(x, y) == 5) renderTile("M", new Color(115, 115, 115), new Color(77, 77, 77), xa, ya, g);
					    else if(chunk.getTileId(x, y) == 6) renderTile("", new Color(215, 237, 250), new Color(215, 237, 250), xa, ya, g);
					    else renderTile("X", new Color(0, 0, 0), new Color(255, 0, 0), xa, ya, g);
				    }
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
				if(x % 16 == 0 && y % 16 == 0){ //Special line, does magic, no touch.
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
		
		chunk = new Chunk(id, Math.abs(x / 16), Math.abs(y / 16));

		int[][] noisemap = noise.startNoise(16, 16, x, y, seed, 0.008, 0.4, 8, 16);
		//int[][] noisemap = noise.startNoise(16, 16, x, y, seed, 0.08, 0.4, 8, 16);		
		
		chunk.setHeight(noisemap);

		/*
		 * Height / Temp
		 * 0   :  0.0
		 * 64  :  0.5
		 * 255 :  2.0
		 */
		int waterRange = (70 - 0);
		int mountRange = (255 - 81);
		double newRange = (1.0 - 0.0);

		for(int ya = 0; ya < 16; ya++){
			for(int xa = 0; xa < 16; xa++){
				chunk.setTemp(xa, ya, 0.0);
				double result = 0.0;
				if(chunk.getHeight(xa, ya) > 80) result = chunk.getTemp(xa, ya) - (((chunk.getHeight(xa, ya) - 81) * newRange) / mountRange);
				else result = chunk.getTemp(xa, ya) - (((chunk.getHeight(xa, ya) - 81) * newRange) / waterRange);
				chunk.setTemp(xa, ya, result);
			}
		}
		
		for(int yb = 0; yb < 16; yb++){
			for(int xb = 0; xb < 16; xb++){
				chunk.setRainfall(xb, yb, 0.5);
				double result = 0.5;
				if(chunk.getHeight(xb, yb) > 80) result = chunk.getRainfall(xb, yb) + (((chunk.getHeight(xb, yb) - 81) * newRange) / mountRange); 
				//else result = chunk.getRainfall(xb, yb) + ((((chunk.getHeight(xb, yb) - 0) * newRange) / waterRange) / 2);
				chunk.setRainfall(xb, yb, result);
				if(result > max) max = result;
				if(result < min) min = result;
			}
		}
		
		/*
		 * 0: Invalid
		 * 1: Water
		 * 2: Deep Water
		 * 3: Beach
		 * 4: Grass
		 * 5: Rock
		 * 6: Snow
		 */
		for(int yc = 0; yc < 16; yc++){
			for(int xc = 0; xc < 16; xc++){
				if(noisemap[xc][yc] < 64) chunk.setTileId(xc, yc, 1); //Below 64: Water
				if(noisemap[xc][yc] < 35) chunk.setTileId(xc, yc, 2); //Below 50: Deep water
				if(noisemap[xc][yc] >= 64 && noisemap[xc][yc] < 72) chunk.setTileId(xc, yc, 3); //Beaches
				if(noisemap[xc][yc] >= 72) chunk.setTileId(xc, yc, 4); //Grass
				if(noisemap[xc][yc] >= 250) chunk.setTileId(xc, yc, 5); //Rock
				
				if(chunk.getTemp(xc, yc) < - 0.85 && chunk.getRainfall(xc, yc) > 0.55) chunk.setTileId(xc, yc, 6);
				if(chunk.getTemp(xc, yc) > 0.85 && chunk.getRainfall(xc, yc) < 0.1) chunk.setTileId(xc, yc, 3);
				
			}
		}
		
		
		//TODO: Place objects
		
		for(int yd = 0; yd < 16; yd++){
			for(int xd = 0; xd < 16; xd++){			
				int dens = (int) (chunk.getRainfall(xd, yd) + chunk.getTemp(xd, yd) * 10);
				if(dens < 0) dens = dens * -1;
				if(chunk.getTemp(xd, yd) > -0.45 && chunk.getTemp(xd, yd) < 0.5){
					if(chunk.getRainfall(xd, yd) > 0.0){
						if(dens >= randObj.nextInt(25)){
							if(chunk.getTileId(xd, yd) == 4){
								//Place normal trees
								chunk.addObject(new Obj(xd, yd, 1));
								obj++;
							}
						}
					}
				}
				
				if(chunk.getTemp(xd, yd) > -0.99 && chunk.getTemp(xd, yd) < -0.40){
					if(chunk.getRainfall(xd, yd) > 0.3){
						if(dens >= randObj.nextInt(25)){
							if(chunk.getTileId(xd, yd) == 4 || chunk.getTileId(xd, yd) == 6 || chunk.getTileId(xd, yd) == 5){
								//Place pine trees
								chunk.addObject(new Obj(xd, yd, 2));
								obj++;
							}
						}
					}
				}
				
				if(chunk.getTemp(xd, yd) > 0.4 && chunk.getTemp(xd, yd) < 0.8){
					if(chunk.getRainfall(xd, yd) > 1.0){
						if(dens >= randObj.nextInt(25)){
							if(chunk.getTileId(xd, yd) == 4 || chunk.getTileId(xd, yd) == 6 || chunk.getTileId(xd, yd) == 5){
								//Place jungle trees
								chunk.addObject(new Obj(xd, yd, 2));
								obj++;
							}
						}
					}
				}
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
	
	public void renderObjects(Graphics g){
		for(Chunk chunk : chunks){
			for(Obj obj : chunk.getObjects()){
				int x = ((obj.getX() + (chunk.getX() * 16) - xPos) * 12);
				int y = ((obj.getY() + (chunk.getY() * 16) - yPos) * 12) + 10;
				
				if(obj.getX() + (chunk.getX() * 16) - xPos > 43) continue;
				if(obj.getX() + (chunk.getX() * 16) - xPos < 0) continue;
				if(obj.getY() + (chunk.getY() * 16) - yPos > 33) continue;
				if(obj.getY() + (chunk.getY() * 16) - yPos < 0) continue;
				    
				int type = obj.getType();
				renderObjTile(x, y, type, obj, chunk, g);
			}
		}
	}
	
	public void renderObjTile(int x, int y, int type, Obj obj, Chunk chunk, Graphics g){
		g.setFont(new Font("Arial", Font.BOLD, 12));
		if(type == 1){
			g.setColor(new Color(108, 151, 49));
			g.drawString("T", x, y);
		}
		if(type == 2){
			g.setColor(new Color(24, 138, 55));
			g.drawString("T", x, y);
		}
		if(type == 3){
			g.setColor(new Color(63, 219, 20));
			g.drawString("T", x, y);
		}
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
