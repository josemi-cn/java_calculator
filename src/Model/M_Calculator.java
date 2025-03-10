/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author jcalahuche
 */
public class M_Calculator {
    public static Double Add(double number1, double number2) {
        System.out.println("Adding " + number2 + " to " + number1 + "...");
        return number1 + number2;
    }

    public static Double Subtract(double number1, double number2) {
        System.out.println("Subtracting " + number2 + " to " + number1 + "...");
        return number1 - number2;
    }

    public static Double Multiply(double number1, double number2) {
        System.out.println("Multiplying " + number1 + " by " + number2 + "...");
        return number1 * number2;
    }

    public static Double Divide(double number1, double number2) {
        System.out.println("Dividing " + number1 + " by " + number2 + "...");
        return number1 / number2;
    }

    public static Double Remainder(double number1, double number2) {
        System.out.println("Taking remainder of " + number1 + " divided by " + number2 + "...");
        return number1 % number2;
    }

    public static Double Power(double number1, double number2) {
        System.out.println("Taking " + number1 + " to power " + number2 + "...");
        return Math.pow(number1, number2);
    }

    public static Double Root(double number1) {
        System.out.println("Taking root of " + number1 + "...");
        return Math.sqrt(number1);
    }
}
