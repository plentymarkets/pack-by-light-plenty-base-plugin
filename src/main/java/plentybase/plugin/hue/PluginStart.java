package plentybase.plugin.hue;

import org.pf4j.Plugin;
import org.pf4j.PluginException;
import org.pf4j.PluginWrapper;
import plentybase.plugin.hue.controller.Controller;


public class PluginStart extends Plugin {

   private static Controller controller;

    public PluginStart(PluginWrapper wrapper) {
        super(wrapper);
    }

    public static Controller getController(){
        if (controller == null){
            controller = new Controller();
            controller.init();
        }
        return controller;
    }
    /**
     * start is a method that is always
     * called when the plugin starts.
     */

    public void start() throws PluginException {
        getController();
    }

    public void stop() throws PluginException {
    }

    public void delete() throws PluginException {
    }

}

