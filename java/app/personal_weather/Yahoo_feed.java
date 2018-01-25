package app.personal_weather;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.AsyncTask;
import app.personal_weather.data.Channel;

/**
 * The class for getting the weather from the yahoo api
 * which has now shuttered the public call, or not, since it is open again.
 * @author Christopher D. Harte
 *
 */
class Yahoo_feed
{   
    private static Exception error;
    private Weather_Activity this_weather;
    
    /**
     * Query the yahoo api and return a JSON of the endpoint. 
     * @param weather The existing instance of the Weather_Activity
     */
    Yahoo_feed(Weather_Activity weather)
    {
        super();
        this_weather = weather;
    }

    /**
     * Queries the weather server and loads up the JSON object from an async task
     * that now use static classes and WeakReferences to prevent memory leaks.
     * @param new_location String
     */
    void refresh (String new_location)
    {
        My_async task = new My_async(this_weather);
        task.execute(new_location);
    }
    private static class My_async extends AsyncTask<String, Void, String>
    {
        private WeakReference<Weather_Activity> weak_ref;

        My_async(Weather_Activity weather)
        {
            weak_ref = new WeakReference<>(weather);
        }

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
            final Weather_Activity weak_weather = weak_ref.get();

            if (result == null && error != null)
            {
                weak_weather.feed_failure(error);
                return;
            }

            try
            {
                JSONObject data = new JSONObject (result);

                JSONObject q_result = data.optJSONObject("query");

                Channel chan = new Channel();
                chan.populate(q_result.optJSONObject("results").optJSONObject("channel"));

                weak_weather.feed_success(chan);
            }
            catch (JSONException e)
            {
                weak_weather.feed_failure(e);
            }
        }
    }
}





