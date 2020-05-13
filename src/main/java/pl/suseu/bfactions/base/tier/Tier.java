package pl.suseu.bfactions.base.tier;

import org.bukkit.entity.Player;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.base.tier.cost.TierCost;

import java.util.List;

public abstract class Tier {

    private final int tier;
    private final String pathItem;
    private List<String> lore;
    private final String pathItemBuy;
    private List<String> loreBuy;
    private final String pathItemOwned;
    private List<String> loreOwned;
    private final List<TierCost> cost;
    private final TierType type;

    public Tier(int tier, String pathItem, String pathItemBuy, String pathItemOwned, List<TierCost> cost, TierType type) {
        this.tier = tier;
        this.pathItem = pathItem;
        this.pathItemBuy = pathItemBuy;
        this.pathItemOwned = pathItemOwned;
        this.cost = cost;
        this.type = type;
    }

    public int getTier() {
        return tier;
    }

    public boolean canAfford(Player player, Guild guild) {
        for (TierCost cost : this.cost) {
            if (!cost.canBuy(player, guild)) {
                return false;
            }
        }
        return true;
    }

    public void buy(Player player, Guild guild) {
        for (TierCost cost : this.cost) {
            cost.buy(player, guild);
        }
        guild.setTier(this);
    }

    public TierType getType() {
        return type;
    }

    public String getPathItem() {
        return pathItem;
    }

    public String getPathItemBuy() {
        return pathItemBuy;
    }

    public String getPathItemOwned() {
        return pathItemOwned;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public List<String> getLoreBuy() {
        return loreBuy;
    }

    public void setLoreBuy(List<String> loreBuy) {
        this.loreBuy = loreBuy;
    }

    public List<String> getLoreOwned() {
        return loreOwned;
    }

    public void setLoreOwned(List<String> loreOwned) {
        this.loreOwned = loreOwned;
    }

    public enum TierType {
        FIELD, REGION,
    }
}
