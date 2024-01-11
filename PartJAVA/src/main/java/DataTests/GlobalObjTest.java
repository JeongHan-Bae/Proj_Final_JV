package DataTests;

import Data.GlobalObj;
import org.testng.annotations.Test;

import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class GlobalObjTest {

    @Test
    public void testInitializeClass() {
        // Example input combining all fields
        String globInfos = "JohnDoe 55.5,KateSmith 101.0;JohnSmith 5.2,KateDoe 10.0;7.8;" +
                "01/12/2023 7.1,02/12/2023 7.2,03/12/2023 7.8;" +
                "company_a 23.73646,company_b 24.71958," +
                "company_c 18.94356,company_d 16.27063,company_e 12.83953;" +
                "01/12/2023:company_a 22.933647,company_b 23.994957," +
                "company_c 19.535602,company_d 15.375431,company_e 11.553673|" +
                "02/12/2023:company_a 23.73646,company_b 24.71958," +
                "company_c 18.94356,company_d 16.27063,company_e 12.83953";

        GlobalObj.initializeClass(globInfos);

        // Assert the values are correctly initialized
        assertEquals(GlobalObj.coin, 7.8f);

        assertEquals(GlobalObj.saleCoins.size(), 2);
        assertEquals(GlobalObj.saleCoins.get("JohnDoe"), 55.5f);
        assertEquals(GlobalObj.saleCoins.get("KateSmith"), 101.0f);

        assertEquals(GlobalObj.buyCoins.size(), 2);
        assertEquals(GlobalObj.buyCoins.get("JohnSmith"), 5.2f);
        assertEquals(GlobalObj.buyCoins.get("KateDoe"), 10.0f);

        assertEquals(GlobalObj.coinMap.size(), 3);
        assertEquals(GlobalObj.coinMap.get("01/12/2023"), 7.1f);
        assertEquals(GlobalObj.coinMap.get("02/12/2023"), 7.2f);
        assertEquals(GlobalObj.coinMap.get("03/12/2023"), 7.8f);

        assertEquals(GlobalObj.currDataMap.size(), 5);
        assertEquals(GlobalObj.currDataMap.get("company_a"), 23.73646f);
        assertEquals(GlobalObj.currDataMap.get("company_b"), 24.71958f);
        assertEquals(GlobalObj.currDataMap.get("company_c"), 18.94356f);
        assertEquals(GlobalObj.currDataMap.get("company_d"), 16.27063f);
        assertEquals(GlobalObj.currDataMap.get("company_e"), 12.83953f);

        assertEquals(GlobalObj.dataMap.size(), 2);
        assertTrue(GlobalObj.dataMap.containsKey("01/12/2023"));
        assertTrue(GlobalObj.dataMap.containsKey("02/12/2023"));

        Map<String, Float> innerMap1 = GlobalObj.dataMap.get("01/12/2023");
        assertEquals(innerMap1.size(), 5);
        assertEquals(innerMap1.get("company_a"), 22.933647f);
        assertEquals(innerMap1.get("company_b"), 23.994957f);
        assertEquals(innerMap1.get("company_c"), 19.535602f);
        assertEquals(innerMap1.get("company_d"), 15.375431f);
        assertEquals(innerMap1.get("company_e"), 11.553673f);

        Map<String, Float> innerMap2 = GlobalObj.dataMap.get("02/12/2023");
        assertEquals(innerMap2.size(), 5);
        assertEquals(innerMap2.get("company_a"), 23.73646f);
        assertEquals(innerMap2.get("company_b"), 24.71958f);
        assertEquals(innerMap2.get("company_c"), 18.94356f);
        assertEquals(innerMap2.get("company_d"), 16.27063f);
        assertEquals(innerMap2.get("company_e"), 12.83953f);
    }

    @Test
    public void testToStringGlobalObj() {
        // Assuming some values are already set in GlobalObj for this test
        String globalObjString = GlobalObj.toStringGlobalObj();

        // You can print or log the string to check if it looks correct
        System.out.println(globalObjString);

        // You can also assert specific parts of the string if needed
        assertTrue(globalObjString.contains("coin:"));
        assertTrue(globalObjString.contains("buyCoins:"));
        assertTrue(globalObjString.contains("saleCoins:"));
        assertTrue(globalObjString.contains("coinMap:"));
        assertTrue(globalObjString.contains("currDataMap:"));
        assertTrue(globalObjString.contains("dataMap:"));
    }
}
