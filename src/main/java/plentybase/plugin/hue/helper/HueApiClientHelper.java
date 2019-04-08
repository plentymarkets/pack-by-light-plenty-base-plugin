package plentybase.plugin.hue.helper;

import org.json.JSONArray;
import org.json.JSONObject;
import plentybase.plugin.hue.api.HueApiClient;
import plentybase.plugin.hue.bean.ConfigBean;
import plentybase.plugin.hue.bean.LightStateData;
import plentybase.plugin.hue.bean.Response;
import plentybase.plugin.hue.context.Context;

public class HueApiClientHelper {

    HueApiClient client;
    ConfigBean configBean;

    public HueApiClientHelper(ConfigBean bean) {
        client = new HueApiClient(bean);
        this.configBean = bean;
    }

    /** getLightData
     * @see HueApiClient#getLightData()
     * uses the getLightData method from the Client that gets the light Data.
     * @return JSONObject where the data is stored in.
     */

    public JSONObject getLightData() {
        JSONObject objects = new JSONObject(client.getLightData().toString());

        return objects;
    }

    /** setLight
     * @see plentybase.plugin.hue.api.HueApiClient#setLight(int, double, double)
     * uses the setLight Method from the Client and turns a light on
     * @param i --> the light that is turned on
     */


    public void setLight(int i) {

        client.setLight(i, configBean.getColourX(), configBean.getColourY());
    }

    /** setLightBoxFinished
     * @see plentybase.plugin.hue.api.HueApiClient#setLightBoxFinished(int, double, double)
     * uses the setLightBoxFinished Method from the Client and turns a light on when the box is finished.
     * @param i --> the light that is turned on
     */

    public void setLightBoxFinished(int i) {

        client.setLightBoxFinished(i, configBean.getColourBoxFinishedX(), configBean.getColourBoxFinishedY());
    }


    /**
     *@see plentybase.plugin.hue.api.HueApiClient#setLightOff(LightStateData)
     * uses the setLightOff Method from the Client and turns it off.
     *
     * @param x --> object of the light that has to be turned off.
     */

    public void setLightOff(LightStateData x) {
        client.setLightOff(x);
    }


    /** generateHubIpAddress
     * generateHubIpAddress generates the hubIpAdress
     * @see HueApiClient#generateHubIpAddress()
     * @return a String wich contains the ipAdress.
     */

    public String generateHubIpAddress() {

        Response response = client.generateHubIpAddress();

        JSONArray ipData = new JSONArray(response.getJSONResponse());

        JSONObject actualData = ipData.getJSONObject(0);

        String currentIPAddress = actualData.getString("internalipaddress"); // gets ip address
        Context.getLogger().info(currentIPAddress);

        return currentIPAddress;
    }

    public HueApiClient getClient() {
        return client;
    }

    public void setClient(HueApiClient client) {
        this.client = client;
    }

    public ConfigBean getBean() {
        return configBean;
    }

    public void setBean(ConfigBean bean) {
        this.configBean = bean;
    }
}