/*
 * Copyright (c) 2022. Gabriel Sterz @ Sterz-Solutions, Cologne, Germany
 */

package de.sterzsolutions.calculatefx;


public interface Function {
    /**
     * checks whether the function can take so much arguments
     * @param count Arguments count
     */
    public boolean validNrOfArguments( int count );

    /**
     * Calculates the function
     * @param values Arguments which had been tested before with
     *               validNrOfArguments.
     * @return the calculation
     */
    public double calculate( double[] values );
}