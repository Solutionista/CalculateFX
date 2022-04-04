package de.sterzsolutions.calculatefx;

import java.util.*;

/**
 * Calculator can analyze and calculate mathematical expressions
 *
 * */

public class Calculator {
    /**
     * Opening Brackets
     */
    private static final Object OPEN = Character.valueOf( '(' );

    /** Closing Brackets */
    private static final Object CLOSE = Character.valueOf( ')' );

    /** comma */
    private static final Object SEPARATOR = Character.valueOf( ',' );

    /** List of all Operators */
    private Map<String, Operation> operations = new HashMap<String, Operation>();

    /** List of all Functions */
    private Map<String, Function> functions = new HashMap<String, Function>();

    /** List of all Constants */
    private Map<String, Double> constants = new HashMap<String, Double>();

    /**
     * Default Constructor - fills the list of Constants and adds Operations and Functions
     */
    public Calculator(){
        addOperation( "+", new Operation(){
            public int getPriority() {
                return 1;
            }
            public double calculate( double a, double b ) {
                return a+b;
            }
        });

        addFunction( "+", new Function(){
            public double calculate( double[] values ) {
                return values[0];
            }
            public boolean validNrOfArguments( int count ) {
                return count == 1;
            }
        });

        addOperation( "-", new Operation(){
            public int getPriority() {
                return 1;
            }
            public double calculate( double a, double b ) {
                return a-b;
            }
        });

        addFunction( "-", new Function(){
            public double calculate( double[] values ) {
                return -values[0];
            }
            public boolean validNrOfArguments( int count ) {
                return count == 1;
            }
        });

        addOperation( "*", new Operation(){
            public int getPriority() {
                return 2;
            }
            public double calculate( double a, double b ) {
                return a*b;
            }
        });

        addOperation( "/", new Operation(){
            public int getPriority() {
                return 2;
            }
            public double calculate( double a, double b ) {
                return a/b;
            }
        });

        addFunction( "sqrt", new Function(){
            public double calculate( double[] values ) {
                return Math.sqrt( values[0] );
            }

            public boolean validNrOfArguments( int count ) {
                return count == 1;
            }
        });

        addFunction( "min", new Function(){
            public double calculate( double[] values ) {
                return Math.min( values[0], values[1] );
            }
            public boolean validNrOfArguments( int count ) {
                return count == 2;
            }
        });

        constants.put( "e", Math.E );
        constants.put( "pi", Math.PI );
    }

    public void addOperation( String name, Operation operation ){
        operations.put( name, operation );
    }

    public void addFunction( String name, Function function ){
        functions.put( name, function );
    }

    /**
     * Calculates the whole expression
     * @param expression mathematical expression
     * @return expression's result
     * @throws IllegalArgumentException if theres a problem with the expression
     */
    public double calculate(String expression ){

        List<Object> tokens = tokenize( expression );


        int count = 0;
        for( Object token : tokens ){
            if( token == OPEN )
                count++;
            if( token == CLOSE )
                count--;

            if( count < 0 )
                throw new IllegalArgumentException( "!!!ERRROR!!! No opening Bracket for Closing" );
            if( token == SEPARATOR && count == 0 )
                throw new IllegalArgumentException( "!!! ERROR !!!" );
        }

        if( count > 0 )
            throw new IllegalArgumentException( "!!!ERROR!!! closing bracket missing" );

        if( count < 0 )
            throw new IllegalArgumentException( "!!!ERROR!!! closing bracket too much" );

        for( int i = 0, n = tokens.size(); i<n; i++ ){
            Object replacement = constants.get( tokens.get( i ) );
            if( replacement != null ){
                tokens.set( i, replacement );
            }
        }

        int size = tokens.size();
        while( size > 1 ){
            calculate( tokens, 0 );
            if( tokens.size() >= size ){
                throw new IllegalArgumentException(
                        "!!!ERROR!!!" );
            }
            size = tokens.size();
        }

        if( size != 1 || !(tokens.get( 0 ) instanceof Double))
            throw new IllegalArgumentException( "!!!ERROR!!!" );

        return (Double)tokens.get( 0 );
    }

    /**
     * Calculates the expression between two brackets recursive
     * @param expression tokenized expression
     * @param offset of the first opening bracket.
     */
    private void calculate(List<Object> expression, int offset ){
        // open is false, if there are virtual Brackets added to surround the expression
        boolean open = expression.get( offset ) == OPEN;

        if( open ){
            expression.remove( offset );
        }

        int begin = offset;
        int length = 0;

        boolean done = false;

        while( begin+length < expression.size() ){
            Object end = expression.get( begin+length );
            if( end == OPEN ){
                calculate( expression, begin+length );
            }
            else if( end == CLOSE || end == SEPARATOR ){
                if( length == 0 )
                    throw new IllegalArgumentException( "!!!Error!!! Check your expression!" );

                calculate( expression, begin, length );
                expression.remove( begin+1 );
                begin++;
                length = 0;

                if( end == CLOSE ){
                    done = true;
                    break;
                }
            }
            else
                length++;
        }

        if( !done && begin+length == expression.size() ){
            calculate( expression, begin, length );
            begin++;
            length = 0;
        }

        if( offset+1 != begin ){
            double[] value = new double[ begin-offset ];
            for( int i = begin-1; i >= offset; i-- ){
                value[i-offset] = (Double)expression.remove( i );
                begin--;
            }
            expression.add( offset, value );
        }
    }

