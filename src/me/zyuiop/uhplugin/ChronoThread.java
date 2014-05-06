package me.zyuiop.uhplugin;

public class ChronoThread extends Thread {
  private UHPlugin pl;
 
  public ChronoThread(UHPlugin plugin) {
    this.pl = plugin;
  }
  public void run() {
	long time = 0;
    while (true) {
    	try {
			this.sleep(1000);
			time++;
			pl.formatTime(time);
			pl.scoreboard();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
  }
}
