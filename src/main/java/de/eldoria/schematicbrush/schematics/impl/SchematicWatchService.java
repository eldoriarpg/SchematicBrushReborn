package de.eldoria.schematicbrush.schematics.impl;

import de.eldoria.schematicbrush.SchematicBrushReborn;
import de.eldoria.schematicbrush.config.Config;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;

public class SchematicWatchService implements Runnable {
    private final Logger logger = SchematicBrushReborn.logger();
    private final Plugin plugin;
    private final Config config;
    private final SchematicBrushCache cache;
    private final ThreadGroup fileWorker = new ThreadGroup("File worker");
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(r -> {
        var thread = new Thread(fileWorker, r);
        thread.setUncaughtExceptionHandler((t, throwable) ->
                SchematicBrushReborn.logger().log(Level.SEVERE, "And error occured on thread " + t.getName() + ".", throwable));
        return thread;
    });
    private WatchService watchService;
    private Thread watchThread;

    private SchematicWatchService(Plugin plugin, Config config, SchematicBrushCache cache) {
        this.plugin = plugin;
        this.config = config;
        this.cache = cache;
    }

    public static SchematicWatchService of(Plugin plugin, Config config, SchematicBrushCache cache) {
        var watchService = new SchematicWatchService(plugin, config, cache);
        watchService.start();
        return watchService;
    }

    @SuppressWarnings({"InfiniteLoopStatement", "CatchMayIgnoreException"})
    @Override
    public void run() {
        while (true) {
            try {
                waitAndHandleEvent();
            } catch (InterruptedException e) {

            }
        }
    }

    private void waitAndHandleEvent() throws InterruptedException {
        var key = watchService.take();
        plugin.getLogger().log(Level.CONFIG, "Detected change in file system.");
        for (var event : key.pollEvents()) {
            var file = ((Path) key.watchable()).resolve(event.context().toString()).toFile();
            switch (event.kind().name()) {
                case "ENTRY_CREATE":
                    if (file.isFile()) {
                        plugin.getLogger().log(Level.CONFIG, "A new schematic was detected. Trying to add.");
                        executorService.schedule(() -> cache.addSchematic(file), 5, TimeUnit.SECONDS);
                    } else {
                        plugin.getLogger().log(Level.CONFIG, "A new directory was detected. Register watcher.");
                        watchDirectory(watchService, file.toPath());
                    }
                    break;
                case "ENTRY_DELETE":
                    if (file.isFile()) {
                        plugin.getLogger().log(Level.CONFIG, "A schematic was deleted. Trying to remove.");
                        cache.removeSchematic(file);
                    } else {
                        plugin.getLogger().log(Level.CONFIG, "A directory was deleted.");
                    }
                    break;
            }
        }
        key.reset();
    }

    private void watchDirectory(WatchService watcher, Path path) {
        if (!path.toFile().exists()) {
            logger.info("Path: " + path + " does not exists. Skipping watch service registration.");
            return;
        }
        try {
            registerWatcher(watcher, path);
            // register directory and subdirectories
            Files.walkFileTree(path, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                        throws IOException {
                    registerWatcher(watcher, dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not register watch service.", e);
        }
    }

    private void registerWatcher(WatchService service, Path path) throws IOException {
        path.register(service, ENTRY_CREATE, ENTRY_DELETE);
        logger.log(Level.CONFIG, "Registered watch service on: " + path);
    }

    public void shutdown() {
        executorService.shutdown();
        watchThread.interrupt();
    }

    private void init() {
        var root = plugin.getDataFolder().toPath().getParent().toString();

        var sources = config.getSchematicConfig().getSources();
        try {
            watchService = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            logger.log(Level.CONFIG, "Could not create watch service");
            return;
        }

        for (var source : sources) {
            var path = Paths.get(root, source.getPath());
            watchDirectory(watchService, path);
        }
    }

    private void start() {
        init();
        watchThread = new Thread(this);
        watchThread.setName("Schematic Brush Watch Service.");
        watchThread.setDaemon(true);
        watchThread.start();
    }
}
