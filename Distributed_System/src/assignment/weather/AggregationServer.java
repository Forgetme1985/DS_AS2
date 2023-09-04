package assignment.weather;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;

public class AggregationServer {


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
    static  void handleClientRequest(Socket socket)
    {
        WeatherInformation weatherInformation = null;
        BufferedReader socketReader = null;
        PrintWriter socketWriter = null;
        try{
            socketReader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            socketWriter = new PrintWriter(socket.getOutputStream());
            String response = "default";
            String httpMessage = "";
            String inMsg = null;
            while(true)
            {
                inMsg = socketReader.readLine();
                if(inMsg != null && inMsg.length() > 0)
                {
                    httpMessage += (inMsg + "\n");
                    response = "Transfering";
                }
                else{
                    if(httpMessage.startsWith("PUT"))
                    {
                        String[] lineMsg = httpMessage.split("\n");
                        if(weatherInformation == null)
                        {
                            weatherInformation = JsonParser.getInstance().readJson(lineMsg[4]);
                            response = "HTTP/1.1 201 CREATED";
                        }
                        else
                        {
                            response = "HTTP/1.1 200 OK";
                        }
                    }
                }
                socketWriter.println(response);
                socketWriter.flush();
            }
           /* while(true)
            {
                String consoleMsg = inputScanner.nextLine();
                if (consoleMsg.equalsIgnoreCase("close")) {
                    break;
                }
                inMsg = socketReader.readLine();
                System.out.println("Received from client: " + inMsg);
                String outMsg = inMsg;
                socketWriter.write(outMsg);
                socketWriter.write("\n");
                socketWriter.flush();
                if(inMsg.startsWith("PUT"))
                {
                    if(weatherInformation == null)
                    {
                        weatherInformation = new WeatherInformation();
                        weatherInformation = JsonParser.getInstance().readJson(httpContent[4]);
                        socketWriter.write("HTTP/1.1 201 CREATED\n");
                        socketWriter.flush();
                    }
                    else
                    {
                        socketWriter.write("HTTP/1.1 200 OK\n");
                        socketWriter.flush();
                    }
                }

            }*/
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
