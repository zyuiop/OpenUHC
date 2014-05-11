package net.zyuiop.openUHC.events;

import net.zyuiop.openUHC.OpenUHC;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
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
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerEvents implements Listener {
	private OpenUHC pl;
	public PlayerEvents(OpenUHC plugin) {
		this.pl = plugin;
	}
	
	@EventHandler
	public void joinEvent(PlayerLoginEvent e) {
		if (!pl.getGame().canJoin() && !e.getPlayer().hasPermission("uhpl.join") && !pl.isIngame(e.getPlayer())) {
			if(pl.getConfig().getBoolean("allow_spectators")) {
				pl.getSpectatorManager().addPlayer(e.getPlayer());
				e.getPlayer().sendMessage(ChatColor.GREEN + "Vous rejoignez le jeu en tant que spectateur");
				return;
			}
			e.disallow(Result.KICK_OTHER, "Le jeu est dèjà lancé.");
			e.setResult(Result.KICK_OTHER);
			e.setKickMessage("Le jeu est dèjà lancé.");
			return;
		}
		
			
	}
	
	@EventHandler
	public void join(PlayerJoinEvent e) {
		e.getPlayer().setScoreboard(pl.getSbManager().getMainScoreboard());
		if (pl.isIngame(e.getPlayer())) {
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
		if (pl.isIngame(e.getPlayer())) {
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
			pl.deletePlayer(p);
			if (!pl.getConfig().getBoolean("allow_spectators"))
				p.kickPlayer("Vous êtes mort !");
				
		
	}
	@EventHandler
	public void respawn(PlayerRespawnEvent e) {
		Player p = e.getPlayer();
		if (!pl.isIngame(p)) {
			p.sendMessage(ChatColor.RED + "Vous êtes mort !");
			p.sendMessage(ChatColor.GREEN + "Vous êtes désormais spectateur.");
			pl.getSpectatorManager().addPlayer(p);
		}
	}
	@EventHandler
	public void interact(PlayerInteractEvent e) {
		e.setCancelled(!pl.getGame().getStarted() || !pl.isIngame(e.getPlayer()));
	}
	
	@EventHandler
	public void itemPickup(PlayerPickupItemEvent e) {
		e.setCancelled(!pl.getGame().getStarted() || !pl.isIngame(e.getPlayer()));
	}
	
}
