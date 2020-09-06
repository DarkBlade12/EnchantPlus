package com.darkblade12.enchantplus.enchantment;

import org.bukkit.enchantments.Enchantment;

import java.util.HashMap;
import java.util.Map;

public enum EnchantmentInformation {
    PROTECTION_ENVIRONMENTAL("Protection", "Reduces most damage", "Exceptions: doesn't reduce damage from the void and the /kill command"),
    PROTECTION_FIRE("Fire Protection", "Reduces fire damage and burn time when set on fire"),
    PROTECTION_FALL("Feather Falling", "Reduces fall damage and fall damage from ender pearl teleportations"),
    PROTECTION_EXPLOSIONS("Blast Protection", "Reduces explosion damage and explosion knockback"),
    PROTECTION_PROJECTILE("Projectile Protection", "Reduces projectile damage"),
    OXYGEN("Respiration",
           "Extends underwater breathing time by +15 seconds per level, " +
           "time between suffocation damage by +1 second per level and underwater vision"),
    WATER_WORKER("Aqua Affinity", "Increases underwater mining rate",
                 "Breaking blocks underwater is allowed at regular speed, though the player can't be floating to get the full effect"),
    THORNS("Thorns", "Damages attackers", "(Level x 15)% chance of inflicting 1 - 4 hearts damage on anyone who attacks the wearer",
           "Success also reduces durability of armor, if present on multiple pieces of armor, only the highest one counts"),
    DEPTH_STRIDER("Depth Strider", "Increases underwater movement speed"),
    BINDING_CURSE("Curse of Binding", "Prevents removal of items (except for in creative)"),
    FROST_WALKER("Frost Walker", "Turns water beneath the player into frosted ice"),
    DAMAGE_ALL("Sharpness", "Increases damage", "Each level separately adds 0.5 to 1.5 hearts random extra damage to each hit"),
    DAMAGE_UNDEAD("Smite", "Increases damage to undead mobs (skeletons, zombies, wither, wither skeletons and zombie pigmen)",
                  "Each level separately adds 0.5 to 2.5 hearts random extra damage to each hit (only to undead mobs)"),
    DAMAGE_ARTHROPODS("Bane of Arthropods", "Increases damage to arthropod mobs (spider, cave spiders and silverfish)",
                      "Each level separately adds 0.5 to 2.5 hearts random extra damage to each hit (only to arthropod mobs)"),
    KNOCKBACK("Knockback", "Increases knockback", "Does combine slightly with knockback caused by attacking while sprinting"),
    FIRE_ASPECT("Fire Aspect", "Sets the target on fire", "Level I adds 3 burn ticks and each additional level adds 4 more burn ticks",
                "Dropped meat will be cooked if killed by fire"),
    LOOT_BONUS_MOBS("Looting", "Mobs can drop more loot",
                    "Increases maximum loot drop by +1 per level and chance of rare drops by +0.5% per level (i.e., 3% at level I, " +
                    "3.5% at level II, and 4% at level III)",
                    "This also applies to kills by Thorns-enchanted armor while holding the sword and to shots with bows if you " +
                    "switch to the enchanted sword before the arrow kills the target"),
    SWEEPING_EDGE("Sweeping Edge", "Increases sweeping attack damage"),
    DIG_SPEED("Efficiency", "Increases mining speed +30% over the previous level: I = 130%, II = 169%, III = 220%, IV = 286%, V = 371%",
              "The speed increase applies to all blocks that when mined, will drop an item"),
    SILK_TOUCH("Silk Touch", "Mined blocks drop themselves instead of the usual items",
               "Allows collection of blocks that are normally unobtainable"),
    DURABILITY("Unbreaking", "Increases durability", "For most items, (100 / (Level + 1))% chance a use reduces durability",
               "On average, lifetime is (Level + 1) times as long", "For armor, ((60 + 40/(Level + 1))% chance a use reduces durability"),
    LOOT_BONUS_BLOCKS(
            "Fortune",
            "Increases block drops",
            "For coal, diamond, emerald, nether quartz and lapis lazuli, level I gives a 33% chance to multiply drops by 2 " +
            "(averaging 33% increase), level II gives a chance to multiply drops by 2 or 3 (25% chance each, averaging 75% increase), " +
            "and level III gives a chance to multiply drops by 2, 3 or 4 (20% chance each, averaging 120% increase)",
            "For redstone, carrots, glowstone, melons, nether wart, and wheat (seeds only), each level increases the drop maximum by +1 " +
            "(maximum 4 for glowstone and 9 for melons)", "For tall grass, each level increases the drop maximum by +2"),
    ARROW_DAMAGE("Power", "Increases arrow damage by 25% x (Level + 1), rounded up to nearest half-heart"),
    ARROW_KNOCKBACK("Punch", "Increases knockback of arrows"),
    ARROW_FIRE("Flame", "Arrows are on fire when shot", "Unlike flint and steel, flaming arrows only affect players, mobs, and TNT",
               "No other blocks catch fire and they do not produce light",
               "Fire damage applies after initial damage, similar to Fire Aspect"),
    ARROW_INFINITE("Infinity", "Shooting consumes no arrows", "Firing requires at least one arrow in inventory, but doesn't consume it",
                   "Fired arrows can't be retrieved, except in creative mode"),
    LUCK("Luck of the Sea", "Lowers chance of junk catches by 2.5% per level and increases chance of treasure catches by 1% per level"),
    LURE("Lure", "Increases rate of fish biting your hook",
         "Decreases time before fish bite your hook by 5 seconds per level and chances of both junk and treasure catches by 1% per level"),
    LOYALTY("Loyalty", "Trident returns after being thrown."),
    IMPALING("Impaling", "Trident deals additional damage to mobs that spawn naturally in the ocean, as well as players"),
    RIPTIDE("Riptide", "Trident launches player with itself when thrown", "Only functions in water or rain"),
    CHANNELING("Channeling", "Trident \"channels\" a bolt of lightning towards a hit entity",
               "Only functions during thunderstorms and if target unobstructed with opaque blocks"),
    MULTISHOT("Multishot", "Shoot 3 arrows at the cost of one"),
    QUICK_CHARGE("Quick Charge", "Decreases crossbow reloading time"),
    PIERCING("Piercing", "Arrows pass through multiple entities"),
    MENDING("Mending", "Repair items with experience"),
    VANISHING_CURSE("Curse of Vanishing", "Item destroyed on death");

    private static final Map<String, EnchantmentInformation> BY_NAME = new HashMap<>();
    private final String minecraftName;
    private final String[] description;

    EnchantmentInformation(String minecraftName, String... description) {
        this.minecraftName = minecraftName;
        this.description = description;
    }

    static {
        for (EnchantmentInformation info : values()) {
            BY_NAME.put(info.name(), info);
        }
    }

    public String getMinecraftName() {
        return minecraftName;
    }

    public String[] getDescription() {
        return description;
    }

    public static EnchantmentInformation fromName(String name) {
        return BY_NAME.get(name.toUpperCase());
    }

    @SuppressWarnings("deprecation")
    public static EnchantmentInformation fromEnchantment(Enchantment enchant) {
        return fromName(enchant.getName());
    }

    public static String getMinecraftName(Enchantment enchant) {
        EnchantmentInformation info = fromEnchantment(enchant);
        return info == null ? "Unknown" : info.getMinecraftName();
    }

    public static String[] getDescription(Enchantment enchant) {
        EnchantmentInformation info = fromEnchantment(enchant);
        return info == null ? new String[] { "No description set for this enchantment!" } : info.description;
    }
}
