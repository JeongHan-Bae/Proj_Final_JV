package DataTests;

import Data.Investment;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

public class InvestmentTest {

    @Test(invocationCount = 30)
    public void testInitializeInvestmentWithCoin() {
        Random random = new Random();
        // Generate random values for the test
        float randomCoin = random.nextFloat() * 100;
        String investInfos = "coin " + randomCoin + getCompanyInvestments(random);

        // Create an Investment object
        Investment investment = new Investment(investInfos);

        // Assert the values are correctly initialized
        assertEquals(investment.coins, randomCoin);

        // Check that investMap doesn't contain "coin" entry
        assertFalse(investment.investMap.containsKey("coin"));
    }

    @Test(invocationCount = 30)
    public void testInitializeInvestmentWithoutCoin() {
        Random random = new Random();
        // Generate random values for the test without "coin" entry
        String investInfos = getCompanyInvestments(random).substring(1);

        // Create an Investment object
        Investment investment = new Investment(investInfos);

        // Assert that coins are initialized to 0.0 when "coin" entry is not present
        assertEquals(investment.coins, 0.0f);
    }

    @Test(invocationCount = 40)
    public void testCompareInvestMap() {
        Random random = new Random();
        // Generate random values for the test
        String investInfos = getCompanyInvestments(random);

        // Create an Investment object
        Investment investment = new Investment("coin 0.0" + investInfos);

        // Create an expected map for comparison
        Map<String, Float> expectedMap = new HashMap<>();
        String[] investments = investInfos.split(",");
        for (String investmentEntry : investments) {
            String[] parts = investmentEntry.trim().split(" ");
            if (parts.length >= 2) {
                String key = parts[0];
                float value = Float.parseFloat(parts[1]);
                expectedMap.put(key, value);
            }
        }

        // Assert that the investMap is equal to the expectedMap
        assertEquals(investment.investMap, expectedMap);
    }

    private String getCompanyInvestments(Random random) {
        StringBuilder companyInvestments = new StringBuilder();

        int numberOfCompanies = random.nextInt(18) + 1; // Random number between 1 and 18

        for (int i = 1; i <= numberOfCompanies; i++) {
            companyInvestments.append(",company_").append(i).append(" ").append(random.nextFloat() * 100);
        }

        return companyInvestments.toString();
    }
}
