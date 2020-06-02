package fr.quentin.contagionsim.model;

import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.Collections;

/**
 * This class manages the game (initialising, drawing, balls collision, state update, game update).
 * @author Quentin Cld
 */
public class Game {

    /**
     * Le pourncetage minimal de personnes diagnostiquées positives pour déclencher le confinement
     */
    private final static float CONTAINMENT_MIN_PEOPLE_PERCENTAGE = 0.25f;
    public static final int NBINDIV_DEFAULT = 99;
    public static final int V_INDIV_DEFAULT = 1;
    public static final int T_INDIV_DEFAULT = 10;
    public static final int PERSONNES_INFECTES_DEPART_DEFAULT = 1;
    public static final double TAUX_MORTAL_DEFAULT = 0.06;
    public static final int TAUX_CONTAG_DEFAULT = 1;

    /**
     * Liste des individus
     */
    private ArrayList<Individual> individuals;

    /**
     * Liste des individus morts
     */
    private ArrayList<Individual> deadIndividuals;

    /**
     * Largeur du canvas
     */
    private int width;

    /**
     * Hauteur du canvas
     */
    private int height;

    /**
     * Nombre total d'itération
     */
    private int iter = 0;

    /**
     * Confinement en cours ou non
     */
    private boolean lockdown = false;

    private int nbIndiv, tIndiv, personnesInfectesDepart;
    private double vIndiv, tauxContag,  tauxMortal;

    public Game(int width, int height) {
        this(width, height, NBINDIV_DEFAULT, V_INDIV_DEFAULT, T_INDIV_DEFAULT, PERSONNES_INFECTES_DEPART_DEFAULT, TAUX_CONTAG_DEFAULT, TAUX_MORTAL_DEFAULT);
    }

    public Game(int width, int height, int nbIndiv, double vIndiv, int tIndiv, int personnesInfectesDepart, double tauxContag, double tauxMortal) {
        individuals = new ArrayList<>();
        deadIndividuals = new ArrayList<>();
        this.width = width;
        this.height = height;
        this.nbIndiv = nbIndiv;
        this.vIndiv = vIndiv;
        this.tIndiv = tIndiv;
        this.personnesInfectesDepart = personnesInfectesDepart;
        this.tauxContag = tauxContag;
        this.tauxMortal = tauxMortal;
    }

    public Game(Game game) {
        this(game.width, game.height, game.nbIndiv, game.vIndiv, game.tIndiv, game.personnesInfectesDepart, game.tauxContag, game.tauxMortal);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public ArrayList<Individual> getIndividuals() {
        return individuals;
    }

    public ArrayList<Individual> getDeadIndividuals() {
        return deadIndividuals;
    }

    /**
     * Checks if there is no collision between an existing individual and the one being placed.
     * @param individual The existing individual.
     * @return True if there's no collision, false otherwise.
     */
    private boolean canPlace(Individual individual) {
        for (int j = 0; j < individuals.size(); j++) {
            if (individual.collideWith(individuals.get(j))) {
                return false;
            }
        }

        return true;
    }

    /**
     * Initialise the game (place individuals).
     */
    public void initialise() {
        Individual individual;
        State state = State.INFECTED;

        for (int i = 0; i < nbIndiv; i++) {

            if (i >= personnesInfectesDepart - 1) {
                state = State.HEALTHY;
            }

            do {
                individual = new Individual(width, height, state);
            }while (!canPlace(individual));

            individual.setSpeed(vIndiv);
            individual.setRadius(tIndiv);
            individual.setChanceOfDeath(tauxMortal);
            individuals.add(individual);
        }

    }

    /**
     * Draws the individuals on the canvas, according to their colour and coordinates.
     * @see Individual
     * @param gc The graphics context used to draw the individuals.
     */
    private void drawBallsOnCanvas(GraphicsContext gc) {
        for (Individual individual: deadIndividuals) {
            gc.setFill(individual.getColour());
            gc.fillOval(individual.getX(), individual.getY(), individual.getRadius(), individual.getRadius());
        }

        for (Individual individual: individuals) {
            gc.setFill(individual.getColour());
            gc.fillOval(individual.getX(), individual.getY(), individual.getRadius(), individual.getRadius());
        }
    }

    /**
     * Makes a collision happen between two individuals. Each individual will bounce
     * towards the initial direction of the other one.
     * @param i1 The first individual.
     * @param i2 The second individual.
     */
    private void makeCollision(Individual i1, Individual i2) {
        int tmp_dx, tmp_dy;
        double tmp_direction;

        tmp_dx = i1.getDx();
        tmp_dy = i1.getDy();
        tmp_direction = i1.getDirection();

        i1.setDx(i2.getDx());
        i1.setDy(i2.getDy());
        i1.setDirection(i2.getDirection());

        i2.setDx(tmp_dx);
        i2.setDy(tmp_dy);
        i2.setDirection(tmp_direction);
    }

    /**
     * Updates the state of the individuals when a collision happens.
     * @param i1 The first individual.
     * @param i2 The second individual.
     */
    private void checkHealth(Individual i1, Individual i2) {
        if (i1.getState() == State.INFECTED && i2.getState() == State.INFECTED)
            return;
        if (i1.getState() == State.IMMUNE || i2.getState() == State.IMMUNE)
            return;

        if ((i1.getState() == State.INFECTED || i1.getState() == State.DIAGNOSED) && i2.getState() == State.HEALTHY && Math.random() < tauxContag) {
            i2.setState(State.INFECTED);
        }

        if ((i2.getState() == State.INFECTED || i2.getState() == State.DIAGNOSED) && i1.getState() == State.HEALTHY && Math.random() < tauxContag) {
            i1.setState(State.INFECTED);
        }

    }

    /**
     * Checks if a collision occurred between some individuals. An ArrayList is used in order
     * to reduce the number of collision checking.
     */
    private void checkForCollision() {
        ArrayList<Integer> alreadyDoneIndex = new ArrayList<>();

        for (int i = 0; i < individuals.size(); i++) {
            if (!alreadyDoneIndex.contains(i)) {
                for (int j = 0; j < individuals.size(); j++) {
                    if (i != j) {
                        if (individuals.get(i).collideWith(individuals.get(j))) {
                            makeCollision(individuals.get(i), individuals.get(j));
                            checkHealth(individuals.get(i), individuals.get(j));
                            alreadyDoneIndex.add(j);
                        }
                    }
                }
            }
        }
    }

    /**
     * Updates the game at each step.
     * @see Individual#update(int, int, boolean)
     * @param gc The graphics context used to draw the individuals.
     */
    public void update(GraphicsContext gc) {
        ArrayList<Integer> toRemove = new ArrayList<>();
        int diagnosedCases = 0;

        for (int i = 0; i < individuals.size(); i++) {
            individuals.get(i).update(width, height, lockdown);

            if (individuals.get(i).getState() == State.DEAD) {
                toRemove.add(i);
            }

            if (individuals.get(i).getState() == State.DIAGNOSED ||
                individuals.get(i).getState() == State.IMMUNE ||
                individuals.get(i).getState() == State.DEAD) {

                diagnosedCases++;
            }
        }

        toRemove.sort(Collections.reverseOrder());

        for (int idx: toRemove) {
            deadIndividuals.add(individuals.get(idx));
            individuals.remove(idx);
        }

        if (!lockdown && (float) diagnosedCases / (float) individuals.size() > CONTAINMENT_MIN_PEOPLE_PERCENTAGE)
            lockdown = true;

        checkForCollision();

        gc.clearRect(0, 0, width, height);
        drawBallsOnCanvas(gc);
        iter++;
    }
}
