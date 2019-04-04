package plentybase.plugin.hue.config;

import plentybase.plugin.hue.context.Context;
import plentybase.plugin.hue.bean.ConfigBean;

import java.io.*;
import java.util.Properties;

public class Configuration {

    ConfigBean cfgBean = new ConfigBean();
    private final String HUE_CONFIGPATH = Context.getPluginDataPath() + File.separator + "hueConfig.properties";
    private final String CSV_FILE_PATH = Context.getPluginDataPath() + File.separator + "lightData.csv";

    File hueConfig = new File(HUE_CONFIGPATH);

    public ConfigBean readColourConfig() {
        try {
            Context.getLogger().info("reads Config");
            cfgBean.setColourX(Double.parseDouble(Context.getConfig().getProperty("colourX")));
            cfgBean.setColourY(Double.parseDouble(Context.getConfig().getProperty("colourY")));
            cfgBean.setColourBoxFinishedX(Double.parseDouble(Context.getConfig().getProperty("colourBoxFinishedX")));
            cfgBean.setColourBoxFinishedY(Double.parseDouble(Context.getConfig().getProperty("colourBoxFinishedY")));

            Context.getLogger().info("config values were read");
            if (cfgBean != null) {
                 return cfgBean;
            }
            } catch (NullPointerException ex) {
                Context.getLogger().error(ex.toString());
            }
        // if plugin config doesn't exist it returns a default config.
        Context.getLogger().info("Config Bean Data in readColourConfig method"+ cfgBean.toString());
        return null;
    }

    public void writeHueConfig(ConfigBean bean) {
        Properties prop = new Properties();
        OutputStream output = null;

        try {
            hueConfig.createNewFile();
            output = new FileOutputStream(hueConfig);

            // set the properties value
            prop.setProperty("username", bean.getUsername());
            prop.setProperty("hubIpAdress", bean.getHubIpAddress());

            // save properties
            prop.store(output, null);

        } catch (IOException io) {
            Context.getLogger().info(io.toString());
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    Context.getLogger().info(e.toString());
                }
            }
        }
    }

    public ConfigBean readHueConfig() {
        if(hueConfig.exists()){
            try {
                FileReader reader = new FileReader(HUE_CONFIGPATH);
                Properties props = new Properties();
                props.load(reader);

                cfgBean.setHubIpAddress(props.getProperty("hubIpAdress"));
                cfgBean.setUsername(props.getProperty("username"));

                reader.close();

                return cfgBean;

            } catch (FileNotFoundException ex) {

            } catch (IOException ex) {

            } catch (NullPointerException ex) {
            }
        }
        return new ConfigBean();
    }

    public static double getColourX() {
        return Double.parseDouble(Context.getConfig().getProperty("ColourX"));
    }

    public static double getColourY(){
        return Double.parseDouble(Context.getConfig().getProperty("Colour>"));
    }

    public static double getColourBoxFinishedX() {
        return Double.parseDouble(Context.getConfig().getProperty("ColourBoxFinishedX"));
    }

    public static double getColourBoxFinishedY() {
        return Double.parseDouble(Context.getConfig().getProperty("ColourBoxFinishedY"));
    }

    public String getCSV_FILEPATH() {
        return CSV_FILE_PATH;
    }
}
