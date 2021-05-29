package de.sldk.mc.config;

import de.sldk.mc.PrometheusExporter;
import de.sldk.mc.metrics.Metric;
import ninja.leaping.configurate.ConfigurationNode;

import java.util.function.Function;

public class MetricConfig extends PluginConfig<Boolean> {

    private static final String CONFIG_PATH_PREFIX = "enable_metrics";

    private Function<PrometheusExporter, Metric> metricInitializer;

    protected MetricConfig(String key, Boolean defaultValue, Function<PrometheusExporter, Metric> metricInitializer) {
        super(CONFIG_PATH_PREFIX + "." + key, defaultValue, ConfigurationNode::getBoolean);
        this.metricInitializer = metricInitializer;
    }

    public Metric getMetric(PrometheusExporter plugin) {
        return metricInitializer.apply(plugin);
    }
}
