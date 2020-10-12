package app.personal_weather;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import app.personal_weather.data.Astronomy;
import app.personal_weather.data.Channel;
import app.personal_weather.data.Condition;
import app.personal_weather.data.Forecast;
import app.personal_weather.data.Wind;


/**
 * Turns out LocationListener is an interface, this means it cannot be
 * instantiated, only implemented. The activity has to implement it.
 * Undocumented and registered as two seperate bugs on google.
 * Probably why they deprecated it.
 *
 * @author Christopher D. Harte
 *
 */
public class Weather_Activity extends Activity implements ConnectionCallbacks,
                                                          OnConnectionFailedListener
{
    private ImageView weather_icon;
    private TextView temperature;
    private TextView conditions;
    private TextView location_text_view;
    private TextView chill_factor;
    private TextView tomorrow;
    private TextView astro;
    private RelativeLayout rel_back;
    private final int REQ_CODE = 111;

    //Heap when I have to, otherwise stack.
    private ProgressDialog dialog;
    private List<Address> address_info;

    private GoogleApiClient api_client;

    private double latitude;
    private double longitude;

    @Override protected void onCreate(Bundle savedInstanceState)
    {
        ImageView logo_link;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        rel_back            = findViewById(R.id.rel_back);
        weather_icon        = findViewById(R.id.weather_icon);
        temperature         = findViewById(R.id.temperature_tv);
        conditions          = findViewById(R.id.conditions_tv);
        location_text_view  = findViewById(R.id.location_tv);
        chill_factor        = findViewById(R.id.chill_factor);
        tomorrow            = findViewById(R.id.forecast);
        astro               = findViewById(R.id.sun_up_down);
        logo_link           = findViewById(R.id.logoView1);


        // Because I am using their api, I have to include this, 
        //even though everything about yahoo is liable to being hacked.
        logo_link.setImageResource(R.drawable.yahoo_white);
        logo_link.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View v)
            {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_VIEW);
                i.addCategory(Intent.CATEGORY_BROWSABLE);
                i.setData(Uri.parse("https://weather.yahoo.com/"));
                startActivity(i);
            }
        });

        //Here be your permissions request, everybody else gets suppressed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            int cluckup1 = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);

            if (cluckup1 != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_NETWORK_STATE}, REQ_CODE);
            }
        }

        // This might take some time
        dialog = new ProgressDialog(this);
        dialog.setMessage("Give me a few secs...");
        dialog.show();

        //The evil ones have deprecated the location listener, now we have to use
        //their invidious api so they can follow everybody who uses this app.
        api_client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        FusedLocationProviderClient fused_client = LocationServices.getFusedLocationProviderClient(this);
        fused_client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>()
        {
            @Override public void onSuccess(Location location)
            {
                if (location != null)
                {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            }
        });
    }

    @Override protected void onStart()
    {
        super.onStart();
        api_client.connect();
    }

    @Override protected void onStop()
    {
        super.onStop();
        api_client.disconnect();
    }

    /**
     * I currently have two interfaces, one for yahoo, which works when it feels like
     * and one for Wunderweather, which is not working. Yahoo is keeping its weather api on line so the
     * other one is just incase they change their mind, again. Apr 2017
     *
     * @param con_hint Bundle
     */

    @Override public void onConnected(Bundle con_hint)
    {
        Yahoo_feed yahoo_weather = new Yahoo_feed(this);
        Geocoder geo = new Geocoder(this, Locale.getDefault());

        //The location settings were not working on the emulator, so do this.
        //Ho Chi Minh city just because it is unusual
//        double latitude = 10.777416;
//        double longitude = 106.639366;
        //new york for the sceptic tanks
//        double latitude = 40.7127;
//        double longitude = -74.0059;
        //New zealand because I am cyclone chasing
//        double latitude = -38.140693;
//        double longitude = 176.253784;
        //Local
//        double latitude = 53.5333;
//        double longitude = -2.2833;

        address_info = null;

        try
        {
            address_info = geo.getFromLocation(latitude, longitude, 1);
        }
        catch (IOException e)
        {
            dialog.dismiss();
            e.printStackTrace();
        }

        if (address_info.isEmpty())
        {
            dialog.dismiss();
            Toast.makeText(this, "Unable to acquire the GPS signal", Toast.LENGTH_LONG)
                 .show();
            finish();
        }
        else
        {
            // for yahow This has the format "Manchester, GB" as a single string, or lat long
            // In fereign places the city can be stored in AdminArea, but that fault lies with yahoo.
//            String city = address_info.get(0).getLocality();
//            String country = address_info.get(0).getCountryCode();
//            String yahoo_location = "location=" + city + "," + country;
            String yahoo_location = "lat=" + latitude + "&lon=" + longitude; //Should I want to do it by lat/lon

            yahoo_weather.refresh(yahoo_location);
        }
    }

    /**
     * When you request permissions you get a call back code, this will make sure everything is allowed without crashing.
     *
     * @param req_code int
     * @param perms String[]
     * @param grants int[]
     */
    @Override public void onRequestPermissionsResult(int req_code, String[] perms, int[] grants)
    {
        switch (req_code)
        {
            case REQ_CODE:
                if(grants[0] != PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(this, "Location access denied", Toast.LENGTH_LONG).show();
                }
            break;
            default:
                super.onRequestPermissionsResult(req_code, perms, grants);
        }
    }

    @Override public void onConnectionFailed(ConnectionResult provider)
    {
        Toast.makeText(getBaseContext(),
                "you are not GPSing at the moment " + provider,
                Toast.LENGTH_LONG)
                .show();
    }

    @Override public void onConnectionSuspended(int arg0)
    {
        api_client.connect();
    }


    /**
     * If the return from the weather feed was successful load up the views.
     * Including the forecast from an array which is a bit long winded.
     * Until I put it in a data class of its own.
     *
     * @param channel Channel object
     * @param forc Forcast object
     */
    public void feed_success(Channel channel, Forecast forc)
    {
        dialog.dismiss();

        Resources res = getResources();
        Wind wind = channel.getWind();
        Astronomy astron = channel.getAstro();
        Condition cond = channel.getCondition();

        String sunrise = "sunrise";
        String sunset = "sunset";

        if (astron != null)
        {
            sunrise = astron.getSunrise();
            sunset = astron.getSunset();
        }

        String chill = "chill";
        String speed = "speed";

        if (wind != null)
        {
            chill = wind.getChill();
            speed = wind.getSpeed();
        }

        int cond_code = 0;
        int temp = 0;
//        String desc = "tornados!";

        if (cond != null)
        {
            cond_code = cond.getCode();
            temp = cond.getTemp();
//            desc = cond.getDesc();
        }

        String forecast_date = forc.getDate();
        String forecast_day = forc.getDay();
        String forecast_forc;
        int forecast_code = forc.getCode();
        JSONArray fore_obj = forc.getCode_obj();

        // Not everything of use comes from the yahoo api...
        String city = address_info.get(0).getLocality();
        String town = address_info.get(0).getSubLocality();
        String street = address_info.get(0).getThoroughfare();
        String post_code = address_info.get(0).getPostalCode();

        String place = street;

        //We do not always get a street or a town, but we should have a city.
        if (street == null)
        {
            place = town;

            if (town == null)
            {
                place = city;
            }

            //if all else fails and city is null
            if (city == null)
            {
                place = post_code;
            }
        }

        /* Leave all this in for reference
        //Set the backgound.        
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        
        int device_width = metrics.widthPixels;  
        int device_height = metrics.heightPixels;
        
        int p_image = R.drawable.night_time_p;
        int t_image = R.drawable.night_time;
        int int_image;                                
        
        //This will fool those bastards at google, just a case of picking the right number.
        //The right number is 720, which is constant regarless of orientation
        //optimised for a 1280 x 720 display ie. my phone, which is good
        if (device_width == 720
            || device_height == 720 )
        {
            int_image = p_image;            
        }
        else
        {            
            int_image = t_image;
        }

        Drawable draw_back_image = res.getDrawable(int_image, getTheme());
         */

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int device_width = metrics.widthPixels;
        int device_height = metrics.heightPixels;

        //Relative on portrait, inside a scroll on landscape but still rel_back (Very clever)
        Drawable draw_back_image = res.getDrawable(R.drawable.night_time, getTheme());
        rel_back.setBackground(draw_back_image);

        //this sets the drawable icon in the format icon_1.png
        int icon_res = res.getIdentifier("drawable/icon_" + cond_code, null, getPackageName());
        Drawable icon = res.getDrawable(icon_res, getTheme());

        String[] for_locals = res.getStringArray(R.array.local_conditions);
        String display_for_locals = for_locals[cond_code];
        forecast_forc = for_locals[forecast_code];

        weather_icon.setImageDrawable(icon);
        temperature.setText(temp + "\u00B0");
        chill_factor.setText("(but feels like " + chill + "\u00B0 " + " in a " + speed + "kph wind)");
        conditions.setText(display_for_locals);
        location_text_view.setText("Which is not bad for " + place + "\n");
        tomorrow.setText("Tomorrow, " + forecast_day + " " + forecast_date + " will be\n" + forecast_forc);
        astro.setText("Sunrise is at " + sunrise + "\nand sunset is " + sunset);

        //Get the strings then concatenate them into an arraylist
        ArrayList<String> fore_array = new ArrayList<>();

        for (int i = 2; i < fore_obj.length(); i++)
        {
            try
            {
                //This is a lot easier than a custon array adapter
                JSONObject o = (JSONObject) fore_obj.get(i);

                long d = Long.parseLong(o.getString("date"));
                SimpleDateFormat df = new SimpleDateFormat("d MMM y", Locale.ENGLISH);

                String oday = o.getString("day");
                String odate = df.format(d * 1000);
                int ocode = o.getInt("code");
                String otext = for_locals[ocode];

                fore_array.add(oday + " " + odate + " " + otext);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

         /* Depending on orientation we need to use different layouts.
         * Landscape is a textView that goes inside the scrollView layout.
         * Portrait is a listView that goes inside the relativeView layout.
         * (Because you cannot have one scroll item inside another).
         */

        if (device_width > device_height)
        {
            //Landscape, use string builder with line breaks
            StringBuilder non_scroll_view = new StringBuilder();

            //Here is your php: foreach ($fore_array as $forcs)
            for (String forcs : fore_array)
            {
                non_scroll_view.append(forcs + "\n");
            }

            TextView text_forcs = findViewById(R.id.text_forcs);
            text_forcs.setText(non_scroll_view);
        }
        else //Portrait, use my listView
        {
            ArrayAdapter<String> fore_adapter = new ArrayAdapter<>(this,
                    R.layout.list_text,
                    fore_array);

            ListView fore_hi_low = findViewById(R.id.fore_hi_low);
            fore_hi_low.setAdapter(fore_adapter);
        }
    }


    /**
     * If there is a problem with the feed and an incoming message toast them.
     * No need to override the Exception.message at all, that is just adding an
     * extra level of inheritance.
     *
     * @param e Exception
     */
    public void feed_failure(Exception e)
    {
        dialog.dismiss();

        Toast.makeText(this, "Ooops... " + e.getMessage(), Toast.LENGTH_LONG)
                .show();
    }

    @Override public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        // ActionBar is now default in the holo theme, no need for a new instance
        getMenuInflater().inflate(R.menu.weather, menu);

        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //I have removed settings from the menu

        switch (item.getItemId())
        {
            case R.id.about_app:
                Intent a = new Intent("app.personal_weather.ABOUT");
                a.putExtra("title", "Your weather (from yahoo)");
                a.putExtra("body", "The weather where you are, with some graphics from http://vclouds.deviantart.com, "
                        + "the signpost OAuth library and responses from the Yahoo weather api."
                        + "\nThis is just a test piece that takes your location from the gps and uses it to "
                        + "query the weather api. Rotating your device will cause it to reload.\n");
                startActivity(a);
                break;

            //No home icon as per the new evil rules from googling
            case android.R.id.home:
                finish();
                break;

        }
        return false;
    }
}







