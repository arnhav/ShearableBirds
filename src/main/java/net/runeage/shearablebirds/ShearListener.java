package net.runeage.shearablebirds;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
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
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ShearListener implements Listener {

    private final NamespacedKey shearTime = new NamespacedKey(ShearableBirds.getProvidingPlugin(ShearableBirds.class), "shearTime");

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

        Long lastShear = pdc.get(shearTime, PersistentDataType.LONG);
        long shearCooldown = ShearableBirds.config.getLong("ShearCooldown");
        long now = System.currentTimeMillis();

        if (lastShear != null && lastShear + shearCooldown > now) return;

        pdc.set(shearTime, PersistentDataType.LONG, now);

        int min = ShearableBirds.config.getInt("MinDrop");
        int max = ShearableBirds.config.getInt("MaxDrop");

        Location location = entity.getLocation();
        location.getWorld().dropItem(location, new ItemStack(Material.FEATHER, randomWithRange(min,max)));

        if (player.getGameMode() == GameMode.CREATIVE) return;

        if (!EnchantmentTarget.BREAKABLE.includes(itemStack)) return;
        int unbreaking = itemStack.getEnchantmentLevel(Enchantment.DURABILITY);
        int chance = 100 / (unbreaking + 1);
        if(unbreaking == 0 || randomWithRange(0,100) < chance) {
            Damageable meta = (Damageable) itemStack.getItemMeta();
            if (meta == null) return;
            int damage = meta.getDamage() + 1;
            if (damage == itemStack.getType().getMaxDurability()) {
                if (player.getInventory().getItemInMainHand().getType() == Material.SHEARS) {
                    player.playEffect(EntityEffect.BREAK_EQUIPMENT_MAIN_HAND);
                } else {
                    player.playEffect(EntityEffect.BREAK_EQUIPMENT_OFF_HAND);
                }
                return;
            }
            meta.setDamage(damage);
            itemStack.setItemMeta(meta);
        }
    }

    private int randomWithRange(int min, int max){
        return min + (int)(Math.random() * ((max - min) + 1));
    }

}
