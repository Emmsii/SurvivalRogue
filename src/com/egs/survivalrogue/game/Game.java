package com.egs.survivalrogue.game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Random;

import com.egs.survivalrogue.MainComponent;
import com.egs.survivalrogue.game.level.Level;
import com.egs.survivalrogue.util.FileHandler;
import com.egs.survivalrogue.util.InputHandler;

public class Game {

	private InputHandler input;
	private MainComponent main;
	private FileHandler file;
	private Level level;
	
	private Random random = new Random();
	
	private boolean debug = true;
	private boolean running = false;
	private boolean escMenu = false;
	private boolean canSave = true;
	private int selection = 0;
		
	public Game(InputHandler input, MainComponent main, FileHandler file){
		System.out.println("New Game");
		this.input = input;
		this.main = main;
		this.file = file;
	}
	
	public void init(String worldName){
		if(!running){
			level = new Level(random.nextLong(), worldName, main, file, input);
			level.init();
			running = true;
		}
	}
	
	public void render(Graphics g){
		if(running){
			level.render(g);
			drawInterface(g);
		}
	}
	
	public void update(){
		if(running){
			if(!escMenu) level.update();
			input();
		}
	}
	
	public void input(){
		if(input.debug.isPressed()){
			if(!debug) debug = true;
			else debug = false;
		}
		
		if(input.esc.isPressed()){
			if(!escMenu) escMenu = true;
			else escMenu = false;
			canSave = true;
		}
		
		if(escMenu){
			if(input.down.isPressed()){
				if(selection == 1) return;
				else selection++;
			}
			
			if(input.up.isPressed()){
				if(selection == 0) return;
				else selection--;
			}
			
			if(input.select.isPressed()){
				if(selection == 0){
					if(canSave)level.saveLevel();
					canSave = false;
				}
				if(selection == 1){
					main.init();
				}
			}
		}
	}
	
	public void drawInterface(Graphics g){
		drawText("GAME INTERFACE HERE", 545, 25, 12, true, g);
				
		//Must be last
		if(debug) drawDebug(g);
		if(escMenu) drawEscWindow(194, 157, 150, 40, g);
		
	}
	
	public void drawDebug(Graphics g){
		drawText("DEBUG MODE! Via game class", 50, 50, 12, true, g);
		drawText(main.getVersion(), 2, 10, 9, false, g);
	}
	
	public void drawEscWindow(int x, int y, int w, int h, Graphics g){
		g.setColor(Color.WHITE);
		g.fillRect(x - 2, y - 2, w + 4, h + 4);
		g.setColor(Color.BLACK);
		g.fillRect(x, y, w, h);
		
		if(canSave) drawText("Save", x + 61, y + 16, 12, true, g);
		else drawText("Save", x + 61, y + 16, 12, false, g);
		drawText("Exit", x + 64, y + 32, 12, true, g);
		
		if(selection == 0){
			drawText(">", x + 50, y + 16, 12, true, g);
			drawText("<", x + 93, y + 16, 12, true, g);
		}
		if(selection == 1){
			drawText(">", x + 53, y + 32, 12, true, g);
			drawText("<", x + 90, y + 32, 12, true, g);
		}
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
