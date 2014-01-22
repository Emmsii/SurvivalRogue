package com.egs.survivalrogue.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
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
	}
	
	public void newWorldFolder(String name){
		file = new File(location + "/worlds/" + name + "/data");
		if(!file.exists()) file.mkdirs();
	}
	
	public void newWorldData(String name, long seed){
		file = new File(location + "/worlds/" + name + "/level.dat");
		if(!file.exists())
			try {
				PrintWriter write = new PrintWriter(file);
				file.createNewFile();
				write.println(name);
				write.println(seed);
				write.close();
			} catch (IOException e) {
				e.printStackTrace();
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
	
	public void saveChunk(Chunk chunk, String name){
		int x = chunk.getX();
		int y = chunk.getY();
		
		int xa = x / 16;
		int ya = y / 16;

		file = new File(location + "/worlds/" + name + "/data/c_" + xa + "_" + ya + ".cnk");

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
			System.exit(16);
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
			System.err.println("Error: Could not load chunk file.");
			e.printStackTrace();
			System.exit(1);
			
		}catch(ClassNotFoundException e){
			e.printStackTrace();
		}
		return result;
	}
	
	public String[] loadNames(String type){
		if(type == null) return null;
		file = new File("res/names/" + type + ".txt");

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
