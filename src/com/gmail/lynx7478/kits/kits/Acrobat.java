package com.gmail.lynx7478.kits.kits;

import java.util.ArrayList;
import java.util.List;

import com.gmail.lynx7478.anni.anniGame.AnniPlayer;
import com.gmail.lynx7478.anni.anniGame.Game;
import com.gmail.lynx7478.anni.kits.Loadout;
import com.gmail.lynx7478.anni.utils.VersionUtils;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.lynx7478.kits.base.KitBase;
import com.gmail.lynx7478.kits.base.DelayUpdate;
import com.gmail.lynx7478.kits.base.Delays;

public class Acrobat extends KitBase
{
	//private final ItemStack Arrows = KitUtils.addSoulbound(new ItemStack(Material.ARROW,6));

	private ChatColor aqua = ChatColor.AQUA;
	
	@Override
	protected void setUp()
	{
		final Acrobat k = this;
		Delays.getInstance().createNewDelay(getInternalName(), new DelayUpdate(){
			@Override
			public void update(Player player, int secondsLeft) throws ClassNotFoundException
			{
				if(secondsLeft <= 0)
				{
					AnniPlayer p = AnniPlayer.getPlayer(player.getUniqueId());
					if(p != null && p.getKit().equals(k))
					{
						Sound sound;
						if(!VersionUtils.getVersion().contains("13"))
						{
							sound = Sound.WITHER_SHOOT;
						}else
						{
							sound = (Sound) Enum.valueOf((Class<Enum>) Class.forName("org.bukkit.Sound"),"ENTITY_WITHER_SHOOT");
						}
						
						player.playSound(player.getLocation(), sound, 1.0F, 2.0F);
						player.setAllowFlight(true);
					}
				}
			}});
	}

	@Override
	protected String getInternalName()
	{
		return "Acrobat";
	}

	@Override
	protected ItemStack getIcon()
	{
		return new ItemStack(Material.FEATHER);
	}
	
	@Override
	protected int setDefaults(ConfigurationSection section)
	{
		return 0;
	}

	@Override
	protected List<String> getDefaultDescription()
	{
		List<String> l = new ArrayList<String>();
		addToList(l,
					aqua+"You are the bird.", 
					"",
					aqua+"Soar through the air with",
					aqua+"grace. You are immune",
					aqua+"to fall damage.",
					"",
					aqua+"Your stamina allows you",
					aqua+"to sprint longer than",
					aqua+"others."
				);
		return l;
	}

//	@Override
//	public void onPlayerSpawn(Player player)
//	{
//		KitUtils.giveTeamArmor(player);
//		player.getInventory().addItem(KitUtils.getWoodSword());
//		player.getInventory().addItem(KitUtils.getWoodPick());
//		player.getInventory().addItem(KitUtils.getWoodAxe());
//		player.getInventory().addItem(KitUtils.getBow());
//		player.getInventory().addItem(Arrows.clone());
//		player.getInventory().addItem(KitUtils.getNavCompass());
//	}
	
	@Override
	protected Loadout getFinalLoadout()
	{
		return new Loadout().addWoodSword().addWoodPick().addWoodAxe().addBow().addSoulboundItem(new ItemStack(Material.ARROW,6));
	}

	@Override
	public void cleanup(Player player)
	{
		assert player != null;
		
		player.setAllowFlight(false);
		player.setFlying(false);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void AcrobatDoubleJump(PlayerToggleFlightEvent event) 
	{  
		Player player = event.getPlayer();
		if(player.getGameMode() != GameMode.CREATIVE) 
		{
			AnniPlayer p = AnniPlayer.getPlayer(player.getUniqueId());
			if(Game.isGameRunning() && p != null && p.getKit().equals(this))
			{
				Delays.getInstance().addDelay(player, System.currentTimeMillis()+10000, this.getInternalName());
			    event.setCancelled(true);
			    player.setAllowFlight(false);
			    player.setFlying(false);		    
			    player.setVelocity(player.getLocation().getDirection().setY(1).multiply(1));
			}
			else
			{
				player.setAllowFlight(false);
				player.setFlying(false);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void fallDamage(EntityDamageEvent event)
	{
		if(event.getEntity().getType() == EntityType.PLAYER && event.getCause() == DamageCause.FALL)
		{
			Player p = (Player)event.getEntity();
			AnniPlayer pl = AnniPlayer.getPlayer(p.getUniqueId());
			if(pl != null && pl.getKit().equals(this))
			{
				event.setCancelled(true);
			}
		}
	}  
		 
	@EventHandler(priority = EventPriority.HIGHEST)
	public void AcrobatJumpMonitor(PlayerMoveEvent event) 
	{ 
		Player player = event.getPlayer();
		if(player.getGameMode() != GameMode.CREATIVE)
		{
			if(player.isFlying())
			{
				player.setAllowFlight(false);
				player.setFlying(false);
				return;
			}
			
			if(Game.isGameRunning())
			{
				if(!player.getAllowFlight())
				{
					AnniPlayer p = AnniPlayer.getPlayer(player.getUniqueId());
					if(p != null && p.getKit().equals(this))
					{
						if(player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR && !Delays.getInstance().hasActiveDelay(player, this.getInternalName()))
						{
							//Bukkit.getLogger().info("Thing 2");
							player.setAllowFlight(true);
							return;
						}
					}
				}
			}
//			else
//			{
//				player.setAllowFlight(false);
//				player.setFlying(false);
//			}
		}
			
			
//		if(player.getGameMode() != GameMode.CREATIVE && player.isFlying())
//		{
//			player.setAllowFlight(false);
//			player.setFlying(false);
//		}
//		
//		if(Game.isGameRunning() && player.getGameMode() != GameMode.CREATIVE && !player.getAllowFlight())
//		{
//			AnniPlayer p = AnniPlayer.getPlayer(player.getUniqueId());
//			if(p != null && p.getKit().equals(this))
//			{
//				//This is possibly also where we would make them able to permanently sprint
//				if(player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR && !Delays.getInstance().hasActiveDelay(player, this.getInternalName()))
//				{
//					//Bukkit.getLogger().info("Thing 2");
//					player.setAllowFlight(true);
//					return;
//				}
//			}
//			player.setAllowFlight(false);
//			player.setFlying(false);
//		}
	}
}

