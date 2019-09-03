package me.shakeforprotein.lazytools;

import Commands.ReloadCommand;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;


public final class LazyTools extends JavaPlugin implements Listener {


    public HashMap stareHash = new HashMap<String, Integer>();
    public HashMap counterHash = new HashMap<String, Integer>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getConfig().options().copyDefaults(true);
        this.getConfig().set("version", this.getDescription().getVersion());
        this.saveConfig();
        this.getCommand("LazyToolsReload").setExecutor(new ReloadCommand(this));
        //this.getCommand("renamecaveblock").setExecutor(new RenameCaveblock(this));
        //stareHash.clear();
        //counterHash.clear();
        //activateStareCheck();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    //Setup signs for sell
    @EventHandler
    public void onSignClick(PlayerInteractEntityEvent e) {
        Entity frame = e.getRightClicked();
        ItemStack mainHand = e.getPlayer().getInventory().getItemInMainHand();
        ItemStack offHand = e.getPlayer().getInventory().getItemInOffHand();
        Material heldSign = mainHand.getType();
        Material heldSign2 = offHand.getType();

        if (frame != null && frame instanceof ItemFrame) {
            if (heldSign.name().contains("SIGN")) {
                e.setCancelled(true);
                doSign(e.getPlayer(), heldSign, "BUY", frame);
            }
            if (heldSign2.name().contains("SIGN")) {
                e.setCancelled(true);
                doSign(e.getPlayer(), heldSign2, "SELL", frame);
            }
        }
    }

    private void doSign(Player p, Material handSign, String which, Entity frame) {
        ItemFrame itemFrame = (ItemFrame) frame;
        if (p.hasPermission("lazytools.editsigns")) {
            Block target = frame.getLocation().getBlock();
            BlockFace facing = ((ItemFrame) frame).getAttachedFace();
            if (itemFrame.getItem() != new ItemStack(Material.AIR)) {
                ItemStack fItem = itemFrame.getItem();
                Block b1 = target;
                String l1 = "Error";
                String l2 = "Error";
                if (which.equalsIgnoreCase("BUY")) {
                    b1 = target.getRelative(0, 1, 0);
                    l1 = "&1[Buy]";
                } else if (which.equalsIgnoreCase("SELL")) {
                    b1 = target.getRelative(0, -1, 0);
                    l1 = "&1[Sell]";
                } else {
                }

                if ((b1.isEmpty() || b1.getType().name().contains("SIGN"))) {


                    ArrayList<String> signTypes = new ArrayList<String>();
                    if (Double.parseDouble(Bukkit.getVersion().split(" ")[2].split("\\.")[1].replace(")", "")) > 13) {
                        signTypes.add("ACACIA_WALL_SIGN");
                        signTypes.add("BIRCH_WALL_SIGN");
                        signTypes.add("OAK_WALL_SIGN");
                        signTypes.add("DARK_OAK_WALL_SIGN");
                        signTypes.add("JUNGLE_WALL_SIGN");
                        signTypes.add("SPRUCE_WALL_SIGN");
                    }
                    signTypes.add("WALL_SIGN");


                    int i = 0;
                    for (i = 0; i < signTypes.toArray().length; i++) {
                        if (signTypes.get(i).contains(handSign.name().toUpperCase().split(" ")[0].split("_")[0])) {
                            handSign = Material.getMaterial(signTypes.get(i));
                            break;
                        }
                    }

                    b1.setType(handSign);

                    Sign s1 = (Sign) b1.getState();

                    s1.setLine(0, l1);


                    if(fItem.getType().toString().contains(("Potion").toUpperCase())){
                        ItemStack potion = fItem;
                        PotionMeta potionMeta = (PotionMeta) potion.getItemMeta();
                        p.sendMessage(potion.getType() + "");
                        p.sendMessage("Extended: " + potionMeta.getBasePotionData().isExtended());
                        p.sendMessage("Upgraded: " + potionMeta.getBasePotionData().isUpgraded());
                        p.sendMessage("Effects: " + potionMeta.getCustomEffects().toString());
                    }


                    String newVal = fItem.getType().name();
                    double buy = 9999999;
                    double sell = 0.01;
                    String buyQty = "1";
                    String sellQty = "1";
                    for (String keyName : getConfig().getConfigurationSection("value").getKeys(false)) {
                        if (keyName.equalsIgnoreCase(newVal)) {
                            if (!keyName.toUpperCase().contains("POTION")) {
                                newVal = keyName;
                                buy = getConfig().getDouble("value." + keyName + ".buy");
                                sell = getConfig().getDouble("value." + keyName + ".sell");
                                buyQty = getConfig().getString("value." + keyName + ".buyQty");
                                sellQty = getConfig().getString("value." + keyName + ".sellQty");
                                if (getConfig().get("value." + keyName + ".essName") != null) {
                                    newVal = getConfig().getString("value." + keyName + ".essName");
                                }
                                break;
                            }
                        }
                    }

                    s1.setLine(2, newVal);

                    if (which.equalsIgnoreCase("BUY")) {
                        s1.setLine(3, "$" + buy);
                        s1.setLine(1, "" + buyQty);
                        if (buy < 0) {
                            p.sendMessage("Buy is less than 0");
                            s1.setLine(0, "+=+=+=+=+=+=+");
                            s1.setLine(3, "+=+=+=+=+=+=+");
                            s1.setLine(1, "&3Item &4Not");
                            s1.setLine(2, "&2For Purcxhase");
                        }
                    } else if (which.equalsIgnoreCase("SELL")) {
                        s1.setLine(3, "$" + sell);
                        s1.setLine(1, "" + sellQty);
                        if (sell < 0) {
                            s1.setLine(0, "+=+=+=+=+=+=+");
                            s1.setLine(3, "+=+=+=+=+=+=+");
                            s1.setLine(1, "&3Item &4Not");
                            s1.setLine(2, "&2For Sale");
                        }
                    }

                    s1.update();
                    s1.update(true);
                }

                if (b1.getBlockData() instanceof WallSign) {
                    Directional s3 = (WallSign) b1.getBlockData();
                    s3.setFacing(facing.getOppositeFace());
                    b1.setBlockData(s3);
                }
            }
        }
    }

    private void activateStareCheck(){
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                for(Player p : Bukkit.getOnlinePlayers()){
                    if(!stareHash.containsKey(p.getUniqueId().toString())){
                        stareHash.put(p.getUniqueId().toString(), p.getLocation());
                        counterHash.put(p.getUniqueId().toString(), 0);
                        p.sendMessage("Added to hash");
                    }

                    else if((int)counterHash.get(p.getUniqueId().toString()) == 0) {
                        if(p.getLocation().equals(stareHash.get(p.getUniqueId().toString()))){
                            if(p.getTargetBlockExact(5) != null) {
                                p.sendMessage(p.getTargetBlockExact(5).getType().name());
                                counterHash.replace(p.getUniqueId().toString(), 1);
                            }
                        else p.sendMessage("You moved");
                        }
                        else{
                            counterHash.replace(p.getUniqueId().toString(), 0);
                        }
                    }
                    else if((int)counterHash.get(p.getUniqueId().toString()) == 5) {
                        counterHash.remove(p.getUniqueId().toString());
                    }
                    if(!p.getLocation().equals(stareHash.get(p.getUniqueId().toString()))){
                        counterHash.remove(p.getUniqueId().toString());
                    }
                    counterHash.replace(p.getUniqueId().toString(), (int) counterHash.get(p.getUniqueId().toString()) +1);
                    p.sendMessage(counterHash.get(p.getUniqueId().toString()) + "");
                }
            }
        }, 100L, 60L);
    }
}
