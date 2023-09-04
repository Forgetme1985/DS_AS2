package assignment.weather;


import java.io.IOException;
import java.util.List;

public class GETClient extends  WeatherConnection{
    public GETClient(String serverAddress)
    {
        super(serverAddress);
        try {
            while (!socket.isClosed()) {
                Thread.sleep(3000);
                String httpMessage = "GET /weather.json HTTP/1.1\n";
                socketWriter.println(httpMessage);
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
