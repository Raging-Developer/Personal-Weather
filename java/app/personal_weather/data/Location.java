package app.personal_weather.data;

import org.json.JSONObject;


public class Location 
{
    private String city;
    private String country;
    
    

    public String getCity()
    {
        return city;
    }



    public String getCountry()
    {
        return country;
    }


    /**
     * Populate the channel->location namespace
     * @param data JSONObject
     */
    public void populate(JSONObject data)
    {
        city = data.optString("city");
        country = data.optString("country");
        
    }

}
