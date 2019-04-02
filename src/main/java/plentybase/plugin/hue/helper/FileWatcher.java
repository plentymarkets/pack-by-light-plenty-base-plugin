package plentybase.plugin.hue.helper;

import plentybase.plugin.hue.config.Configuration;
import plentybase.plugin.hue.context.Context;
import plentybase.plugin.hue.extensions.PluginStart;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

public class FileWatcher extends Thread {
    private Path directory;
    public FileWatcher(Path directory){
        this.directory = directory;
    }
        public void run() {
            if (directory != null) {
            Context.getLogger().info("watch files" + directory);
            try (final WatchService watchService = FileSystems.getDefault().newWatchService()) {
                final WatchKey watchKey = directory.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
                while (true) {
                    final WatchKey wk = watchService.take();
                    for (WatchEvent<?> event : wk.pollEvents()) {
                        final Path changed = (Path) event.context();
                        Context.getLogger().info("File changed: " + changed);
                        if (changed.endsWith("lightData.csv")) {
                            Context.getLogger().info("lightdata csv changed method should be called");
                            PluginStart.getController().cacheBoxIdLightIdMapping();
                        }
                    }
                    // reset the key
                    boolean valid = wk.reset();
                    if (!valid) {
                        Context.getLogger().info("Key has been unregistered");
                    }
                }
            } catch (InterruptedException e) {
                Context.getLogger().info(e.toString());
            } catch (IOException e) {
                Context.getLogger().info(e.toString());
            }
        }
    }
}
