package plentybase.plugin.hue.bean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import plentybase.plugin.hue.context.Context;

public class ConfigBean {

    private static Logger logger = Context.getLogger();

    private String username;
    private String hubIpAddress;

    // default values

    private Double colourX; // yellow
    private Double colourY; // yellow
    private Double colourBoxFinishedX; // green
    private Double colourBoxFinishedY;  // green

    public ConfigBean() {
        colourX = 0.58; // yellow
        colourY = 0.48; // yellow
        colourBoxFinishedX = 0.15; // green
        colourBoxFinishedY = 0.8;  // green
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHubIpAddress() {
        return hubIpAddress;
    }

    public void setHubIpAddress(String hubIpAddress) {
        this.hubIpAddress = hubIpAddress;
    }

    public Double getColourX() {
        return colourX;
    }

    public void setColourX(Double colourX) {
        if (colourX >= 0.01 && colourX <= 1.0) {
            this.colourX = colourX;
        } else {
            logger.info("BoxColour > 1.0 or negative");
        }

    }

    public Double getColourY() {
        return colourY;
    }

    public void setColourY(Double colourY) {
        if (colourY > 0.01 && colourY <= 1.0) {
            this.colourY = colourY;
        } else {
            logger.info("BoxColour > 1.0 or negative");
        }
    }

    public Double getColourBoxFinishedX() {
        return colourBoxFinishedX;
    }

    public void setColourBoxFinishedX(Double colourBoxFinishedX) {
        if (colourBoxFinishedY >= 0.01 && colourBoxFinishedY <= 1.0) {
            this.colourBoxFinishedX = colourBoxFinishedX;
        } else {
            logger.info("FinishedBoxColour > 1.0 or negative");
        }
    }

    public Double getColourBoxFinishedY() {
        return colourBoxFinishedY;
    }

    public void setColourBoxFinishedY(Double colourBoxFinishedY) {
        if (colourBoxFinishedY >= 0.01 && colourBoxFinishedY <= 1.0) {
            this.colourBoxFinishedY = colourBoxFinishedY;
        } else {
            logger.info("FinishedBoxColour > 1.0 or negative");
        }
    }

    @Override
    public String toString() {
        return "ConfigBean{" +
                "username='" + username + '\'' +
                ", hubIpAddress='" + hubIpAddress + '\'' +
                ", colourX=" + colourX +
                ", colourY=" + colourY +
                ", colourBoxFinishedX=" + colourBoxFinishedX +
                ", colourBoxFinishedY=" + colourBoxFinishedY +
                '}';
    }
}
