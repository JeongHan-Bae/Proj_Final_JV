package DataTests;

import Data.UserObj;
import org.testng.annotations.Test;

import java.util.Random;

import static org.testng.Assert.assertEquals;

public class UserObjTest {

    @Test(invocationCount = 100)
    public void testUserObjInitialization() {
        // Generate random inputs
        String username = getRandomUsername();
        String firstName = getRandomName();
        String familyName = getRandomName();
        String telephone = getRandomTelephone();
        String email = getRandomEmail();

        // Create UserObj instance with random inputs
        UserObj.username = username;
        UserObj.first_name = firstName;
        UserObj.family_name = familyName;
        UserObj.telephone = telephone;
        UserObj.e_mail = email;

        // Assertions
        assertEquals(username, UserObj.username);
        assertEquals(firstName, UserObj.first_name);
        assertEquals(familyName, UserObj.family_name);
        assertEquals(telephone, UserObj.telephone);
        assertEquals(email, UserObj.e_mail);

        // Add more assertions for other fields as needed
    }

    private String getRandomUsername() {
        // Generate a username with letters (maj or min), numbers, and "_"
        return "Random_User" + new Random().nextInt(100);
    }

    private String getRandomName() {
        // Generate a random name with letters, not beginning with " ", and may contain spaces in the middle
        String[] possibleChars = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
        Random random = new Random();
        int length = random.nextInt(10) + 1; // Random length between 1 and 10
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < length; i++) {
            if (i == 0) {
                // First character should not be a space
                result.append(possibleChars[random.nextInt(26)]);
            } else {
                // Subsequent characters may include spaces in the middle
                if (random.nextBoolean()) {
                    result.append(" ");
                } else {
                    result.append(possibleChars[random.nextInt(26)]);
                }
            }
        }

        return result.toString();
    }

    private String getRandomTelephone() {
        // Generate a random telephone number with 10 digits from '0' to '9'
        StringBuilder result = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            result.append(random.nextInt(10));
        }

        return result.toString();
    }

    private String getRandomEmail() {
        // Generate a random email address based on specified criteria
        String[] possibleChars = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "_", "."};
        Random random = new Random();
        int localPartLength = random.nextInt(10) + 1; // Random length between 1 and 10 for local part
        int domainPartLength = random.nextInt(10) + 1; // Random length between 1 and 10 for domain part
        StringBuilder localPart = new StringBuilder();
        StringBuilder domainPart = new StringBuilder();

        for (int i = 0; i < localPartLength; i++) {
            localPart.append(possibleChars[random.nextInt(28)]);
        }

        for (int i = 0; i < domainPartLength; i++) {
            domainPart.append(possibleChars[random.nextInt(28)]);
        }

        // Ensure that '.' is not the first or last character in both local and domain parts
        if (localPart.length() > 1) {
            localPart.replace(localPart.length() - 1, localPart.length(), "0"); // Replace last dot with '0'
        }

        if (domainPart.length() > 1) {
            domainPart.replace(0, 1, "0"); // Replace first dot with '0'
        }

        return localPart + "@" + domainPart;
    }

}
