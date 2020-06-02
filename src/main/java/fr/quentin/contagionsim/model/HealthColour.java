package fr.quentin.contagionsim.model;

import javafx.scene.paint.Color;

/**
 * This class is used to return the colour corresponding to the state of an individual.
 * @author Quentin Cld
 */
public abstract class HealthColour {
    /**
     * Returns the colour corresponding to the state of an individual.
     * @param state The state of the individual.
     * @return The colour of the state.
     */
    public static Color get(State state) {
        if (state == State.HEALTHY)
            return Color.CADETBLUE;
        else if (state == State.INFECTED)
            return Color.LAWNGREEN;
        else if (state == State.DIAGNOSED)
            return Color.DARKRED;
        else if (state == State.DEAD)
            return Color.BLACK;
        else if (state == State.IMMUNE)
            return Color.LIGHTPINK;

        return Color.WHITE;
    }
}
