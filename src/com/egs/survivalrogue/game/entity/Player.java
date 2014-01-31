package com.egs.survivalrogue.game.entity;

public class Player extends Mob{

	protected String name;
	protected int pClass;
	protected int race;
	protected int gender;	
	
	public Player(String name, int pClass, int race, int gender){
		this.name = name;
		this.pClass = pClass;
		this.race = race;
		this.gender = gender;
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


	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}
}
