package plentybase.plugin.hue;

import org.pf4j.Plugin;
import org.pf4j.PluginException;
import org.pf4j.PluginWrapper;
import plentybase.plugin.hue.context.Context;
import plentybase.plugin.hue.controller.Controller;

import javax.swing.*;


public class PluginStart extends Plugin {

   private static Controller controller;

    public PluginStart(PluginWrapper wrapper) {
        super(wrapper);

        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e)
        {
            Context.getLogger().error(e);
        }
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

