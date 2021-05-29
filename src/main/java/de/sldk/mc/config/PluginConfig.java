package de.sldk.mc.config;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import javax.security.auth.login.Configuration;
import java.util.function.Function;

public class PluginConfig<T> {

    protected final String key;
    protected final T defaultValue;
    protected final Function<ConfigurationNode, T> getter;

    protected PluginConfig(String key, T defaultValue, Function<ConfigurationNode, T> getter) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.getter = getter;
    }

    public void setDefault(ConfigurationNode configurationNode) {
        configurationNode.getNode((Object[]) this.key.split("\\.")).setValue(defaultValue);
    }

    public T get(ConfigurationNode config) {
        return getter.apply(config.getNode((Object[]) this.key.split("\\.")));
    }
}
