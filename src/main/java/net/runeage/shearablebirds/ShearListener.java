package net.runeage.shearablebirds;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ShearListener implements Listener {

    private NamespacedKey shearTime = new NamespacedKey(ShearableBirds.getProvidingPlugin(ShearableBirds.class), "shearTime");

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event){
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        EquipmentSlot e = event.getHand();
        ItemStack itemStack = player.getInventory().getItemInMainHand();

        if (e != EquipmentSlot.HAND) return;

        if (itemStack.getType() != Material.SHEARS) return;

        if (!(entity instanceof Chicken || entity instanceof Parrot)) return;

        PersistentDataContainer pdc = entity.getPersistentDataContainer();

        if (!pdc.has(shearTime, PersistentDataType.LONG))
            pdc.set(shearTime, PersistentDataType.LONG, 0L);

        long lastShear = pdc.get(shearTime, PersistentDataType.LONG);
        long shearCooldown = ShearableBirds.config.getLong("ShearCooldown");
        long now = System.currentTimeMillis();

        if (lastShear + shearCooldown > now) return;

        pdc.set(shearTime, PersistentDataType.LONG, now);

        int min = ShearableBirds.config.getInt("MinDrop");
        int max = ShearableBirds.config.getInt("MaxDrop");

        Location location = entity.getLocation();
        location.getWorld().dropItem(location, new ItemStack(Material.FEATHER, randomWithRange(min,max)));

        if (player.getGameMode() == GameMode.CREATIVE) return;

        int unbreaking = itemStack.getEnchantmentLevel(Enchantment.DURABILITY);
        int chance = 100 / (unbreaking + 1);
        if(unbreaking == 0 || randomWithRange(0,100) < chance) {
            Damageable meta = (Damageable) itemStack.getItemMeta();
            meta.setDamage((short) (meta.getDamage() + 1));
            itemStack.setItemMeta((ItemMeta) meta);
        }
    }

    private int randomWithRange(int min, int max){
        return min + (int)(Math.random() * ((max - min) + 1));
    }

}
