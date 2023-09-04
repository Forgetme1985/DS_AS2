package example.socket;

import java.net.InetAddress;

public class InetAddressTest {
    public  static void main(String[] args)
    {
        PrintAddressDetails("www.yahoo.com");
        PrintAddressDetails(null);
        PrintAddressDetails("::1");
    }
    public  static void PrintAddressDetails(String host)
    {
        System.out.println("Host name: " + host);
        try{
            InetAddress addr = InetAddress.getByName(host);
            System.out.println("Host IP Address: " + addr.getHostAddress());
            System.out.println("Canonical Host Name: " + addr.getCanonicalHostName());
            int timeOutInMillis = 10000;
            System.out.println("isReachable(): " + addr.isReachable(timeOutInMillis));
            System.out.println("isLoopbackAddress(): " + addr.isLoopbackAddress());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            System.out.println("-------------------------------\n");
        }
    }
}
