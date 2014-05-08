package me.zyuiop.uhplugin;

import org.bukkit.scheduler.BukkitRunnable;

public class RunCommandTask extends BukkitRunnable {
	private final UHPlugin pl;
	private final String cmd;
	public RunCommandTask(UHPlugin pl, String cmd) {
		this.pl = pl;
		this.cmd = cmd;
	}
	public void run() {
		pl.getServer().dispatchCommand(pl.getServer().getConsoleSender(), cmd);
	}
}
