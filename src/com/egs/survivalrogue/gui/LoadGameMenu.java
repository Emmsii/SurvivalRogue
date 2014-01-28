package com.egs.survivalrogue.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.egs.survivalrogue.MainComponent;
import com.egs.survivalrogue.game.Game;
import com.egs.survivalrogue.util.FileHandler;
import com.egs.survivalrogue.util.InputHandler;

public class LoadGameMenu {

	private MainComponent main;
	private Game game;
	private InputHandler input;
	private FileHandler file;
	private Menu menu;
		
	private String[] worlds;
	private String[] worldData;
	private String gender = null;
	private String race = null;
	private String pClass = null;
	
	private boolean canSelect = false;
	private int selection = 0;
	
	public LoadGameMenu(MainComponent main, Game game, InputHandler input, Menu menu, FileHandler file){
		this.main = main;
		this.game = game;
		this.input = input;
		this.menu = menu;
		this.file = file;
	}
	
	public void render(Graphics g){
		drawText("Load World", 50, 40, 18, true, g);
		drawText("Press ENTER to load the world.", 50, 52, 10, true, g);

		drawList(g);
		drawInfo(g);
	}
	
	public void update(){
		double start = System.nanoTime();
		worlds = file.loadWorlds();
		System.out.println((System.nanoTime() - start) / 1000000 + "ms");
		if(canSelect){
			if(input.esc.isPressed()){
				menu.setState(0);
				selection = 0;
				canSelect = false;
				return;
			}
			
			if(input.up.isPressed()){
				if(selection == 0) return;
				else selection--;
			}
			
			if(input.down.isPressed()){
				if(selection == worlds.length - 1) return;
				else selection++;
			}
			
			if(input.select.isPressed()){
				//Load the level selected.
				System.out.println("Loading selected: " + selection);
				game.loadLevel(worlds[selection]);
				game.setRunning(true);
				main.setState(1);
				menu.setState(0);
				selection = 0;
				canSelect = false;
				
			}
		}
		canSelect = true;
	}

	public void drawList(Graphics g){
		for(int i = 0; i < worlds.length; i++){
			String[] parts = worlds[i].split("_");
					
			drawText(parts[0], 75, 75 + (12 * i), 12, true, g);
			if(selection == i) drawText(">", 65, 75 + (12 * i), 12, true, g);
		}
		
		
		for(int i = 0; i < worlds.length; i++){
			String[] parts = worlds[i].split("_");
					
			drawText(parts[0], 75, 75 + (12 * i), 12, true, g);
				if(selection == i) drawText(">", 65, 75 + (12 * i), 12, true, g);
		}
	}
	
	public void drawInfo(Graphics g){
		drawText("World Info", 300, 75, 15, true, g);
		worldData = file.getWorldData(worlds[selection]);
		if(worldData[0].equals("Error: level.dat not found in world folder.")){
			drawText(worldData[0], 310, 95, 12, true, g);
		}else{
			String rawName = worldData[0];
			String[] dateAr = rawName.split("_");
			String date = dateAr[1];
			String finalDate = date.substring(0, 2) + "/" + date.substring(2, 4) + "/" + date.substring(4, 8);
	
			String rawSeed = worldData[1];
			String[] seedAr = rawSeed.split(":");
			String seed = seedAr[1];
			
			drawText("Created: " + finalDate, 310, 95, 12, true, g);
			drawText("Seed: " + seed, 310, 107, 12, true, g);
		}
			
		drawText("Player Info", 300, 130, 15, true, g);
		String[] playerData = file.getPlayerInfo(worlds[selection]);
		
		if(playerData[0].equals("Error: player.dat not found in world folder.")){
			drawText(playerData[0], 310, 150, 12, true, g);
		}else{
			String rawPName = playerData[0];
			String[] pNameAr = rawPName.split(":");
			String pName = pNameAr[1];
			
			String rawGender = playerData[1];
			String[] genderAr = rawGender.split(":");
			if(genderAr[1].equalsIgnoreCase(" 0")) gender = "Male";
			if(genderAr[1].equalsIgnoreCase(" 1")) gender = "Female";

			String rawRace = playerData[2];
			String[] raceAr = rawRace.split(":");
			if(raceAr[1].equalsIgnoreCase(" 0")) race = "Human";
			if(raceAr[1].equalsIgnoreCase(" 1")) race = "Orc";
			if(raceAr[1].equalsIgnoreCase(" 2")) race = "Dwarf";
			if(raceAr[1].equalsIgnoreCase(" 3")) race = "Elf";

			String rawClass = playerData[3];
			String[] classAr = rawClass.split(":");
			
			if(classAr[1].equalsIgnoreCase(" 0")) pClass = "Warrior";
			if(classAr[1].equalsIgnoreCase(" 1")) pClass = "Archer";
			if(classAr[1].equalsIgnoreCase(" 2")) pClass = "Mage";
					
			
			drawText("Name: " + pName, 310, 150, 12, true, g);
			drawText("Gender: " + gender, 310, 162, 12, true, g);
			drawText("Race: " + race, 310, 174, 12, true, g);
			drawText("Class: " + pClass, 310, 186, 12, true, g);
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
