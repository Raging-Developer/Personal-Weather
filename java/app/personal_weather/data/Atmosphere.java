package app.personal_weather.data;

import org.json.JSONObject;

public class Atmosphere
{
    private String humidity;
    private String pressure;
    private String rising;
    private String visibility;
    
    public String getHumidity()
    {
        return humidity;
    }
    public String getPressure()
    {
        return pressure;
    }
    public String getRising()
    {
        return rising;
    }
    public String getVisibility()
    {
        return visibility;
    }
    
    /**
     * Populate the channel->atmosphere namespace
     * @param data JSONObject
     */
    public void populate (JSONObject data)
    {
        humidity = data.optString("humidity");
        pressure = data.optString("pressure");
        rising = data.optString("rising");
        visibility = data.optString("visibility");
    }
}
