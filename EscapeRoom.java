/*
* Problem 1: Escape Room
* 
* V1.0
* 10/10/2019
* Copyright(c) 2019 PLTW to present. All rights reserved
*/

/**
 * Create an escape room game where the player must navigate
 * to the other side of the screen in the fewest steps, while
 * avoiding obstacles and collecting prizes.
 */
public class EscapeRoom
{
  /* TO-DO: Process game commands from user input:
      right, left, up, down: move player size of move, m, if player try to go off grid or bump into wall, score decreases
      jump over 1 space: player cannot jump over walls
      pick up prize: score increases, if there is no prize, penalty
      help: display all possible commands
      end: reach the far right wall, score increase, game ends, if game ends without reaching far right wall, penalty
      replay: shows number of player steps and resets the board, player or another player can play the same board
        
      if player land on a trap, spring a trap to increase score: the program must first check if there is a trap, if none exists, penalty
      Note that you must adjust the score with any method that returns a score
      Optional: create a custom image for player - use the file player.png on disk
    */

  public static void main(String[] args) 
  {      
    // welcome message
    System.out.println("Welcome to EscapeRoom!");
    System.out.println("Get to the other side of the room, avoiding walls and invisible traps,");
    System.out.println("pick up all the prizes.\n");
    
    GameGUI game = new GameGUI();
    game.createBoard();

    // size of move
    int m = 60; 
    // individual player moves
    int px = 0;
    int py = 0; 
    
    int score = 0;

    String[] validCommands = { "right", "left", "up", "down", "r", "l", "u", "d",
    "jump", "jr", "jumpleft", "jl", "jumpup", "ju", "jumpdown", "jd",
    "pickup", "p", "springtrap", "st", "quit", "q", "replay", "help", "?", "findtrap"};
  
    // set up game
    boolean play = true;
    while (play)
    {

      // get user command and validate
      System.out.print("Enter command:");
      String input = UserInput.getValidInput(validCommands);

      /* process user commands*/
      switch (input) {
        case "right", "r", "d"  -> {
            if (game.hasActiveTrap(game.x, game.y)) {
                System.out.println("You got caught! Spring the Trap and move on. -10 points.");
                score -= 10;
            } else {
                game.movePlayer(m, 0); 
            }
        }
        case "left", "l", "a" -> {
            if (game.hasActiveTrap(game.x, game.y)) {
                System.out.println("You got caught! Spring the Trap and move on. -10 points.");
                score -= 10;
            } else {
                game.movePlayer(-m, 0); 
            }
        }
        case "up", "u", "w" -> {
            if (game.hasActiveTrap(game.x, game.y)) {
                System.out.println("You got caught! Spring the Trap and move on. -10 points.");
                score -= 10;
            } else {
                game.movePlayer(0, -m); 
            }
        }
        case "down", "s" -> {
            if (game.hasActiveTrap(game.x, game.y)) {
                System.out.println("You got caught! Spring the Trap and move on. -10 points.");
                score -= 10;
            } else {
                game.movePlayer(0, m); 
            }
        }
        case "springtrap", "st" -> {
          int trapScore = game.springTrap(px, py);
          if (trapScore > 0) {
            System.out.println("TRAP IS SPRUNG! Score: +" + trapScore);
          } else {
            System.out.println("THERE IS NO TRAP HERE TO SPRING. Penalty: " + trapScore);
          }
          score += trapScore;
        }
        case "help", "?" -> {
          System.out.println("\n=== COMMANDS ===");
          System.out.println("right, r, d   : Move right");
          System.out.println("left, l, a    : Move left");
          System.out.println("up, u, w      : Move up");
          System.out.println("down, s       : Move down");
          System.out.println("jump, jr      : Jump right");
          System.out.println("jumpleft, jl  : Jump left");
          System.out.println("jumpup, ju    : Jump up");
          System.out.println("jumpdown, jd  : Jump down");
          System.out.println("pickup, p     : Pick up a prize if present");
          System.out.println("springtrap, st: Spring a trap if present (for points)");
          System.out.println("findtrap      : Reveal all trap locations (cheat)");
          System.out.println("replay        : Reset the board and play again");
          System.out.println("help, ?       : Show this help menu");
          System.out.println("quit, q       : Quit the game");
          System.out.println("\nType one of these commands and press Enter to play.");
        }
        case "quit", "q" -> play = false;
        case "findtrap" -> {
          System.out.println("Trap locations:");
          game.printTraps();
        }
        // Add other command processing as needed
        default -> {
          // Handle other commands or invalid input
        }
      }

      // Example usage of px and py to avoid "never read" error
      System.out.println("Player Position: (" + px + ", " + py + ")");

      /* uncomment when user quits */
      // play = false;
      // Check if player is on a coin and collect it
      int prize = game.pickupPrize();
      if (prize > 0) {
        score += prize;
        System.out.println("You collected a coin! Score: " + score);
      }

      System.out.println("Player position: (" + game.x + ", " + game.y + ")");
      System.out.println("Score: " + score); // Always show current score
    }

    score += game.endGame();

    System.out.println("Score=" + score);
    System.out.println("Steps=" + game.getSteps());
    System.out.println("test");
    System.out.println("Player position: (" + game.x + ", " + game.y + ")");
    System.out.println("score=" + score);
    System.out.println("steps=" + game.getSteps());


    
  }
  

}


