package app.personal_weather.data;

import org.json.JSONException;
import org.json.JSONObject;

//The RSS used be called channel, now it is current_observation

public class Channel 
{    

    private Wind wind;
    private Astronomy astro;
    private Atmosphere atmos;
    private Condition condition;

    public Atmosphere getAtmos()
    {
        return atmos;
    }

    public Condition getCondition()
    {
        return condition;
    }
    
    public Wind getWind()
    {
        return wind;
    }
    
    public Astronomy getAstro()
    {
        return astro;
    }

    
    /**
     * Populate the channel namespace
     * @param data JSONObject
     * @throws JSONException 
     */
    public void populate(JSONObject data) throws JSONException
    {
        astro = new Astronomy();
        astro.populate(data.optJSONObject("astronomy"));

        atmos = new Atmosphere();
        atmos.populate(data.optJSONObject("atmosphere"));

        condition = new Condition();
        condition.populate(data.optJSONObject("condition"));

        wind = new Wind();
        wind.populate(data.optJSONObject("wind"));
    }
    
}
