package assignment.weather;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


public class AggregationServer {


   public static WeatherInformation weatherInformation = null;
   public static   String jsonWeatherData = "";
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
            while(true)
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

                        if(weatherInformation == null)
                        {
                            weatherInformation = JsonParser.getInstance().readJson(lineMsg[4]);
                            if(weatherInformation != null)
                            {
                                response = "HTTP/1.1 201 CREATED";
                                jsonWeatherData = lineMsg[4];
                            }
                            else
                                response = "HTTP/1.1 500 INTERNAL_SERVER_ERROR";
                        }
                        else
                        {
                            weatherInformation = JsonParser.getInstance().readJson(lineMsg[4]);
                            if(weatherInformation != null)
                            {
                                response = "HTTP/1.1 200 OK";
                                jsonWeatherData = lineMsg[4];
                            }
                            else
                                response = "HTTP/1.1 500 INTERNAL_SERVER_ERROR";
                        }
                    }
                    else if( httpMessage.startsWith("GET"))
                    {
                        response = "HTTP/1.1 200 OK\r\n";
                        response += "Server: AggregationServer\r\n";
                        response += "Content-Type: text/json\r\n";
                        response += "Content-Length: " + jsonWeatherData.length()  + "\r\n";
                        response += jsonWeatherData;
                    }
                    else
                    {
                        if(httpMessage.length() != 0)
                            response = "HTTP/1.1 400 ERROR";
                        else
                            response = "HTTP/1.1 204 ERROR";
                    }
                }
                socketWriter.println(response);
                socketWriter.flush();
            }
        }
        catch (Exception e)
        {
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
