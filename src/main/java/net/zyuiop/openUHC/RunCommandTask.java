package net.zyuiop.openUHC;

import org.bukkit.scheduler.BukkitRunnable;

public class RunCommandTask extends BukkitRunnable {
	private final OpenUHC pl;
	private final String cmd;
	public RunCommandTask(OpenUHC openUHC, String cmd) {
		this.pl = openUHC;
		this.cmd = cmd;
	}
	public void run() {
		pl.getServer().dispatchCommand(pl.getServer().getConsoleSender(), cmd);
	}
}
