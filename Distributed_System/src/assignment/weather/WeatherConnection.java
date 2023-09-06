package assignment.weather;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class WeatherConnection {
    protected Socket socket;
    protected BufferedReader socketReader;
    protected PrintStream socketWriter;
    protected BufferedReader consoleReader;
    public WeatherConnection(String serverAddress)
    {
        try
        {
            URL url = new URL(serverAddress);
            socket = new Socket(url.getHost(),url.getPort());
            System.out.println("Started client " +socket.getLocalAddress());
            socketReader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );
            socketWriter = new PrintStream(socket.getOutputStream());
             consoleReader = new BufferedReader(
                    new InputStreamReader(System.in)
            );
             Runnable runnable = ()->handleCloseSocket();
             new Thread(runnable).start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public List<String> readInputFromSocket( )
    {
        ArrayList<String> listInput = new ArrayList<>();
        if(socketReader != null) {
            try {
                String inStr = "";
                while ((inStr = socketReader.readLine()) != null) {
                    System.out.println(inStr);
                    listInput.add(inStr);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return listInput;
    }


    public void closeSocket()
    {
        if(socket != null)
        {
            try{
                socket.close();
                System.out.println("Closed Socket");
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    public void handleCloseSocket()
    {
        System.out.println("Input close to close the connection");
        Scanner inputScanner = new Scanner(System.in);
        while(true)
        {
            String consoleMsg = inputScanner.nextLine();
            if (consoleMsg.equalsIgnoreCase("close")) {
                break;
            }
        }
        closeSocket();
    }
}
