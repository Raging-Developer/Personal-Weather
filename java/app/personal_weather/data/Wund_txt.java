package app.personal_weather.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Christopher D. Harte on 12/09/2016.
 */
public class Wund_txt
{
    private String txt_metric;

    public String getTxt_metric()
    {
        return txt_metric;
    }

    public void populate (JSONObject data) throws JSONException
    {
        txt_metric = data.getJSONArray("forecastday")
                .getJSONObject(0)
                .optString("fcttext");


    }
}