    /**
     * Calculates all elements of the given Token List from offset to length
     * @param formula The tokenized expression
     * @param offset Begin of Expression
     * @param length End of Expression
     */
    private void calculate(List<Object> formula, int offset, int length ){

        for( int i = offset+length-2; i >= offset; i-- ){
            Function function = functions.get( formula.get( i ) );
            if( function != null ){

                if( i == offset || (!(formula.get( i-1 ) instanceof Double ) && !(formula.get(i-1) instanceof double[]))){
                    Object arguments = formula.get( i+1 );
                    double[] values = null;
                    if( arguments instanceof Double ){
                        values = new double[]{ (Double)arguments };
                    }
                    else if( arguments instanceof double[] ){
                        values = (double[])arguments;
                    }
                    else{
                        throw new IllegalArgumentException( "Wrong count of Arguments found for " + function +
                                ": " + arguments );
                    }

                    if( !function.validNrOfArguments( values.length ))
                        throw new IllegalArgumentException( "Wrong count of Arguments found for " + function +
                                ": " + values.length );

                    formula.remove( i+1 );
                    formula.set( i, function.calculate( values ) );
                    length--;
                }
            }
        }


        for( int i = offset; i < offset+length; i++ ){
            Object check = formula.get( i );
            if( check instanceof String ){
                Operation operation = operations.get( check );
                if( operation == null ){
                    throw new IllegalArgumentException( "Element " + check +
                            " is not an operation" );
                }
                formula.set( i, operation );
            }
        }


        while( length > 1 ){
            int current = length;

            int priority = Integer.MIN_VALUE;
            for( int i = offset; i < offset+length; i++ ){
                Object check = formula.get( i );
                if( check instanceof Operation ){
                    priority = Math.max( priority, ((Operation)check).getPriority() );
                }
            }

            for( int i = offset+1; i < offset+length-1; i++ ){
                Object check = formula.get( i );
                if(check instanceof Operation operation){
                    if( operation.getPriority() == priority ){
                        Object left = formula.get( i-1 );
                        Object right = formula.get( i+1 );

                        if( !(left instanceof Double ))
                            throw new IllegalArgumentException( "Operation not surrounded by numbers" );

                        if( !(right instanceof Double ))
                            throw new IllegalArgumentException( "Operation not surrounded by numbers" );

                        formula.set( i, operation.calculate( (Double)left, (Double)right ) );
                        formula.remove( i+1 );
                        formula.remove( i-1 );
                        i--;
                        length -= 2;
                    }
                }
            }


            if( length == current ){
                throw new IllegalArgumentException( "expression could not be evaluated" );
            }
        }
    }

    /**
     * extracts single parts of the expression as tokens like {@link #OPEN}, {@link #CLOSE} und {@link #SEPARATOR}
     *
     * @param expresssion
     * @return List of Token Objects
     */
    private List<Object> tokenize( String expresssion ){

        int offset = 0;
        int length = expresssion.length();
        List<Object> parts = new ArrayList<Object>();

        Set<String> texts = new HashSet<String>();
        texts.addAll( constants.keySet() );
        texts.addAll( operations.keySet() );
        texts.addAll( functions.keySet() );

        while( offset < length ){
            char current = expresssion.charAt( offset );

            if( !Character.isWhitespace( current )){
                if( current == '(' ){
                    parts.add( OPEN );
                    offset++;
                }
                else if( current == ')' ){
                    parts.add( CLOSE );
                    offset++;
                }
                else if( current == ',' ){
                    parts.add( SEPARATOR );
                    offset++;
                }
                else if( Character.isDigit( current ) || current == '.' ){

                    int end = offset+1;
                    boolean pointSeen = current == '.';

                    while( end < length ){
                        char next = expresssion.charAt( end );
                        if( Character.isDigit( next ))
                            end++;
                        else if( next == '.' && !pointSeen ){
                            pointSeen = true;
                            end++;
                        }
                        else
                            break;
                    }

                    parts.add( Double.parseDouble( expresssion.substring( offset, end ) ) );
                    offset = end;
                }
                else{
                    int bestLength = 0;
                    String best = null;

                    for( String check : texts ){
                        if( expresssion.startsWith( check, offset )){
                            if( check.length() > bestLength ){
                                bestLength = check.length();
                                best = check;
                            }
                        }
                    }

                    if( best == null )
                        throw new IllegalArgumentException( "!!!ERROR!!! check the expression !!!" );

                    offset += bestLength;
                    parts.add( best );
                }
            }
            else
                offset++;
        }
        return parts;
    }
}