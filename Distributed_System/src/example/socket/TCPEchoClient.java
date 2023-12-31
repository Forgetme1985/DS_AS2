package example.socket;

import java.io.*;
import java.net.Socket;

public class TCPEchoClient {
    public  static void main(String[] args)
    {
        Socket socket = null;
        BufferedReader socketReader = null;
        BufferedWriter socketWriter = null;
        try
        {
            socket = new Socket("localhost",12900);
            System.out.println("Started client " +socket.getLocalAddress());
            socketReader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );
            socketWriter = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())
            );
            BufferedReader consoleReader = new BufferedReader(
                    new InputStreamReader(System.in)
            );
            String promptMsg = "Please enter a message (Bye to quit)";
            String outMsg = null;
            System.out.println(promptMsg);
            while ((outMsg = consoleReader.readLine()) != null)
            {
                if(outMsg.equalsIgnoreCase("bye"))
                {
                    break;
                }
                socketWriter.write(outMsg);
                socketWriter.write("\n");
                socketWriter.flush();

                String inMsg = socketReader.readLine();
                System.out.println("Server: " + inMsg);
                System.out.println();
                System.out.println(promptMsg);
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
}
