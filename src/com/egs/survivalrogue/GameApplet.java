package com.egs.survivalrogue;

import java.applet.Applet;
import java.awt.BorderLayout;

public class GameApplet extends Applet{

	private static final long serialVersionUID = 1L;

	private MainComponent main = new MainComponent();
	
	public void init(){
		setLayout(new BorderLayout());
		add(main, BorderLayout.CENTER);
	}
	
	public void start(){
		main.start();
	}
	
	public void stop(){
		main.stop();
	}
	
}
