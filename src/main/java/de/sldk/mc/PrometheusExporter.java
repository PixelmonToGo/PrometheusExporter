package de.sldk.mc;

import com.google.inject.Inject;
import de.sldk.mc.config.PrometheusExporterConfig;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.SpongeExecutorService;

import java.io.File;
import java.io.IOException;

@Plugin(id = "spongeprometheusexporter",
        name = "SpongePrometheusExporter",
        version = "2.0.0",
        description = "Prometheus Exporter for Sponge",
        url = "https://ore.spongepowered.org/phit/PrometheusExporter",
        authors = "phit"
)
public class PrometheusExporter {

    private MetricsServer server;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private File config;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> cfgMgr;
    private ConfigurationNode configurationNode;

    @Inject
    private Logger logger;

    private SpongeExecutorService executorService;
    private final PrometheusExporterConfig exporterConfig = new PrometheusExporterConfig(this);

    public PrometheusExporter() {

    }

    public Logger getLogger() {
        return this.logger;
    }

    public SpongeExecutorService getExecutorService() {
        return this.executorService;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Listener
    public void onPreinit(GamePreInitializationEvent event) {
        this.executorService = Sponge.getScheduler().createSyncExecutor(this);
        logger.info("Setting up config...");
        try {
            if (!config.exists()) {
                config.createNewFile();
            }

            configurationNode = cfgMgr.load();
            exporterConfig.loadDefaultsAndSave();

            this.cfgMgr.save(configurationNode);

            exporterConfig.enableConfiguredMetrics();
        } catch (Exception e) {
            logger.error("Failed to Start server", e);
        }
    }

    @Listener
    public void onServerStarted(GameStartedServerEvent event) {
        try {
            String host = exporterConfig.get(PrometheusExporterConfig.HOST);
            Integer port = exporterConfig.get(PrometheusExporterConfig.PORT);

            server = new MetricsServer(host, port, this);
            server.start();
            logger.info("Started Prometheus metrics endpoint at: " + host + ":" + port);

        } catch (Exception e) {
            logger.error("Could not start embedded Jetty server", e);
        }
    }

    @Listener
    public void onServerStop(GameStoppingServerEvent event) {
        if (server != null) {
            try {
                server.stop();
            } catch (Exception e) {
                logger.error("Failed to Stop", e);
            }
        }
    }

    public ConfigurationNode getConfigurationNode() {
        return configurationNode;
    }

    public void saveConfig() {
        try {
            this.cfgMgr.save(configurationNode);
        } catch (IOException e) {
            logger.error("Failed to Save config", e);
        }
    }
}
