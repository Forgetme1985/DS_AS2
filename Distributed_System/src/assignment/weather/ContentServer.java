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
                System.out.println("Send MSG from client");
                List<String> weatherData = readingWeatherData(weatherFile);
                String httpPostMessage = "PUT /weather.json HTTP/1.1\n";
                httpPostMessage += "User-Agent: ATOMClient/1/0\n";
                httpPostMessage += "Content-Type: text/json\n";
                String jsonString = JsonParser.getInstance().writeJson(weatherData);
                httpPostMessage += "Content-Length: " + (jsonString.length() + 2) + "\n";
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
                //the first time to send HTTP CREATE
                ContentServer contentServer = new ContentServer(args[0],weatherFile);
                // contentServer.postHTTP(contentServer.readingWeatherData(weatherFile));
                //String[] response = contentServer.getResponse().split(" ");
                //int statusCode = Integer.parseInt(response[1]);

                /*if(statusCode == 201)
                {
                    System.out.println("Created");
                    Thread.sleep(3000);
                    System.out.println("Send new data");
                    contentServer.postHTTP(contentServer.readingWeatherData(weatherFile));
                    response = contentServer.getResponse().split(" ");
                    System.out.println(response[0]);
                    statusCode = Integer.parseInt(response[1]);
                }*/
                /*while (statusCode == 200)
                {
                    System.out.println("OK");
                    Thread.sleep(3000);
                    System.out.println("Send new data");
                    contentServer.postHTTP(contentServer.readingWeatherData(weatherFile));
                    response = contentServer.getResponse().split(" ");
                    statusCode = Integer.parseInt(response[1]);
                }
                if(statusCode == 204)
                {

                }
                else  if(statusCode == 400)
                {

                }
                else  if(statusCode == 500)
                {

                }*/
               // contentServer.postHTTP(weatherData);
                /*if(contentServer.statusCode == 201)
                {
                    while (!contentServer.isClosedSocket()) {
                        System.out.println("Send msg to server");

                    }
                }*/
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
