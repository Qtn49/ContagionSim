package fr.quentin.contagionsim.model;

import javafx.scene.paint.Color;

import java.util.concurrent.ThreadLocalRandom;

/**
 * This class represents an individual member of the population.
 * @author Quentin Cld
 */
public class Individual {

    /**
     * The chance of death for an individual that is infected with disease
     */
    private double chanceOfDeath = 0.06;

    /**
     * The x position of the individual
     */
    private int x;

    /**
     * The y position of the individual
     */
    private int y;

    /**
     * The direction in x of the individual
     */
    private int dx;

    /**
     * The direction in y of the individual
     */
    private int dy;

    /**
     * The size of the individual
     */
    private int radius;

    /**
     * The x position of the individual as a float
     */
    private double true_x;

    /**
     * The y position of the individual as a float
     */
    private double true_y;

    /**
     * The speed of the individual
     */
    private double speed;

    /**
     * The direction of the individual, as an angle (0 - 360)
     */
    private double direction;

    /**
     * The colour of the individual
     * @see HealthColour
     */
    private Color colour;

    /**
     * The state of the individual
     * @see State
     */
    private State state;

    /**
     * The number of iteration the individual has been infected
     */
    private int timeInfected = 0;
    private double initSpeed;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }

    public double getSpeed() {
        return speed;
    }

    public double getDirection() {
        return direction;
    }

    public Color getColour() {
        return colour;
    }

    public int getRadius() {
        return radius;
    }

    public State getState() {
        return state;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

    public void setDy(int dy) {
        this.dy = dy;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
        initSpeed = speed;
    }

    public void setState(State state) {
        this.state = state;
        this.colour = HealthColour.get(this.state);
    }

    public void setDirection(double direction) {
        this.direction = direction;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void setChanceOfDeath(double chanceOfDeath) {
        this.chanceOfDeath = chanceOfDeath;
    }

    public Individual() {
        radius = 10;

        x = 50;
        y = 50;
        true_x = x;
        true_y = y;

        dx = Math.random() > 0.5? 1: -1;
        dy = Math.random() > 0.5? 1: -1;
        speed = 1;
        initSpeed = 1;
        direction = (int) (Math.random() * 360);

        state = State.HEALTHY;
        colour = HealthColour.get(state);
    }

    public Individual(int max_x, int max_y) {
        radius = 10;

        x = ThreadLocalRandom.current().nextInt(radius + 1, max_x - radius);
        y = ThreadLocalRandom.current().nextInt(radius + 1, max_y - radius);
        true_x = x;
        true_y = y;

        dx = Math.random() > 0.5? 1: -1;
        dy = Math.random() > 0.5? 1: -1;
        speed = 1;
        direction = (int) (Math.random() * 360);

        state = State.HEALTHY;
        colour = HealthColour.get(state);
    }

    public Individual(int max_x, int max_y, State state) {
        radius = 10;

        x = ThreadLocalRandom.current().nextInt(radius + 1, max_x - radius);
        y = ThreadLocalRandom.current().nextInt(radius + 1, max_y - radius);
        true_x = x;
        true_y = y;

        dx = Math.random() > 0.5? 1: -1;
        dy = Math.random() > 0.5? 1: -1;
        speed = 1;
        direction = (int) (Math.random() * 360);

        this.state = state;
        colour = HealthColour.get(state);
    }

    public Individual(int max_x, int max_y, float speed) {
        radius = 10;

        x = ThreadLocalRandom.current().nextInt(radius + 1, max_x - radius);
        y = ThreadLocalRandom.current().nextInt(radius + 1, max_y - radius);
        true_x = x;
        true_y = y;

        dx = Math.random() > 0.5? 1: -1;
        dy = Math.random() > 0.5? 1: -1;
        this.speed = speed;
        direction = (int) (Math.random() * 360);;

        state = State.HEALTHY;
        colour = HealthColour.get(state);
    }

    /**
     * This function is called each step to compute the direction and the distance traveled
     * by an individual.
     */
    public void move() {
        true_x = true_x + speed * Math.cos(direction * Math.PI / 180);
        true_y = true_y + speed * Math.sin(direction * Math.PI / 180);

        x = (int) true_x;
        y = (int) true_y;

    }

    /**
     * Checks if the individual reached a bound of the canvas, and changes the direction
     * accordingly.
     * @param x_limit The width of the canvas (representing the space available
     *                to the individuals).
     * @param y_limit The height of the canvas (representing the space available
     *                to the individuals).
     */
    private void changeDirection(int x_limit, int y_limit) {
        if (y < radius + 1) {
            if (dx == 1)
                direction -= direction * 2;
            else
                direction += (180 - direction) * 2;

            dx -= dx;
        }

        if (y > y_limit - radius - 1) {
            if (dx == 1)
                direction += (360 - direction) * 2;
            else
                direction -= (direction - 180) * 2;

            dx -= dx;
        }

        if (x < radius + 1) {
            if (dy == 1)
                direction += (270 - direction) * 2;
            else
                direction -= (direction - 90) * 2;

            dy -= dy;
        }

        if (x > x_limit - radius - 1) {
            if (dy == 1)
                direction -= (direction - 270) * 2;
            else
                direction += (90 - direction) * 2;

            dy -= dy;
        }
    }

    /**
     * Checks for a collision with another individual. The +1 is here to solve an edge case.
     * @param i2 The other individual.
     * @return True if there is a collision, false otherwise.
     */
    public boolean collideWith(Individual i2) {
        return (Math.sqrt(Math.pow(x - i2.getX(), 2) + Math.pow(y - i2.getY(), 2)) <= (radius / 2.0f) + (i2.getRadius() / 2.0f) + 1);
    }

    /**
     * This function is called each step, for each individual. It takes care of checking the state of the individual,
     * moving it and changing its state.
     * @see #move()
     * @param x_limit The width of the canvas (representing the space available
     *                to the individuals).
     * @param y_limit The height of the canvas (representing the space available
     *                to the individuals).
     */
    public void update(int x_limit, int y_limit, boolean containment) {
        if (state == State.DEAD)
            return;

        move();
        changeDirection(x_limit, y_limit);

        if (state == State.INFECTED || state == State.DIAGNOSED) {
            timeInfected++;
            if (timeInfected == 250) {
                setState(State.DIAGNOSED);
            }

            if (timeInfected == 400) {
                if (Math.random() < chanceOfDeath)
                    setState(State.DEAD);
            }

            if (timeInfected > 500) {
                setState(State.IMMUNE);
            }
        }

        if (containment && speed != initSpeed * 0.1 && (state == State.DIAGNOSED))
            speed = initSpeed * 0.1;

        else if (containment && speed != initSpeed * 0.5 && state == State.IMMUNE)
            speed = initSpeed * 0.5;

        else if (containment && speed > initSpeed * 0.5 && !(state == State.DEAD))
            speed = initSpeed * 0.5;


    }
}
