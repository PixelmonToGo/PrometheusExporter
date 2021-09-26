package de.sldk.mc.metrics;

import de.sldk.mc.PrometheusExporter;
import io.prometheus.client.Gauge;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class PlayersOnlineTotal extends Metric {

    private static final Gauge PLAYERS_ONLINE = Gauge.build()
            .name(prefix("players_online_total"))
            .help("Players currently online per world")
            .labelNames("world")
            .create();

    public PlayersOnlineTotal(PrometheusExporter plugin) {
        super(plugin, PLAYERS_ONLINE);
    }

    @Override
    protected void doCollect() {
        for (WorldServer worldServer : FMLCommonHandler.instance().getMinecraftServerInstance().worlds) {
            PLAYERS_ONLINE.labels(worldServer.getWorldInfo().getWorldName()).set(worldServer.playerEntities.size());
        }
    }
}
