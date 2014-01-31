package com.egs.survivalrogue.util;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import com.egs.survivalrogue.MainComponent;

public class InputHandler implements KeyListener{
	
	public InputHandler(MainComponent main){
		main.addKeyListener(this);
	}
	
	public class Key{
		public int numPressed;
		public boolean pressed = false;
		
		public Key(){
			keys.add(this);
		}
		
		public int getNumPressed(){
			return numPressed;
		}
		
		public boolean isPressed(){
			return pressed;
		}
		
		public void toggle(boolean isPressed){
			pressed = isPressed;
			if(pressed){
				numPressed++;
			}
		}
	}
	
	public List<Key> keys = new ArrayList<Key>();
	
	public Key up = new Key();
	public Key down = new Key();
	public Key left = new Key();
	public Key right = new Key();
	
	public Key select = new Key();
	public Key esc = new Key();
	
	public Key debug = new Key();
	public Key reload = new Key();
	
	public Key tempView = new Key();
	public Key rainView = new Key();
	
	public void release(){
		for(int i = 0; i < keys.size(); i++){
			keys.get(i).pressed = false;
		}
	}
	
	public void keyPressed(KeyEvent e) {
		toggleKey(e.getKeyCode(), true);
	}

	public void keyReleased(KeyEvent e) {
		toggleKey(e.getKeyCode(), false);
	}

	public void keyTyped(KeyEvent e) {
		
	}
	
	public void toggleKey(int keyCode, boolean isPressed){
		if(keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_UP) up.toggle(isPressed);
		if(keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_DOWN) down.toggle(isPressed);
		if(keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT) left.toggle(isPressed);
		if(keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT) right.toggle(isPressed);
		
		if(keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_SPACE) select.toggle(isPressed);
		if(keyCode == KeyEvent.VK_ESCAPE) esc.toggle(isPressed);
		
		if(keyCode == KeyEvent.VK_F3) debug.toggle(isPressed);
		if(keyCode == KeyEvent.VK_F4) tempView.toggle(isPressed);
		if(keyCode == KeyEvent.VK_F5) rainView.toggle(isPressed);
		if(keyCode == KeyEvent.VK_F) reload.toggle(isPressed);
	}
}