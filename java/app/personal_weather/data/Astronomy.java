package app.personal_weather.data;

import org.json.JSONObject;

public class Astronomy
{
    private String sunrise;
    private String sunset;
    
    
    public String getSunrise()
    {
        return sunrise;
    }
    public String getSunset()
    {
        return sunset;
    }
    
    /**
     * Populate the channel->astronomy namespace
     * @param data JSONObject
     */
    public void populate (JSONObject data)
    {
        sunrise = data.optString("sunrise");
        sunset = data.optString("sunset");
    }
}
