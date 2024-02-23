import java.util.*;
import java.io.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.*;

// Class to hold labyrinth game, control access, and track which guests have eaten the cupcake
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

    // Continues access to the game while any number of guests have not entered before
    public synchronized void enterLabyrinth(int threadID)
    {
        if (count != numGuests)
        {
            // If semaphore is in use, other threads cannot enter the maze
            try
            {
                flag.acquire();
                // Guest 1 acts as counter and is the only one to replace the cupcake
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
                // Other guests only eat one cupcake and use the array to check if they have eaten one yet
                // This information is not shared with any other guests
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
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            // When the guest exits the maze, the semaphore is released
            finally
            {
                flag.release();
            }
        }
    }

    public synchronized int getCount()
    {
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

        // Uses arraylist to hold guest threads
        for (int i = 0; i < numGuests; i++)
        {
            guestList.add(new Guest(i + 1, labyrinth));
        }

        // While the game is in play, guests are chosen at random to enter
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

        // This prints when the game is over
        System.out.println("All guests have completed the maze.");
    }
}
