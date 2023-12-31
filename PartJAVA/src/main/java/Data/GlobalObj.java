package Data;

import java.util.HashMap;
import java.util.Map;

public class GlobalObj {
    public static Map<String, Float> saleCoins;
    public static Map<String, Float> buyCoins;
    public static float coin;
    public static Map<String, Float> coinMap;
    public static Map<String, Float> currDataMap;
    public static Map<String, Map<String, Float>> dataMap;

    // Static block to initialize static fields
    static {
        saleCoins = new HashMap<>();
        buyCoins = new HashMap<>();
        coinMap = new HashMap<>();
        currDataMap = new HashMap<>();
        dataMap = new HashMap<>();
    }

    // Static method to modify the entire class
    public static void initializeClass(String globInfos) {
        String[] parts = globInfos.split(";");

        // Process "saleCoins"
        if (!parts[0].isEmpty()) {
            String[] saleCoinsParts = parts[0].split(",");
            for (String entry : saleCoinsParts) {
                String[] entryParts = entry.split(" ");
                saleCoins.put(entryParts[0], Float.parseFloat(entryParts[1]));
            }
        }

        // Process "buyCoins"
        if (!parts[1].isEmpty()) {
            String[] buyCoinsParts = parts[1].split(",");
            for (String entry : buyCoinsParts) {
                String[] entryParts = entry.split(" ");
                buyCoins.put(entryParts[0], Float.parseFloat(entryParts[1]));
            }
        }

        // Process "coin"
        coin = Float.parseFloat(parts[2]);

        // Process "coinMap"
        String[] coinMapParts = parts[3].split(",");
        for (String entry : coinMapParts) {
            String[] entryParts = entry.split(" ");
            coinMap.put(entryParts[0], Float.parseFloat(entryParts[1]));
        }

        // Process "currDataMap"
        String[] currDataMapParts = parts[4].split(",");
        for (String entry : currDataMapParts) {
            String[] entryParts = entry.split(" ");
            currDataMap.put(entryParts[0], Float.parseFloat(entryParts[1]));
        }

        // Process "dataMap"
        String[] dataMapParts = parts[5].split("\\|");
        for (String dataMapPart : dataMapParts) {
            String[] dateAndValues = dataMapPart.split(":");
            String date = dateAndValues[0];
            Map<String, Float> innerMap = new HashMap<>();
            String[] innerParts = dateAndValues[1].split(",");
            for (String innerEntry : innerParts) {
                String[] innerEntryParts = innerEntry.split(" ");
                innerMap.put(innerEntryParts[0], Float.parseFloat(innerEntryParts[1]));
            }
            dataMap.put(date, innerMap);
        }
    }

    // Static method to get the string representation of the entire class
    public static String toStringGlobalObj() {
        StringBuilder builder = new StringBuilder();

        // Append saleCoins
        builder.append("saleCoins: {");
        appendMapToString(builder, saleCoins);
        builder.append("}, \n");

        // Append buyCoins
        builder.append("buyCoins: {");
        appendMapToString(builder, buyCoins);
        builder.append("}, \n");

        // Append coin
        builder.append("coin: ").append(coin).append(", \n");

        // Append coinMap
        builder.append("coinMap: {");
        appendMapToString(builder, coinMap);
        builder.append("}, \n");

        // Append currDataMap
        builder.append("currDataMap: {");
        appendMapToString(builder, currDataMap);
        builder.append("}, \n");

        // Append dataMap
        builder.append("dataMap: {\n");
        for (Map.Entry<String, Map<String, Float>> entry : dataMap.entrySet()) {
            builder.append(entry.getKey()).append(": {");
            appendMapToString(builder, entry.getValue());
            builder.append("}, \n");
        }
        if (!builder.isEmpty() && builder.toString().endsWith(", \n")) {
            builder.setLength(builder.length() - 3); // Remove the last 3 characters (", \n")
        }
        builder.append("\n}");

        return builder.toString();
    }

    private static void appendMapToString(StringBuilder builder, Map<String, Float> map) {
        for (Map.Entry<String, Float> entry : map.entrySet()) {
            builder.append(entry.getKey()).append(": ").append(entry.getValue()).append(", \n");
        }
        if (!builder.isEmpty() && builder.toString().endsWith(", \n")) {
            builder.setLength(builder.length() - 3); // Remove the last 3 characters (", \n")
        }
    }
}
