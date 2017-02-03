package app.personal_weather.data;

import org.json.JSONObject;

/**
 * 
 * @author Christopher D. Harte
 *
 */
public class Wind
{
    private String chill;
    private String direction;
    private String speed;        
    
    public String getChill()
    {
        return chill;
    }

    public String getDirection()
    {
        return direction;
    }

    public String getSpeed()
    {
        return speed;
    }

    
    /**
     * Populate the channel->wind namespace
     * @param data JsonObject
     */
    public void populate (JSONObject data)
    {
        chill = data.optString("chill");
        direction = data.optString("direction");
        speed = data.optString("speed");
    }
}
