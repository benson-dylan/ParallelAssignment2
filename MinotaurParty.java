import java.util.*;
import java.io.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.*;

class Labyrinth 
{
    private int numGuests;
    private Semaphore flag = new Semaphore(1);
    private Boolean cupcake = true;
    private Boolean[] eatenList;
    private int count;

    public Labyrinth(int numGuests)
    {
        this.numGuests = numGuests;
        this.eatenList = new Boolean[numGuests];
        Arrays.fill(this.eatenList, false);
        this.count = 0;
    }

    public synchronized void enterLabyrinth(int threadID)
    {
        if (count != numGuests)
        {
            try
            {
                flag.acquire();
                //System.out.println("Guest #" + threadID + " has entered the maze!");
                if (threadID == 1)
                {
                    if (!cupcake)
                    {
                        this.count++;
                        this.cupcake = true;
                        System.out.println("Guest #1 replaced the cupcake.");
                    }
                    else if (!eatenList[0])
                    {
                        this.eatenList[0] = true;
                        this.cupcake = false;
                        System.out.println("Guest #1 ate the cupcake.");
                    }
                }
                else if (eatenList[threadID - 1])
                {
                    System.out.println("Guest #" + threadID + " is full and did not eat the cupcake.");
                }
                else if (cupcake)
                {
                    eatenList[threadID - 1] = true;
                    cupcake = false;
                    System.out.println("Guest #" + threadID + " ate the cupcake.");
                }
                //System.out.println("Guest #" + threadID + " has completed the maze!");
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            finally
            {
                flag.release();
            }
        }
    }

    public synchronized int getCount()
    {
        //System.out.println("Count: " + this.count);
        return this.count;
    }
    
}

class Guest extends Thread
{
    private int threadID;
    private Labyrinth labyrinth;
    private int counter;

    public Guest (int ID, Labyrinth labyrinth)
    {
        this.threadID = ID;
        this.labyrinth = labyrinth;
    }

    public void run()
    {
        labyrinth.enterLabyrinth(this.threadID);
    }
}

public class MinotaurParty
{
    public static void main (String [] args)
    {
        System.out.println("How many guests are attending the party: ");
        Scanner input = new Scanner(System.in);
        int numGuests = input.nextInt();

        Labyrinth labyrinth = new Labyrinth(numGuests);
        ExecutorService executor = Executors.newFixedThreadPool(numGuests);
        ArrayList<Guest> guestList = new ArrayList<>();

        for (int i = 0; i < numGuests; i++)
        {
            guestList.add(new Guest(i + 1, labyrinth));
        }

        while (labyrinth.getCount() < numGuests)
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

        System.out.println("All guests have completed the maze.");
    }
}