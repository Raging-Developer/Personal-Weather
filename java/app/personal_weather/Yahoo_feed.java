package app.personal_weather;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import app.personal_weather.data.Channel;
import app.personal_weather.data.Forecast;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;



/**
 * The class for getting the weather from the yahoo api
 *
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
        private final String client_id = "dj0yJmk9dlJkNDZ2UzlzRWRZJnM9Y29uc3VtZXJzZWNyZXQmc3Y9MCZ4PWU3";
        private final String client_secret = "f78183fa2a4fd48f55c1820c230af87e901a3540";
        private final String yahoo_url = "https://weather-ydn-yql.media.yahoo.com/forecastrss";

        private WeakReference<Weather_Activity> weak_ref;

        My_async(Weather_Activity weather)
        {
            weak_ref = new WeakReference<>(weather);
        }

        @Override protected String doInBackground(String... params)
        {
            //Imported an old signpost OAuth library.
            OAuthConsumer consumer = new DefaultOAuthConsumer(client_id, client_secret);

            try
            {
                URL               url   = new URL(yahoo_url + "?" + params[0] + "&format=json&u=c");
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                consumer.sign(conn);
                conn.connect();

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

                Channel chan = new Channel();
                chan.populate(data.optJSONObject("current_observation"));

                Forecast forc = new Forecast();
                forc.populate(data.getJSONArray("forecasts"));

                weak_weather.feed_success(chan, forc);
            }
            catch (JSONException e)
            {
                weak_weather.feed_failure(e);
            }
        }
    }
}





