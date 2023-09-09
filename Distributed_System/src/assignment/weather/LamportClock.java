package assignment.weather;

public class LamportClock {
    public int counter;

    public LamportClock()
    {
        counter = 0;
    }
    public void increaseCounter()
    {
        counter++;
    }

    public  void updateCounterReceive(int receiveCounter)
    {
        if(receiveCounter > counter)
        {
            counter = receiveCounter;
        }
    }
}
