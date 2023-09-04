package assignment.weather;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

public class ContentServer extends WeatherConnection {
    public ContentServer(String serverAddress,File weatherFile) {
        super(serverAddress);
        try {
            while (!socket.isClosed()) {
                Thread.sleep(3000);
                System.out.println("Send data from content server");
                List<String> weatherData = readingWeatherData(weatherFile);
                String httpPostMessage = "PUT /weather.json HTTP/1.1\r\n";
                httpPostMessage += "User-Agent: ATOMClient/1/0\r\n";
                httpPostMessage += "Content-Type: text/json\r\n";
                String jsonString = JsonParser.getInstance().writeJson(weatherData);
                httpPostMessage += "Content-Length: " + jsonString.length()  + "\r\n";
                httpPostMessage += (jsonString + "\n");
                socketWriter.println(httpPostMessage);
                socketWriter.flush();
                String inMsg = socketReader.readLine();
                System.out.println("Server: " + inMsg);
            }
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

    public static void main(String[] args)
    {
        if(args.length >= 2)
        {
            File weatherFile = new File(args[1]);
            try {
                ContentServer contentServer = new ContentServer(args[0],weatherFile);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else{
            System.out.println("Please input the server address and the path to the weather file");
            System.exit(0);
        }
    }
    private  List<String> readingWeatherData(File weatherFile)
    {
        List<String> weatherData = new ArrayList<>();
        try {
            Scanner readingScanner = new Scanner(weatherFile);
            while (readingScanner.hasNextLine()) {
                weatherData.add(readingScanner.nextLine());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return weatherData;
    }
}
