package com.darkblade12.enchantplus.plugin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.bstats.bukkit.Metrics;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class PluginBase extends JavaPlugin {
    private static final Pattern VERSION_PATTERN = Pattern.compile("\\d+(\\.\\d+){2}");
    protected final int projectId;
    protected final int pluginId;
    protected final String prefix;
    protected final Logger logger;
    protected final File config;
    protected final MessageManager messageManager;

    protected PluginBase(int projectId, int pluginId, String prefix) {
        this.projectId = projectId;
        this.pluginId = pluginId;
        this.prefix = prefix;
        logger = getLogger();
        config = new File(getDataFolder(), "config.yml");
        messageManager = new MessageManager(this);
    }

    private static int[] splitVersion(String version) {
        String[] split = version.split("\\.");
        int[] numbers = new int[split.length];
        for (int i = 0; i < split.length; i++) {
            try {
                numbers[i] = Integer.parseInt(split[i]);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return numbers;
    }

    @Override
    public void onEnable() {
        long startTime = System.currentTimeMillis();

        try {
            messageManager.enable();
        } catch (Exception e) {
            logException(e, "Failed to enable the message manager!");
            disable();
            return;
        }

        if (!load()) {
            disable();
            return;
        }

        enableMetrics();
        long duration = System.currentTimeMillis() - startTime;
        logInfo("Plugin version {0} enabled! ({1} ms)", getVersion(), duration);

        checkForUpdates();
    }

    @Override
    public void onDisable() {
        unload();
        messageManager.disable();
        logInfo("Plugin version {0} disabled.", getVersion());
    }

    public boolean onReload() {
        logInfo("Reloading plugin version {0}...", getVersion());

        try {
            messageManager.reload();
        } catch (Exception e) {
            logException(e, "Failed to reload the message manager!");
            disable();
            return false;
        }

        if (!reload()) {
            disable();
            return false;
        }

        logInfo("Plugin version {0} reloaded!", getVersion());
        return true;
    }

    public abstract boolean load();

    public abstract void unload();

    public abstract boolean reload();

    public final void disable() {
        logInfo("Plugin will disable...");
        getServer().getPluginManager().disablePlugin(this);
    }

    public void logInfo(String message) {
        logger.info(message);
    }

    public void logInfo(String message, Object... args) {
        logger.info(MessageFormat.format(message, args));
    }

    public void logWarning(String message) {
        logger.warning(message);
    }

    public void logWarning(String message, Object... args) {
        logger.warning(MessageFormat.format(message, args));
    }

    public void logException(Exception exception, String message) {
        logger.log(Level.SEVERE, message, exception);
    }

    public void logException(Exception exception, String message, Object... args) {
        logger.log(Level.SEVERE, MessageFormat.format(message, args), exception);
    }

    public final String formatMessage(String key, Object... args) {
        return messageManager.formatMessage(key, args);
    }

    public final void sendMessage(CommandSender sender, String key, Object... args) {
        sender.sendMessage(getPrefix() + " " + messageManager.formatMessage(key, args));
    }

    public void copyResource(String resourcePath, File outputFile, boolean replace) throws IOException {
        if (!replace && outputFile.exists()) {
            return;
        }

        InputStream stream = getResource(resourcePath);
        if (stream == null) {
            throw new IllegalArgumentException("Resource could not be found.");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line + "\n");
            }
        }
    }

    public void copyResource(String resourcePath, String outputPath, boolean replace) throws IOException {
        copyResource(resourcePath, new File(outputPath), replace);
    }

    private void enableMetrics() {
        try {
            Metrics metrics = new Metrics(this, pluginId);
            if (!metrics.isEnabled()) {
                logInfo("Metrics is disabled.");
            } else {
                logInfo("This plugin is using Metrics by BtoBastian.");
            }
        } catch (Exception e) {
            logException(e, "Failed to enable Metrics!");
        }
    }

    private void checkForUpdates() {
        JsonArray files;
        try {
            URL url = new URL("https://api.curseforge.com/servermods/files?projectIds=" + projectId);
            URLConnection conn = url.openConnection();
            conn.addRequestProperty("User-Agent", getName() + " Update Checker");
            conn.setReadTimeout(5000);
            conn.setDoOutput(true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String response = reader.readLine();
            reader.close();
            JsonElement element = new JsonParser().parse(response);
            files = element.getAsJsonArray();
            if (files.size() == 0) {
                logInfo("Failed to find any files for project id {0}!", projectId);
                return;
            }
        } catch (IOException | JsonSyntaxException e) {
            logException(e, "Failed to retrieve update information!");
            return;
        }

        JsonObject latestFile = (JsonObject) files.get(files.size() - 1);
        String fileName = latestFile.get("name").getAsString();
        Matcher matcher = VERSION_PATTERN.matcher(fileName);
        if (!matcher.find()) {
            logWarning("Failed to compare versions!");
            return;
        }

        String latestVersion = matcher.group();
        int[] currentNumbers = splitVersion(getVersion());
        int[] latestNumbers = splitVersion(latestVersion);
        if (currentNumbers == null || latestNumbers == null || currentNumbers.length != latestNumbers.length) {
            logWarning("Failed to compare versions!");
            return;
        }

        boolean updateAvailable = false;
        for (int i = 0; i < currentNumbers.length; i++) {
            if (latestNumbers[i] > currentNumbers[i]) {
                updateAvailable = true;
                break;
            }
        }

        if (!updateAvailable) {
            logInfo("There is no update available.");
            return;
        }

        String fileUrl = latestFile.get("fileUrl").getAsString();
        logInfo("Version {0} is available for download at:", latestVersion);
        logInfo(fileUrl);
    }

    public abstract Locale getCurrentLocale();

    public String getPrefix() {
        return prefix;
    }

    public String getVersion() {
        return getDescription().getVersion();
    }

    @Override
    public FileConfiguration getConfig() {
        if (!config.exists()) {
            saveDefaultConfig();
        }

        return super.getConfig();
    }
}
