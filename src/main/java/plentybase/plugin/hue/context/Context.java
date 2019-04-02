package plentybase.plugin.hue.context;

import com.plentymarkets.tool.plugins.api.context.ContextInjector;
import com.plentymarkets.tool.plugins.api.context.PluginApplicationContext;
import com.plentymarkets.tool.plugins.api.context.PluginContext;
import org.apache.logging.log4j.Logger;
import org.pf4j.PluginWrapper;

import java.nio.file.Path;
import java.util.Properties;

@PluginContext
public class Context {
 private static PluginApplicationContext pluginApplicationContext;

@ContextInjector
 public static void inject(PluginApplicationContext context){
     pluginApplicationContext = context;
 }

 public static Properties getConfig(){
   return pluginApplicationContext.getPluginConfig();
 }

public static Logger getLogger(){
    return pluginApplicationContext.getPluginLogger();
}

public static PluginWrapper getPluginWrapper(){
    return pluginApplicationContext.getPluginWrapper();
}

public static Path getPluginDataPath(){
    return pluginApplicationContext.getPluginDataPath();
}

public static void saveConfig() {
    pluginApplicationContext.savePluginConfig();
    }
}
