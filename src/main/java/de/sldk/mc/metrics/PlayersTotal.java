package de.sldk.mc.metrics;

import de.sldk.mc.PrometheusExporter;
import io.prometheus.client.Gauge;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.user.UserStorageService;

import java.util.Collection;

public class PlayersTotal extends Metric {

    private static final Gauge PLAYERS = Gauge.build()
            .name(prefix("players_total"))
            .help("Unique players (online + offline)")
            .create();

    public PlayersTotal(PrometheusExporter plugin) {
        super(plugin, PLAYERS);
    }

    @Override
    public void doCollect() {
        PLAYERS.set(Sponge.getServiceManager().provide(UserStorageService.class)
                .map(UserStorageService::getAll)
                .map(Collection::size)
                .orElse(0)
        );
    }
}
