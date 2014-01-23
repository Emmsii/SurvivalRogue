package com.egs.survivalrogue;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

import com.egs.survivalrogue.game.Game;
import com.egs.survivalrogue.gui.Menu;
import com.egs.survivalrogue.util.FileHandler;
import com.egs.survivalrogue.util.InputHandler;

public class MainComponent extends Canvas implements Runnable{

	private static final long serialVersionUID = 1L;
	
	private InputHandler input;
	private Menu menu;
	private Game game;
	private FileHandler file;
	
	private static int width = 720;
	private static int height = 405;
	private static final String VERSION = "v0.1a"  ;
	private static final String NAME = "Survival Rogue " + VERSION;
	private boolean running = false;

	private int fps;
	private int ups;
	
	private int state = 0;
	
	public MainComponent(){
		input = new InputHandler(this);
		file = new FileHandler();
		game = new Game(input, this, file);
		menu = new Menu(input, game, this, file);

	}
	
	public static void main(String[] args){
		MainComponent main = new MainComponent();
		Dimension size = new Dimension(width, height);
		main.setPreferredSize(size);
		main.setMaximumSize(size);
		main.setMinimumSize(size);
		
		JFrame frame = new JFrame(NAME);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(main);
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
			
		main.requestFocusInWindow();
		main.start();
	}

	public void start(){
		running = true;
		new Thread(this, "main").start();
	}
	
	public void stop(){
		running = false;
	}
	
	public void run() {
		long lastTime = System.nanoTime();
		double unprocessed = 0;
		double ns = 1000000000.0 / 60.0;
		int frames = 0;
		int updates = 0;
		long last = System.currentTimeMillis();
		while(running){
			long now = System.nanoTime();
			unprocessed += (now - lastTime) / ns;
			lastTime = now;
			boolean render = true;
			while(unprocessed >= 1){
				updates++;
				update();
				unprocessed -= 1;
				render  = true;
			}
			
			if(render){
				frames++;
				render();
			}
			
			if(System.currentTimeMillis() - last > 1000){
				last += 1000;
				fps = frames;
				ups = updates;
				System.out.println(ups + " ups, " + fps + " fps");
				frames = 0;
				updates = 0;
			}
		}
		stop();
	}
	
	public void render(){
		BufferStrategy bs = getBufferStrategy();
		if(bs == null){
			createBufferStrategy(3);
			return;
		}
		
		Graphics g = bs.getDrawGraphics();
		g.setColor(new Color(50, 50, 50));
		g.fillRect(0, 0, getWidth(), getHeight());
		

		if(state == 0) menu.render(g);
		if(state == 1) game.render(g);
		
		g.dispose();
		bs.show();
	}
	
	public void update(){
		if(state == 0) menu.update();
		if(state == 1){
			game.update();
			game.init();
		}

		input.release();
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
	
	public String getVersion(){
		return VERSION;
	}

}
