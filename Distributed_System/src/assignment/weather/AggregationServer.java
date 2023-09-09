package assignment.weather;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;


public class AggregationServer {
   public static ArrayList<WeatherInformation>  lstWeatherInformation = new ArrayList<>();
   public static   String jsonWeatherData = "";
   private static LamportClock serverClock = new LamportClock();
    public static void main(String[] args)
    {

        try {
            int portNumber = 4567;
            if(args.length > 0)
            {
                portNumber = Integer.parseInt(args[0]);
            }
           ServerSocket serverSocket = new ServerSocket(portNumber,100,
                    InetAddress.getByName("localhost"));
            System.out.println("Server started");
            lstWeatherInformation = JsonParser.getInstance().readJsonFile();
            while(true)
            {
                System.out.println("waiting for connections...");
                final Socket activeSocket = serverSocket.accept();
                System.out.println("Received a connection from " + activeSocket);
                Runnable runnable = ()->handleClientRequest(activeSocket);
                new Thread(runnable).start();
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public static  void handleClientRequest(Socket socket)
    {
        BufferedReader socketReader = null;
        PrintWriter socketWriter = null;
        try{
            socketReader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            socketWriter = new PrintWriter(socket.getOutputStream());
            String response = "";
            String httpMessage = "";
            String inMsg = null;
            while(!socket.isClosed())
            {
                inMsg = socketReader.readLine();
                if(inMsg != null && inMsg.length() > 0)
                {
                    httpMessage += (inMsg + "\n");
                }
                else{
                    if(httpMessage.startsWith("PUT"))
                    {
                        String[] lineMsg = httpMessage.split("\n");
                        WeatherInformation weatherInformation = null;
                        if(lstWeatherInformation.size() == 0)
                        {
                            weatherInformation = JsonParser.getInstance().readJson(lineMsg[4]);
                            if(weatherInformation != null)
                            {
                                try
                                {
                                    response = "HTTP/1.1 201 CREATED\r\n";
                                    jsonWeatherData = JsonParser.getInstance().updateWeatherInformation(lineMsg[4]);
                                    FileWriter fileWriter = new FileWriter("weather.json");
                                    fileWriter.write(jsonWeatherData);
                                    fileWriter.close();
                                    lstWeatherInformation.add(weatherInformation);
                                }
                                catch (IOException e)
                                {
                                    response = "HTTP/1.1 500 INTERNAL_SERVER_ERROR";
                                }
                            }
                            else
                                response = "HTTP/1.1 500 INTERNAL_SERVER_ERROR";
                        }
                        else
                        {
                            weatherInformation = JsonParser.getInstance().readJson(lineMsg[4]);
                            if(weatherInformation != null)
                            {
                                response = "HTTP/1.1 200 OK\r\n";
                                String temp = JsonParser.getInstance().updateWeatherInformation(lineMsg[4]);
                                FileWriter fileWriter = new FileWriter("weather.json");
                                fileWriter.write(temp);
                                fileWriter.close();
                                jsonWeatherData = temp;
                                lstWeatherInformation.add(weatherInformation);
                            }
                            else
                                response = "HTTP/1.1 500 INTERNAL_SERVER_ERROR";
                        }
                        serverClock.updateCounterReceive(weatherInformation.clockCounter);
                        serverClock.increaseCounter();
                        response += "Server: AggregationServer\r\n";
                        response += "Content-Type: application/json\r\n";
                        String jsonString = JsonParser.getInstance().writeJson(serverClock,weatherInformation);
                        response += "Content-Length: " + jsonString.length()  + "\r\n";
                        response += "\r\n";
                        response += (jsonString);
                        socketWriter.println(response);
                        socketWriter.flush();
                        httpMessage = "";
                        response = "";
                        inMsg = null;
                    }
                    else if( httpMessage.startsWith("GET"))
                    {
                        String[] lineMsg = httpMessage.split("\n");
                        WeatherInformation weatherInformation = null;
                        if(lineMsg.length == 5)
                        {
                            lineMsg[4] = lineMsg[4].replace("{","");
                            lineMsg[4] = lineMsg[4].replace("}","");
                            String[] lamportClock = lineMsg[4].split(":");
                            int counter = Integer.parseInt(lamportClock[1].trim());
                            int i = 0;
                            for(i = 0; i < lstWeatherInformation.size();i++)
                            {
                                if(lstWeatherInformation.get(i).clockCounter > counter)
                                {
                                    weatherInformation = lstWeatherInformation.get(i);
                                    break;
                                }
                            }
                            if(i == lstWeatherInformation.size())
                            {
                                response = "HTTP/1.1 503 Service Unavailable\r\n";
                            }
                            else
                            {
                                response = "HTTP/1.1 200 OK\r\n";
                                response += "Server: AggregationServer\r\n";
                                response += "Content-Type: application/json\r\n";
                                String jsonString = JsonParser.getInstance().writeJson(serverClock,weatherInformation);
                                response += "Content-Length: " + jsonString.length()  + "\r\n";
                                response += "\r\n";
                                response += (jsonString);
                            }
                        }
                        else
                        {
                            if(lstWeatherInformation.size() > 0)
                            {
                                response = "HTTP/1.1 200 OK\r\n";
                                response += "Server: AggregationServer\r\n";
                                response += "Content-Type: application/json\r\n";
                                String jsonString = JsonParser.getInstance().writeJson(serverClock, lstWeatherInformation.get(lstWeatherInformation.size() - 1));
                                response += "Content-Length: " + jsonString.length() + "\r\n";
                                response += "\r\n";
                                response += (jsonString);
                            }
                            else
                            {
                                response = "HTTP/1.1 503 Service Unavailable\r\n";
                            }
                        }
                        if(weatherInformation != null)
                        {
                            serverClock.updateCounterReceive(weatherInformation.clockCounter);
                            serverClock.increaseCounter();
                        }
                        socketWriter.println(response);
                        socketWriter.flush();
                        httpMessage = "";
                        response = "";
                        inMsg = null;
                    }
                    else
                    {
                        if(httpMessage.length() != 0)
                            response = "HTTP/1.1 400 ERROR";
                        else
                            response = "HTTP/1.1 204 ERROR";
                        socketWriter.println(response);
                        socketWriter.flush();
                    }
                }
            }
        }
        catch (Exception e)
        {
            try {
                if(jsonWeatherData.length() > 0)
                {
                    FileWriter fileWriter = new FileWriter("weather.json");
                    fileWriter.write(jsonWeatherData);
                    fileWriter.close();
                }
            }
            catch (IOException ex)
            {
                e.printStackTrace();
            }
            e.printStackTrace();
        }
        finally {
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
