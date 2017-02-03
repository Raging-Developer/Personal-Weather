package app.personal_weather.data;

import org.json.JSONException;
import org.json.JSONObject;

//For use with yahoo api

public class Item 
{
    private Condition cond;    
    private String title;
    private Forecast forecast;

    public Condition getCond()
    {
        return cond;
    }

    public String getTitle()
    {
        return title;
    } 
    
    public Forecast getForecast()
    {
        return forecast;
    }
    
    

    /**
     * Populate part of the channel->item namespace
     * @param data JSONObject
     * @throws JSONException 
     */
    public void populate(JSONObject data) throws JSONException
    {
        cond = new Condition();
        cond.populate(data.optJSONObject("condition"));
        
        title = data.optString("title");                       
        
        forecast = new Forecast();
        forecast.populate(data.getJSONArray("forecast"));
        
    }        
}
