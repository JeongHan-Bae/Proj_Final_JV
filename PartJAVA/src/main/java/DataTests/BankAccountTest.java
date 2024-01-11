package DataTests;

import Data.BankAccount;
import org.testng.annotations.Test;

import java.util.Random;

import static org.testng.Assert.assertEquals;

public class BankAccountTest {

    @Test(invocationCount = 100)
    public void testBankAccountInitialization() {
        // Generate random inputs
        float currency = getRandomFloat();
        float deposit = getRandomFloat();
        float debt = getRandomFloat();
        float coins = getRandomFloat();
        float stock = getRandomFloat();
        float bond = getRandomFloat();

        // Create BankAccount instance with random inputs
        String bankAccountInfo = currency + " " + deposit + " " + debt + "|coin " + coins + ",stock " + stock + ",bond " + bond;
        BankAccount bankAccount = new BankAccount(bankAccountInfo);

        // Assertions
        assertEquals(currency, bankAccount.currency);
        assertEquals(deposit, bankAccount.deposit);
        assertEquals(debt, bankAccount.debt);

        assertEquals(coins, bankAccount.investment.coins);
        assertEquals(stock, bankAccount.investment.investMap.get("stock"));
        assertEquals(bond, bankAccount.investment.investMap.get("bond"));
    }

    private float getRandomFloat() {
        Random random = new Random();
        return random.nextFloat() * 100; // Adjust as needed
    }
}
