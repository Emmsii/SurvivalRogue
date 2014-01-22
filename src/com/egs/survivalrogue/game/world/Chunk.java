package com.egs.survivalrogue.game.world;

import java.io.Serializable;

public class Chunk implements Serializable{

	private static final long serialVersionUID = 1L;
	
	protected String id;
	protected int x;
	protected int y;
	protected int[][] height;
	protected int[][] tileId;

	public Chunk(String id){
		this.id = id;
		height = new int[16][16];
		tileId = new int[16][16];
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int[][] getHeight() {
		return height;
	}

	public void setHeight(int x, int y, int height) {
		this.height[x][y] = height;
	}

	public void setHeight(int[][] height) {
		this.height = height;
	}

	public int[][] getTileId() {
		return tileId;
	}

	public void setTileId(int[][] tileId) {
		this.tileId = tileId;
	}
	
	public void setTileId(int x, int y, int tile){
		this.tileId[x][y] = tile;
	}
	
	public int getTileId(int x, int y){
		return this.tileId[x][y];
	}
}
