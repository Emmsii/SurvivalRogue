package com.egs.survivalrogue.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import com.egs.survivalrogue.MainComponent;
import com.egs.survivalrogue.game.world.WorldGen;
import com.egs.survivalrogue.util.FileHandler;
import com.egs.survivalrogue.util.InputHandler;

public class CreateWorldMenu {

	private InputHandler input;
	private Menu menu;
	private MainComponent main;
	private FileHandler file;
	private WorldGen world;
	
	private Random random = new Random();
	private DateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
	private Date date = new Date();
		
	private String[] temp = { "Cold", "Warm", "Hot" };
	
	private int progress = 0;
	private int selection = 0;
	private int tempSelect = 0;
		
	public CreateWorldMenu(MainComponent main, InputHandler input, Menu menu, FileHandler file){
		this.main = main;
		this.input = input;
		this.menu = menu;
		this.file = file;
	}
	
	public void render(Graphics g){
		drawText("World Creation", 50, 40, 18, true, g);
		
		drawText("Tempterature: (" , 75, 75, 12, true, g);
		drawText(temp[tempSelect] + " )", 165, 75, 12, true, g);
		if(selection == 0) drawText(">", 65, 75, 12, true, g);
		
		drawText("Create World", 75, 350, 12, true, g);
		if(selection == 1) drawText(">", 65, 350, 12, true, g);
		
		if(progress > 0) drawProgress(g);
		
	}
	
	public void update(){
		if(progress == 100){
			menu.setState(5);
		}
		if(progress == 0){
			if(input.up.isPressed()){
				if(selection == 0) return;
				else selection--;
			}
			
			if(input.down.isPressed()){
				if(selection == 1) return;
				else selection++;
			}
			
			if(input.left.isPressed()){
				if(selection == 0){
					if(tempSelect == 0) return;
					else tempSelect--;
				}
			}
			
			if(input.right.isPressed()){
				if(selection == 0){
					if(tempSelect == (temp.length - 1)) return;
					else tempSelect++;
				}
			}
			
			if(input.esc.isPressed()){
				menu.setState(0);
			}
			
			if(input.select.isPressed()){
				if(selection == 1){
					generateStartingChunks();
					menu.setState(5);
				}
			}
		}
	}
	
	public void generateStartingChunks(){
		String[] names = file.loadNames("world_names");
//		wg = new Thread(new WorldGen(64, 64, random.nextLong(), tempSelect, names[random.nextInt(names.length)] + "_" + dateFormat.format(date), this, file));
//		wg.start();
	}
	
	public void drawProgress(Graphics g) {
		g.setColor(Color.WHITE);
		drawText(progress + "%", 338, 366, 10, true, g);
		g.drawRect(50, 370, 620, 5);
		g.fillRect(50, 370, (int) (progress * 6.2), 5);
	}
	
	public void drawText(String msg, int x, int y, int size, boolean bold, Graphics g){
		if(!bold) g.setFont(new Font("Arial", Font.PLAIN, size));
		if(bold) g.setFont(new Font("Arial", Font.BOLD, size));
		
		g.setColor(Color.BLACK);
		g.drawString(msg, x + 1, y + 1);
		g.setColor(Color.WHITE);
		g.drawString(msg, x, y);
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

}
