/*
 * Copyright (c) 2022. Gabriel Sterz @ Sterz-Solutions, Cologne, Germany
 */

package de.sterzsolutions.calculatefx;


public interface Operation{
    /**
     * checks the priority of the operation. higher priority comes first in calculation
     * @return Priority
     */
    public int getPriority();

    /**
     * Calculates the operation
     * @param a
     * @param b
     * @return result of calculation
     */
    public double calculate( double a, double b );
}