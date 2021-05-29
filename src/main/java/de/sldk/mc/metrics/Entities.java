package de.sldk.mc.metrics;

import de.sldk.mc.PrometheusExporter;
import io.prometheus.client.Gauge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.world.World;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Get current count of all entities.
 * <p>
 * Entities are labelled by
 * <ol>
 *     <li> world,
 *     <li> type ({@link EntityType}),
 *     <li> alive ({@link EntityType#isAlive()}),
 *     <li> and spawnable ({@link EntityType#isSpawnable()})
 * </ol>
 */
public class Entities extends WorldMetric {

    private static final Gauge ENTITIES = Gauge.build()
            .name(prefix("entities_total"))
            .help("Entities loaded per world")
            .labelNames("world", "type")
            .create();

    public Entities(PrometheusExporter plugin) {
        super(plugin, ENTITIES);
    }

    @Override
    protected void clear() {
        ENTITIES.clear();
    }

    @Override
    public void collect(World world) {
        Map<EntityType, Long> mapEntityTypesToCounts = world.getEntities().stream()
                .collect(Collectors.groupingBy(Entity::getType, Collectors.counting()));

        mapEntityTypesToCounts
                .forEach((entityType, count) ->
                        ENTITIES
                                .labels(world.getDimension().getType().getId(),
                                        getEntityName(entityType))
                                .set(count)
                );
    }
}
