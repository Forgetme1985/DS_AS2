package assignment.weather;


import java.io.IOException;


public class GETClient extends  WeatherConnection{
    /**
     *
     * @param serverAddress: the address of the aggregation server
     * Start client and sending http get every 30 seconds
     */
    public GETClient(String serverAddress)
    {
        super(serverAddress);
        try {
            //while (!socket.isClosed()) {
                String httpMessage = "GET / HTTP/1.1\r\n";
                httpMessage += "User-Agent: ATOMClient/1/0\r\n";
                httpMessage += "Content-Type: application/json\r\n";
                lamportClock.increaseCounter();
                String jsonString = "{\"lamportClock\" : " + lamportClock.counter + "}";
                httpMessage += "Content-Length: " + jsonString.length()  + "\r\n";
                httpMessage += (jsonString + "\r\n");
                socketWriter.println(httpMessage);
                socketWriter.flush();


                String inMsg = socketReader.readLine();
                while (!inMsg.contains("WeatherInformation") && !inMsg.contains("lamportClock"))
                {
                    inMsg = socketReader.readLine();
                }
                WeatherInformation weatherInformation = JsonParser.getInstance().readJson(inMsg);
                System.out.println("id: " + weatherInformation.id);
                System.out.println("name: " + weatherInformation.name);
                System.out.println("state: " + weatherInformation.state);
                System.out.println("time_zone: " + weatherInformation.time_zone);
                System.out.println("lat: " + weatherInformation.lat);
                System.out.println("lon: " + weatherInformation.lon);
                System.out.println("local_date_time: " + weatherInformation.local_date_time);
                System.out.println("local_date_time_full: " + weatherInformation.local_date_time_full);
                System.out.println("air_temp: " + weatherInformation.air_temp);
                System.out.println("apparent_t: " + weatherInformation.apparent_t);
                System.out.println("cloud: " + weatherInformation.cloud);
                System.out.println("dewpt: " + weatherInformation.dewpt);
                System.out.println("press: " + weatherInformation.press);
                System.out.println("rel_hum: " + weatherInformation.rel_hum);
                System.out.println("wind_dir: " + weatherInformation.wind_dir);
                System.out.println("wind_spd_kmh: " + weatherInformation.wind_spd_kmh);
                System.out.println("wind_spd_kt: " + weatherInformation.wind_spd_kt);

                lamportClock.updateCounterReceive(weatherInformation.clockCounter);
             //   Thread.sleep(30000);
           // }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            if(socket != null)
            {
                try{
                    socket.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    public static  void main(String[] args)
    {
        if(args.length >= 1)
        {
            try {
                GETClient getClient = new GETClient(args[0]);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else{
            System.out.println("Please input the server address");
            System.exit(0);
        }
    }
}
