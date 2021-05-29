package de.sldk.mc.metrics;

import de.sldk.mc.PrometheusExporter;
import io.prometheus.client.Collector;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.user.UserStorageService;

import java.util.Optional;

public abstract class PlayerMetric extends Metric {

    public PlayerMetric(PrometheusExporter plugin, Collector collector) {
        super(plugin, collector);
    }

    @Override
    public final void doCollect() {
        Optional<UserStorageService> userStorageService = Sponge.getServiceManager().provide(UserStorageService.class);
        if (userStorageService.isPresent()) {
            for (GameProfile player : userStorageService.get().getAll()) {

                userStorageService.get().get(player.getUniqueId()).ifPresent(this::collect);
            }
        } else {
            getPlugin().getLogger().warn("Failed to find User Service");
        }
    }

    protected abstract void collect(User player);

    protected String getUid(User player) {
        return player.getUniqueId().toString();
    }

    protected String getNameOrUid(User player) {
        return player.getName();
    }

}
