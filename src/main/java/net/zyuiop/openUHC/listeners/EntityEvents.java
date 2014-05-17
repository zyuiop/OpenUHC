package net.zyuiop.openUHC.listeners;

import java.util.ArrayList;
import java.util.List;

import net.zyuiop.openUHC.OpenUHC;

import org.bukkit.Material;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.inventory.ItemStack;

public class EntityEvents implements Listener {
	private OpenUHC pl;
	public EntityEvents(OpenUHC plugin) {
		this.pl = plugin;
	}
	
	@EventHandler
	public void takeDamage(EntityDamageEvent e) {
		if (!pl.getGame().getStarted())
			e.setCancelled(true);
		else {
			if (e.getEntity() instanceof Player) {
				Player p = (Player) e.getEntity();
				if(!pl.getGame().canTakeDamage() || !pl.isIngame(p))
					e.setCancelled(true);
			}
			if (e instanceof EntityDamageByEntityEvent)
			{
				EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) e;
				if (ev.getDamager() instanceof Player && ev.getEntity() instanceof Player && !pl.getGame().canTakeDamage())
					ev.setCancelled(true);
				else if(ev.getDamager() instanceof Player) {
					Player damager = (Player) ev.getDamager();
					e.setCancelled(!pl.isIngame(damager));
				}
			}
		}
	}
	
	
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent ev) {
		if (ev.getEntity() instanceof Ghast) {
			List<ItemStack> drops = new ArrayList<ItemStack>(ev.getDrops());
			ev.getDrops().clear();
			for (ItemStack i : drops) {
				if (i.getType() == Material.GHAST_TEAR) {
					ev.getDrops().add(new ItemStack(Material.GOLD_INGOT,i.getAmount()));
				} else {
					ev.getDrops().add(i);
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityTarget(EntityTargetLivingEntityEvent event) {
		if (event.getTarget() instanceof Player) {
			event.setCancelled(!pl.isIngame((Player) event.getTarget()));
		}
	}
}
