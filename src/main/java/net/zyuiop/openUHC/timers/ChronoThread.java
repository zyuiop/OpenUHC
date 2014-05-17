package net.zyuiop.openUHC.timers;

import net.zyuiop.openUHC.OpenUHC;

public class ChronoThread extends Thread {
  private OpenUHC pl;
  private long time;
  private boolean cont = true;
  public ChronoThread(OpenUHC openUHC, long time) {
	  	this.time = time;
	  	this.pl = openUHC;
  }
  public void end() {
	  cont = false;
  }
  public ChronoThread(OpenUHC openUHC) {
	    this(openUHC, 0);
  }
  @SuppressWarnings("static-access")
public void run() {
    while (cont) {
    	try {
			this.sleep(1000);
			time++;
			pl.getSbManager().formatTime(time);
			pl.getSbManager().refresh();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
  }
  
}
