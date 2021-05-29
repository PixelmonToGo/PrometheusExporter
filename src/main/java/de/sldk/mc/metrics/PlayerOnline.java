package de.sldk.mc.metrics;

import de.sldk.mc.PrometheusExporter;
import io.prometheus.client.Gauge;
import org.spongepowered.api.entity.living.player.User;

public class PlayerOnline extends PlayerMetric {

    private static final Gauge PLAYERS_WITH_NAMES = Gauge.build()
            .name(prefix("player_online"))
            .help("Online state by player name")
            .labelNames("name", "uid")
            .create();

    public PlayerOnline(PrometheusExporter plugin) {
        super(plugin, PLAYERS_WITH_NAMES);
    }

    @Override
    public void collect(User player) {
        PLAYERS_WITH_NAMES.labels(getNameOrUid(player), getUid(player)).set(player.isOnline() ? 1 : 0);
    }
}
