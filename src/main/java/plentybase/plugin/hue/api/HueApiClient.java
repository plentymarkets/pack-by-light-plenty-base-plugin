package plentybase.plugin.hue.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import plentybase.plugin.hue.bean.ConfigBean;
import plentybase.plugin.hue.bean.Response;
import plentybase.plugin.hue.bean.LightStateData;
import plentybase.plugin.hue.context.Context;
import plentybase.plugin.hue.helper.RequestHelper;

import javax.swing.*;


public class HueApiClient {

    private static Logger logger = Context.getLogger();

    private RequestHelper builder = new RequestHelper();
    private ConfigBean configBean;
    public HueApiClient(ConfigBean configBean){
       this.configBean = configBean;
    }


    /**
     * getLightData
     * getLightData is a method that gets data of all Phillips Hue lights that are registered in the Phillips Hue bridge.
     *
     * @return the data as a JSONObject.
     */

    public JSONObject getLightData() {

        Response response = builder.doGETRequest("http://" + configBean.getHubIpAddress() + "/api" + configBean.getUsername() + "/lights");
        return new JSONObject(response.getJSONResponse());
    }

    /**
     * setLight
     * setLight turns a on specific light and uses colours specified in the config.
     *
     * @param i Specifies wich lamp is turned on.
     */

    public Response setLight(int i, double x, double y) {

        Response response = builder.doPUTRequest("http://" + configBean.getHubIpAddress()  + "/api" + configBean.getUsername() + "/lights/" + i + "/state",
                "{\"on\": true, \"xy\":[" + x+ "," + y + "],\"transitiontime\": 0,\"sat\": 100, \"bri\": 100}");

        return new Response(response.JSONResponse,response.responseCode);
    }

    /**
     * setLightOff
     * setLightOff is a method that turns a single light off by doing a PUT request.
     * @param x The light that is turned off.
     */

    public void setLightOff(LightStateData x) {

        builder.doPUTRequest("http://" + configBean.getHubIpAddress() + "/api" + configBean.getUsername() + "/lights/" + x.getLightID() + "/state",
                "{\"on\": false,\"transitiontime\": 0}");
    }

    /**
     * generateHubIpAddress
     * gets the current hubIpAdress through a get call on "https://www.meethue.com/api/nupnp"
     * and and sets the variable hubIpAdress in cfg class.
     */

    public Response generateHubIpAddress() {

        Response response = builder.doGETRequest("https://www.meethue.com/api/nupnp");

        return response;
    }

    /**
     * setLightBoxFinished
     * sets the light of a finished box permanently to a colour that
     * is stored in the config file.
     * It does this by using the used variable.
     * used --> box finished --> box stays on
     *
     * @param i determines wich light is being turned on.
     */

    public Response setLightBoxFinished(int i, double x, double y) {

        Response response = builder.doPUTRequest("http://" + configBean.getHubIpAddress()  + "/api" + configBean.getUsername() + "/lights/" + i + "/state",
                "{\"on\": true, \"xy\":[" + x + "," + y + "],\"transitiontime\": 0, \"sat\": 100, \"bri\": 100}");
        return response;
    }

    /**
     * authenticate
     * authenticate is a method that is used to generate a username by doing a POST request.
     * @return JSONObject that is filled with the response for future use.
     */

    public JSONObject authenticate() {
            logger.info(configBean.getHubIpAddress());
            Response response = builder.doPOSTRequest("http://" + configBean.getHubIpAddress()  + "/api", "\t{\"devicetype\":\"packByLight#plenty\"}");

            // JSONArray --> JSONObject because the way the response is build.
            // it has a size of 1, thats why you need to get the "first" --> 0 object.
            JSONArray arr = new JSONArray(response.getJSONResponse());
            JSONObject obj = (arr.getJSONObject(0));
            return obj;
    }

    public ConfigBean getConfigBean() {
        return configBean;
    }

    public void setConfigBean(ConfigBean configBean) {
        this.configBean = configBean;
    }
}