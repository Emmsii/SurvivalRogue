package com.egs.survivalrogue.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JOptionPane;

import com.egs.survivalrogue.MainComponent;
import com.egs.survivalrogue.game.Game;
import com.egs.survivalrogue.util.FileHandler;
import com.egs.survivalrogue.util.InputHandler;

public class CreatePlayerMenu {

	private InputHandler input;
	private Menu menu;
	private Game game;
	private MainComponent main;
	private FileHandler file;
	
	private String name = "Unknown";
	private String[] gender = { "Male", "Female" };
	private String[] race = { "Human", "Orc", "Dwarf", "Elf" };
	private String[] pClass = { "Warrior", "Archer", "Mage" };
	
	private int selection = 0;
	private int genderSelect = 0;
	private int raceSelect = 0;
	private int classSelect = 0;
	
	public CreatePlayerMenu(InputHandler input, Menu menu, Game game, MainComponent main, FileHandler file){
		this.input = input;
		this.menu = menu;
		this.game = game;
		this.main = main;
		this.file = file;
	}
	
	public void render(Graphics g){
		drawText("Character Creation", 50, 40, 18, true, g);
		
		drawText("Name: " + name, 75, 75, 12, true, g);
		if(selection == 0) drawText(">", 65, 75, 12, true, g);
		
		drawText("Gender: (" , 75, 90, 12, true, g);
		drawText(gender[genderSelect] + " )", 130, 90, 12, true, g);
		if(selection == 1) drawText(">", 65, 90, 12, true, g);
		
		drawText("Race: (" , 75, 105, 12, true, g);
		drawText(race[raceSelect] + " )", 117, 105, 12, true, g);
		if(selection == 2) drawText(">", 65, 105, 12, true, g);
		
		drawText("Class: (" , 75, 120, 12, true, g);
		drawText(pClass[classSelect] + " )", 121, 120, 12, true, g);
		if(selection == 3) drawText(">", 65, 120, 12, true, g);
		
		drawText("Begin", 75, 350, 12, true, g);
		if(selection == 4) drawText(">", 65, 350, 12, true, g);
	}
	
	public void update(){
		if(input.up.isPressed()){
			if(selection == 0) return;
			else selection--;
		}
		
		if(input.down.isPressed()){
			if(selection == 4) return;
			else selection++;
		}
		
		if(input.right.isPressed()){
			if(selection == 0){
				name = JOptionPane.showInputDialog("Choose your name.");
			}
		}
		
		if(input.left.isPressed()){		
			if(selection == 1){
				if(genderSelect == 0) return;
				else genderSelect--;
			}
			
			if(selection == 2){
				if(raceSelect == 0) return;
				else raceSelect--;
			}
			
			if(selection == 3){
				if(classSelect == 0) return;
				else classSelect--;
			}
		}
		
		if(input.right.isPressed()){
			if(selection == 1){
				if(genderSelect == (gender.length - 1)) return;
				else genderSelect++;
			}
			
			if(selection == 2){
				if(raceSelect == (race.length - 1)) return;
				else raceSelect++;
			}
			
			if(selection == 3){
				if(classSelect == (pClass.length - 1)) return;
				else classSelect++;
			}
		}
		
		if(input.select.isPressed()){
			if(selection == 4){
				game.init();
				main.setState(1);
			}
		}
		
		if(input.esc.isPressed()){
			menu.setState(0);
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
