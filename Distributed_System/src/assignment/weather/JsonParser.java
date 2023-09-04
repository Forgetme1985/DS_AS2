package assignment.weather;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class JsonParser {
private static JsonParser instance;
private JSONParser jsonParser;
    private JsonParser()
    {
        jsonParser = new JSONParser();
    }
    public static synchronized JsonParser getInstance()
    {
        if(instance ==null)
            instance =new JsonParser();
        return instance;
    }
    public WeatherInformation readJson(String jsonString)
    {
        WeatherInformation weatherInformation = new WeatherInformation();
        try {
            JSONObject jsonObj = (JSONObject) jsonParser.parse(jsonString);
            weatherInformation.id = (String)jsonObj.get("id");
            weatherInformation.name = (String)jsonObj.get("name");
            weatherInformation.state = (String)jsonObj.get("state");
            weatherInformation.timeZone = (String)jsonObj.get("timeZone");
            weatherInformation.lat = (String) jsonObj.get("lat");
            weatherInformation.lon = (String)jsonObj.get("lon");
            weatherInformation.local_date_time = (String)jsonObj.get("local_date_time");
            weatherInformation.local_date_time_full = (String)jsonObj.get("local_date_time_full");
            weatherInformation.air_temp = (String) jsonObj.get("air_temp");
            weatherInformation.apparent_t = (String) jsonObj.get("apparent_t");
            weatherInformation.cloud = (String)jsonObj.get("cloud");
            weatherInformation.dewpt = (String) jsonObj.get("dewpt");
            weatherInformation.press = (String) jsonObj.get("press");
            weatherInformation.rel_hum = (String) jsonObj.get("rel_hum");
            weatherInformation.wind_dir = (String)jsonObj.get("wind_dir");
            weatherInformation.wind_spd_kmh = (String) jsonObj.get("wind_spd_kmh");
            weatherInformation.wind_spd_kt = (String) jsonObj.get("wind_spd_kt");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            weatherInformation = null;
        }
        return weatherInformation;
    }
    public String writeJson(List<String> weatherData)
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id",weatherData.get(0));
        jsonObject.put("name",weatherData.get(1));
        jsonObject.put("state",weatherData.get(2));
        jsonObject.put("timeZone",weatherData.get(3));
        jsonObject.put("lat",weatherData.get(4));
        jsonObject.put("lon",weatherData.get(5));
        jsonObject.put("local_date_time",weatherData.get(6));
        jsonObject.put("local_date_time_full",weatherData.get(7));
        jsonObject.put("air_temp",weatherData.get(8));
        jsonObject.put("apparent_t",weatherData.get(9));
        jsonObject.put("cloud",weatherData.get(10));
        jsonObject.put("dewpt",weatherData.get(11));
        jsonObject.put("press",weatherData.get(12));
        jsonObject.put("rel_hum",weatherData.get(13));
        jsonObject.put("wind_dir",weatherData.get(14));
        jsonObject.put("wind_spd_kmh",weatherData.get(15));
        jsonObject.put("wind_spd_kt",weatherData.get(16));
        return jsonObject.toJSONString();
    }
}
