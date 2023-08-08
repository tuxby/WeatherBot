package by.tux.weatherbot.Service;

import lombok.Data;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.net.URL;
import org.json.JSONObject;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

@Configuration
@Data
@PropertySource("config.properties")
public class Openweathermap {

    public static String appid="d51a78d090fa7653e461eac94138aca2";
    public static String apiUrl="api.openweathermap.org/data/2.5/weather";

    public static String getWeatherByCity(String city) {
        try {
            URL url = UriComponentsBuilder.newInstance()
                    .scheme("https").host(apiUrl)
                    .queryParam("appid", appid)
                    .queryParam("lang", "ru")
                    .queryParam("q", city)
                    .build().toUri().toURL();
            return createMessage(url);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getWeatherByLocation(String lat,String lon) {
        try{
            URL url = UriComponentsBuilder.newInstance()
                    .scheme("https").host(apiUrl)
                    .queryParam("appid", appid)
                    .queryParam("lang", "ru")
                    .queryParam("lat", lat)
                    .queryParam("lon", lon)
                    .build().toUri().toURL();
            return createMessage(url);
        } catch (Exception e) {
            return null;
        }
    }
    public static String createMessage(URL url) {
        try{
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
            String data = bufferedReader.readLine();
            JSONObject jsonObject = new JSONObject(data);
            String city = parserGetCity(jsonObject);
            String country = parserGetCountry(jsonObject);
            String temp = parserGetTemp(jsonObject);
            String disc = parserGetDisc(jsonObject);
            if (city != null && temp !=null && country!=null && disc!=null)
                return city + " " +
                        "("+ country+ ") \n<b>" +
                        temp  + " °C</b>\n" +
                        "<i>" + disc + "</i>";
            else
                return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static String parserGetCity(JSONObject serverJson) {
        try{
            String city =serverJson.get("name").toString(); //get city
            return city;
        } catch (JSONException e) {
            return null;
        }
    }

    public static String parserGetTemp(JSONObject serverJson) {
        try{
            String main = serverJson.get("main").toString(); //get main block
            serverJson = new JSONObject(main);
            String temp = serverJson.get("temp").toString();
            return (new DecimalFormat("##.#").format(Double.valueOf(temp) - 273.00));
        } catch (JSONException e) {
            return null;
        }
    }
    public static String parserGetDisc(JSONObject serverJson) {
        try{
            String weather = serverJson.get("weather").toString(); //get weather block
            //weather = weather.replace("[", "").replace("]", "");
            JSONArray description = new JSONArray(weather);
            String desc=null;
            for(int i = 0; i <description.length() ; i++ ){
                if (desc!=null)
                    desc = desc + ",";
                desc =((JSONObject) description.get(i)).get("description").toString();
            }
            return desc;
        } catch (JSONException e) {
            return null;
        }
    }

    public static String parserGetCountry(JSONObject serverJson) {
        try{
            String sys = serverJson.get("sys").toString(); //get sys block
            serverJson = new JSONObject(sys);
            String country = serverJson.get("country").toString();
            return country;
        } catch (JSONException e) {
            return null;
        }
    }
}
