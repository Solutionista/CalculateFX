package de.sterzsolutions.calculatefx;

import javafx.scene.control.Label;
import com.jfoenix.controls.JFXTextArea;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

public class CalculatorControl {

    static boolean comma = false;
    static boolean operator = false;
    static int operatorCount = 0;
    static boolean bracket = false;
    static int bracketsOpen = 0;
    static boolean number = false;
    static Integer numberCount = 0;

    @FXML
    private JFXTextArea mainDisplay;

    @FXML
    private Label errorLabel;

    /**
     * deletes the last entry from the {@link #mainDisplay}
     * @param event
     */
    @FXML
    void delete(MouseEvent event){
        errorLabel.setVisible(false);
        if(mainDisplay.getText().length() == 0){
            errorLabel.setText("!!!ERROR!!! There is nothing to delete");
            errorLabel.setVisible(true);
        } else {
            String deleted = mainDisplay.getText().substring(0, mainDisplay.getText().length()-1);
            mainDisplay.setText(deleted);
        }
    }

     /**
     * sets single numbers
     */
    @FXML
    void setNumber(MouseEvent event){
        if (numberCount <= 12){
            errorLabel.setVisible(false);
            Label buttonClicked = (Label) event.getSource();
            String charValue = buttonClicked.getText();
            setMainDisplay(charValue);
            number = true;
            numberCount++;
            bracket = false;
            operator = false;
        }else{
            errorLabel.setText("SORRY ONLY 12 DIGITS POSSIBLE");
            errorLabel.setVisible(true);
        }
    }


    /**
     * sets opening brackets
     */
    @FXML
    void setBracketsOpen(MouseEvent event) {
        operatorCount = 0;
        errorLabel.setVisible(false);
        bracketsOpen += 1;
        bracket = true;
        comma = false;
        number = false;
        numberCount = 0;
        setMainDisplay("(");
    }

    /**
     * sets closing brackets
     */
    @FXML
    void setBracketsClose(MouseEvent event) {
        if (bracketsOpen == 0 || bracket || operator || operatorCount == 0){
            errorLabel.setText("!!!ERROR!!! Please Check your Entry !!!ERROR!!!");
            errorLabel.setVisible(true);
        } else {
            errorLabel.setVisible(false);
            setMainDisplay(")");
            bracketsOpen -= 1;
            bracket = true;
            number = false;
            numberCount = 0;
            comma=false;
        }

    }

    /**
     * sets the comma in a number
     */
    @FXML
    void setComma(MouseEvent event) {
        if (operator || bracket || comma || mainDisplay.getText().length() == 0){
            errorLabel.setText("!!!ERROR!!! Please Check your Entry !!!ERROR!!!");
            errorLabel.setVisible(true);
        } else {
            setMainDisplay(".");
            number = true;
            numberCount++;
            comma = true;
            bracket = false;
            errorLabel.setVisible(true);
        }

    }

    /**
     * sets the Operator pressed
     *
     */
    @FXML
    void setOperator(MouseEvent event) {
        if (operator){
            errorLabel.setText("!!!ERROR!!! Please Check your Entry !!!ERROR!!!");
            errorLabel.setVisible(true);
        } else {
            Label l = (Label) event.getSource();
            setMainDisplay(l.getText());
            operator = true;
            operatorCount++;
            number = false;
            numberCount = 0;
            comma= false;
        }

    }

    /**
     * Adds a given String to the {@link #mainDisplay}
     * @param toSet
     */
    void setMainDisplay(String toSet){
        mainDisplay.setText(mainDisplay.getText() + toSet);
    }

    /**
     * passes the expression from the {@link #mainDisplay} to the calculator
     * and sets the result to the {@link #mainDisplay}
     */
    @FXML
    void result(MouseEvent event) {
        if(bracketsOpen != 0){
            errorLabel.setText("!!!ERROR!!! " + bracketsOpen + " Brackets to be closed!!!");
        } else {
            Calculator calculator = new Calculator();
            String calculation = mainDisplay.getText();
            double result = calculator.calculate(calculation);
            mainDisplay.setText(String.valueOf(result));
        }
    }

}
