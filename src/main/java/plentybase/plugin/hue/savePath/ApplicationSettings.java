package plentybase.plugin.hue.savePath;


import org.apache.commons.lang3.SystemUtils;

import java.io.File;

/**
 * @author deubel
 */
public class ApplicationSettings {


    public static String APP_IDENTIFIER_MACOS = "eu.plentymarkets.plentybase";
    public static String APP_IDENTIFIER = "plentybase";
    public static String APP_IDENTIFIER_LINUX = ".plentybase"; // Hidden DIRECTORY

    /**
     * Returns path to Caches DIRECTORY on MacOS
     * Returns path to home DIRECTORY on Linux
     * Returns path to AppData/Local on Windows
     * Returns path to tmp on other systems
     *
     * @return String
     */
    private static String getCachesDirectory() {
        if (SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_MAC_OSX) {
            return SystemUtils.USER_HOME + File.separator + "Library" + File.separator + "Caches";
        } else if (SystemUtils.IS_OS_LINUX) {
            return SystemUtils.USER_HOME;
        } else if (SystemUtils.IS_OS_WINDOWS) {
            return SystemUtils.USER_HOME + File.separator + "AppData" + File.separator + "Local" + File.separator + "plentymarkets";
        } else {
            return SystemUtils.JAVA_IO_TMPDIR;
        }
    }

    /**
     * Returns path to Caches DIRECTORY on MacOs and tmp DIRECTORY on other systems
     *
     * @return String
     */
    public static String getCachesDirectoryWithIdentifier() {
        return getCachesDirectory() + File.separator + getApplicationIdentifier();
    }


    /**
     * Linux uses dot + application name as hidden config DIRECTORY in home DIRECTORY
     *
     * @return String identifier for application
     */
    private static String getApplicationIdentifier() {
        if (SystemUtils.IS_OS_LINUX) {
            return APP_IDENTIFIER_LINUX;
        } else if (SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_MAC_OSX) {
            return APP_IDENTIFIER_MACOS;
        } else {
            return APP_IDENTIFIER;
        }
    }

}
