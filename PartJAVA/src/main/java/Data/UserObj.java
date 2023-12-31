package Data;

import java.util.HashMap;
import java.util.Map;

public class UserObj {
    public static String username;
    public static String first_name;
    public static String family_name;
    public static String telephone;
    public static String e_mail;

    public static BankAccount account;

    public static Map<String, BankAccount> accountMap = new HashMap<>();

    public static String toStringUserObj(){
        StringBuilder result = new StringBuilder();
        result.append("Username: ").append(username).append('\n');
        result.append("First Name: ").append(first_name).append('\n');
        result.append("Family Name: ").append(family_name).append('\n');
        result.append("Telephone: ").append(telephone).append('\n');
        result.append("E-mail: ").append(e_mail).append('\n');

        if (account != null) {
            result.append("Account: ").append(account).append('\n');
        } else {
            result.append("No account information available").append('\n');
        }

        if (!accountMap.isEmpty()) {
            result.append("Account Map: ").append('\n');
            for (Map.Entry<String, BankAccount> entry : accountMap.entrySet()) {
                result.append(entry.getKey()).append(": ").append(entry.getValue()).append('\n');
            }
        } else {
            result.append("No account map information available").append('\n');
        }

        return result.toString();
    }
}
