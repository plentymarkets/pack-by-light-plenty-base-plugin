package plentybase.plugin.hue.helper;

import org.json.JSONArray;
import org.json.JSONObject;
import plentybase.plugin.hue.api.HueApiClient;
import plentybase.plugin.hue.bean.ConfigBean;
import plentybase.plugin.hue.bean.LightStateData;
import plentybase.plugin.hue.bean.Response;
import plentybase.plugin.hue.context.Context;

import javax.swing.*;

public class HueApiClientHelper
{

	HueApiClient client;
	ConfigBean configBean;

	public HueApiClientHelper(ConfigBean bean)
	{
		client = new HueApiClient(bean);
		this.configBean = bean;
	}

	/**
	 * getLightData
	 *
	 * @return JSONObject where the data is stored in.
	 *
	 * @see HueApiClient#getLightData() uses the getLightData method from the Client that gets the light Data.
	 */

	public JSONObject getLightData()
	{
		JSONObject objects = new JSONObject(client.getLightData().toString());

		return objects;
	}

	/**
	 * setLight
	 *
	 * @param i --> the light that is turned on
	 *
	 * @see plentybase.plugin.hue.api.HueApiClient#setLight(int, double, double) uses the setLight Method from the Client and turns a light
	 * on
	 */


	public void setLight(int i)
	{

		client.setLight(i, configBean.getColourX(), configBean.getColourY());
	}

	/**
	 * setLightBoxFinished
	 *
	 * @param i --> the light that is turned on
	 *
	 * @see plentybase.plugin.hue.api.HueApiClient#setLightBoxFinished(int, double, double) uses the setLightBoxFinished Method from the
	 * Client and turns a light on when the box is finished.
	 */

	public void setLightBoxFinished(int i)
	{

		client.setLightBoxFinished(i, configBean.getColourBoxFinishedX(), configBean.getColourBoxFinishedY());
	}


	/**
	 * @param x --> object of the light that has to be turned off.
	 *
	 * @see plentybase.plugin.hue.api.HueApiClient#setLightOff(LightStateData) uses the setLightOff Method from the Client and turns it
     * off.
	 */

	public void setLightOff(LightStateData x)
	{
		client.setLightOff(x);
	}


	/**
	 * generateHubIpAddress generateHubIpAddress generates the hubIpAdress
	 *
	 * @return a String wich contains the ipAdress.
	 *
	 * @see HueApiClient#generateHubIpAddress()
	 */

	public String generateHubIpAddress()
	{

		Response response = client.generateHubIpAddress();

        JSONArray ipData = new JSONArray(response.getJSONResponse());

		if (ipData.length() >= 1)
		{
			JSONObject actualData = ipData.getJSONObject(0);

			String currentIPAddress = actualData.getString("internalipaddress"); // gets ip address
			Context.getLogger().info(currentIPAddress);

			return currentIPAddress;
		}
		else {
            JFrame frame = new JFrame();
            frame.setAlwaysOnTop(true);
            JOptionPane.showMessageDialog(frame, "Please connect the phillips hue bridge to a power socket", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
        return null;
	}

	public HueApiClient getClient()
	{
		return client;
	}

	public void setClient(HueApiClient client)
	{
		this.client = client;
	}

	public ConfigBean getBean()
	{
		return configBean;
	}

	public void setBean(ConfigBean bean)
	{
		this.configBean = bean;
	}
}