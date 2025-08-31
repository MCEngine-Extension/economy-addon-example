package io.github.mcengine.extension.addon.economy.example;

import io.github.mcengine.api.core.MCEngineCoreApi;
import io.github.mcengine.api.core.extension.logger.MCEngineExtensionLogger;
import io.github.mcengine.api.economy.extension.addon.IMCEngineEconomyAddOn;

import io.github.mcengine.extension.addon.economy.example.command.EconomyAddOnCommand;
import io.github.mcengine.extension.addon.economy.example.listener.EconomyAddOnListener;
import io.github.mcengine.extension.addon.economy.example.tabcompleter.EconomyAddOnTabCompleter;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Main class for the Economy AddOn example module.
 * <p>
 * Registers the {@code /economyaddonexample} command and related event listeners.
 */
public class ExampleEconomyAddOn implements IMCEngineEconomyAddOn {

    /**
     * Custom extension logger for this module, with contextual labeling.
     */
    private MCEngineExtensionLogger logger;

    /**
     * Initializes the Economy AddOn example module.
     * Called automatically by the MCEngine core plugin.
     *
     * @param plugin The Bukkit plugin instance.
     */
    @Override
    public void onLoad(Plugin plugin) {
        // Initialize contextual logger once and keep it for later use.
        this.logger = new MCEngineExtensionLogger(plugin, "AddOn", "EconomyExampleAddOn");

        try {
            // Register event listener
            PluginManager pluginManager = Bukkit.getPluginManager();
            pluginManager.registerEvents(new EconomyAddOnListener(plugin, this.logger), plugin);

            // Reflectively access Bukkit's CommandMap
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());

            // Define the /economyaddonexample command
            Command economyAddOnExampleCommand = new Command("economyaddonexample") {

                /**
                 * Handles command execution for /economyaddonexample.
                 */
                private final EconomyAddOnCommand handler = new EconomyAddOnCommand();

                /**
                 * Handles tab-completion for /economyaddonexample.
                 */
                private final EconomyAddOnTabCompleter completer = new EconomyAddOnTabCompleter();

                @Override
                public boolean execute(CommandSender sender, String label, String[] args) {
                    return handler.onCommand(sender, this, label, args);
                }

                @Override
                public java.util.List<String> tabComplete(CommandSender sender, String alias, String[] args) {
                    return completer.onTabComplete(sender, this, alias, args);
                }
            };

            economyAddOnExampleCommand.setDescription("Economy AddOn example command.");
            economyAddOnExampleCommand.setUsage("/economyaddonexample");

            // Dynamically register the /economyaddonexample command
            commandMap.register(plugin.getName().toLowerCase(), economyAddOnExampleCommand);

            this.logger.info("Enabled successfully.");
        } catch (Exception e) {
            this.logger.warning("Failed to initialize ExampleEconomyAddOn: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Called when the Economy AddOn example module is disabled/unloaded.
     *
     * @param plugin The Bukkit plugin instance.
     */
    @Override
    public void onDisload(Plugin plugin) {
        if (this.logger != null) {
            this.logger.info("Disabled.");
        }
    }

    /**
     * Sets the unique ID for this module.
     *
     * @param id the assigned identifier (ignored; a fixed ID is used for consistency)
     */
    @Override
    public void setId(String id) {
        MCEngineCoreApi.setId("mcengine-economy-addon-example");
    }
}
