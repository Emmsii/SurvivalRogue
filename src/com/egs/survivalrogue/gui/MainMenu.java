package com.egs.survivalrogue.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.egs.survivalrogue.util.InputHandler;

public class MainMenu {
	
	private InputHandler input;
	private Menu menu;
	
	private String[] options = { "Start Game", "Secret Menu", "Misc Menu" };
	private String[] subMenu = { "New Game", "Load Game" };
	
	private int selection = 0;
	private int subSelect = 0;
	private boolean inSub = false;
	
	public MainMenu(InputHandler input, Menu menu){
		this.input = input;
		this.menu = menu;
	}
	
	public void render(Graphics g){
		for(int i = 0; i < options.length; i++){
			drawText(options[i], 52, 50 + (12 * i), 12, true, g);
			if(selection == i) drawText(">", 42, 50 + (12 * i), 12, true, g);
		}
		
		if(inSub){
			for(int i = 0; i < subMenu.length; i++){
				drawText(subMenu[i], 145, 50 + (12 * i), 12, true, g);
				if(subSelect == i) drawText(">", 135, 50 + (12 * i), 12, true, g);
			}
		}
	}
	
	public void update(){
		if(input.up.isPressed() && !inSub){
			if(selection == 0) return;
			else selection--;
		}else if(input.up.isPressed() && inSub){
			if(subSelect == 0) return;
			else subSelect--;
		}
				
		if(input.down.isPressed() && !inSub){
			if(selection == (options.length - 1)) return;
			else selection++;
		}else if(input.down.isPressed() && inSub){
			if(subSelect == (subMenu.length - 1)) return;
			else subSelect++;
		}
		
		if(input.left.isPressed() && inSub){
			inSub = false;
			selection = 0;
		}
		
		if(input.right.isPressed() && !inSub){
			if(selection == 0){
				inSub = true;
				subSelect = 0;
			}
		}
		
		if(input.select.isPressed() && selection == 0){
			inSub = true;
			for(int i = 0; i < subMenu.length; i++){
				if(i == subSelect) menu.setState(i + 3);
				inSub = false;
			}
		}else if(input.select.isPressed() && !inSub){
			for(int i = 0; i < options.length; i++){
				if(i == selection) menu.setState(i);
			}
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
