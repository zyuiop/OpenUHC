package net.zyuiop.openUHC.timers;

import net.zyuiop.openUHC.OpenUHC;

public class ChronoThread extends Thread {
  private OpenUHC pl;
 
  public ChronoThread(OpenUHC openUHC) {
    this.pl = openUHC;
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
