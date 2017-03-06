package de.sldk.mc.prometheusexporter;

import com.google.inject.Inject;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;

import java.io.File;

import static de.sldk.mc.prometheusexporter.PluginInfo.*;

@Plugin(id = ID,
        name = NAME,
        version = VERSION,
        description = DESCRIPTION,
        url = URL
)

public class PrometheusExporter {
    @Inject
    Logger logger;

    private Server server;
    private int port;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private File config;
    @Inject
    @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> cfgMgr;
    private ConfigurationNode cfg;

    @Listener
    public void onPreinit(GamePreInitializationEvent event) {
        logger.info("Setting up config...");
        try {
            if (!config.exists()) {
                config.createNewFile();

                this.cfg = cfgMgr.load();
                this.cfg.getNode("exporter", "port").setValue(9225);
                this.cfgMgr.save(cfg);
            }

            this.cfg = cfgMgr.load();
            port = cfg.getNode("exporter", "port").getInt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Listener
    public void onServerStarted(GameStartedServerEvent event) {
        server = new Server(port);

        server.setHandler(new MetricsController(this));

        try {
            server.start();

            getLogger().info("Started Prometheus metrics endpoint on port " + port);

        } catch (Exception e) {
            getLogger().error("Could not start embedded Jetty server");
        }
    }

    @Listener
    public void onGameStopping(GameStoppingServerEvent event) {
        if (server != null) {
            try {
                server.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Logger getLogger() {
        return logger;
    }
}
