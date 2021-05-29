package de.sldk.mc.metrics.player;

import org.spongepowered.api.entity.living.player.User;

import java.util.Map;

public interface PlayerStatisticLoader {

    Map<Enum<?>, Integer> getPlayerStatistics(User offlinePlayer);

}
