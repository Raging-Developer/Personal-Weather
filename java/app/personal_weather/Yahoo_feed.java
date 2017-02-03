package app.personal_weather;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.AsyncTask;
import app.personal_weather.data.Channel;

/**
 * The class for getting the weather from the yahoo api
 * which has now shuttered the public call, so they can go fuck.
 * @author Christopher D. Harte
 *
 */
public class Yahoo_feed 
{   
    private Exception error;  
    private Weather_Activity weather;  
    
    /**
     * Query the yahoo api and return a JSON of the endpoint. 
     * @param weather The existing instance of the Weather_Activity
     */
    public Yahoo_feed(Weather_Activity weather)
    {
        super();
        this.weather = weather;
    }
    


    /**
     * Queries the weather server and loads up the JSON object from an async task
     * @param new_location String
     */
    public void refresh (String new_location) 
    {        
        new AsyncTask<String, Void, String>()        
        {
            @Override protected String doInBackground(String... params)            
            {                                
                //The query and endpoint are taken from the yahoo developer site.
                String query = String.format("select * from weather.forecast where woeid in "
                                             +"(select woeid from geo.places(1) where text=\"%s\") and u='c'", params[0]);

                
                String endpoint = String.format("https://query.yahooapis.com/v1/public/yql?q=%s&format=json",
                                                 Uri.encode(query));                  
                                                                
                try
                {
                    URL           url   = new URL(endpoint);
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
                    weather.feed_failure(error);
                    return;
                }                                
                
                try
                {                                    
                    JSONObject data = new JSONObject (result);
                    
                    JSONObject q_result = data.optJSONObject("query");                                        
                    
                    Channel chan = new Channel();
                    chan.populate(q_result.optJSONObject("results").optJSONObject("channel"));                    
 
                    weather.feed_success(chan);
                    
                }
                catch (JSONException e)
                {
                    weather.feed_failure(e);
                } 
            }
            
        }.execute(new_location); 
    }            
    
    
}







