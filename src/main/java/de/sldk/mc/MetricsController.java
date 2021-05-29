package de.sldk.mc;

import com.google.common.collect.Iterables;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.common.TextFormat;
import net.minecraft.entity.EntityLiving;
import net.minecraft.server.MinecraftServer;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.World;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MetricsController extends AbstractHandler {

    private final PrometheusExporter exporter;

    public MetricsController(PrometheusExporter exporter) {
        this.exporter = exporter;
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {

        if (!target.equals("/metrics")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Future<Object> future = exporter.getExecutorService().submit(() -> {
            MetricRegistry.getInstance().collectMetrics();
            return null;
        });

        try {
            future.get(5, TimeUnit.SECONDS);

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(TextFormat.CONTENT_TYPE_004);

            TextFormat.write004(response.getWriter(), CollectorRegistry.defaultRegistry.metricFamilySamples());

            baseRequest.setHandled(true);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            exporter.getLogger().warn("Failed to read server statistics", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private static long mean(long[] values) {
        long sum = 0l;
        for (long v : values) {
            sum += v;
        }

        return sum / values.length;
    }
}
