package it.polito.extgol;

import jakarta.persistence.*;

@Embeddable
public class CellState {
    protected Boolean isAlive;
    protected Integer lifepoints;
    protected CellMood cellMood;

    /**
     * Protected no-arg constructor required by JPA.
     */
    protected CellState(){ }

    /**
     * Constructor of cellState
     *
     * @param isAlive true or false depending on the cell
     * @param lifepoints the lifepoints of the cell in that generation
     * @param mood the mood of that cell at that moment
     */
    public CellState(boolean isAlive, Integer lifepoints, CellMood mood){
        this.isAlive = isAlive;
        this.lifepoints = lifepoints;
        cellMood = mood;
    }

    /**
     * Getter for the alive state of the cell
     *
     * @return true if alive false if dead
     */
    public boolean checkAlive(){
        return isAlive;
    }

    /**
     * Getter for the lifepoints of the cell
     *
     * @return the lifepoints of the cell in that moment
     */
    public Integer getLifepoints(){
        return lifepoints;
    }

    /**
     * Getter for the mood of the cell
     *
     * @return the mood of the cell in that moment
     */
    public CellMood getCellMood(){
        return cellMood;
    }

}
