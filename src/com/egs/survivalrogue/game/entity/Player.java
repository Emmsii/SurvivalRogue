package com.egs.survivalrogue.game.entity;

public class Player extends Mob{

	protected String name;
	protected int pClass;
	protected int race;
	
	
	public Player(String name, int pClass, int race){
		this.name = name;
		this.pClass = pClass;
		this.race = race;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public int getpClass() {
		return pClass;
	}


	public void setpClass(int pClass) {
		this.pClass = pClass;
	}


	public int getRace() {
		return race;
	}


	public void setRace(int race) {
		this.race = race;
	}
}
