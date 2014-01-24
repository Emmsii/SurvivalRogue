package com.egs.survivalrogue.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.egs.survivalrogue.game.world.Chunk;

public class FileHandler {
	
	private String location = System.getenv("APPDATA") + "/.survivalrogue";
	private File file;

	public FileHandler(){
		file = new File(location + "/worlds/");
		if(!file.exists()) file.mkdirs();
		file = new File("res/");
		if(!file.exists()) file.mkdirs();
	}
	
	public void newWorldFolder(String name){
		file = new File(location + "/worlds/" + name + "/data");
		if(!file.exists()) file.mkdirs();
	}
	
	public void newWorldData(String name, long seed, int x, int y){
		file = new File(location + "/worlds/" + name + "/level.dat");
		if(!file.exists())
			try {
				PrintWriter write = new PrintWriter(file);
				file.createNewFile();
				write.println("name: " + name);
				write.println("seed: " + seed);
				write.println("x: " + x);
				write.println("y: " + y);
				write.close();
			} catch (IOException e){
				System.err.println("Error: Could not make level data file.");
				e.printStackTrace();
			}
	}
	
	public void updateWorldData(String name, long seed, int x, int y){
		file = new File(location + "/worlds/" + name + "/level.dat");
		if(file.exists()){
			try{
				FileInputStream in = new FileInputStream(file);
				BufferedReader reader = new BufferedReader(new FileReader(file));
				if(reader.readLine().startsWith("name: " + name)){
					PrintWriter write = new PrintWriter(file);
					write.println("name: " + name);
					write.println("seed: " + seed);
					write.println("x: " + x);
					write.println("y: " + y);
					write.close();
				}else System.err.println("Error: level.dat file id invalid for world: " + name);
				in.close();
				reader.close();
			}catch(IOException e){
				System.err.println("Could not save level.dat file for world: " + name);
				e.printStackTrace();
			}
		}else System.err.println("Error: level.dat file does not exist for world: " + name);
	}
	
	public void newPlayerFile(String worldName, String name, int gender, int race, int pClass){
		file = new File(location + "/worlds/" + worldName + "/player.dat");
		if(!file.exists()){
			try{
				PrintWriter write = new PrintWriter(file);
				file.createNewFile();
				write.println("name: " + name);
				write.println("gender: " + gender);
				write.println("race: " + race);
				write.println("class: " + pClass);
				write.close();
			}catch(IOException e){
				System.err.println("Error: Could not make player data file.");
				e.printStackTrace();
			}
		}
	}
	
	public String[] loadWorlds(){
		file = new File(location + "/worlds/");
		String[] worlds = file.list(new FilenameFilter(){
			public boolean accept(File dir, String name){
				return new File(dir, name).isDirectory();
			}
		});
		return worlds;		
	}
	
	public String[] getWorldData(String name){
		file = new File(location + "/worlds/" + name + "/level.dat");
		
		List<String> lines = new ArrayList<String>();
		String line = null;
		try{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			while((line = reader.readLine()) != null){
				lines.add(line);
			}
			reader.close();
			return lines.toArray(new String[lines.size()]);

		}catch(IOException e){
			String[] err = {"Error: level.dat not found in world folder."}; 
			return err; 
		}
	}
	
	public String[] getPlayerInfo(String name){
		file = new File(location + "/worlds/" + name + "/player.dat");
		
		List<String> lines = new ArrayList<String>();
		String line = null;
		try{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			while((line = reader.readLine()) != null){
				lines.add(line);
			}
			reader.close();
			return lines.toArray(new String[lines.size()]);
		}catch(IOException e){
			String[] err = {"Error: player.dat not found in world folder."};
			return err;
		}
	}
	
	public void saveChunk(Chunk chunk, String name){
		int x = chunk.getX();
		int y = chunk.getY();

		file = new File(location + "/worlds/" + name + "/data/c_" + x + "_" + y + ".cnk");
		
		try {
			FileOutputStream out = new FileOutputStream(file);
			if(!file.exists()) file.createNewFile();
			ObjectOutputStream oos = new ObjectOutputStream(out);
			oos.writeObject(chunk);
			oos.flush();
			oos.close();
			out.flush();
			out.close();
			
		} catch (FileNotFoundException e) {
			System.err.println("Error: Could not save chunk file: " + chunk.getId() + ".");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Chunk loadChunk(int x, int y, String name){
		file = new File(location + "/worlds/" + name + "/data/c_" + x + "_" + y + ".cnk");
		Chunk result = null;
		try{
			FileInputStream in = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(in);
			result = (Chunk) ois.readObject();
			ois.close();
			in.close();
		}catch(IOException e){
			System.err.println("Error: Could not load chunk file: " + x + "_" + y + ". Chunk is missing or corrupted.");
			e.printStackTrace();
		}catch(ClassNotFoundException e){
			e.printStackTrace();
		}
		return result;
	}
	
	public String[] loadNames(String type){
		if(type == null) return null;
		file = new File("res/names/" + type + ".txt");
		//InputStream is = this.getClass().getResourceAsStream("/res/names/" + type + ".txt");

		List<String> lines = new ArrayList<String>();
		String line = null;
		
		try{
			//BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			BufferedReader reader = new BufferedReader(new FileReader(file));
			while((line = reader.readLine()) != null){
				lines.add(line);
			}
			reader.close();
			return lines.toArray(new String[lines.size()]);
		}catch(IOException e){
			e.printStackTrace();
		}
		return null;
	}

	public boolean checkFileFor(int x, int y, String name){
		file = new File(location + "/worlds/" + name + "/data/c_" + x + "_" + y + ".cnk");
		if(file.exists()) return true;
		return false;
	}
	
}
