package net.zyuiop.openUHC.events;

import net.zyuiop.openUHC.OpenUHC;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

public class PlayerEvents implements Listener {
	private OpenUHC pl;
	public PlayerEvents(OpenUHC plugin) {
		this.pl = plugin;
	}
	
	@EventHandler
	public void joinEvent(PlayerLoginEvent e) {
		if (!pl.getGame().canJoin() && !e.getPlayer().hasPermission("uhpl.join") && !pl.isIngame(e.getPlayer().getName())) {
			e.disallow(Result.KICK_OTHER, "Le jeu est dèjà lancé.");
			e.setResult(Result.KICK_OTHER);
			e.setKickMessage("Le jeu est dèjà lancé.");
			return;
		}
		
	}
	
	@EventHandler
	public void join(PlayerJoinEvent e) {
		e.getPlayer().setScoreboard(pl.getSbManager().getMainScoreboard());
		if (pl.isIngame(e.getPlayer().getName())) {
			e.getPlayer().sendMessage(ChatColor.GOLD+"Re !");
			Location l = e.getPlayer().getLocation();
			if (!pl.isInLimits(l.getBlockX(), l.getBlockZ())) {
				e.getPlayer().teleport(pl.getRandLoc());
				e.getPlayer().sendMessage(ChatColor.DARK_RED+"Vous êtes HORS LIMITES.");
			}
		}
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		Location l = e.getTo();
		if (pl.isIngame(e.getPlayer().getName())) {
			if (!pl.isInLimits(l.getBlockX(), l.getBlockZ())) {
				Location p = e.getFrom();
				if (!pl.isInLimits(p.getBlockX(), p.getBlockZ())) {
					e.getPlayer().teleport(pl.getRandLoc());
					return;
				}
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void playerDeath(PlayerDeathEvent death) {
			Player p = (Player) death.getEntity();
			String n = p.getName();
			pl.deletePlayer(n);
			p.kickPlayer("Vous êtes mort !");
		
		
	}
	
	@EventHandler
	public void interact(PlayerInteractEvent e) {
		e.setCancelled(!pl.getGame().getStarted());
	}
}
