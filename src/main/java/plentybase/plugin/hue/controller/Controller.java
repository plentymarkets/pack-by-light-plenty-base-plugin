package plentybase.plugin.hue.controller;

import com.plentymarkets.tool.core.event.EventBean;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import plentybase.plugin.hue.context.Context;
import plentybase.plugin.hue.bean.ConfigBean;
import plentybase.plugin.hue.bean.LightStateData;
import plentybase.plugin.hue.config.Configuration;

import plentybase.plugin.hue.helper.FileWatcher;
import plentybase.plugin.hue.helper.HueApiClientHelper;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

public class Controller {

    private Configuration config;
    private ConfigBean configBean;
    private boolean moreThanTenLights;

    private HueApiClientHelper clientHelper;
    private HashMap<Integer, Integer> lightMap;
    private Logger logger = Context.getLogger();

    private int boxID;
    private int totalItemCount;
    private Integer scannedItemCount;
    private boolean allOrdersComplete;

    private File csv;
    private FileWatcher fw;
    private Boolean isUsernameSet = false;

    public Controller() {
        configBean = new ConfigBean();
        config = new Configuration();
        lightMap = new HashMap<>();
        csv = new File(config.getCSV_FILEPATH());
        fw = new FileWatcher(Context.getPluginDataPath());
    }

    ArrayList<LightStateData> dataList = new ArrayList<>(); // is used to store hueLightObjects

    /**
     * This ArrayList is used to store states Objects from Phillips Hue lights
     * the information about the states is gotten through a api call
     */
    ArrayList<JSONObject> statesList = new ArrayList<>(); // is used to store the states object of hueLights

    /**
     * OnScan
     * OnScan is a method that is called when the event route "scan" is called.
     * this method turns all lights off if all orders are finished, it turns lights on if a item is scanned
     * it uses the mapping from the lightMap to determine wich light has to be turned on.
     * it turns lights on by using the setLightBoxFinished method that also sets the light to used if the box is finished. It also sets lights off that are unused.
     *
     * @param eventBean the eventbean that contains the data that is aviable when a item is scanned in the plentymarkets Backend
     */

    public void onScan(EventBean eventBean) {
        try {
            JSONObject response = new JSONObject(eventBean.getJsonData());

            // set values
            boxID = response.getInt("boxNumber");
            totalItemCount = response.getInt("totalItemCount");
            scannedItemCount = response.getInt("scannedItemCount");
            allOrdersComplete = response.getBoolean("allOrdersComplete");

            Integer value = lightMap.get(boxID);

            if (scannedItemCount == totalItemCount) {
                setLightBoxFinished(value); // if a light is finished , used == true
            }

            if (allOrdersComplete) {
                Thread.sleep(2000);
                setAllLightsOff();
            }

            logger.info("value" + value);

            if (value != null) {
                setUnusedLightOff();
                logger.info("light off scan method ");
                // value - 1 because a arraylist starts at 0
                if (!dataList.get(value - 1).isUsed()) {
                    setUnusedLight(value); // turns light on
                }
            }
        } catch (Exception e) {
            setUnusedLightOff();
            logger.error(e.toString());
        }
    }

    /**
     * OnProcessEnd
     * OnProcessEnd is a method that is called when a process ends.
     * it turns all lights that were previously on off, by calling setAllLights off.
     * Additionaly it also sets the status off all lights to unused, because after the process is finished all lights aren't used anymore.
     */

    public void OnProcessEnd() {
        setAllLightsOff();

        for (LightStateData x : dataList) {
            x.setUsed(false);
        }
    }

    /**
     * OnProcessStart
     * OnProcessStart is a method that is called when a process is started.
     * it turns all lights off by calling the setAllLightsOff() method.
     */

    public void OnProcessStart() {
        initLightList();
        clientHelper.setBean(new ConfigBean());
        updateConfigBeanInClientAndClientHelper();
        setAllLightsOff();
        logger.info("lightsoffprocessstart" );
    }

    /**
     * loadHueConfig
     * loadHueConfig is a method that fills the configBean with values from the Config.
     */

    public boolean loadHueConfig() {
        setBean(config.readHueConfig());
        updateConfigBeanInClientAndClientHelper();
        if (configBean.getUsername() == null || configBean.getUsername().equals("error")) {
            return false;
        } else {
            isUsernameSet = true;
            return true;
        }
    }

