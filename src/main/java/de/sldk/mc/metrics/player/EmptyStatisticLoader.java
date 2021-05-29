package de.sldk.mc.metrics.player;

import org.spongepowered.api.entity.living.player.User;

import java.util.Collections;
import java.util.Map;

public class EmptyStatisticLoader implements PlayerStatisticLoader {

    @Override
    public Map<Enum<?>, Integer> getPlayerStatistics(User offlinePlayer) {
        return Collections.emptyMap();
    }
}
