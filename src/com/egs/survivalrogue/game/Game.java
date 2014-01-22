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
		
	public Game(InputHandler input, MainComponent main, FileHandler file){
		System.out.println("New Game");
		this.input = input;
		this.main = main;
		this.file = file;
	}
	
	public void init(){
		if(!running){
			level = new Level(random.nextLong(), main, file, input);
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
			level.update();
			input();
		}
	}
	
	public void input(){
		if(input.debug.isPressed()){
			if(!debug) debug = true;
			else debug = false;
		}
	}
	
	public void drawInterface(Graphics g){
		drawText("GAME INTERFACE HERE", 545, 25, 12, true, g);
				
		//Must be last
		if(debug) drawDebug(g);
	}
	
	public void drawDebug(Graphics g){
		drawText("DEBUG MODE! Via game class", 50, 50, 12, true, g);
		drawText(main.getVersion(), 2, 10, 9, false, g);
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
