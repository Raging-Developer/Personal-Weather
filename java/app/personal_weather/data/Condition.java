package app.personal_weather.data;

import org.json.JSONObject;

public class Condition 
{
    private int code;
    private int temp;
    private String desc;              
    
    public int getCode()
    {
        return code;
    }

    public int getTemp()
    {
        return temp;
    }

    public String getDesc()
    {
        return desc;
    }

    /**
     * Populate the channel->item->condition namespace
     * @param data JSONObject
     */
    public void populate(JSONObject data)
    {       
        code = data.optInt("code");
        temp = data.optInt("temperature");
        desc = data.optString("text");
        
    }

}
