package com.egs.survivalrogue.game.world;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.egs.survivalrogue.game.level.objects.Obj;

public class Chunk implements Serializable{

	private static final long serialVersionUID = 1L;
	
	protected String id;
	protected int x;
	protected int y;
	protected int[][] height;
	protected double[][] temp;
	protected double[][] rainfall;
	protected int[][] tileId;

	protected List<Obj> objects = new ArrayList<Obj>();

	public Chunk(String id, int x, int y){
		this.id = id;
		this.x = x;
		this.y = y;
		height = new int[16][16];
		temp = new double[16][16];
		rainfall = new double[16][16];
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
	
	public int getHeight(int x, int y){
		return height[x][y];
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

	public int[][] getTemp() {
		return height;
	}
	
	public double getTemp(int x, int y){
		return temp[x][y];
	}

	public void setTemp(int x, int y, double temp) {
		this.temp[x][y] = temp;
	}

	public void setTemp(double[][] temp) {
		this.temp = temp;
	}
	
	public double[][] getRainfall() {
		return rainfall;
	}
	
	public double getRainfall(int x, int y){
		return rainfall[x][y];
	}

	public void setRainfall(double[][] rainfall) {
		this.rainfall = rainfall;
	}

	public void setRainfall(int x, int y, double rainfall){
		this.rainfall[x][y] = rainfall;
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
	
	public void addObject(Obj object){
		objects.add(object);
	}
	
	public void removeObject(Obj object){
		for(ListIterator<Obj> i = objects.listIterator(); i.hasNext();){
			Obj obj = i.next();
			if(object.equals(obj)) i.remove();
		}
	}

	public List<Obj> getObjects() {
		return objects;
	}
}
