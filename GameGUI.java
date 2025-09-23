import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 * A Game board on which to place and move players.
 * 
 * @author PLTW
 * @version 1.0
 */
public class GameGUI extends JComponent implements KeyListener {
  static final long serialVersionUID = 141L;

  private static final int BOARD_WIDTH = 510;
  private static final int BOARD_HEIGHT = 360;
  private static final int SPACE_SIZE = 60;
  private static final int GRID_W = 8;
  private static final int GRID_H = 5;
  private static final int START_LOC_X = 15;
  private static final int START_LOC_Y = 15;

  int x = START_LOC_X;
  int y = START_LOC_Y;

  private Image bgImage;

  private Image player;
  private final Point playerLoc = new Point(START_LOC_X, START_LOC_Y);
  private int playerSteps;

  private int totalWalls;
  private Rectangle[] walls;
  private Image prizeImage;
  private int totalPrizes;
  private Rectangle[] prizes;
  private int totalTraps;
  private Rectangle[] traps;
  public Image trapImage;

  private int prizeVal = 10;
  private int trapVal = 5;
  private int endVal = 10;
  private int offGridVal = 5;
  private int hitWallVal = 5;

  private int totalScore = 0;
  private boolean jumpMode = false;
  private JFrame frame;
  // game frame
  private final JFrame frame;

  /**
   * Constructor for the GameGUI class.
   * Creates a frame with a background image and a player that will move around the board.
   */
  public GameGUI() {
    try {
      bgImage = ImageIO.read(new File("grid.png"));
    } catch (java.io.IOException | NullPointerException e) {
      System.err.println("Could not open file grid.png");
    }
    try {
      prizeImage = ImageIO.read(new File("coin.png"));
    } catch (java.io.IOException | NullPointerException e) {
      System.err.println("Could not open file coin.png");
    }
    try {
      player = ImageIO.read(new File("player.png"));
    } catch (java.io.IOException | NullPointerException e) {
      System.err.println("Could not open file player.png");
    }
    try {
      trapImage = ImageIO.read(new File("trap.png"));
    }  catch (java.io.IOException | NullPointerException e) {
      System.err.println("Could not open file trap.png");
      }
  
    frame = new JFrame();
    frame.setTitle("EscapeRoom");
    frame.setSize(BOARD_WIDTH, BOARD_HEIGHT + 50);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setResizable(false);

    frame.setLayout(new java.awt.BorderLayout());

    frame.add(this, java.awt.BorderLayout.CENTER);

    javax.swing.JPanel controlPanel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

    JButton finishButton = new JButton("Finish");
    finishButton.addActionListener(e -> checkFinish());
    controlPanel.add(finishButton);

    JButton quitButton = new JButton("Press Q to Quit");
    quitButton.addActionListener(e -> System.exit(0));
    controlPanel.add(quitButton);

    frame.add(controlPanel, java.awt.BorderLayout.SOUTH);

    frame.setVisible(true);
    totalWalls = 20;
    totalPrizes = 3;
    totalTraps = 5;
    setFocusable(true);
    addKeyListener(this);
    requestFocusInWindow();
  }

  /**
   * Call this method after constructing GameGUI to finish GUI initialization.
   */
  public void initializeGUI() {
    frame.add(this);
  }

  /**
   * After a GameGUI object is created, this method adds the walls, prizes, and traps to the gameboard.
   * Note that traps and prizes may occupy the same location.
   */
  public void createBoard() {
    traps = new Rectangle[totalTraps];
    createTraps();

    prizes = new Rectangle[totalPrizes];
    createPrizes();

    walls = new Rectangle[totalWalls];
    createWalls();
  }


  public int movePlayer(int incrx, int incry) {
    int newX = x + incrx;
    int newY = y + incry;

    playerSteps++;

    if ((newX < 0 || newX > BOARD_WIDTH - SPACE_SIZE) || (newY < 0 || newY > BOARD_HEIGHT - SPACE_SIZE)) {
      System.out.println("OFF THE GRID!");
      return -offGridVal;
    }

    for (Rectangle r : walls) {
      int startX = (int) r.getX();
      int endX = (int) r.getX() + (int) r.getWidth();
      int startY = (int) r.getY();
      int endY = (int) r.getY() + (int) r.getHeight();

      if ((incrx > 0) && (x <= startX) && (startX <= newX) && (y >= startY) && (y <= endY)) {
        System.out.println("A WALL IS IN THE WAY");
        return -hitWallVal;
      } else if ((incrx < 0) && (x >= startX) && (startX >= newX) && (y >= startY) && (y <= endY)) {
        System.out.println("A WALL IS IN THE WAY");
        return -hitWallVal;
      } else if ((incry > 0) && (y <= startY && startY <= newY && x >= startX && x <= endX)) {
        System.out.println("A WALL IS IN THE WAY");
        return -hitWallVal;
      } else if ((incry < 0) && (y >= startY) && (startY >= newY) && (x >= startX) && (x <= endX)) {
        System.out.println("A WALL IS IN THE WAY");
        return -hitWallVal;
      }
    }

    x += incrx;
    y += incry;
    playerLoc.setLocation(x, y);
    repaint();
    return 0;
  }

