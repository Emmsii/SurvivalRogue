package com.egs.survivalrogue.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.egs.survivalrogue.MainComponent;
import com.egs.survivalrogue.game.Game;
import com.egs.survivalrogue.util.FileHandler;
import com.egs.survivalrogue.util.InputHandler;

public class Menu {
	
	private MainMenu mainMenu;
	private SecretMenu secretMenu;
	private MiscMenu miscMenu;
	private CreateWorldMenu createWorldMenu;
	private CreatePlayerMenu createPlayerMenu;
	private LoadGameMenu loadGameMenu;
	
	private int state = 0;
	
	public Menu(InputHandler input, Game game, MainComponent main, FileHandler file){
		mainMenu = new MainMenu(input, this);
		secretMenu = new SecretMenu(input, this);
		miscMenu = new MiscMenu(input, this);
		createWorldMenu = new CreateWorldMenu(main, input, this, file);
		createPlayerMenu = new CreatePlayerMenu(input, this, game, main, file);
		loadGameMenu = new LoadGameMenu(input, this, file);
	}
	
	public void render(Graphics g){
		//Render some form of background
		
		if(state == 0) mainMenu.render(g); //Render the main menu.
		if(state == 1) secretMenu.render(g); //Render the secret menu.
		if(state == 2) miscMenu.render(g); //Render the misc menu.
		if(state == 3) createPlayerMenu.render(g); //Render the player creation menu.
		if(state == 4) loadGameMenu.render(g); //Render the load game menu.
		if(state == 5) createWorldMenu.render(g); //Render the world creation menu.
	}
	
	public void update(){
		if(state == 0) mainMenu.update(); //Update the main menu.
		if(state == 1) secretMenu.update(); //Update the secret menu.
		if(state == 2) miscMenu.update(); //Update the misc menu.
		if(state == 3) createPlayerMenu.update(); //Update the player creation menu.
		if(state == 4) loadGameMenu.update(); //Update the load game menu.
		if(state == 5) createWorldMenu.update(); //Update the world creation menu.
	}

	public void drawText(String msg, int x, int y, int size, boolean bold, Graphics g){
		if(!bold) g.setFont(new Font("Arial", Font.PLAIN, size));
		if(bold) g.setFont(new Font("Arial", Font.BOLD, size));
		
		g.setColor(Color.BLACK);
		g.drawString(msg, x + 1, y + 1);
		g.setColor(Color.WHITE);
		g.drawString(msg, x, y);
	}
	
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
}
