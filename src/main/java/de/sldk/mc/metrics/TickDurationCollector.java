package de.sldk.mc.metrics;

import de.sldk.mc.PrometheusExporter;
import io.prometheus.client.Gauge;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class TickDurationCollector extends Metric {
    public TickDurationCollector(PrometheusExporter plugin, Gauge gauge, String name) {
        super(plugin, gauge);
    }

    /**
     * Returns either the internal minecraft long array for tick times in ns,
     * or a long array containing just one element of value -1 if reflection
     * was unable to locate the minecraft tick times buffer
     */
    protected static long[] getTickDurations() {
        return ((MinecraftServer) Sponge.getServer()).tickTimeArray;
    }
}
