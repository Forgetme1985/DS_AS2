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
        /**
         * Start Server and waiting for connections
         *
         */
        try {
            int portNumber = 4567;
            if(args.length > 0)
            {
                portNumber = Integer.parseInt(args[0]);
            }
           ServerSocket serverSocket = new ServerSocket(portNumber,100,
                    InetAddress.getByName("localhost"));
            System.out.println("Server started");
            //Reading Json file to restore data if server crashed
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
    public static void handleClientRequest(Socket socket)
    {
        BufferedReader socketReader = null;
        PrintWriter socketWriter = null;
        long startTime = System.currentTimeMillis();
        String idContentServer = "";
        try{
            socketReader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            socketWriter = new PrintWriter(socket.getOutputStream());
            String response = "";
            String httpMessage = "";
            String inMsg = null;
            while(!socket.isClosed())
            {
                /**
                 * Remove any content server if it doesn't send any message
                 * within 30 seconds
                 */
                if(idContentServer.compareTo("")  != 0 && System.currentTimeMillis() - startTime >= 30000)
                {
                    for(int i = 0; i < lstWeatherInformation.size();i++)
                    {
                        if(lstWeatherInformation.get(i).id.compareTo(idContentServer) == 0)
                        {
                            lstWeatherInformation.remove(i);
                            break;
                        }
                    }
                    String temp = JsonParser.getInstance().removeWeatherInformation(idContentServer);
                    FileWriter fileWriter = new FileWriter("weather.json");
                    fileWriter.write(temp);
                    fileWriter.close();
                    jsonWeatherData = temp;
                    break;
                }
                inMsg = socketReader.readLine();
                if(inMsg != null && inMsg.length() > 0)
                {
                    httpMessage += (inMsg + "\n");
                }
                else{
                    /**
                     * Handle HTTP PUT from content servers
                     *
                     */
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
                                    jsonWeatherData = JsonParser.getInstance().updateWeatherInformation(weatherInformation.id,lineMsg[4]);
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
                                String temp = JsonParser.getInstance().updateWeatherInformation(weatherInformation.id,lineMsg[4]);
                                FileWriter fileWriter = new FileWriter("weather.json");
                                fileWriter.write(temp);
                                fileWriter.close();
                                jsonWeatherData = temp;
                                int i;
                                for(i = 0; i < lstWeatherInformation.size();i++)
                                {
                                    if(lstWeatherInformation.get(i).id.compareTo(weatherInformation.id) == 0)
                                    {
                                        lstWeatherInformation.set(i,weatherInformation);
                                        break;
                                    }
                                }
                                if(i == lstWeatherInformation.size())
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
                        startTime = System.currentTimeMillis();
                        idContentServer = weatherInformation.id;
                    }
                    /**
                     * Handle HTTP GET from GET CLIENTS
                     *
                     */
                    else if( httpMessage.startsWith("GET"))
                    {
                        WeatherInformation weatherInformation = null;
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
