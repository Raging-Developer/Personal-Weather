package app.personal_weather.data;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Locale;

//For use with yahoo api

public class Forecast
{
    private int code;
    private String date;
    private String day;
    private int high;
    private int low;
    private String desc;
    
    private JSONArray code_obj;
    
    public JSONArray getCode_obj()
    {
        return code_obj;
    }
    
    public int getCode()
    {
        return code;
    }
    public String getDate()
    {
        Long d = Long.parseLong(date);
        SimpleDateFormat df = new SimpleDateFormat("d MMM y", Locale.ENGLISH);
        String ds = df.format(d * 1000);
        return ds;
    }
    public String getDay()
    {
        return day;
    }
    public int getHigh()
    {
        return high;
    }
    public int getLow()
    {
        return low;
    }
    public String getDesc()
    {
        return desc;
    }
    
    /**
     * About time I got all the days, not just tomorrow.
     * @param data
     * @throws JSONException
     */
    public void populate (JSONArray data) throws JSONException
    {
        //This is all the days
        code_obj = data;

        //This is just day 1, ie tomorrow.
        code = data.getJSONObject(1).optInt("code");       
        date = data.getJSONObject(1).optString("date");
        day = data.getJSONObject(1).optString("day");
        high = data.getJSONObject(1).optInt("high");
        low = data.getJSONObject(1).optInt("low");
        desc = data.getJSONObject(1).optString("text");               
        
    }
    
}
