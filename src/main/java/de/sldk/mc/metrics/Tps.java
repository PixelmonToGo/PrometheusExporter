package de.sldk.mc.metrics;

import de.sldk.mc.PrometheusExporter;
import de.sldk.mc.tps.TpsCollector;
import io.prometheus.client.Gauge;
import org.spongepowered.api.scheduler.Task;

public class Tps extends Metric {

    private static final Gauge TPS = Gauge.build()
            .name(prefix("tps"))
            .help("Server TPS (ticks per second)")
            .create();

    private Task task;

    private final TpsCollector tpsCollector = new TpsCollector();

    public Tps(PrometheusExporter plugin) {
        super(plugin, TPS);
    }

    @Override
    public void enable() {
        super.enable();
        this.task = startTask(getPlugin());
    }

    @Override
    public void disable() {
        super.disable();
        task.cancel();
    }

    private Task startTask(PrometheusExporter plugin) {
        return Task.builder()
                .execute(tpsCollector)
                .intervalTicks(TpsCollector.POLL_INTERVAL)
                .submit(plugin);
    }

    @Override
    public void doCollect() {
        TPS.set(tpsCollector.getAverageTPS());
    }
}
