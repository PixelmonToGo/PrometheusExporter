package de.sldk.mc.metrics;

import de.sldk.mc.PrometheusExporter;
import io.prometheus.client.Collector;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.world.World;

public abstract class WorldMetric extends Metric {

    public WorldMetric(PrometheusExporter plugin, Collector collector) {
        super(plugin, collector);
    }

    @Override
    public final void doCollect() {
        clear();
        for (World world : Sponge.getGame().getServer().getWorlds()) {
            collect(world);
        }
    }

    protected abstract void clear();

    protected abstract void collect(World world);

    protected String getEntityName(EntityType type) {
        return type.getName();
    }
}
