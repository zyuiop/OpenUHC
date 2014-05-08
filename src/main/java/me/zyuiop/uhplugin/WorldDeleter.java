package me.zyuiop.uhplugin;

import java.io.File;
import java.io.FileNotFoundException;

public class WorldDeleter extends Thread {
	private final String wname;
	public WorldDeleter(String world) {
		this.wname = world;
	}
	private void deleteDir(File f) throws FileNotFoundException {
		if (f.isDirectory()) {
		  for (File c : f.listFiles())
		    deleteDir(c);
		}
		if (!f.delete())
		  throw new FileNotFoundException();
	}
	public void run() {
		try {
			deleteDir(new File(wname));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