  public int jumpPlayer(int incrx, int incry) {
    int jumpX = 2 * incrx;
    int jumpY = 2 * incry;
    int midX = x + incrx;
    int midY = y + incry;
    int newX = x + jumpX;
    int newY = y + jumpY;

    playerSteps++;

    if ((newX < 0 || newX > BOARD_WIDTH - SPACE_SIZE) || (newY < 0 || newY > BOARD_HEIGHT - SPACE_SIZE)) {
      System.out.println("CAN'T JUMP OFF THE GRID!");
      return -offGridVal;
    }

    for (Rectangle r : walls) {
      int startX = (int) r.getX();
      int endX = (int) r.getX() + (int) r.getWidth();
      int startY = (int) r.getY();
      int endY = (int) r.getY() + (int) r.getHeight();

      if ((incrx > 0) && (x <= startX) && (startX <= midX) && (y >= startY) && (y <= endY)) {
        System.out.println("CAN'T JUMP OVER WALL!");
        return -hitWallVal;
      } else if ((incrx < 0) && (x >= startX) && (startX >= midX) && (y >= startY) && (y <= endY)) {
        System.out.println("CAN'T JUMP OVER WALL!");
        return -hitWallVal;
      } else if ((incry > 0) && (y <= startY && startY <= midY && x >= startX && x <= endX)) {
        System.out.println("CAN'T JUMP OVER WALL!");
        return -hitWallVal;
      } else if ((incry < 0) && (y >= startY) && (startY >= midY) && (x >= startX) && (x <= endX)) {
        System.out.println("CAN'T JUMP OVER WALL!");
        return -hitWallVal;

    
  /**
   * Increment/decrement the player location by the amount designated.
   * This method checks for bumping into walls and going off the grid,
   * both of which result in a penalty.
   * <P>
   * precondition: amount to move is not larger than the board, otherwise player may appear to disappear
   * postcondition: increases number of steps even if the player did not actually move (e.g. bumping into a wall)
   * <P>
   * @param incrx amount to move player in x direction
   * @param incry amount to move player in y direction
   * @return penalty score for hitting a wall or potentially going off the grid, 0 otherwise
   */
  public int movePlayer(int incrx, int incry)
  {
      int newX = x + incrx;
      int newY = y + incry;
      
      // increment regardless of whether player really moves
      playerSteps++;

      // check if off grid horizontally and vertically
      if ( (newX < 0 || newX > BOARD_WIDTH-SPACE_SIZE) || (newY < 0 || newY > BOARD_HEIGHT-SPACE_SIZE) )
      {
        System.out.println ("OFF THE GRID!");
        return -offGridVal;
      }

      if ((incrx > 0) && (midX <= startX) && (startX <= newX) && (y >= startY) && (y <= endY)) {
        System.out.println("CAN'T JUMP OVER WALL!");
        return -hitWallVal;
      } else if ((incrx < 0) && (midX >= startX) && (startX >= newX) && (y >= startY) && (y <= endY)) {
        System.out.println("CAN'T JUMP OVER WALL!");
        return -hitWallVal;
      } else if ((incry > 0) && (y <= startY && startY <= newY && x >= startX && x <= endX)) {
        System.out.println("CAN'T JUMP OVER WALL!");
        return -hitWallVal;
      } else if ((incry < 0) && (y >= startY) && (startY >= newY) && (x >= startX) && (x <= endX)) {
        System.out.println("CAN'T JUMP OVER WALL!");
        return -hitWallVal;
      }
    }

    x += jumpX;
    y += jumpY;
    playerLoc.setLocation(x, y);
    repaint();
    return 0;
  }

  public boolean isTrap(int newx, int newy) {
    double px = playerLoc.getX() + newx;
    double py = playerLoc.getY() + newy;

    for (Rectangle r : traps) {
      if (r.getWidth() > 0) {
        if (r.contains(px, py)) {
          return true;
        }
      }
    }
    return false;
  }

  public int landOnTrap() {
    double px = playerLoc.getX();
    double py = playerLoc.getY();

    for (Rectangle r : traps) {
      if (r.getWidth() > 0 && r.contains(px, py)) {
        System.out.println("YOU LANDED ON A TRAP! Press E to unspring it.");
        return -trapVal;
      }
    }
    return 0;
  }

  public int unspringTrap() {
    double px = playerLoc.getX();
    double py = playerLoc.getY();

    for (Rectangle r : traps) {
      if (r.getWidth() > 0 && r.contains(px, py)) {
        r.setSize(0, 0);
        System.out.println("TRAP UNSPRUNG!");
        repaint();
        return 0;
      }
    }
    System.out.println("NO TRAP HERE TO UNSPRING");
    return 0;
  }

  public int pickupPrize() {
    double px = playerLoc.getX();
    double py = playerLoc.getY();

    for (Rectangle p : prizes) {
      if (p.getWidth() > 0 && p.contains(px, py)) {
        System.out.println("YOU PICKED UP A PRIZE!");
        p.setSize(0, 0);
        repaint();
        return prizeVal;
      }
   
    }

    return 0;

 
    return -prizeVal;  

  }

  public int getSteps() {
    return playerSteps;
  }

  public void setPrizes(int p) {
    totalPrizes = p;
  }

  public void setTraps(int t) {
    totalTraps = t;
  }

  public void setWalls(int w) {
    totalWalls = w;
  }

  public int replay() {
    int win = playerAtEnd();

    for (Rectangle p : prizes)
      p.setSize(SPACE_SIZE / 3, SPACE_SIZE / 3);
    for (Rectangle t : traps)
      t.setSize(SPACE_SIZE / 3, SPACE_SIZE / 3);

    x = START_LOC_X;
    y = START_LOC_Y;
    playerSteps = 0;
    totalScore = 0;
    jumpMode = false;
    repaint();
    return win;
  }

  public int endGame() {
    int win = playerAtEnd();
    setVisible(false);
    frame.dispose();
    return win;
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;

    g.drawImage(bgImage, 0, 0, null);

    for (Rectangle t : traps) {
      g2.setPaint(Color.WHITE);
      g2.fill(t);
    }

    for (Rectangle p : prizes) {
      if (p.getWidth() > 0) {
        int px = (int) p.getX();
        int py = (int) p.getY();
        g.drawImage(prizeImage, px, py, null);
      }
    }

    for (Rectangle r : walls) {
      g2.setPaint(Color.BLACK);
      g2.fill(r);
    }

    g.drawImage(player, x, y, 40, 40, null);
    playerLoc.setLocation(x, y);

    g.setColor(Color.BLACK);
    g.drawString("Score: " + totalScore, 10, 15);
    if (jumpMode) {
      g.drawString("JUMP MODE - Select direction with WASD", 10, 30);
    }
  }

  private void createPrizes() {
    int s = SPACE_SIZE;
    Random rand = new Random();
    for (int numPrizes = 0; numPrizes < totalPrizes; numPrizes++) {
      int h = rand.nextInt(GRID_H);
      int w = rand.nextInt(GRID_W);
      Rectangle r = new Rectangle((w * s + 15), (h * s + 15), 15, 15);
      prizes[numPrizes] = r;
    }
  }

  private void createTraps() {
    int s = SPACE_SIZE;
    Random rand = new Random();
    for (int numTraps = 0; numTraps < totalTraps; numTraps++) {
      int h = rand.nextInt(GRID_H);
      int w = rand.nextInt(GRID_W);
      Rectangle r = new Rectangle((w * s + 15), (h * s + 15), 15, 15);
      traps[numTraps] = r;
    }
  }

  private void createWalls() {
    int s = SPACE_SIZE;
    Random rand = new Random();
    int numWalls = 0;
    while (numWalls < totalWalls) {
      int h = rand.nextInt(GRID_H);
      int w = rand.nextInt(GRID_W);
      Rectangle r;
      if (rand.nextInt(2) == 0) {
        r = new Rectangle((w * s + s - 5), h * s, 8, s);
      } else {
        r = new Rectangle(w * s, (h * s + s - 5), s, 8);
      }
      Rectangle playerStart = new Rectangle(START_LOC_X, START_LOC_Y, 40, 40);
      if (!r.intersects(playerStart)) {
        walls[numWalls] = r;
        numWalls++;
      }
    }
  }

  public void checkFinish() {
    int gridX = x / SPACE_SIZE;
    int gridY = y / SPACE_SIZE;
    
    if (gridX == (GRID_W - 1) && gridY == (GRID_H - 1)) {
      int finalScore = totalScore + endVal;
      showFinishScreen(finalScore);
    } else {
      System.out.println("You must be in the bottom right corner to finish!");
    }
  }
  
  private void showFinishScreen(int finalScore) {
    frame.getContentPane().removeAll();
    
    javax.swing.JPanel finishPanel = new javax.swing.JPanel();
    finishPanel.setLayout(new java.awt.BorderLayout());
    finishPanel.setBackground(Color.BLACK);
    
    javax.swing.JLabel titleLabel = new javax.swing.JLabel("GAME FINISHED!", javax.swing.SwingConstants.CENTER);
    titleLabel.setForeground(Color.WHITE);
    titleLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 36));
    
    javax.swing.JLabel scoreLabel = new javax.swing.JLabel("Final Score: " + finalScore, javax.swing.SwingConstants.CENTER);
    scoreLabel.setForeground(Color.YELLOW);
    scoreLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));
    
    javax.swing.JLabel stepsLabel = new javax.swing.JLabel("Steps taken: " + playerSteps, javax.swing.SwingConstants.CENTER);
    stepsLabel.setForeground(Color.WHITE);
    stepsLabel.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 18));
    
    javax.swing.JPanel centerPanel = new javax.swing.JPanel();
    centerPanel.setLayout(new java.awt.GridLayout(3, 1, 0, 20));
    centerPanel.setBackground(Color.BLACK);
    centerPanel.add(titleLabel);
    centerPanel.add(scoreLabel);
    centerPanel.add(stepsLabel);
    
    javax.swing.JButton exitButton = new javax.swing.JButton("Exit Game");
    exitButton.addActionListener(e -> System.exit(0));
    exitButton.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 16));
    
    javax.swing.JPanel buttonPanel = new javax.swing.JPanel();
    buttonPanel.setBackground(Color.BLACK);
    buttonPanel.add(exitButton);
    
    finishPanel.add(centerPanel, java.awt.BorderLayout.CENTER);
    finishPanel.add(buttonPanel, java.awt.BorderLayout.SOUTH);
    
    frame.add(finishPanel);
    frame.revalidate();
    frame.repaint();
  }

  @Override
  public void keyPressed(KeyEvent e) {
    int key = e.getKeyCode();

    int score = 0;

    if (key == KeyEvent.VK_SPACE) {
      jumpMode = !jumpMode;
      System.out.println(jumpMode ? "JUMP MODE ACTIVATED" : "JUMP MODE DEACTIVATED");
      repaint();
      return;
    }

    if (key == KeyEvent.VK_E) {
      score += unspringTrap();
      if (score != 0) {
        totalScore += score;
        System.out.println("Score change: " + score + " | Total: " + totalScore);
        repaint();
      }
      return;
    }

    if (jumpMode) {
      if (key == KeyEvent.VK_W) {
        score += jumpPlayer(0, -SPACE_SIZE);
        jumpMode = false;
      } else if (key == KeyEvent.VK_A) {
        score += jumpPlayer(-SPACE_SIZE, 0);
        jumpMode = false;
      } else if (key == KeyEvent.VK_S) {
        score += jumpPlayer(0, SPACE_SIZE);
        jumpMode = false;
      } else if (key == KeyEvent.VK_D) {
        score += jumpPlayer(SPACE_SIZE, 0);
        jumpMode = false;
      } else {
        return;
      }
    } else {
      if (key == KeyEvent.VK_W) {
        score += movePlayer(0, -SPACE_SIZE);
      } else if (key == KeyEvent.VK_A) {
        score += movePlayer(-SPACE_SIZE, 0);
      } else if (key == KeyEvent.VK_S) {
        score += movePlayer(0, SPACE_SIZE);
      } else if (key == KeyEvent.VK_D) {
        score += movePlayer(SPACE_SIZE, 0);
      } else {
        return;
      }
    }

    score += landOnTrap();
    score += pickupPrize();

    if (score != 0) {
      totalScore += score;
      System.out.println("Score change: " + score + " | Total: " + totalScore);
      repaint();
    }

      switch (key) {
          case KeyEvent.VK_W -> movePlayer(0, -SPACE_SIZE);
          case KeyEvent.VK_A -> movePlayer(-SPACE_SIZE, 0);
          case KeyEvent.VK_S -> movePlayer(0, SPACE_SIZE);
          case KeyEvent.VK_D -> movePlayer(SPACE_SIZE, 0);
          case KeyEvent.VK_Q -> System.exit(0);
          case KeyEvent.VK_P -> pickupPrize();
         // case KeyEvent.VK_SPACE -> jump();
          default -> {
          }
      }

  }

  @Override
  public void keyReleased(KeyEvent e) {}

  @Override
  public void keyTyped(KeyEvent e) {}

  public boolean hasActiveTrap(int x, int y) {
    for (Rectangle trap : traps) {
        if (trap.getWidth() > 0 && trap.getHeight() > 0) { // not sprung
            if (trap.contains(x, y)) {
                return true;
            }
        }
    }
    return false;
}

public void printTraps() {
    for (Rectangle trap : traps) {
        System.out.println("Trap at: (" + (int)trap.getX() + ", " + (int)trap.getY() + ")");
    }
}
}