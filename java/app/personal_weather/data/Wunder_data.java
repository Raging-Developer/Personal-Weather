package app.personal_weather.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Wunder_data
{    
    private Wund_txt wund_text_forecast;

    public Wund_txt getWund_text_forecast()
    {
        return wund_text_forecast;
    }


    public void populate(JSONObject data) throws JSONException
    {
        wund_text_forecast = new Wund_txt();
        wund_text_forecast.populate(data.getJSONObject("txt_forecast"));
        
    }            
}
