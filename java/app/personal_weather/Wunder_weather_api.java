package app.personal_weather;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import app.personal_weather.data.Wunder_data;

public class Wunder_weather_api
{
    
    private Exception error;  
    private Weather_Activity weather_activity;  
    private String API_KEY = "";
    
    /**
     * Query the Under weather api and return a JSON of the endpoint.
     * @param weather The existing instance of the Weather_Activity
     */
    public Wunder_weather_api(Weather_Activity weather)
    {
        super();
        this.weather_activity = weather;
    }
    
    /**
     * Queries the weather server and loads up the JSON object from an async task
     * @params country String, city String
     */
    public void refresh (String country, String city)
    {        
        new AsyncTask<String, Void, String>()        
        {
            @Override protected String doInBackground(String... params)            
            {
                String query = String.format("http://api.wunderground.com/api/"
                                              + API_KEY
                                              + "/forecast/q/"
                                              + "%s"    //country [0]
                                              + "/"
                                              + "%s"   //city [1]
                                              + ".json", params[0], params[1]);
                                
                try
                {
                    URL           url   = new URL(query);
                    URLConnection conn  = url.openConnection();
                    InputStream   input = conn.getInputStream();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder  result = new StringBuilder();
                    String line;
                    
                    while ((line = reader.readLine()) != null)
                    {
                        result.append(line);                        
                    }                                                                  
                    
                    return result.toString();

                }
                catch (Exception e)
                {
                    error = e;
                    
                }
                
                return null;
            }

            @Override protected void onPostExecute(String result)            
            {         
                                
                if (result == null && error != null)
                {
                    weather_activity.feed_failure(error);
                    return;
                }                                
                
                try
                {                                    
                    JSONObject data         = new JSONObject (result);
                    Wunder_data wunder_data = new Wunder_data();
                    wunder_data.populate(data.optJSONObject("forecast"));

                    weather_activity.wund_success(wunder_data);
                    
                }
                catch (JSONException e)
                {
                    weather_activity.feed_failure(e);
                } 
            }
            
        }.execute(country, city);
    }            
    


}
