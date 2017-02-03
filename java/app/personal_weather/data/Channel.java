package app.personal_weather.data;

import org.json.JSONException;
import org.json.JSONObject;

//For use with Yahoo api

public class Channel 
{    
    private Units units;
    private Item item;
    private Location feed_loc;
    private Wind wind;
    private Astronomy astro;
    
        
    //description from the channel
    private String desc;                 

    public Units getUnits()
    {
        return units;
    }

    public Item getItem()
    {
        return item;
    }    
    
    public Location get_feed_location()
    {
        return feed_loc;
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
     * The description of the channel namespace
     * @return  A string
     */
    public String getDesc()
    {
        return desc;
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
        
        units = new Units();
        units.populate(data.optJSONObject("units"));
        
        item = new Item();
        item.populate(data.optJSONObject("item"));
        
        feed_loc = new Location();
        feed_loc.populate(data.optJSONObject("location"));
        
        wind = new Wind();
        wind.populate(data.optJSONObject("wind"));
        
        desc = data.optString("description");
        
    }
    
}
