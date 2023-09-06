package assignment.weather;

import java.util.*;

public class JsonParser {
private static JsonParser instance;
    private JsonParser()
    {

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
            int indexOfOpenParenthesis = jsonString.indexOf("{");
            int indexOfCloseParenthesis = jsonString.indexOf("}");
            if(indexOfCloseParenthesis != -1 && indexOfCloseParenthesis != -1)
            {
                jsonString = jsonString.substring(indexOfOpenParenthesis + 1,indexOfCloseParenthesis);
                String[] elements = jsonString.split(",");
                if(elements.length != 17)
                {
                    weatherInformation = null;
                }
                else
                {
                    HashMap<String,String> mapperToken = new HashMap<>();
                    for(int i =0; i < elements.length;i++)
                    {
                        String[] element = elements[i].split(":");
                        element[0] = element[0].replaceAll("\"","");
                        if(element[1].contains("\""))
                        {
                            element[1] = element[1].replaceAll("\"","");
                        }
                        mapperToken.put(element[0].trim(),element[1].trim());
                    }
                    weatherInformation.id = mapperToken.get("id");
                    weatherInformation.name = mapperToken.get("name");
                    weatherInformation.state = mapperToken.get("state");
                    weatherInformation.timeZone = mapperToken.get("timeZone");
                    weatherInformation.lat = Double.parseDouble(mapperToken.get("lat").trim());
                    weatherInformation.lon = Double.parseDouble(mapperToken.get("lon"));
                    weatherInformation.local_date_time = mapperToken.get("local_date_time");
                    weatherInformation.local_date_time_full = mapperToken.get("local_date_time_full");
                    weatherInformation.air_temp = Double.parseDouble(mapperToken.get("air_temp"));
                    weatherInformation.apparent_t = Double.parseDouble(mapperToken.get("apparent_t"));
                    weatherInformation.cloud = mapperToken.get("cloud");
                    weatherInformation.dewpt = Double.parseDouble(mapperToken.get("dewpt"));
                    weatherInformation.press = Double.parseDouble(mapperToken.get("press"));
                    weatherInformation.rel_hum = Double.parseDouble(mapperToken.get("rel_hum"));
                    weatherInformation.wind_dir = mapperToken.get("wind_dir");
                    weatherInformation.wind_spd_kmh = Double.parseDouble(mapperToken.get("wind_spd_kmh"));
                    weatherInformation.wind_spd_kt = Double.parseDouble(mapperToken.get("wind_spd_kt"));
                }
            }
            else{
                weatherInformation = null;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            weatherInformation = null;
        }
        return weatherInformation;
    }
    public String writeJson(LinkedHashMap<String,String> weatherData)
    {
        String jsonString = "{";
        for(Map.Entry<String,String> elements:weatherData.entrySet())
        {
            String element = elements.getKey();
            element = "\"" + element + "\"" + " " + ":" + " ";
            if(!element.contains("wind_spd_kt"))
            {
                try {
                    double value = Double.parseDouble(elements.getValue());
                    element += (value + ",");
                } catch (Exception e) {
                    element += ("\"" + elements.getValue() + "\"" + ",");
                }

            }
            else
            {
                double value = Double.parseDouble(elements.getValue());
                element += (value);
            }
            jsonString+=element;
        }
        jsonString+="}";
        return jsonString;
    }
}
