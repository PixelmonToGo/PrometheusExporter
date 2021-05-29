package de.sldk.mc.metrics;

import de.sldk.mc.PrometheusExporter;
import de.sldk.mc.metrics.player.EmptyStatisticLoader;
import de.sldk.mc.metrics.player.PlayerStatisticLoader;
import io.prometheus.client.Gauge;
import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.User;

import java.util.LinkedHashSet;
import java.util.Map;

/**
 * Offline player -> fetch data from files
 * <p>
 * Online player -> fetch data from Minecraft API
 */
public class PlayerStatistics extends PlayerMetric {

    private static final Gauge PLAYER_STATS = Gauge.build()
            .name(prefix("player_statistic"))
            .help("Player statistics")
            .labelNames("player_name", "player_uid", "statistic")
            .create();

    private static Logger logger;

    private final LinkedHashSet<PlayerStatisticLoader> statisticLoaderChain = new LinkedHashSet<>();

    public PlayerStatistics(PrometheusExporter plugin) {
        super(plugin, PLAYER_STATS);

        logger = plugin.getLogger();

        statisticLoaderChain.add(new EmptyStatisticLoader());
    }

    @Override
    public void collect(User player) {

        for (PlayerStatisticLoader playerStatisticLoader : statisticLoaderChain) {
            if (collectSuccessful(playerStatisticLoader, player)) {
                return;
            }
        }
    }

    private boolean collectSuccessful(PlayerStatisticLoader loader, User player) {
        final String playerNameLabel = getNameOrUid(player);
        final String playerUidLabel = getUid(player);

        try {
            Map<Enum<?>, Integer> statistics = loader.getPlayerStatistics(player);

            if (statistics == null || statistics.isEmpty()) {
                return false;
            }

            statistics.forEach(
                    (stat, value) -> PLAYER_STATS.labels(playerNameLabel, playerUidLabel, stat.name()).set(value));

            return true;
        } catch (Exception e) {
            String message =
                    String.format("%s: Could not load statistics for player '%s'", loader.getClass().getSimpleName(),
                            player.getUniqueId());
            logger.warn(message, e);
            return false;
        }
    }
}
