import java.util.*;

/*
A command line program that takes operations on fractions as an input and produce a fractional result:

    - Legal operators are: *, /, +, - (multiply, divide, add, subtract)
    - Operands and operators are separated by one or more spaces
    - Mixed numbers are represented by whole_numerator/denominator. e.g. "3_1/4"
    - Improper fractions and whole numbers are also allowed as operands 

Example run:
    ? 1/2 * 3_3/4
    = 1_7/8

    ? 2_3/8 + 9/8
    = 3_1/2

Assumptions:
    - There is only one operator in the computation.
    - Operands are all +ve, but a result can be -ve
*/

// The following class stores whole_numerator/denominator as n/d so that
// subsequent operations can be performed easily:

class Fraction {
    Integer n;      // numerator
    Integer d;      // denominator

    public Fraction (Integer whole, Integer numerator, Integer denominator) {

        this.n = (whole * denominator) + numerator;
        this.d = denominator;
    }

    public String toString() {

        Integer quotient = n/d;
        Integer remainder = n%d;

        String resultStr = null;
        if (remainder == 0) {
            resultStr = "" + quotient;
        } else {
            if (quotient == 0) {
                resultStr = remainder + "/" + d;
            } else {
                resultStr = quotient + "_" + remainder + "/" + d;
            }
            
        }
        return resultStr;
    }
}

public class OperateOnFractions {

    // parseOperand() parses:
    // -> 4 => 4_0/1
    // -> 3/8 => 0_3/8
    // -> 4_3/8 => 4_3/8
    
    private static Fraction parseOperand(String operand) {

        String[] split1 = operand.split("_");
        if (split1.length > 2) {
            System.out.println("Invalid operand: " + operand);
            return null;
        }

        String wStr;
        String nStr;
        String dStr;

        if (split1.length == 1) {
            if (split1[0].contains("/")) { // e.g. case of "3/8"
                wStr = "0";
                String[] split2 = split1[0].split("/");
                if (split2.length != 2) {
                    System.out.println("Invalid operand: " + operand);
                    return null;
                }
                nStr = split2[0];
                dStr = split2[1];
            } else { // e.g. case of "4"
                wStr = split1[0];
                nStr = "0";
                dStr = "1";
            }
        } else { // e.g. case of "4_3/8"
            wStr = split1[0];
            String[] split2 = split1[1].split("/");
            if (split2.length != 2) {
                System.out.println("Invalid operand: " + operand);
                return null;
            }
            nStr = split2[0];
            dStr = split2[1];
        }

        Integer wInt;
        Integer nInt;
        Integer dInt;
        try {
            wInt = Integer.parseInt(wStr);
            nInt = Integer.parseInt(nStr);
            dInt = Integer.parseInt(dStr);
        } catch(Exception e) { // make sure all w, n, and d strings are integers
            System.out.println("Invalid operand: " + operand);
            return null;
        }

        if (dInt == 0) {
            System.out.println("Invalid operand, denominator is 0: " + operand);
            return null;
        }

        return new Fraction(wInt, nInt, dInt);
    }

    private static Fraction computeMultiply(Fraction operand1, Fraction operand2) {
        Integer resultN = operand1.n * operand2.n;
        Integer resultD = operand1.d * operand2.d;
        return new Fraction(0, resultN, resultD);
    }

    private static Fraction computeDivide(Fraction operand1, Fraction operand2) {
        Integer resultN = operand1.n * operand2.d;
        Integer resultD = operand1.d * operand2.n;
        if (resultD == 0) {
            System.out.println("Division with denominator as 0: ");
            return null;
        }
        return new Fraction(0, resultN, resultD);
    }

    private static Fraction computePlus(Fraction operand1, Fraction operand2) {
        Integer resultN = (operand1.n * operand2.d) + (operand2.n * operand1.d);
        Integer resultD = operand1.d * operand2.d;
        return new Fraction(0, resultN, resultD);
    }

    private static Fraction computeMinus(Fraction operand1, Fraction operand2) {
        Integer resultN = (operand1.n * operand2.d) - (operand2.n * operand1.d);
        Integer resultD = operand1.d * operand2.d;
        return new Fraction(0, resultN, resultD);
    }

    // Parse a compute command line, and call appropriate computeMultiply/Divide/Plus/Minus functions

    private static String compute(String[] commandAndArgs) {
        if (commandAndArgs.length != 4) {
            System.out.println("Invalid number of arguments, should be '? <operand> <operator> <operand>'");
            return null;
        }
        Fraction operand1 = parseOperand(commandAndArgs[1]);
        if (operand1 == null) {
            return null;
        }

        String operator = commandAndArgs[2];

        Fraction operand2 = parseOperand(commandAndArgs[3]);
        if (operand2 == null) {
            return null;
        }

        Fraction computeResult = null;

        if (operator.equals("*")) {
            computeResult = computeMultiply(operand1, operand2);
        } else if (operator.equals("/")) {
            computeResult = computeDivide(operand1, operand2);
        } else if (operator.equals("+")) {
            computeResult = computePlus(operand1, operand2);
        } else if (operator.equals("-")) {
            computeResult = computeMinus(operand1, operand2);
        } else {
            System.out.println("Invalid operator, valid operators are: *, /, +, -");
            return null;
        }

        if (computeResult == null) {
            return null;
        }

        return computeResult.toString();
    }
    
    // Read commands from stdin:
    // - a command can either begin with "?" or with "end"
    // - ? => compute
    // - end => end of reading from stdin
    // - blank lines and lines starting with '#' are ignored.

    public static void main(String[] args) {
        
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String nextLine = scanner.nextLine();
            System.out.println(nextLine);
            if (nextLine.length() == 0 || nextLine.charAt(0) == '#') {
                continue;
            }
            String[] commandAndArgs = nextLine.split("\\s+");
            if (commandAndArgs[0].equalsIgnoreCase("END")) {
                break;
            } else if (commandAndArgs[0].equals("?")) {
                String result = compute(commandAndArgs);
                if (result != null) {
                    System.out.println("= " + result);
                }
            } else {
                System.out.println("Invalid start word, should be END or ?");
                continue;
            }
        }
    }
}