    /**
     * generateUsernameAndHubIpAddress uses the authenticate method to generate a username,
     * by calling the /api route twice
     * and pressing the link button of the phillips hue bridge in between.
     * if the username and hubipAddress isn't set, a method is called that generates them automatically
     */

    public ConfigBean generateUsernameAndHubIpAddress() {
        if (configBean.getUsername() == null || !isUsernameSet) {
            logger.info("username is being generated");

            configBean.setHubIpAddress(clientHelper.generateHubIpAddress());

            // update because the clientHelper needs the username to call the authenticate method
            updateConfigBeanInClientAndClientHelper();

            JSONObject obj = clientHelper.getClient().authenticate();

            if (obj != null) {
                JSONObject error = obj.getJSONObject("error");
                if (error != null && error.getString("description").equals("link button not pressed")) {
                    JFrame frame = new JFrame();
                    frame.setAlwaysOnTop(true);
                    JOptionPane.showMessageDialog(frame, "Press link button on Phillips Hue Bridge (big button in the middle)", "Info", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            // calls the route again after link button is pressed to generate username.
            obj = clientHelper.getClient().authenticate();

            if (obj != null) {
                try {
                    JSONObject success = obj.getJSONObject("success");
                    if (success != null) {
                        logger.info(success.toString());
                        configBean.setUsername(success.getString("username"));
                        config.writeHueConfig(configBean);
                        logger.info("write config called");
                        isUsernameSet = true;
                        return configBean;
                    }
                } catch (JSONException ex) {
                    logger.info("username isnt generated properly username == error");
                    configBean.setUsername("error");
                    config.writeHueConfig(configBean);
                    isUsernameSet = false;
                    JFrame frame = new JFrame();
                    frame.setAlwaysOnTop(true);
                    JOptionPane.showMessageDialog(frame, "username is not generated properly, please take a look at the marketplace documentation", "Info", JOptionPane.INFORMATION_MESSAGE);
                }

            }
        }
        return null;
    }

    /**
     * init
     * a Method called at the start of the plugin to initialize everything that is needed.
     */

    public void init() {

        // starts thread that detects change in lightdata.csv
        fw.start();

        clientHelper = new HueApiClientHelper(configBean);

        // # Step 1: reads ColourConfig and updates the configBean with new values if they were set
        config.readColourConfig();

        if (!csv.exists()){
            createCsv();
        }

        // # Step 2: loads config  hubIp and Username into config Object and fills variables with values
        // if config is empty username and hubIpAddress is generated.
        // creates csv for mapping purposes

        if (!loadHueConfig()) {
            configBean = generateUsernameAndHubIpAddress();
        }

        if (isUsernameSet) {
            // # Step 3: reads csv file and puts values into lightMap
            cacheBoxIdLightIdMapping();

            // # Step 4: fills List with information about lightbulbs in hueApi
            initLightList();

            // # Step 5: set moreThanTenLights

            if (dataList.size() > 10) {
                moreThanTenLights = true;
            }
        }

    }

    /**
     * setAllLightsOff
     * setAllLightsOff is a method that iterates through a list that is filled with lightObjects and their values (Lightdata)
     * turns all lights off that are on and then turns them off, by calling the setLightOff method from clientHelper.
     * Due to api call limitations it is only possible to fire 10 calls per second.
     * if there are more than 10 lights in the dataList the api will reach its limits, thats why theres a 100 millisecond delay if moreThanTenLights is true.
     */

    public void setAllLightsOff() {
        for (LightStateData y : dataList) {
            if (y.isOn()) {
                clientHelper.setLightOff(y);
                Context.getLogger().info(y.toString());
                y.setOn(false);
                if (moreThanTenLights) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * setUnusedLightsOff
     * it iterates through a list that is filled with lightObjects (dataList)
     * and checks if these lights are on and unused, if yes they are turned off.
     */

    public void setUnusedLightOff() {
        for (LightStateData x : dataList) {
            if (x.isOn() && !x.isUsed()) {
                clientHelper.setLightOff(x);
                x.setOn(false);
            }
        }
    }

    /**
     * cacheBoxIDLightIdMapping
     * chaches the mapping from the csv file into a Hashmap
     * to store the values. Later the map can be used to know
     * what box belongs to wich light.
     */

    public void cacheBoxIdLightIdMapping() {
        logger.info("cache csv start");
        HashMap<Integer, Integer> map = new HashMap<>();
        if (csv.exists()) {

            try {
                Scanner sc;
                sc = new Scanner(csv);
                // reads header
               String header = sc.nextLine();

                while (sc.hasNext()) {
                    String data = sc.nextLine();
                    data = data.trim();
                    String[] values = data.split(";");

                    map.put(Integer.parseInt(values[0]), Integer.parseInt(values[1]));
                    logger.info("map values"+ map.toString());
                }
                //sets map with new values
                setLightMap(map);

                if (!getLightMap().isEmpty()) {
                    logger.info("cache csv successful");
                }
                else {
                    logger.info("csv is not mapped");
                    JFrame frame = new JFrame();
                    frame.setAlwaysOnTop(true);
                    JOptionPane.showMessageDialog(frame, "Please map the lightData.csv", "Info", JOptionPane.INFORMATION_MESSAGE);
                }

            } catch (FileNotFoundException e) {
                logger.error(e.toString());
            } catch (Exception e) {
                logger.error(e.toString());
            }
        } else {
           createCsv();
        }
    }

    public void createCsv(){
        if (!csv.exists()) {
            try {
                FileWriter fw = new FileWriter(csv);
                fw.write("boxID;LightID");
                fw.flush();
                fw.close();

            } catch (IOException e) {
                logger.error(e.toString());
            }
        }
    }

    /**
     * initLightList
     * initLightList is a method that uses the getLightData from clientHelper to getLightData
     * and parses the information into a states and then into a dataList.
     */

    public void initLightList() {

        int x = 0;

        JSONObject objects = clientHelper.getLightData();

        // gets 1,2,3 as a key through iterators
        for (Iterator iter = objects.keys(); iter.hasNext(); ) {
            String key = (String) iter.next();

            JSONObject obji = objects.getJSONObject(key);

            // adds the "state" values of lightbulbs into a list
            statesList.add(obji.getJSONObject("state"));
        }
        for (Iterator iter = objects.keys(); iter.hasNext(); ) {
            String key = (String) iter.next();

            dataList.add(new LightStateData(Integer.parseInt(key), statesList.get(x).getBoolean("on"), statesList.get(x).getBoolean("reachable"), false));
            x++;
            System.out.println(dataList.toString());
        }
    }

    /**
     * setUnusedLight
     * setUnusedLight is a method that sets a light on by calling the setLight method in clientHelper.
     * it also sets the status of the light that is being turned on to on.
     *
     * @param i the light that is turned on.
     */

    public void setUnusedLight(int i) {

        clientHelper.setLight(i);

        // i - 1 because a array starts at 0
        if (dataList.get(i - 1) != null) {
            dataList.get(i - 1).setOn(true);
        }
    }

    /**
     * setLightBoxFinished
     * setLightBoxFinished is a method that turns a light on by calling the setLightBoxFinished method in clientHelper.
     * it also sets the status to on and sets it to used.
     *
     * @param i
     */

    public void setLightBoxFinished(int i) {

        clientHelper.setLightBoxFinished(i);

        // i - 1 because a array starts at 0
        if (dataList.get(i - 1) != null) {
            dataList.get(i - 1).setOn(true);
            dataList.get(i - 1).setUsed(true);
        }
    }

    /**
     * updateConfigBeanInCliendAndClientHelper
     * updateConfigBeanInCliendAndClientHelper is a method that updates the config bean ac
     */
    public void updateConfigBeanInClientAndClientHelper(){
     clientHelper.setBean(configBean);
     clientHelper.getClient().setConfigBean(configBean);
    }

    public HashMap<Integer, Integer> getLightMap() {
        return lightMap;
    }

    public void setLightMap(HashMap<Integer, Integer> lightMap) {
        this.lightMap = lightMap;
    }

    public ArrayList<LightStateData> getDataList() {
        return dataList;
    }

    public void setDataList(ArrayList<LightStateData> dataList) {
        this.dataList = dataList;
    }

    public ArrayList<JSONObject> getStatesList() {
        return statesList;
    }

    public void setStatesList(ArrayList<JSONObject> statesList) {
        this.statesList = statesList;
    }

    public HueApiClientHelper getClientHelper() {
        return clientHelper;
    }

    public void setClientHelper(HueApiClientHelper clientHelper) {
        this.clientHelper = clientHelper;
    }

    public Configuration getConfig() {
        return config;
    }

    public void setConfig(Configuration config) {
        this.config = config;
    }

    public ConfigBean getBean() {
        return configBean;
    }

    public void setBean(ConfigBean bean) {
        this.configBean = bean;
        clientHelper.setBean(bean);
    }
}