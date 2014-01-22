package com.egs.survivalrogue.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.egs.survivalrogue.util.InputHandler;

public class SecretMenu {
	
	private InputHandler input;
	private Menu menu;
	
	public SecretMenu(InputHandler input, Menu menu){
		this.input = input;
		this.menu = menu;
	}
	
	public void render(Graphics g){
		drawText("Secrets", 100, 100, 12, true, g);
	}
	
	public void update(){
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
