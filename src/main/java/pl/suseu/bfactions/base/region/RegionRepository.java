package pl.suseu.bfactions.base.region;

import org.bukkit.Location;
import pl.suseu.bfactions.BFactions;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RegionRepository {

    private final BFactions plugin;

    private final Map<UUID, Region> regions = new ConcurrentHashMap<>();

    public RegionRepository(BFactions plugin) {
        this.plugin = plugin;
    }

    public Region nearestRegion(Location location) {
        double min = Double.MAX_VALUE;
        Region nearest = null;

        for (Region region : getRegions()) {
            try {
                double dist = region.getCenter().distance(location);
                if (dist < min) {
                    min = dist;
                    nearest = region;
                }
            } catch (IllegalArgumentException ignored) {
            }
        }

        return nearest;
    }

    public Region getRegion(UUID uuid) {
        return this.regions.get(uuid);
    }

    public void addRegion(Region region) {
        regions.put(region.getUuid(), region);
    }

    public void removeRegion(UUID uuid) {
        regions.remove(uuid);
    }

    public void removeRegion(Region region) {
        this.removeRegion(region.getUuid());
    }

    /**
     * @return a copy of regions set
     */
    public Set<Region> getRegions() {
        return new HashSet<>(regions.values());
    }
}
