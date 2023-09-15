package assignment.weather;

public class LamportClock {
    public int counter;

    public LamportClock()
    {
        counter = 0;
    }

    /**
     * Increasing counter before sending any events
     */
    public void increaseCounter()
    {
        counter++;
    }

    /**
     *
     * @param receiveCounter: this counter from another entity's clock
     * Check to update counter in receive events of
     * The aggregation server, content servers or clients
     */
    public  void updateCounterReceive(int receiveCounter)
    {
        if(receiveCounter > counter)
        {
            counter = receiveCounter;
        }
        else if(receiveCounter == counter)//concurrency occur
        {

        }
    }
}
