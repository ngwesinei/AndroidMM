package com.nyp.sit.elearning.weatherapplication

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.nyp.sit.elearning.weatherapplication.utilities.OpenWeatherJsonUtils
import com.nyp.sit.elearning.weatherapplication.utilities.NetworkUtils
import android.os.AsyncTask
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_forecast.*
import com.nyp.sit.elearning.weatherapplication.MainActivity.FetchWeatherTask
import com.nyp.sit.elearning.weatherapplication.entity.WeatherEntry
import com.nyp.sit.elearning.weatherapplication.utilities.OpenWeatherDateUtils
import com.nyp.sit.elearning.weatherapplication.utilities.OpenWeatherUtils
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast)
        loadWeatherData()


    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.main,menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {


        if(item?.itemId == R.id.refreshMenuItem)
        {
            loadWeatherData()

        }
        else if(item?.itemId == R.id.quitMenuItem)
        {

            finish()

        }

        return super.onOptionsItemSelected(item)
    }

    private fun loadWeatherData() {
        val location = "Singapore"
         FetchWeatherTask().execute(location)
        //TODO 7: Execute the AsyncTask using location as argument
    }

    //TODO 3: Update FetchWeatherTask to extend Async task with Params as String type , Progress as Void Type and Result as List<WeatherEntry> type
    //TODO 4: Override doInBackground and onPostExecute methods


    inner class FetchWeatherTask : AsyncTask<String, Void, List<WeatherEntry>>(){

        //TODO 5: Update doInBackground that
        //  - makes use of params value to retrieve
        //  - Build URL
        //  - Makes use of getResponseFromHttpUrl method from NetworkUtils to get the jsonWeatherResponse

        override fun onPostExecute(weatherData: List<WeatherEntry>?) {
            if (weatherData != null) {
                for (weatherEntry in weatherData) {
                    val description =  OpenWeatherUtils.getStringForWeatherCondition(this@MainActivity, weatherEntry.weatherID)
                    val highString = OpenWeatherUtils.formatTemperature(this@MainActivity, weatherEntry.tempMax)
                    val lowString = OpenWeatherUtils.formatTemperature(this@MainActivity, weatherEntry.tempMin)
                    val humidityString = getString(R.string.format_humidity, weatherEntry.humidity)
                    val windString = OpenWeatherUtils.getFormattedWind(this@MainActivity, weatherEntry.windspeed, weatherEntry.degrees)
                    val pressureString = getString(R.string.format_pressure, weatherEntry.pressure)

                    weather_descriptionTV.text = description
                    weatherDateTV.text = weatherEntry.date
                    weather_tempHighTV.text = highString
                    weather_tempLowTV.text = lowString
                    weatherIV.setImageResource(OpenWeatherUtils.getArtResourceForWeatherCondition(weatherEntry.weatherID))
                    weather_humidityTV.text = humidityString
                    weather_pressureTV.text = pressureString
                    weather_windSpeedTV.text = windString
                }
            }
        }

        override fun doInBackground(vararg params: String?): List<WeatherEntry>? {
           if (params.size  == 0) return null
            val location = params[0]
            val weatherRequestUrl = NetworkUtils.buildUrl(location!!)

            try {
                val jsonWeatherResponse = NetworkUtils.getResponseFromHttpUrl(weatherRequestUrl)

                return OpenWeatherJsonUtils.getSimpleWeatherStringsFromJson(this@MainActivity, jsonWeatherResponse!!)
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }

        }

    }

}
