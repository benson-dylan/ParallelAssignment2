<h1>How to Compile and Run</h1>
javac MinotaurParty.java or javac CrystalVase.java <br>
java MinotaurParty or java CrystalVase <br>
Both programs prompt the user to input a number of guests they would like to simulate at the start <br>

<h1>Minotaur's Birthday Party</h1>
The technique I used for this solution was having the first guest thread act as a counter. The guest cannot communicate with any other guests so all guests only eat one cupcake and allow the first guest to replace it. The first guest will then count how many times the cupcake is needed to be replaced. The guests are then chosen at random until the first guest announces to the minotaur that everyone has visited the maze. A semaphore is used to control access to the maze, allowing only one thread to access it at a time. A shared array is used to keep track of which guests have eaten the cupcake. This array is not shared with any other thread and is only used for a guest to see if they have already eaten or not.

<h1>Minotaur's Crystal Vase</h1>

The three possible solutions are as follows: <br>
1. Do nothing to notify other guests that the room is occupied. <br> The benefit to this method is that it is the easiest to implement with no real mutual exclusion in place to prevent others from entering the room until they open the door and see it's occupied. <br>
The issue with this method is the lack of mutual exclusion, it can lead to multiple guests trying to enter the room all at once. <br>
2. Place a sign on the door to indicate if the room is currently occupied. <br>
The benefit of this method is that it is only a slight modification of the first. If the door is labeled as busy then a guest placed to walked by the room will keep walking and come back later. If the room is available a guest will enter, setting the sign to busy. <br>
The disadvantage of this method is that it is up to random chance that the room will be available at any point when a guest walks by. Although this allows the threads to be doing other things in the meantime. <br>
3. Create a queue of guests for the room, only removing guests from the queue one at a time once the room has become available. <br>
The benefit of this method is that it is not random and very orderly. It has the same level of mutual exclusion and a much more predictable runtime. <br>
The disadvantage of this method is that it is much more complex to implement and also does not allow the threads to do anything else while waiting in line for the vase. <br>

The method I used to solve this problem was to leave a sign on the door of the vase room. This solution isn't as neat or orderly as a queue but it is simpler to implement and works just as well. Guests will only visit the vase room once and keep walking if they pass by the room after visiting it prior. If a guest passes the room and has not visited, and the room is available, they will enter. Guests are chosen at random to walk by the room to simulate the wandering of guests, and once in the room they will spend a random (brief) amount of time. When a guest enters the room, they flip the sign to busy, and once finished, they flip the sign back to available. These actions are synchronized in order to prevent multiple guests from entering the room at the same time.

<h1>Experimental Evaluation</h1>

The outcome of both algorithms are not dependent on the number of guests, only the time is dependent. The time is difficult to determine as both are controlled by a random number generator. Both algorithms stop once all threads have completed the objective. <br>
I ran numerous tests to determine if my algorithms and mutual exclusion were working properly and eventually achieve the level of exclusion that I desired.
