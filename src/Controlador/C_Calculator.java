/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Model.M_Calculator;
import Vista.V_Calculator;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.util.Arrays;
import java.util.List;
import java.util.regex.*;
import javax.swing.JButton;

/**
 *
 * @author jcalahuche
 */
public class C_Calculator implements ActionListener {
    
    V_Calculator v_cal;
    Boolean result_showed;
    String baseSimbolRegex = "[\\+\\-\\×\\÷\\%\\^\\√]";
    String baseParenthesisRegex = "[[[0-9].][\\+\\-\\×\\÷\\%\\^\\√]]";
    
    public C_Calculator() {
        v_cal = new V_Calculator();
        
        JButton[] buttons = v_cal.getButtons();
        
        for (JButton button : buttons) {
            button.addActionListener(this);
        }
        
        v_cal.getScreen().addActionListener(this);
        v_cal.getScreen().addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                switch (e.getKeyChar()) {
                    case '*':
                        e.setKeyChar('×');
                        break;
                    case '/':
                        e.setKeyChar('÷');
                        break;
                    case ',':
                        e.setKeyChar('.');
                        break;
                    case '\u0008':
                        if (result_showed == true) {
                            e.setKeyChar('\u0000');
                            v_cal.getScreen().setText("");
                        } else {
                            e.setKeyChar('\u0008');
                        }   break;
                    case '\u007F':
                        if (result_showed == true) {
                            e.setKeyChar('\u0000');
                            v_cal.getScreen().setText("");
                        } else {
                            e.setKeyChar('\u007F');
                        }   break;
                    case '\u2386':
                        e.setKeyChar('\u0000');
                        calculate();
                        break;
                    default:
                        break;
                }
            }
        });
        
        v_cal.getScreen().requestFocus();
        
        v_cal.getContentPane().setBackground(new Color(25, 25, 25));
        result_showed = false;
    }

    public void run() {
        v_cal.setTitle("Calculator");
        v_cal.setLocationRelativeTo(null);
        v_cal.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            switch (e.getActionCommand()) {
                case "=":
                    calculate();
                    break;
                case "C":
                    v_cal.getScreen().setText("");
                    result_showed = false;
                    break;
                case "←":
                    if (result_showed == true) {
                        v_cal.getScreen().setText("");
                    } else {
                        v_cal.getScreen().setText(v_cal.getScreen().getText().replaceAll(".$", ""));
                    }
                    break;
                default:
                    insertarCaracter(e.getActionCommand());
                    break;
            }
            if (e.getModifiers() == 0) {
                calculate();
            }
        } catch (NumberFormatException exNF) {
            v_cal.getScreen().setText("Syntax Error");
        }
        
    }

    public void insertarCaracter(String caracter) {
        if (result_showed == true) {
            v_cal.getScreen().setText(caracter);
            result_showed = false;
        } else {
            v_cal.getScreen().setText("" + v_cal.getScreen().getText() + caracter);
        }
    } 

    public void calculate() {
        String operation = v_cal.getScreen().getText();
        v_cal.getScreen().setText(calculate(operation, true));
        result_showed = true;
    }
    
    public String calculate(String operation, boolean timer) {
        /*
            This take me too long to figure it out, both for
            the text recognition system and the calculation inself.
        
            To make it quick, this recursive function gets the operation text
            and filters it to make it more easier to handle it...
        
            The program treats the text in 6 (7) phases in the following order:
                0. Parenthesis: This is the recursive part of the function,
                                not a big mystery, filters the main text using
                                Regex to get only the inside of the parenthesis
                                and sends it to "inself" to calculate it and
                                receives the end result, and yeah, this also
                                works for parethesis inside of parenthesis.
                1. Split numbers and operators: This is also easy to explain,
                                                all this program it's based on
                                                identify operators to figure
                                                out what operation is required
                                                and the numbers are choosed
                                                using the operator array
                                                position and the same +1
                                                (This last rule it's not
                                                universal tho)
                2. Negatives: This part turns all the numbers to negative,
                              it's necessary to do this early since it could
                              cause problems on the calculation.
                3. Square root: In this phase it calculates the square root, 
                                and yeah, tecnically it should go together with 
                                the powers, but they work a little different, 
                                so I decided to split them in two phases.
                4. Powers: Now it's time for the powers same modus operandi.
                           At first the idea was to make it only power
                           to square, like the root phase, but after giving it
                           some thought I decided it will be better to do it
                           free choose for a more flexible functionality.
                5. Multiply and divide: Multiplications and divisions go next,
                                        no need any explanation for this one.
                6. Add and subtract: Same as before, not need explanation.
        */
        
        /* Tarea: Añadir medidor de tiempo, recuerda tambien que solo debe
           funcionar si timer = true.
        */
        long startTime = System.currentTimeMillis();
        
        // Parenthesis
        String regex = "(\\((?<=\\()(?:[" + baseParenthesisRegex + "]+|\\([^)]+\\))+(?=\\))\\)*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(operation);
                
        while (matcher.find()) {
            String parOperation = matcher.group(0);
            operation = operation.replace(matcher.group(0), calculate(parOperation.substring(1, parOperation.length() - 1), false));
        }
        // --------------------------------------------------------------------
        
        // Split numbers and operators
        String[] numbers = operation.split(baseSimbolRegex);
        String operantString = operation.replaceAll("[[0-9].]", "");
        String[] operants = operantString.split("");
        
        List<String> operantList = Arrays.asList(operants);
        // --------------------------------------------------------------------

        double result = 0;
        String textResult = "";
        int position = -1;
        
        // Negative values
        if (operantList.contains("-") == true) {
            while (Arrays.stream(numbers).anyMatch(""::equals)) {
                for (int i = 0; i < operants.length; i++) {
                    if (numbers[i].length() == 0) {
                        textResult = "-" + numbers[i + 1];
                        position = i;
                        break;
                    }
                }
                
                if (position != -1) {
                    operants = updateOperants(operants, position);
                    
                    String[] newnumbers = null;
                    if (numbers.length == 1) {
                        newnumbers = new String[numbers.length];
                    } else {
                        newnumbers = new String[numbers.length-1];
                    }
                    int nnposition = 0;
                    for (int y = 0; y < numbers.length; y++) {
                        if (y != position && y != position + 1) {
                            newnumbers[nnposition] = numbers[y];
                            nnposition++;
                        } else if (y == position + 1) {
                            newnumbers[nnposition] = textResult;
                            nnposition++;
                        } 
                    }
                    numbers = newnumbers;
                    position = -1;
                }
            }
        }
        // --------------------------------------------------------------------
        
        // Square root
        while (operantList.contains("√") == true) {
            for (int i = 0; i < operants.length; i++) {
                switch (operants[i]) {
                    case "√":
                        result = M_Calculator.Root(Double.parseDouble(numbers[i + 1]));
                        position = i;
                        break;
                }

                if (position != -1) {
                    operants = updateOperants(operants, position);

                    String[] newnumbers = null;
                    if (numbers.length == 1) {
                        newnumbers = new String[numbers.length];
                    } else {
                        newnumbers = new String[numbers.length-1];
                    }
                    int nnposition = 0;
                    for (int y = 0; y < numbers.length; y++) {
                        if (y != position && y != position + 1) {
                            newnumbers[nnposition] = numbers[y];
                            nnposition++;
                        } else if (y == position + 1) {
                            newnumbers[nnposition] = "" + result;
                            nnposition++;
                        } 
                    }
                    numbers = newnumbers;
                    position = -1;
                    operantList = Arrays.asList(operants);
                    break;
                }
            }
        }
        // --------------------------------------------------------------------
        
        // Powers
        while (operantList.contains("^") == true) {
            for (int i = 0; i < operants.length; i++) {
                switch (operants[i]) {
                    case "^":
                        result = M_Calculator.Power(Double.parseDouble(numbers[0 + i]), Double.parseDouble(numbers[1 + i]));
                        position = i;
                        break;
                }
                
                if (position != -1) {
                    numbers = updateNumbers(numbers, position, result);
                    operants = updateOperants(operants, position);
                    position = -1;
                    operantList = Arrays.asList(operants);
                    break;
                }
            }
        }
        // --------------------------------------------------------------------
        
        // Multiply y divide
        while (operantList.contains("×") == true || operantList.contains("÷") == true || operantList.contains("%") == true) {
            for (int i = 0; i < operants.length; i++) {
                switch (operants[i]) {
                    case "×":
                        result = M_Calculator.Multiply(Double.parseDouble(numbers[0 + i]), Double.parseDouble(numbers[1 + i]));
                        position = i;
                        break;
                    case "÷":
                        result = M_Calculator.Divide(Double.parseDouble(numbers[0 + i]), Double.parseDouble(numbers[1 + i]));
                        position = i;
                        break;
                    case "%":
                        result = M_Calculator.Remainder(Double.parseDouble(numbers[0 + i]), Double.parseDouble(numbers[1 + i]));
                        position = i;
                        break;
                }
                if (position != -1) {
                    numbers = updateNumbers(numbers, position, result);
                    operants = updateOperants(operants, position);
                    position = -1;
                    operantList = Arrays.asList(operants);
                    break;
                }
            }
        }
        // --------------------------------------------------------------------

        // Add y substract
        while (operantList.contains("+") == true || operantList.contains("-") == true) {
            for (int i = 0; i < operants.length; i++) {
                switch (operants[i]) {
                    case "+":
                        result = M_Calculator.Add(Double.parseDouble(numbers[0]), Double.parseDouble(numbers[1]));
                        position = i;
                        break;
                    case "-":
                        result = M_Calculator.Subtract(Double.parseDouble(numbers[0]), Double.parseDouble(numbers[1]));
                        position = i;
                        break;
                }
                if (position != -1) {
                    numbers = updateNumbers(numbers, position, result);
                    operants = updateOperants(operants, position);
                    position = -1;
                    operantList = Arrays.asList(operants);
                    break;
                }
            }   
        }
        // --------------------------------------------------------------------
        
        long endTime = System.currentTimeMillis() - startTime;
        if (timer = true) {
            System.out.println("The operation was completed in " + endTime + "ms.");
        }
        return numbers[0];
    }
    
    public String[] updateNumbers(String[] numbers, int position, double result) { 
        String[] newnumbers;
        
        if (numbers.length == 1) {
            newnumbers = new String[numbers.length];
        } else {
            newnumbers = new String[numbers.length-1];
        }
        
        int nnposition = 0;
        
        for (int y = 0; y < numbers.length; y++) {
            if (y != position && y != position + 1) {
                newnumbers[nnposition] = numbers[y];
                nnposition++;
            } else if (y == position) {
                newnumbers[nnposition] = "" + result;
                nnposition++;
            }
        }
        
        return newnumbers;
    }
    
    public String[] updateOperants(String[] operants, int position) {
        String[] newoperants;
        
        if (operants.length == 1) {
            newoperants = new String[operants.length];
        } else {
            newoperants = new String[operants.length-1];
        }
        
        if (operants.length != 0) {
            int noposition = 0;
            for (int y = 0; y < operants.length; y++) {
                if (y != position) {
                    newoperants[noposition] = operants[y];
                    noposition++;
                }
            }
        }
        
        return newoperants;
    }
}