package Tools;

public class Password2Hash {
    private static final int Modulo = Integer.parseInt("BAE2001", 16);
    public static int hashPassword(String password) {
        // Convert each character to its ASCII value + 1
        char[] charArray = password.toCharArray();
        int[] asciiValues = new int[charArray.length];
        for (int i = 0; i < charArray.length; i++) {
            asciiValues[i] = (int) charArray[i] + 1;
        }
        // Multiply all the ASCII values together
        long product = 1;
        for (int value : asciiValues) {
            product *= value;
            product %= Modulo;
        }

        return (int) product;
    }
}