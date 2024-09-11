package by.tux.weatherbot.Service;

import by.tux.weatherbot.utils.ApiClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URL;
import java.text.DecimalFormat;

@Slf4j
@Data
@Service
@PropertySource("config.properties")
public class Openweathermap {
    @Value("${openweathermap.appid}")
    private String appid;

    private final String apiUrl="api.openweathermap.org/data/2.5/weather";

    public String getWeatherByCity(String city) {
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

    public String getWeatherByLocation(String lat,String lon) {
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
    public String createMessage(URL url) {
        try{
            log.info("Запрос данных с сайта: " + url);
            String data=ApiClient.Get(String.valueOf(url));
            JSONObject jsonObject = new JSONObject(data);
            String city = parserGetCity(jsonObject);
            String country = parserGetCountry(jsonObject);
            String temp = parserGetTemp(jsonObject);
            String disc = parserGetDisc(jsonObject);
            if (city != null && temp !=null && country!=null && disc!=null){
                log.info("Ответ получен");
                return city + " " +
                        "("+ country+ ") \n<b>" +
                        temp  + " °C</b>\n" +
                        "<i>" + disc + "</i>";
            }
            else{
                log.info("Ответ получен, но некоторые данные не определены");
                return null;
            }

        } catch (Exception e) {
            log.info("Ошибка: " + e.toString());
            return null;
        }
    }

    public String parserGetCity(JSONObject serverJson) {
        try{
            String city =serverJson.get("name").toString(); //get city
            return city;
        } catch (JSONException e) {
            return null;
        }
    }

    public String parserGetTemp(JSONObject serverJson) {
        try{
            String main = serverJson.get("main").toString(); //get main block
            serverJson = new JSONObject(main);
            String temp = serverJson.get("temp").toString();
            return (new DecimalFormat("##.#").format(Double.valueOf(temp) - 273.00));
        } catch (JSONException e) {
            return null;
        }
    }
    public String parserGetDisc(JSONObject serverJson) {
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

    public String parserGetCountry(JSONObject serverJson) {
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
