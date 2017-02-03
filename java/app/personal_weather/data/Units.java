package app.personal_weather.data;

import org.json.JSONObject;

public class Units
{
    private String temp;        

    public String getTemp()
    {
        return temp;
    }

    /**
     * Populate with the units from the channel->units->temperature namespace
     * @param data JSONObject
     */
    public void populate(JSONObject data)
    {
        temp = data.optString("temperature");
        
    }
}
