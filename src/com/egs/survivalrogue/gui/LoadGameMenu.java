package com.egs.survivalrogue.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.egs.survivalrogue.util.FileHandler;
import com.egs.survivalrogue.util.InputHandler;

public class LoadGameMenu {

	private InputHandler input;
	private FileHandler file;
	private Menu menu;
	
	private String[] worlds;
	private String[] worldData;
	
	private int selection = 0;
	
	public LoadGameMenu(InputHandler input, Menu menu, FileHandler file){
		this.input = input;
		this.menu = menu;
		this.file = file;
		
	}
	
	public void render(Graphics g){
		drawText("Load World", 50, 40, 18, true, g);

		drawList(g);
		drawInfo(g);
	}
	
	public void update(){
		worlds = file.loadWorlds();
		
		if(input.esc.isPressed()){
			menu.setState(0);
		}
		
		if(input.up.isPressed()){
			if(selection == 0) return;
			else selection--;
		}
		
		if(input.down.isPressed()){
			if(selection == worlds.length - 1) return;
			else selection++;
		}
	}

	public void drawList(Graphics g){

		
		for(int i = 0; i < worlds.length; i++){
			String[] parts = worlds[i].split("_");
					
			drawText(parts[0], 75, 75 + (12 * i), 12, true, g);
			if(selection == i) drawText(">", 65, 75 + (12 * i), 12, true, g);
		}
		
		
		
//		for(int i = 0; i < console.size(); i++){
//			String msg = console.get(i);
//			
//			if(console.size() <= 16) g.drawString("> " + msg, 513, 215 + (i * 12));
//			else if(i >= console.size() - 16)g.drawString("> " + msg, 513, 215 + ((i - (console.size() - 16)) * 12));
//		}
	}
	
	public void drawInfo(Graphics g){
		drawText("World Info", 300, 75, 15, true, g);
		worldData = file.getWorldData(worlds[selection]);
		if(worldData[0].equals("Error: level.dat not found in world folder.")){
			drawText(worldData[0], 310, 95, 12, true, g);
			return;
		}
		
		String raw = worldData[0];
		String[] dateAr = raw.split("_");
		String date = dateAr[1];
		String finalDate = date.substring(0, 2) + "/" + date.substring(2, 4) + "/" + date.substring(4, 8);
		String temp = null;
		
		if(worldData[3].equals("0")) temp = "Cold";
		if(worldData[3].equals("1")) temp = "Mild";
		if(worldData[3].equals("2")) temp = "Warm";
		
		drawText("Created: " + finalDate, 310, 95, 12, true, g);
		drawText("Size: " + worldData[1] + "x" + worldData[2], 310, 107, 12, true, g);
		drawText("Temp: " + temp, 310, 119, 12, true, g);
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
