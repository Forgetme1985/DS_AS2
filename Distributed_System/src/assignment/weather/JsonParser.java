package assignment.weather;

import java.util.*;
import java.util.Scanner;
import java.io.*;
public class JsonParser {
private static JsonParser instance;
private List<String> lstWeatherInformation = new ArrayList<>();

    /**
     *
     * Singleton pattern for JsonParser
     */

    private JsonParser()
    {

    }
    public static synchronized JsonParser getInstance()
    {
        if(instance ==null)
            instance =new JsonParser();
        return instance;
    }

    /**
     *
     * @param jsonString: this string is used to parse to a weather information object
     * @return a weather information instance
     * This result will be stored in the list of weather information in the aggregation server
     */
    public WeatherInformation readJson(String jsonString)
    {;
        WeatherInformation weatherInformation = new WeatherInformation();
        try {
            int indexOfOpenParenthesis = jsonString.indexOf("{");
            int indexOfTheFirstComma = jsonString.indexOf(",");
            String lamportClock = jsonString.substring(indexOfOpenParenthesis + 1,indexOfTheFirstComma);
            String[] lamportClockElement = lamportClock.split(":");
            weatherInformation.clockCounter = Integer.parseInt(lamportClockElement[1].trim());
            int indexOfTheSecondOpenParenThesis = jsonString.lastIndexOf("{");
            int indexOfCloseParenthesis = jsonString.indexOf("}");
            if(indexOfTheSecondOpenParenThesis != -1 && indexOfCloseParenthesis != -1)
            {
                jsonString = jsonString.substring(indexOfTheSecondOpenParenThesis + 1,indexOfCloseParenthesis);
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
                    weatherInformation.time_zone = mapperToken.get("time_zone");
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

    /**
     *
     * @param clock: this clock is included into http body message,
     * it is used for comparison with the aggregation server's clock
     * in order to update the clock order following Lamport algorithm
     * @param weatherData: is used to parse json
     * @return jsonString: it is embedded in http body message to send to the
     * aggregation server.
     */
    public String writeJson(LamportClock clock,LinkedHashMap<String,String> weatherData)
    {
        String jsonString = "{";
        jsonString += ("\"lamportClock\" : " + clock.counter + ",");
        jsonString += "\"WeatherInformation\" : {";
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
        jsonString+="}}";
        return jsonString;
    }
    /**
     *
     * @param clock: this clock is included into http body message,
     * it is used for comparison with the aggregation server's clock
     * in order to update the clock order following Lamport algorithm
     * @param weatherInformation: is used to parse json
     * @return jsonString: it is embedded in http body message to send to the
     * aggregation server.
     */
    public String writeJson(LamportClock clock,WeatherInformation weatherInformation)
    {
        LinkedHashMap<String,String> weatherData = new LinkedHashMap<>();
        weatherData.put("id",weatherInformation.id);
        weatherData.put("name",weatherInformation.name);
        weatherData.put("state",weatherInformation.state);
        weatherData.put("time_zone",weatherInformation.time_zone);
        weatherData.put("lat",String.valueOf(weatherInformation.lat));
        weatherData.put("lon",String.valueOf(weatherInformation.lon));
        weatherData.put("local_date_time",weatherInformation.local_date_time);
        weatherData.put("local_date_time_full",weatherInformation.local_date_time_full);
        weatherData.put("air_temp",String.valueOf(weatherInformation.air_temp));
        weatherData.put("apparent_t",String.valueOf(weatherInformation.apparent_t));
        weatherData.put("cloud",weatherInformation.cloud);
        weatherData.put("dewpt",String.valueOf(weatherInformation.dewpt));
        weatherData.put("press",String.valueOf(weatherInformation.press));
        weatherData.put("rel_hum",String.valueOf(weatherInformation.rel_hum));
        weatherData.put("wind_dir",weatherInformation.wind_dir);
        weatherData.put("wind_spd_kmh",String.valueOf(weatherInformation.wind_spd_kmh));
        weatherData.put("wind_spd_kt",String.valueOf(weatherInformation.wind_spd_kt));

        return writeJson(clock,weatherData);
    }

    /**
     *
     * @param id: is used to identify content server which send the weather information
     * @param weatherInformation: is used to add to the list of weather information in the
     * aggregation server or is used to update if it is already in the list
     * @return: updated jsonString
     */
    public String updateWeatherInformation(String id,String weatherInformation)
    {
        String jsonString = "[";
        int j;
        for(j = 0; j < lstWeatherInformation.size();j++)
        {
           if(lstWeatherInformation.get(j).contains(id))
           {
               lstWeatherInformation.set(j,weatherInformation);
               break;
           }
        }
        if(j == lstWeatherInformation.size()) {
            System.out.println("add new content server: " + id);
            lstWeatherInformation.add(weatherInformation);
        }

        for (int i = 0; i < lstWeatherInformation.size(); i++) {
            if (i != lstWeatherInformation.size() - 1) {
                jsonString += (lstWeatherInformation.get(i) + ",");
            } else {
                jsonString += lstWeatherInformation.get(i);
            }
        }
        jsonString += "]";
        return jsonString;
    }
    /**
     *
     * @param id: is used to identify content server which send the weather information,
     * remove the weather information if content server doesn't send any message during last
     * 30 seconds
     * @return: updated jsonString
     */
    public  String removeWeatherInformation(String id)
    {
        String jsonString = "[";
        int j;
        for(j = 0; j < lstWeatherInformation.size();j++)
        {
            if(lstWeatherInformation.get(j).contains(id))
            {
                lstWeatherInformation.remove(j);
                System.out.println("Remove content server: " + id);
                break;
            }
        }
        for (int i = 0; i < lstWeatherInformation.size(); i++) {
            if (i != lstWeatherInformation.size() - 1) {
                jsonString += (lstWeatherInformation.get(i) + ",");
            } else {
                jsonString += lstWeatherInformation.get(i);
            }
        }
        jsonString += "]";
        return jsonString;
    }

    /**
     * Reading data from weather.json
     * @return: the list of weather information objects , the list is used in the
     * aggregation server for communicating with clients and content servers
     */
    public ArrayList<WeatherInformation> readJsonFile()
    {
        ArrayList<WeatherInformation> lstWeatherInfor = new ArrayList<>();
        File weatherFile = new File("weather.json");
        try {
            String jsonWeatherData = "";
            Scanner scanner = new Scanner(weatherFile);
            while (scanner.hasNextLine()) {
                jsonWeatherData += scanner.nextLine();
            }
            jsonWeatherData = jsonWeatherData.replace("[","");
            jsonWeatherData = jsonWeatherData.replace("]","");
            int indexOfOpenParenThesis = jsonWeatherData.indexOf("{");
            int indexOfCloseParenThesis = jsonWeatherData.indexOf('}');
            while(indexOfOpenParenThesis != -1 && indexOfCloseParenThesis != -1)
            {
                String element = jsonWeatherData.substring(indexOfOpenParenThesis, indexOfCloseParenThesis + 1);
                WeatherInformation weatherInformation = readJson(element);
                jsonWeatherData = jsonWeatherData.replace(element + "}", "");
                if(jsonWeatherData.length() > 0 && jsonWeatherData.charAt(0) == ',')
                {
                    jsonWeatherData.replace(",","");
                }
                lstWeatherInfor.add(weatherInformation);
                indexOfOpenParenThesis = jsonWeatherData.indexOf("{");
                indexOfCloseParenThesis = jsonWeatherData.indexOf('}');
            }
        }
        catch (IOException e)
        {
            System.out.println("weather.json " + "is needed to be created");
        }
        return lstWeatherInfor;
    }
}
