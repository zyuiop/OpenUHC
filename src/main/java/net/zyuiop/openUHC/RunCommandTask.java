package net.zyuiop.openUHC;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class RunCommandTask extends BukkitRunnable {
	private final String cmd;
	public RunCommandTask(String cmd) {
		this.cmd = cmd;
	}
	public void run() {
		Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), cmd);
	}
}
