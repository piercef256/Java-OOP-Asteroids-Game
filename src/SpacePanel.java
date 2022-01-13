// package src;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.List;
import java.util.ArrayList;
import java.util.Timer;

public class SpacePanel extends JPanel implements Runnable {
    static final int GAME_WIDTH = 600;
    static final int GAME_HEIGHT = 600;
    static final Dimension SCREEN_SIZE = new Dimension(GAME_WIDTH, GAME_HEIGHT);
    public List<Asteroid> asteroidList = new ArrayList<Asteroid>();

    Thread gameThread;
    Image image;
    Graphics graphics;
    Random random;

    Asteroid asteroid;
    Asteroid asteroid2;
    int gameTicks = 0;
    Ship ship;

    SpacePanel() {
        addAsteroids(1);
        addShip();
        this.setFocusable(true);
        this.addKeyListener(new KeyboardListener());
        this.setPreferredSize(SCREEN_SIZE);
        asteroidCreationLoop();

        gameThread = new Thread(this);
        gameThread.start();

    }

    public void addShip() {
        ship = new Ship(50, 50);
    }

    public static int getGameWidth() {
        return GAME_WIDTH;
    }

    public void paint(Graphics g) {
        image = createImage(getWidth(), getHeight());
        graphics = image.getGraphics();
        draw(graphics);
        g.drawImage(image, 0, 0, this);
    }

    public void draw(Graphics g) {
        for (int i = 0; i < asteroidList.size(); i++) {
            Asteroid asteroid = asteroidList.get(i);
            asteroid.draw(g);
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(ship.getImage(), ship.getX(),
                ship.getY(), this);

        List<Projectile> projectiles = ship.getProjectiles();

        for (Projectile projectile : projectiles) {
            projectile.draw(g);
        }
    }

    public void updatePosition() {
        for (int i = 0; i < asteroidList.size(); i++) {
            Asteroid asteroid = asteroidList.get(i);

            if (asteroid.getVisibility()) {
                asteroid.updatePosition();
            } else {
                asteroidList.remove(i);
            }
        }
        ship.updatePosition();
        updateProjectiles();
    }

    public void asteroidCreationLoop() {
        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                int numAsteroids = ThreadLocalRandom.current().nextInt(1, 4);
                addAsteroids(numAsteroids);
            }
        }, 0, 1000);
    }

    private void updateProjectiles() {
        List<Projectile> projectiles = ship.getProjectiles();

        for (int i = 0; i < projectiles.size(); i++) {
            Projectile projectile = projectiles.get(i);

            if (projectile.getVisibility()) {
                projectile.updatePosition();
            } else {
                projectiles.remove(i);
            }
        }
    }

    public Asteroid generateRandomAsteroid() {
        int x = ThreadLocalRandom.current().nextInt(0, GAME_WIDTH);
        int y = ThreadLocalRandom.current().nextInt(0, GAME_HEIGHT);
        int size = ThreadLocalRandom.current().nextInt(15, 75);

        return new Asteroid(x, y, size);
    }

    public void addAsteroids(int numAsteroids) {
        for (int i = 0; i < numAsteroids + 1; i++) {
            Asteroid asteroid = generateRandomAsteroid();
            asteroidList.add(asteroid);
        }
    }

    public void run() {
        while (true) {
            updatePosition();
            repaint();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public class KeyboardListener extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            ship.keyPressed(e);
        }

        public void keyReleased(KeyEvent e) {
            ship.keyReleased(e);
        }
    }
}