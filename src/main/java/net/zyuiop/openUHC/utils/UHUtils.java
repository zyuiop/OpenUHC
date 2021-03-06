package net.zyuiop.openUHC.utils;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class UHUtils {
	public static int showProgress(double done, double work) {
		if ((done/work) == 0.1)
			return 10;
		if ((done/work) == 0.2)
			return 20;
		if ((done/work) == 0.3)
			return 30;
		if ((done/work) == 0.4)
			return 40;
		if ((done/work) == 0.5)
			return 50;
		if ((done/work) == 0.6)
			return 60;
		if ((done/work) == 0.7)
			return 70;
		if ((done/work) == 0.8)
			return 80;
		if ((done/work) == 0.9)
			return 90;
		return 0;
	}
	

	public static ChatColor getCol(int num) {
		switch (num) {
		case 5:
			return ChatColor.DARK_BLUE;
		case 6:
			return ChatColor.DARK_GREEN;
		case 7:
			return ChatColor.DARK_RED;
		case 8:
			return ChatColor.DARK_AQUA;
		case 9:
			return ChatColor.DARK_PURPLE;
		case 1:
			return ChatColor.GOLD;
		case 2:
			return ChatColor.BLUE;
		case 3:
			return ChatColor.GREEN;
		case 0:
			return ChatColor.AQUA;
		case 4:
			return ChatColor.RED;
		case 10:
			return ChatColor.LIGHT_PURPLE;
		case 11:
			return ChatColor.GRAY;
		default:
			return ChatColor.WHITE;
		}
	}
	
	public static int randomInt(int aStart, int aEnd){
		 Random aRandom = new Random();
		 if (aStart > aEnd) {
		    throw new IllegalArgumentException("Start cannot exceed End.");
		 }
		 //get the range, casting to long to avoid overflow problems
		 long range = (long)aEnd - (long)aStart + 1;
		 // compute a fraction of the range, 0 <= frac < range
		 long fraction = (long)(range * aRandom.nextDouble());
		 int randomNumber =  (int)(fraction + aStart);    
		 return randomNumber;
	}
	
	public static String parseColors(String c) {
		if (c == null) {
			Bukkit.getLogger().warning("parseColors called with an empty argument. This is not good");
			return null;
		}
		
		return c.replaceAll("(?i)&([a-f0-9])", "\u00A7$1");
	}
	
	public static int getClosestChestSize(int size) {
		if (size%9 == 0)
			return size;
		else
			return (int) (Math.ceil(size/9)*9);
	}
	

}
