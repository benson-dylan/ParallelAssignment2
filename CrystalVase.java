import java.util.*;
import java.util.concurrent.*;

class VaseRoom
{
    private Set<Integer> visitedSet;
    private volatile boolean available;
    private int numGuests;

    public VaseRoom(int numGuests)
    {
        this.numGuests = numGuests;
        this.visitedSet = Collections.synchronizedSet(new HashSet<Integer>());
        this.available = true;
    }

    public void visitCrystalVase(int threadID)
    {
        synchronized (this)
        {
            if (visitedSet.size() != numGuests && available)
            {
                if (visitedSet.contains(threadID))
                {
                    System.out.println("Guest #" + threadID + " has already visited the vase and keeps walking.");
                }
                else
                {
                    try
                    {
                        setBusy();
                        System.out.println("Guest #" + threadID + " has entered the vase room and is admiring the vase.");
                        Thread.sleep((int) Math.floor(Math.random() * 150 + 1));
                        this.visitedSet.add(threadID);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    finally
                    {
                        setAvailable();
                        System.out.println("Guest #" + threadID + " has exited the vase room.");
                    }
                }
            }
        }
    }

    public synchronized int getVisitedCount()
    {
        return this.visitedSet.size();
    }

    private synchronized void setBusy()
    {
        this.available = false;
    }

    private synchronized void setAvailable()
    {
        this.available = true;
    }
}

class Guest extends Thread
{
    private int threadID;
    private VaseRoom vaseRoom;

    public Guest(int threadID, VaseRoom vaseRoom)
    {
        this.threadID = threadID;
        this.vaseRoom = vaseRoom;
    }

    public void run()
    {
        vaseRoom.visitCrystalVase(this.threadID);
    }
}

public class CrystalVase
{
    public static void main(String [] args)
    {
        int numGuests = 0;
        System.out.println("How many guests are visiting the vase today?");
        Scanner scanner = new Scanner(System.in);
        numGuests = scanner.nextInt();
        ArrayList<Guest> guestList = new ArrayList<>();
        VaseRoom vaseRoom = new VaseRoom(numGuests);

        for (int i = 1; i <= numGuests; i++)
        {
            guestList.add(new Guest(i, vaseRoom));
        }

        ExecutorService executor = Executors.newFixedThreadPool(numGuests);

        while (vaseRoom.getVisitedCount() < numGuests)
        {
            int randomNumber = (int) (Math.random() * numGuests) + 1;
            Guest guest = guestList.get(randomNumber - 1);
            executor.execute(guest);
        }

        // Executor service shuts threads down
        executor.shutdown();

        // This ensures all threads have ended processes
        try 
        {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);  
        } 
        catch (InterruptedException e) 
        {
            e.printStackTrace();
        }

        System.out.println("All guests have seen the crystal vase.");
    }
}